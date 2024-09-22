package com.pcm.automailsender.agent;

import android.app.Application;

public class CommonAgent {

    private static Application application = null;

    public static Application getApplication() {
        return application;
    }

    public static void setApplication(Application application) {
        CommonAgent.application = application;
    }
}
