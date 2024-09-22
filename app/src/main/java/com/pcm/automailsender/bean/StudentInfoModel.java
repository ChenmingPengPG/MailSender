package com.pcm.automailsender.bean;

import android.util.Log;

import com.pcm.automailsender.common.json.JsonUtil;
import com.pcm.automailsender.model.MainReaderModel;

import java.util.HashMap;
import java.util.Map;

public class StudentInfoModel {
    public static final String TAG = "StudentInfoModel";
    private static class StudentInfoModelHolder {
        private static final StudentInfoModel INSTANCE = new StudentInfoModel();
    }
    public static StudentInfoModel getInstance() {
        return StudentInfoModelHolder.INSTANCE;
    }
    private StudentInfoModel() {}

    private Map<String, String> contactMap = new HashMap<>(); //key:姓名+班级 value:联系方式(手机号)

    public void updateStudentInfo(Map<String, String> contactInfo) {
        this.contactMap = contactInfo;
        try {
            MainReaderModel.getInstance().saveToSp(MainReaderModel.KEY_CURRENT_CONTACT, JsonUtil.toJson(contactInfo));
        } catch (Exception e) {
            Log.d(TAG, Log.getStackTraceString(e));
        }
    }

    public Map<String, String> getContactInfo() {
        return this.contactMap;
    }

}
