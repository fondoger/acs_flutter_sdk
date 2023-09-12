package com.microsoft.acsflutter.acsflutter;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import android.content.Context;
import android.media.AudioManager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.azure.android.communication.calling.Call;
import com.azure.android.communication.calling.CallAgent;
import com.azure.android.communication.calling.CallClient;
import com.azure.android.communication.calling.CreateViewOptions;
import com.azure.android.communication.calling.DeviceManager;
import com.azure.android.communication.calling.HangUpOptions;
import com.azure.android.communication.calling.LocalVideoStream;
import com.azure.android.communication.calling.ScalingMode;
import com.azure.android.communication.calling.StartCallOptions;
import com.azure.android.communication.calling.VideoDeviceInfo;
import com.azure.android.communication.calling.VideoOptions;
import com.azure.android.communication.calling.VideoStreamRenderer;
import com.azure.android.communication.common.CommunicationIdentifier;
import com.azure.android.communication.common.CommunicationTokenCredential;
import com.azure.android.communication.common.CommunicationUserIdentifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

import io.flutter.Log;

public class Implementations {

    private final CallClient callClient = new CallClient();
    @NonNull
    private final ViewManager viewManager;
    @NonNull
    private final Context context;
    @Nullable
    private Activity activity;
    @Nullable
    private Call call;

    public Implementations(@NonNull Context context, @NonNull ViewManager viewManager) {
        this.context = context;
        this.viewManager = viewManager;
    }
    public void setActivity(@Nullable Activity activity) {
        this.activity = activity;
    }

