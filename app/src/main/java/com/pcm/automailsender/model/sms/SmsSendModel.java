package com.pcm.automailsender.model.sms;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.telephony.SmsManager;
import android.util.Log;

import com.pcm.automailsender.agent.CommonAgent;
import com.pcm.automailsender.common.ui.UiUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SmsSendModel {
    public static final String TAG = "SmsSendModel";
    public static final String SEND_SMS_ACTION = "SEND_SMS_ACTION";


    public static void sendSms(Context context, String phoneNo, String msg) {

        try {
            Log.d(TAG, "send sms: phoneNo:" + phoneNo);
            PendingIntent intent = PendingIntent.getBroadcast(context, 0, new Intent(SEND_SMS_ACTION), 0);
            SmsManager smsManager = SmsManager.getDefault();
            ArrayList<String> parts = smsManager.divideMessage(msg);
            smsManager.sendMultipartTextMessage(phoneNo, null, parts, null, null);
//            smsManager.sendTextMessage(phoneNo, null, msg, intent, null);
            UiUtil.show( "phone:" + phoneNo + " send:" + msg);
        } catch (Exception e) {
            Log.d(TAG, Log.getStackTraceString(e));
        }
    }

    public static void sendSmsBySystem(Context context, String number, String text) {
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("sms:" + number));
        intent.putExtra("sms_body", text);
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            UiUtil.show("send fail:" + Log.getStackTraceString(e));
            Log.d(TAG, Log.getStackTraceString(e));
        }
    }

    /**
     * 上行短信状态监听
     */
    private static SmsSendReceiver sendReceiver;

    /**
     * 下行短信内容监听
     */
    private static SmsReceiver smsReceiver;
    public static void registerSmsReceiver() {
        sendReceiver = new SmsSendReceiver();
        smsReceiver = new SmsReceiver();
        CommonAgent.getApplication().registerReceiver(sendReceiver, new IntentFilter(SEND_SMS_ACTION));
        CommonAgent.getApplication().registerReceiver(smsReceiver, new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));
    }
}
