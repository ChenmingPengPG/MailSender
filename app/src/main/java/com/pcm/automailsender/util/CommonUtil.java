package com.pcm.automailsender.util;

import java.util.Iterator;
import java.util.Map;

public class CommonUtil {
    public static String getStringFromMap(Map<String, String> map) {
        if (map == null || map.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();
        if (!iterator.hasNext()) {
            return "";
        }
        Map.Entry<String, String> entry = iterator.next();
        sb.append(entry.getKey()).append(":").append(entry.getValue());
        while(iterator.hasNext()) {
            Map.Entry<String, String> currentEntry = iterator.next();
            sb.append("\n").append(currentEntry.getKey()).append(":").append(currentEntry.getValue());
        }
        return sb.toString();
    }
}
