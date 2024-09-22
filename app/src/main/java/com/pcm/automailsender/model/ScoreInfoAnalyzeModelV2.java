package com.pcm.automailsender.model;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.internal.LinkedHashTreeMap;

import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScoreInfoAnalyzeModelV2 {
    public static final String TAG = "ScoreInfoAnalyzeModelV2";
    private ScoreInfoAnalyzeModelV2() {}

    private static final class ScoreInfoAnalyzeModelHolder {
        private static final ScoreInfoAnalyzeModelV2 INSTANCE = new ScoreInfoAnalyzeModelV2();
    }

    public static ScoreInfoAnalyzeModelV2 getInstance() {
        return ScoreInfoAnalyzeModelV2.ScoreInfoAnalyzeModelHolder.INSTANCE;
    }

    private Map<String, ItemStruct> READ_ITEM = new HashMap<String, ItemStruct>() { {
        put("姓名", new ItemStruct(false));
        put("班级", new ItemStruct(false));
        put("总分", new ItemStruct(true));
        put("语文", new ItemStruct(true));
        put("数学", new ItemStruct(true));
        put("外语", new ItemStruct(true));
        put("物理", new ItemStruct(true));
        put("化学", new ItemStruct(true));
        put("生物", new ItemStruct(true));
        put("政治", new ItemStruct(true));
        put("历史", new ItemStruct(true));
        put("地理", new ItemStruct(true));
    }};
    public Map<String, String> result = new LinkedHashTreeMap<>(); // key:姓名+班级, value:成绩描述

    private List<CellRangeAddress> regions = new ArrayList<>(); // 合并单元格 位置数据

    public void startAnalyze(InputStream in, AnalyseCallback callback) throws Exception {
        result.clear();
        if (!readDataV2(in)) {
            Log.d(TAG, "read error!");
            if (callback != null) {
                callback.onAnalyzeFail();
            }
            result.clear();
            return;
        }
        for (Map.Entry<String, String> item : result.entrySet()) {
            Log.d(TAG, "KEY:" + item.getKey() + " VALUE:" + item.getValue());
        }
        Log.d(TAG, "finish analyze");
        if (result.isEmpty()) {
            if (callback != null) {
                callback.onAnalyzeFail();
            }
            in.close();
            return;
        }
        if (callback != null) {
            callback.onAnalyzeSuc(result);
            in.close();
        }
    }

    public Map<String, String> generateContent() {
        return new LinkedHashTreeMap<>();
    }

    private boolean readDataV2(InputStream in) throws Exception {
        XSSFWorkbook wb = new XSSFWorkbook(in);
        // first sheet
        XSSFSheet sheet = wb.getSheetAt(0);
        int lastRow = sheet.getLastRowNum();
        XSSFRow row;

        regions = sheet.getMergedRegions();
        Log.d(TAG, "REGIONS SIZE:" + regions.size());

        // 读取Excel表格
        // 前两行读取表结构
        for (int i = 0; i < Math.min(2, lastRow); i++) { // 行循环,
            row = sheet.getRow(i);
            int columnNum = row.getLastCellNum();
            String lastFindKey = "";
            for (int j = 0; j < columnNum; j++) { // 列循环
                XSSFCell cell = row.getCell(j);
                String content = cell == null ? "" : cell.toString();
                if (!TextUtils.isEmpty(content) && READ_ITEM.containsKey(content) && !content.equals(lastFindKey)) {
                    lastFindKey = content;
                }
                CellRangeAddress range = findRegionRange(i, j);
                if (range == null) {
                    range = new CellRangeAddress(i, i+1, j, j+1);
                }
                if (!TextUtils.isEmpty(content) && READ_ITEM.containsKey(content)) {
                    updateRange(range, content, i, j);
                } else if (TextUtils.isEmpty(content) && !TextUtils.isEmpty(lastFindKey)) {
                    updateRange(range, lastFindKey, i, j);
                }
            }
        }
        // 后面读取内容
        int startRow = 2;
        for (int i = startRow; i <= lastRow; i++) {
            row = sheet.getRow(i);
            int columnNum = row.getLastCellNum();
            StringBuilder sb = new StringBuilder();
            StringBuilder studentId = new StringBuilder();
            String currentKey = null;
            for (int j = 0; j < columnNum; j++) { // 列循环
                XSSFCell cell = row.getCell(j);
                if (cell == null) {
                    Log.d(TAG, "cell is null:" + i + "," + j);
                    continue;
                }
                String content = cell.toString();
                if (TextUtils.isEmpty(content)) {
                    continue;
                }
                ItemStruct itemStruct = getItemStruct(j);
                if (itemStruct == null) {
                    continue;
                }
                String key = getItemKey(j);
                if ("姓名".equals(key)) {
                    studentId.append(content);
                }
                if ("班级".equals(key)) {
                    studentId.append(content);
                }
                if (!TextUtils.isEmpty(key) && !TextUtils.equals(currentKey, key)) {
                    sb.append("------------\n");
                    sb.append(key).append(":");
                    currentKey = key;
                    if (itemStruct.hasSubItem) {
                        sb.append("\n");
                    }
                }

                if (!itemStruct.hasSubItem) {
                    sb.append(content).append("\n");
                } else {
                    String subItemKey = sheet.getRow(itemStruct.subItemRow).getCell(j).toString();
                    sb.append(subItemKey).append(":").append(content).append("\n");
                }
            }
            sb.append("------------");
            Log.d(TAG, "analyse studentId:" + studentId + "\ndata:" + sb.toString());
            if (!TextUtils.isEmpty(studentId) && !TextUtils.isEmpty(sb)) {
                result.put(studentId.toString(), sb.toString());
            }
        }
        return true;
    }

    private void updateRange(CellRangeAddress range, String key, int curRow, int curColumn) {
        ItemStruct itemStruct = READ_ITEM.get(key);
        if (itemStruct == null || range == null) {
            return;
        }

        CellRangeAddress curRange = itemStruct.range;
        if (curRange == null) {
            itemStruct.range = range;
        } else {
            if (curRange.getLastColumn() - 1 < curColumn) {
                curRange.setLastColumn(curColumn + 1);
            }
            if (curRange.getLastRow() - 1  < curRow) {
                curRange.setLastRow(curRow + 1);
            }
        }
        if (itemStruct.hasSubItem) {
            itemStruct.subItemRow = itemStruct.range.getLastRow(); // 默认下一行为子item(例如:分数、排名等)
        }
    }

    private int getContentStartRow() {
        int maxRow = -1;
        for (Map.Entry<String, ItemStruct> item : READ_ITEM.entrySet()) {
            if (item.getValue() != null
                    && item.getValue().range != null
                    && item.getValue().range.getLastRow() > maxRow) {
                maxRow = item.getValue().range.getLastRow();
            }
        }
        return maxRow + 1;
    }

    private ItemStruct getItemStruct(int column) {
        for (Map.Entry<String, ItemStruct> item : READ_ITEM.entrySet()) {
            if (item.getValue() != null
                    && item.getValue().range != null
                    && item.getValue().range.getLastColumn() > column
                    && item.getValue().range.getFirstColumn() <= column) {
                return item.getValue();
            }
        }
        Log.d(TAG, "column:" + column + " get value fail!!");
        return null;
    }

    private String getItemKey(int column) {
        for (Map.Entry<String, ItemStruct> item : READ_ITEM.entrySet()) {
            if (item.getValue() != null
                    && item.getValue().range != null
                    && item.getValue().range.getLastColumn() > column
                    && item.getValue().range.getFirstColumn() <= column) {
                return item.getKey();
            }
        }
        Log.d(TAG, "column:" + column + " get key fail!!");
        return null;
    }

    private boolean hasSubItem(int column) {
        for (Map.Entry<String, ItemStruct> item : READ_ITEM.entrySet()) {
            if (item.getValue() != null
                    && item.getValue().range != null
                    && item.getValue().range.getLastColumn() > column
                    && item.getValue().range.getFirstColumn() <= column) {
                return item.getValue().hasSubItem;
            }
        }
        return false;
    }

    private CellRangeAddress findRegionRange(int row, int column) {
        if (regions == null || regions.isEmpty()) {
            return null;
        }
        for (CellRangeAddress range : regions) {
            if (range.getFirstRow() <= row && range.getLastRow() >= row
                    && range.getFirstColumn() <= column && range.getLastColumn() >= column) {
                return range;
            }
        }
        return null;
    }

    public static class ItemStruct {
        CellRangeAddress range = null;
        boolean hasSubItem = false;
        int subItemRow = -1;

        public ItemStruct(boolean hasSub) {
            if (hasSub) {
                this.hasSubItem = true;
                subItemRow = 1;
            }
        }
    }
}
