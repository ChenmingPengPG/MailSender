package com.pcm.automailsender.common.ui;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.StringRes;

import com.pcm.automailsender.agent.CommonAgent;

public class UiUtil {
    public static final String TAG = "UiUtil";

    private static DisplayMetrics metrics;
    public static void init(Context context) {
        metrics = context.getResources().getDisplayMetrics();
    }

    public static int dp2px(int dp) {
        return (int) (dp * metrics.density + 0.5f);
    }


    private static final Handler sHandler = new Handler(Looper.getMainLooper());

    public static void show(@StringRes int resId, boolean shortDuration) {
        Application context = CommonAgent.getApplication();
        if (context == null) {
            Log.d(TAG, "context is null!");
            return;
        }
        CharSequence text = "";
        try {
            text = context.getResources().getText(resId);
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
        show(text, shortDuration, 0);
    }

    /**
     * @param text          文本内容
     * @param shortDuration 是否为短间隔
     * @param gravity       Toast展示的位置，见{@link android.view.Gravity}
     */
    public static void show(final CharSequence text, final boolean shortDuration, final int gravity) {
        final Application context = CommonAgent.getApplication();
        if (context == null || TextUtils.isEmpty(text)) {
            Log.d(TAG, "context is null! content=" + text);
            return;
        }
        sHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    Toast toast = Toast.makeText(context, text, shortDuration ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG);
                    if (gravity > 0) {
                        toast.setGravity(gravity, 0, 0);
                    }
                    toast.show();
                } catch (Exception e) {
                    Log.e(TAG, "show toast fail! content=" + text);
                }
            }
        });
    }

    /**
     * 弹短Toast便捷方法
     *
     * @param text 文本内容
     */
    public static void show(CharSequence text) {
        show(text, true, 0);
    }
}
