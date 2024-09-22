package com.pcm.automailsender.bean;

import android.util.Log;

import com.google.gson.Gson;

import java.io.Serializable;

public class BaseBean implements Serializable {
    public static final String TAG = BaseBean.class.getSimpleName();

    public String toString() {
        return buildString();
    }

    private String buildString() {
        try {
            return new Gson().toJson(this);
        } catch (Exception e) {
            Log.e(TAG, "buildString error,msg:" + e.getMessage());
            return super.toString();
        }
    }
}
