package com.pcm.automailsender;

import android.app.Application;

import com.pcm.automailsender.agent.CommonAgent;
import com.pcm.automailsender.common.ui.UiUtil;
import com.pcm.automailsender.model.MainReaderModel;
import com.pcm.automailsender.model.sms.SmsSendModel;

public class AppApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        CommonAgent.setApplication(this);
        UiUtil.init(this);
        MainReaderModel.getInstance().initSp(this);
        SmsSendModel.registerSmsReceiver();
    }
}
