package com.pcm.automailsender.common.json;

import android.os.Bundle;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class DummyBundleTypeAdapterFactory implements TypeAdapterFactory {

    @SuppressWarnings("unchecked")
    @Override
    public <T> TypeAdapter<T> create(final Gson gson, TypeToken<T> type) {
        if (!Bundle.class.isAssignableFrom(type.getRawType())) {
            return null;
        }
        return (TypeAdapter<T>) new TypeAdapter<Bundle>() {
            @Override
            public void write(JsonWriter out, Bundle bundle) throws IOException {

            }

            @Override
            public Bundle read(JsonReader in) throws IOException {
                return new Bundle();
            }
        };
    }
}
