package com.pcm.automailsender.model;

import java.util.Map;

public interface AnalyseCallback {
    void onAnalyzeSuc(Map<String, String> data);
    void onAnalyzeFail();
}
