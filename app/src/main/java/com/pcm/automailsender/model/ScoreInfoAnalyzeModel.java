package com.pcm.automailsender.model;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ScoreInfoAnalyzeModel {
    public static final String TAG = "ScoreInfoAnalyzeModel";

    private ScoreInfoAnalyzeModel() {}

    private static final class ScoreInfoAnalyzeModelHolder {
        private static final ScoreInfoAnalyzeModel INSTANCE = new ScoreInfoAnalyzeModel();
    }

    public static ScoreInfoAnalyzeModel getInstance() {
        return ScoreInfoAnalyzeModelHolder.INSTANCE;
    }

    private Context context;

    private final Map<Integer, String> keyData = new LinkedHashMap<>(); // 列， key
    private final List<Map<Integer, String>> finalData = new ArrayList<>(); //成绩数据集合
    private List<CellRangeAddress> regions = new ArrayList<>(); // 合并单元格 位置数据
    private CellRangeAddress nameRange;

    public void startAnalyze(InputStream in, Context context, AnalyseCallback callback) throws Exception {
        this.context = context;
        if (!readData(in)) {
            Log.d(TAG, "read error!");
            if (callback != null) {
                callback.onAnalyzeFail();
            }
            return;
        }
        Map<String, String> data = generateContent();
        for (Map.Entry<String, String> item : data.entrySet()) {
            Log.d(TAG, "KEY:" + item.getKey() + " VALUE:" + item.getValue());
        }
        Log.d(TAG, "finish analyze");
        if (data.isEmpty()) {
            if (callback != null) {
                callback.onAnalyzeFail();
            }
            in.close();
            return;
        }
        if (callback != null) {
            callback.onAnalyzeSuc(data);
            in.close();
        }
    }

    private boolean readData(InputStream in) {
        try {
            XSSFWorkbook wb = new XSSFWorkbook(in);
            // first sheet
            XSSFSheet sheet = wb.getSheetAt(0);
            int lastRow = sheet.getLastRowNum();
            XSSFRow row;

            regions = sheet.getMergedRegions();
            Log.d(TAG, "REGIONS SIZE:" + regions.size());

            // 读取Excel表格
            for (int i = 0; i <= lastRow; i++) { // 行循环
                row = sheet.getRow(i);
                int columnNum = row.getLastCellNum();
                Map<Integer, String> valueData = new HashMap<>(); // 列， 值
                for (int j = 0; j < columnNum; j++) { // 列循环
                    XSSFCell cell = row.getCell(j);
                    if (cell == null) {
                        Log.i(TAG, "ROW:" + i + " COLUMN:" + j + " IS NULL!");
                        continue;
                    }
                    Log.i(TAG, "ROW:" + i + " COLUMN:" + j + ", " + cell.toString());
                    CellRangeAddress range = null;
                    if ("姓名".equals(cell.toString())) { // 以"姓名"为锚点，计算key值
                        nameRange = findRegionRange(i, j);
                        if (nameRange == null) {
                            nameRange = new CellRangeAddress(i, i, j, j);
                        }
                        range = nameRange;
                    } else if (!TextUtils.isEmpty(cell.toString())){
                        range = findRegionRange(i, j);
                    }
                    if (nameRange != null && i <= nameRange.getLastRow() && range != null) {
                        for (int cur = range.getFirstColumn(); cur <= range.getLastColumn(); cur++) {
                            if (keyData.containsKey(cur)) {
                                keyData.put(cur, keyData.get(cur) + "-" + cell.toString());
                            } else {
                                keyData.put(cur, cell.toString());
                            }
                        }
                        continue;
                    }
                    valueData.put(j, cell.toString());
                }
                if (nameRange != null && i > nameRange.getLastRow()) {
                    finalData.add(valueData);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private Map<String, String> generateContent() { // key: mail; value: data
        Map<String, String> result = new HashMap<>();
        for (Map<Integer, String> item : finalData) {
            StringBuilder itemContent = new StringBuilder();
            String studentId = null;
            for (Map.Entry<Integer, String> entry : keyData.entrySet()) {
                String keyName = entry.getValue();
                String content = item.get(entry.getKey());
                if ("学号".equals(keyName)) {
                    studentId = content;
                }
                itemContent.append(keyName).append(":").append(content).append("\n");
            }
            if (studentId == null) {
                Log.e(TAG, "don't find 学号!!!");
            }
            result.put(getEmailInfo(studentId), itemContent.toString());
        }
        return result;
    }

    private String getEmailInfo(String studentId) {
        return  studentId + "@qq.com";
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

}
