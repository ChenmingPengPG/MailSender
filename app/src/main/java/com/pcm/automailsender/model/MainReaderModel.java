package com.pcm.automailsender.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

import com.pcm.automailsender.bean.StudentInfoModel;
import com.pcm.automailsender.common.ui.UiUtil;
import com.pcm.automailsender.model.email.EmailSendModel;
import com.pcm.automailsender.ui.home.HomeViewModel;
import com.pcm.automailsender.util.CommonUtil;

import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static android.content.Context.MODE_PRIVATE;

public class MainReaderModel {
    public static final String TAG = "MainReaderModel";

    private static final class MainReaderModelHolder {
        private static final MainReaderModel INSTANCE = new MainReaderModel();
    }
    public static MainReaderModel getInstance() {
        return MainReaderModelHolder.INSTANCE;
    }
    private MainReaderModel() {}

    private boolean currentUseSms = true;
    private static final String SP_NAME = "data";
    public static final String KEY_CURRENT_TYPE = "KEY_CURRENT_TYPE";
    public static final String KEY_CURRENT_CONTACT = "KEY_CURRENT_CONTACT";

    private SharedPreferences sp;
    public Uri fileUri = null;

    public void initSp(Context context) {
        //通过getSharedPreferences() 方法指定SharedPreferences的文件名为data
        sp = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
    }

    public <T> void saveToSp(String key, T value) {
        SharedPreferences.Editor editor = sp.edit();
        if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        } else if (value instanceof String) {
            editor.putString(key, (String)value);
        }
        editor.apply();
    }

    public int getIntFromSp(String key) {
        return sp.getInt(key, HomeViewModel.CURRENT_READ_SCORE);
    }

    public String getStringFromSp(String key) {
        return sp.getString(key, "");
    }

    public void readData(Context context, HomeViewModel hvm) {
        if (fileUri == null) {
            Log.d(TAG, "file uri is still null");
            UiUtil.show("等待向应用分享文件");
            return;
        }
        if (hvm == null) {
            Log.d(TAG, "hvm is null");
            UiUtil.show("应用异常");
            return;
        }
        if (hvm.getCheckChoose() == null || hvm.getCheckChoose().getValue() == null) {
            Log.d(TAG, "choose type is null");
            return;
        }
        int currentReadType = hvm.getCheckChoose().getValue();
        try {
            InputStream in = context.getContentResolver().openInputStream(fileUri);
            if (currentReadType == HomeViewModel.CURRENT_READ_CONTACT) {
                UserInfoReader.getInstance().startAnalyze(in, new ReaderCallback() {
                    @Override
                    public void onSuc(Map<String, String> data) {
                        StudentInfoModel.getInstance().updateStudentInfo(data);
                        hvm.setContactInfo(CommonUtil.getStringFromMap(data));
//                        hvm.setCanSendSms(false);
                    }

                    @Override
                    public void onFail() {
                        Log.d(TAG, "read contact fail");
                        UiUtil.show("读取联系信息失败");
//                        hvm.setCanSendSms(false);
                    }
                });
            } else if (currentReadType == HomeViewModel.CURRENT_READ_SCORE) {
                if (currentUseSms) {
                    ScoreInfoAnalyzeModelV2.getInstance().startAnalyze(in, new AnalyseCallback() {
                        @Override
                        public void onAnalyzeSuc(Map<String, String> data) {
                            Map<String, String> contactInfo = StudentInfoModel.getInstance().getContactInfo();
                            if (contactInfo == null || contactInfo.isEmpty()) {
                                hvm.setScoreInfo(CommonUtil.getStringFromMap(data));
                            } else {
                                data = data.entrySet()
                                        .stream()
                                        .filter((entry) -> contactInfo.containsKey(entry.getKey()))
                                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                                Log.d(TAG, "SIZE:" + data.size());
                                hvm.setScoreInfo(CommonUtil.getStringFromMap(data));
                                if (data.size() > 0) {
                                    hvm.setCanSendSms(true);
                                }
                            }
                            hvm.setScoreResult(data);

                        }

                        @Override
                        public void onAnalyzeFail() {
                            UiUtil.show("解析文件失败。");
                            hvm.setCanSendSms(false);
                        }
                    });
                } else {
                    ScoreInfoAnalyzeModel.getInstance().startAnalyze(in, context, new AnalyseCallback() {
                        @Override
                        public void onAnalyzeSuc(Map<String, String> data) {
                            EmailSendModel.getInstance().setEmailData(data);
                            EmailSendModel.getInstance().sendEmails();
                        }

                        @Override
                        public void onAnalyzeFail() {
                            UiUtil.show("解析文件失败。");
                        }
                    });
                }
            }
        } catch (Exception e) {
            Log.d(TAG,  Log.getStackTraceString(e));
        }
    }
}
