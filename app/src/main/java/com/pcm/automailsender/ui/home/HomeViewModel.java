package com.pcm.automailsender.ui.home;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.reflect.TypeToken;
import com.pcm.automailsender.bean.StudentInfoModel;
import com.pcm.automailsender.common.json.JsonUtil;
import com.pcm.automailsender.common.ui.UiUtil;
import com.pcm.automailsender.model.MainReaderModel;
import com.pcm.automailsender.model.sms.SmsSendModel;
import com.pcm.automailsender.util.CommonUtil;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeViewModel extends ViewModel {
    public static final String TAG = "HomeViewModel";

    private MutableLiveData<Integer> mCheckChoose;
    public static final int CURRENT_READ_CONTACT = 1;
    public static final int CURRENT_READ_SCORE = 2;


    private MutableLiveData<String> mContactButtonTitle;
    private MutableLiveData<String> mContactResultText;

    private MutableLiveData<String> mScoreButtonTitle;
    private MutableLiveData<String> mScoreResultText;

    private MutableLiveData<String> mSendSms;
    private MutableLiveData<Boolean> mCanSendSms;

    private Map<String, String> scoreResult;

    public HomeViewModel() {
        mCheckChoose = new MutableLiveData<>();
        try {
            int type = MainReaderModel.getInstance().getIntFromSp(MainReaderModel.KEY_CURRENT_TYPE);
            Log.d(TAG, "init type:" + type);
            mCheckChoose.setValue(type);
        } catch (Exception e) {
            Log.d(TAG, Log.getStackTraceString(e));
        }

        mContactResultText = new MutableLiveData<>();
        String contactInfo = "";
        try {
            String json = MainReaderModel.getInstance().getStringFromSp(MainReaderModel.KEY_CURRENT_CONTACT);
            Map<String, String> map = JsonUtil.fromJson(json, new TypeToken<LinkedHashMap<String, String>>(){}.getType());
            StudentInfoModel.getInstance().updateStudentInfo(map);
            contactInfo = CommonUtil.getStringFromMap(map);
        } catch (Exception e) {
            Log.d(TAG, Log.getStackTraceString(e));
        }
        mContactResultText.setValue(contactInfo);

        mContactButtonTitle = new MutableLiveData<>();
        mContactButtonTitle.setValue("当前读取学生联系方式文件\n(仅支持xlsx文件)");

        mScoreResultText = new MutableLiveData<>();
        mScoreResultText.setValue("");

        mScoreButtonTitle = new MutableLiveData<>();
        mScoreButtonTitle.setValue("当前读取学生成绩文件\n(仅支持xlsx文件)");

        mSendSms = new MutableLiveData<>();
        mSendSms.setValue("发送短信");

        mCanSendSms = new MutableLiveData<>();
        mCanSendSms.setValue(false);
    }

    public LiveData<String> getContactButtonTitle() {
        return mContactButtonTitle;
    }

    public LiveData<String> getContactResultText() {
        return mContactResultText;
    }

    public LiveData<String> getScoreButtonTitle() {
        return mScoreButtonTitle;
    }

    public LiveData<String> getScoreResultText() {
        return mScoreResultText;
    }

    public LiveData<String> getSendSmsButtonTitle() {
        return mSendSms;
    }

    public LiveData<Integer> getCheckChoose() {
        return mCheckChoose;
    }

    public LiveData<Boolean> getCanSendSms() {
        return mCanSendSms;
    }

    public void setCheckContact(int value) {
        mCheckChoose.setValue(value);
        MainReaderModel.getInstance().saveToSp(MainReaderModel.KEY_CURRENT_TYPE, value);
        Log.d(TAG, "current:" + value);
    }

    public void setContactInfo(String value) {
        mContactResultText.setValue(value);
    }

    public void setScoreInfo(String value) {
        mScoreResultText.setValue(value);
    }

    public void setCanSendSms(boolean b) {
        mCanSendSms.setValue(b);
    }

    public void onSendSmsClick(Context context) {
        Map<String, String> contactInfo = StudentInfoModel.getInstance().getContactInfo();
        if (scoreResult == null || contactInfo == null) {
            UiUtil.show("数据错误");
            return;
        }
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                // 在这里执行子线程任务
                for (Map.Entry<String, String> entry : scoreResult.entrySet()) {
                    String phone = contactInfo.get(entry.getKey());
                    String content = entry.getValue();
                    if (!TextUtils.isEmpty(phone) && !TextUtils.isEmpty(content)) {
                        SmsSendModel.sendSms(context, phone, content);
                    }
                }
            }
        });

    }

    public void setScoreResult(Map<String, String> scoreResult) {
        this.scoreResult = scoreResult;
    }
}