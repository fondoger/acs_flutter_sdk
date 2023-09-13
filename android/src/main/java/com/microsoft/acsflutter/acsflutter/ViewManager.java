package com.microsoft.acsflutter.acsflutter;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.GridLayout;

import com.azure.android.communication.calling.CreateViewOptions;
import com.azure.android.communication.calling.LocalVideoStream;
import com.azure.android.communication.calling.ScalingMode;
import com.azure.android.communication.calling.VideoStreamRenderer;

import org.jetbrains.annotations.NotNull;

import io.flutter.Log;

public class ViewManager {
    @NotNull
    final FrameLayout localVideoContainer;
    @NotNull
    final FrameLayout remoteVideoContainer;

    public ViewManager(Context context){
        localVideoContainer = new FrameLayout(context);
        remoteVideoContainer = new FrameLayout(context);
    }

    public void setPreviewVideoView(Context context, LocalVideoStream stream) {
        if (localVideoContainer.getChildCount() > 0) {
            return;
        }

        VideoStreamRenderer previewRenderer = new VideoStreamRenderer(stream, context);
        View preview = previewRenderer.createView(new CreateViewOptions(ScalingMode.FIT));
        preview.setTag(0);

        localVideoContainer.addView(preview);
    }

    public void addRemoteVideoView(Activity activity, View view) {
        Log.d("tag", "Add remote video view");
        remoteVideoContainer.addView(view);
    }

    public void removeRemoteVideoView(Activity activity, int streamId) {
        for(int i = 0; i < remoteVideoContainer.getChildCount(); ++ i) {
            View childView =  remoteVideoContainer.getChildAt(i);
            if ((int)childView.getTag() == streamId) {
                remoteVideoContainer.removeViewAt(i);
            }
        }
    }
}
