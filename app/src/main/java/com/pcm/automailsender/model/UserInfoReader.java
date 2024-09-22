package com.pcm.automailsender.model;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.internal.LinkedHashTreeMap;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.w3c.dom.Text;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class UserInfoReader {
    public static final String TAG = "UserInfoReader";
    private static final class UserInfoReaderHolder {
        private static UserInfoReader INSTANCE = new UserInfoReader();
    }
    public static UserInfoReader getInstance() {
        return UserInfoReaderHolder.INSTANCE;
    }
    private UserInfoReader() {};

    private Map<String, String> data = new LinkedHashTreeMap<>();

    public void startAnalyze(InputStream in, ReaderCallback callback) throws Exception {
        if (!readData(in)) {
            Log.d(TAG, "read error!");
            if (callback != null) {
                callback.onFail();
            }
            return;
        }
        for (Map.Entry<String, String> item : data.entrySet()) {
            Log.d(TAG, "KEY:" + item.getKey() + " PHONE:" + item.getValue());
        }
        Log.d(TAG, "finish analyze");
        if (data.isEmpty()) {
            if (callback != null) {
                callback.onFail();
            }
            in.close();
            return;
        }
        if (callback != null) {
            callback.onSuc(data);
            in.close();
        }
        data.clear();
    }

    private boolean readData(InputStream in) throws Exception {
        XSSFWorkbook wb = new XSSFWorkbook(in);
        // first sheet
        XSSFSheet sheet = wb.getSheetAt(0);
        int lastRow = sheet.getLastRowNum();
        XSSFRow row;
        if (lastRow < 2) {
            Log.d(TAG, "数据太少!");
            return false;
        }

        // 读取Excel表格
        // 前两行读取表结构
        row = sheet.getRow(0);
        int nameColumn = -1, phoneColumn = -1, classColumn = -1, personIDColumn = -1;
        for (int column = 0; column < row.getLastCellNum(); column++) {
            XSSFCell cell = row.getCell(column);
            if (cell == null) {
                continue;
            }
            String content = cell.toString();
            if (TextUtils.isEmpty(content)) {
                continue;
            }
            if (content.contains("姓名")) {
                nameColumn = column;
            }
            if (content.contains("电话")) {
                phoneColumn = column;
            }
            if (content.contains("班级")) {
                classColumn = column;
            }
            if (content.contains("身份证")) {
                personIDColumn = column;
            }
        }
        if (nameColumn == -1 || phoneColumn == -1 || classColumn == -1) {
            Log.d(TAG, "缺少关键信息");
            return false;
        }
        data.clear();
        for (int i = 1; i < lastRow; i++) { // 行循环,
            row = sheet.getRow(i);
            XSSFCell nameCell = row.getCell(nameColumn);
            XSSFCell classCell = row.getCell(classColumn);
            XSSFCell phoneCell = row.getCell(phoneColumn);
            if (nameCell == null) {
                Log.d(TAG, "nameCell is null:" + i + "nameColumn:" + nameColumn);
                continue;
            }
            if (classCell == null) {
                Log.d(TAG, "classCell is null:" + i + "classColumn:" + classColumn);
                continue;
            }
            if (phoneCell == null) {
                Log.d(TAG, "phoneCell is null:" + i + "phoneColumn:" + phoneColumn);
                continue;
            }
            String key = nameCell.toString() + "" + classCell.toString();
            CellType cellTypeEnum = phoneCell.getCellTypeEnum();
            String phone;
            if (cellTypeEnum.equals(CellType.NUMERIC)) {
                phone = String.format("%.0f", phoneCell.getNumericCellValue());
            } else if (cellTypeEnum.equals(CellType.STRING)) {
                phone = phoneCell.getStringCellValue();
            } else {
                phone = phoneCell.toString();
            }
            Log.d(TAG, "key:" + key + " value:" + phone);
            if (TextUtils.isEmpty(key) || TextUtils.isEmpty(phone)) {
                continue;
            }
            data.put(key, phone);
        }
        return true;
    }

    public void test(){

    }
}
