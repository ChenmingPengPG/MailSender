package com.pcm.automailsender.model.sms;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.util.Log;

public class SmsSendReceiver extends BroadcastReceiver {
    public static final String TAG = "SmsSendReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {

        switch (getResultCode()) {
            case Activity.RESULT_OK:
                Log.d(TAG, "发送成功");
                break;
            case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
            case SmsManager.RESULT_ERROR_RADIO_OFF:
            case SmsManager.RESULT_ERROR_NULL_PDU:
            default:
                Log.d(TAG, "短信发送失败,请查看当前信号是否正常,或者是否禁止了短信权限:" + getResultCode() + getResultData());
                break;
        }
    }
}

