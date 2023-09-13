package com.microsoft.acsflutter.acsflutter;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Map;

import io.flutter.Log;
import io.flutter.plugin.common.StandardMessageCodec;
import io.flutter.plugin.platform.PlatformView;
import io.flutter.plugin.platform.PlatformViewFactory;

public class NativeVideoStreamViewFactory extends PlatformViewFactory {
    @NonNull
    final private  ViewManager viewManager;

    NativeVideoStreamViewFactory(ViewManager viewManager) {
        super(StandardMessageCodec.INSTANCE);
        this.viewManager = viewManager;
    }

    @NonNull
    @Override
    public PlatformView create(@NonNull Context context, int id, @Nullable Object args) {
        final Map<String, Object> creationParams = (Map<String, Object>) args;
        String viewKey = (String) creationParams.get("viewKey");

        return new PlatformView() {
            @Override
            public View getView()  {
                switch (viewKey) {
                    case "localVideoView":
                        return viewManager.localVideoContainer;
                    case "remoteVideoView":
                        Log.d("tag", "get remote video view");
                        return viewManager.remoteVideoContainer;
                    default:
                        Log.d("tag","Unexpected view key: "+ viewKey);
                        return null;
                }
            }

            @Override
            public void dispose() {}
        };
    }
}
