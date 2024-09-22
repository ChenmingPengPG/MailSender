package com.pcm.automailsender.model.sms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.pcm.automailsender.util.CommonUtil;

import java.util.HashMap;
import java.util.Map;

public class SmsReceiver extends BroadcastReceiver {
    public static final String TAG = "SmsReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle == null || !bundle.containsKey("pdus")) {
            return;
        }
        Object[] smsObj = (Object[]) bundle.get("pdus");
        if (smsObj == null || smsObj.length == 0) {
            return;
        }
        SmsMessage[] msgs = new SmsMessage[smsObj.length];
        Map<String, String> smsMap = new HashMap<>(smsObj.length);
        Log.d(TAG, CommonUtil.getStringFromMap(smsMap));
        for (int i = 0; i < smsObj.length; i++) {
            msgs[i] = SmsMessage.createFromPdu((byte[]) smsObj[i]);
            String originatingAddress = msgs[i].getOriginatingAddress();
            if (!smsMap.containsKey(originatingAddress)) {
                smsMap.put(originatingAddress, msgs[i].getMessageBody());
            } else {
                String text = smsMap.get(originatingAddress);
                String msgTxt = text + msgs[i].getMessageBody();
                smsMap.put(originatingAddress, msgTxt);
            }
        }
    }
}

