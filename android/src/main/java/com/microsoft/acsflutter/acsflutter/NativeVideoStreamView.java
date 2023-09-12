package com.microsoft.acsflutter.acsflutter;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Map;

import io.flutter.plugin.platform.PlatformView;

public class NativeVideoStreamView implements PlatformView {

    NativeVideoStreamView(@NonNull Context context, int id, @Nullable Map<String, Object> creationParams) {

    }

    @NonNull
    @Override
    public View getView() {
//        return view;
        return null;
    }

    @Override
    public void dispose() {
    }
}
