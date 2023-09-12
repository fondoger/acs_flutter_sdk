package com.microsoft.acsflutter.acsflutter;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;

import com.azure.android.communication.calling.CreateViewOptions;
import com.azure.android.communication.calling.LocalVideoStream;
import com.azure.android.communication.calling.ScalingMode;
import com.azure.android.communication.calling.VideoStreamRenderer;

import org.jetbrains.annotations.NotNull;

public class ViewManager {
    @NotNull
    final FrameLayout selfPreviewVideoContainer;

    public ViewManager(Context context){
        selfPreviewVideoContainer = new FrameLayout(context);
    }

    public void setPreviewVideoView(Context context, LocalVideoStream stream) {
        VideoStreamRenderer previewRenderer = new VideoStreamRenderer(stream, context);
        View preview = previewRenderer.createView(new CreateViewOptions(ScalingMode.FIT));
        preview.setTag(0);

        selfPreviewVideoContainer.addView(preview);
    }
}
