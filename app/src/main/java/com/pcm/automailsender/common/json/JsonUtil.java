package com.pcm.automailsender.common.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class JsonUtil {
    private static volatile Gson gson;
    private static final Object initLock = new Object();

    public static <T> T fromJson(String json, Class<T> cls) {
        try {
            return getGson().fromJson(json, cls);
        } catch (JsonParseException e) {
            throw new JsonException(e);
        }
    }

    public static String toJson(Object src) {
        try {
            return getGson().toJson(src);
        } catch (JsonParseException e) {
            throw new JsonException(e);
        }
    }

    public static <T> T fromJson(JsonElement json, Class<T> cls) {
        try {
            return getGson().fromJson(json, cls);
        } catch (JsonParseException e) {
            throw new JsonException(e);
        }
    }

    public static <T> T fromJson(JsonElement jsonElement, Type type) {
        try {
            return getGson().fromJson(jsonElement, type);
        } catch (JsonParseException e) {
            throw new JsonException(e);
        }
    }

    public static <T> List<T> parseArray(String json, Class<T> cls) {
        try {
            JsonElement jsonElement = getGson().fromJson(json, JsonArray.class);
            if (!jsonElement.isJsonArray()) {
                throw new JsonException("json is not a json array");
            }
            ArrayList<T> arrayList = new ArrayList<>();
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            for (int i = 0, count = jsonArray.size(); i < count; i++) {
                JsonElement element = jsonArray.get(i);
                T t = getGson().fromJson(element, cls);
                arrayList.add(t);
            }
            return arrayList;
        } catch (JsonParseException e) {
            throw new JsonException(e);
        }
    }

    public static <T> T fromJson(String json, Type typeOfT) {
        try {
            return getGson().fromJson(json, typeOfT);
        } catch (JsonParseException e) {
            throw new JsonException(e);
        }
    }

    public static Gson getGson() {
        if (gson == null) {
            synchronized (initLock) {
                if (gson == null) {
                    gson = new GsonBuilder()
                            // 支持部分json体以string方式取出
                            .registerTypeAdapter(String.class, new StringJsonDeserializer())
                            // 避免序列化/反序列化Bundle出现问题
                            .registerTypeAdapterFactory(new DummyBundleTypeAdapterFactory())
                            .create();
                }
            }
        }
        return gson;
    }
}
