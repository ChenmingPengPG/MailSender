package com.pcm.automailsender.model;

import java.util.Map;

public interface ReaderCallback {
    void onSuc(Map<String, String> data);
    void onFail();
}