    public void getAllPermissions() {
        String[] requiredPermissions = new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE};
        ArrayList<String> permissionsToAskFor = new ArrayList<>();
        for (String permission : requiredPermissions) {
            if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsToAskFor.add(permission);
            }
        }
        if (!permissionsToAskFor.isEmpty()) {
            ActivityCompat.requestPermissions(activity, permissionsToAskFor.toArray(new String[0]), 1);
        }
    }
    private CallAgent createCallAgent() throws Exception {
        // Create Agent
        String userToken = "eyJhbGciOiJSUzI1NiIsImtpZCI6IjVFODQ4MjE0Qzc3MDczQUU1QzJCREU1Q0NENTQ0ODlEREYyQzRDODQiLCJ4NXQiOiJYb1NDRk1kd2M2NWNLOTVjelZSSW5kOHNUSVEiLCJ0eXAiOiJKV1QifQ.eyJza3lwZWlkIjoiYWNzOjc1ZTQ3YzVjLTVhYTMtNDMxNC1hZDAxLWEzNjc0YzZkYTJjNF8wMDAwMDAxYi0yN2Q3LTZmNjEtMDJjMy01OTNhMGQwMDE0OTEiLCJzY3AiOjE3OTIsImNzaSI6IjE2OTQ0OTgxNDciLCJleHAiOjE2OTQ1ODQ1NDcsInJnbiI6ImFtZXIiLCJhY3NTY29wZSI6InZvaXAiLCJyZXNvdXJjZUlkIjoiNzVlNDdjNWMtNWFhMy00MzE0LWFkMDEtYTM2NzRjNmRhMmM0IiwicmVzb3VyY2VMb2NhdGlvbiI6InVuaXRlZHN0YXRlcyIsImlhdCI6MTY5NDQ5ODE0N30.aA2K0jVAZJIzdKc4yneVcs0xB1uRs1Uii5kqGUGtRXNn8X8ZsqnNwQzQJUWei9tKbxHZdPKQ3GClMLPEvlHCvcpMSEWm8c4i4B2BTqYg8Z06tnoLEvxjCW_yKKfwYWLobgD2Ym5pCHVkc_GZSAxttWWGCzOoZKFbNOQ0Ldzvlp_9amG1x7qWGCx7qo_KF1NzULyBVLr3XROefLYjPv_Rx08jypaPJDxeNXilUV_gEq0dl548QH5ztG8_aoqHt4gzJYybo4DDcEsC-UOZuqS7WBbm4YgCPT2x9nXYSvLvorscfKSKl5lteBOVj_WcVVQ4wyI60hB9WUGlh6T-7eBeKA";

        try {
            activity.setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);

            // create agent
            CommunicationTokenCredential credential = new CommunicationTokenCredential(userToken);
            return callClient.createCallAgent(context, credential).get();
        } catch (Exception ex) {
            Toast.makeText(context, "Failed to create start call.", Toast.LENGTH_SHORT).show();
            throw new Exception("Failed to create agent");
        }
    }

    public void startCall(String calleeId) {
        try {
            CallAgent callAgent = createCallAgent();

            // start call
            StartCallOptions options = new StartCallOptions();
            call = callAgent.startCall(context,
                    Collections.singletonList(new CommunicationUserIdentifier(calleeId) ),
                    options);
        } catch (Exception ex) {
            
            Toast.makeText(context, "Failed to create start call.", Toast.LENGTH_SHORT).show();
        }
    }

    public void stopCall() {
        if (this.call != null) {
            this.call.hangUp(new HangUpOptions()).exceptionally(ex -> {
                Toast.makeText(context, "Failed to stop call", Toast.LENGTH_SHORT).show();
                return null;
            });
        }
    }

    public void startOneToOneVideoCall(String callId) {
        try {
            CallAgent callAgent = createCallAgent();

            ArrayList<CommunicationIdentifier> participants = new ArrayList<CommunicationIdentifier>();

            DeviceManager deviceManager = callClient.getDeviceManager(context).get();
            List<VideoDeviceInfo> cameras = deviceManager.getCameras();

            StartCallOptions options = new StartCallOptions();
            LocalVideoStream stream = getOrCreateLocalVideoStream();
            if (stream != null) {
                LocalVideoStream[] videoStreams = { stream };
                VideoOptions videoOptions = new VideoOptions(videoStreams);
                options.setVideoOptions(videoOptions);
                showPreview(stream);
            }

            participants.add(new CommunicationUserIdentifier(callId));

            call = callAgent.startCall(
                    context,
                    participants,
                    options);

            //Subscribe to events on updates of call state and remote participants
            // TODO:
//            remoteParticipantUpdatedListener = this::handleRemoteParticipantsUpdate;
//            onStateChangedListener = this::handleCallOnStateChanged;
//            call.addOnRemoteParticipantsUpdatedListener(remoteParticipantUpdatedListener);
//            call.addOnStateChangedListener(onStateChangedListener);
        } catch (Exception ex) {

        }
    }

    private VideoDeviceInfo getNextAvailableCamera(DeviceManager deviceManager, VideoDeviceInfo camera) {
        List<VideoDeviceInfo> cameras = deviceManager.getCameras();
        int currentIndex = 0;
        if (camera == null) {
            return cameras.isEmpty() ? null : cameras.get(0);
        }

        for (int i = 0; i < cameras.size(); i++) {
            if (camera.getId().equals(cameras.get(i).getId())) {
                currentIndex = i;
                break;
            }
        }
        int newIndex = (currentIndex + 1) % cameras.size();
        return cameras.get(newIndex);
    }

    private void showPreview(LocalVideoStream stream) {
        viewManager.setPreviewVideoView(context, stream);
    }

    @Nullable
    LocalVideoStream _localVideoStream;

    private @Nullable LocalVideoStream getOrCreateLocalVideoStream() {
        if (_localVideoStream != null) {
            return _localVideoStream;
        }

        try {
            DeviceManager deviceManager = callClient.getDeviceManager(context).get();
            List<VideoDeviceInfo> cameras = deviceManager.getCameras();
            if (!cameras.isEmpty()) {
                VideoDeviceInfo currentCamera = getNextAvailableCamera(deviceManager, null);
                _localVideoStream = new LocalVideoStream(currentCamera, context);
                return _localVideoStream;
            }
            return null;
        } catch (Exception ex) {
            Log.d("tag", "Unable to get local video stream");
            return null;
        }
    }

    public void showLocalVideoPreview(Boolean show) {
        if (!show) {
            // TODO: hide
            return;
        }

        try {
            LocalVideoStream stream = getOrCreateLocalVideoStream();
            showPreview(stream);
        }
        catch (Exception ex) {

        }
    }
}
