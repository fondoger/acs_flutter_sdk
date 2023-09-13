package com.microsoft.acsflutter.acsflutter;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import android.content.Context;
import android.media.AudioManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.GridLayout;
import android.widget.Toast;

import com.azure.android.communication.calling.AcceptCallOptions;
import com.azure.android.communication.calling.Call;
import com.azure.android.communication.calling.CallAgent;
import com.azure.android.communication.calling.CallAgentBase;
import com.azure.android.communication.calling.CallClient;
import com.azure.android.communication.calling.CallState;
import com.azure.android.communication.calling.CreateViewOptions;
import com.azure.android.communication.calling.DeviceManager;
import com.azure.android.communication.calling.HangUpOptions;
import com.azure.android.communication.calling.IncomingCall;
import com.azure.android.communication.calling.LocalVideoStream;
import com.azure.android.communication.calling.ParticipantsUpdatedEvent;
import com.azure.android.communication.calling.ParticipantsUpdatedListener;
import com.azure.android.communication.calling.PropertyChangedEvent;
import com.azure.android.communication.calling.PropertyChangedListener;
import com.azure.android.communication.calling.RemoteParticipant;
import com.azure.android.communication.calling.RemoteVideoStream;
import com.azure.android.communication.calling.RemoteVideoStreamsEvent;
import com.azure.android.communication.calling.RendererListener;
import com.azure.android.communication.calling.ScalingMode;
import com.azure.android.communication.calling.StartCallOptions;
import com.azure.android.communication.calling.VideoDeviceInfo;
import com.azure.android.communication.calling.VideoOptions;
import com.azure.android.communication.calling.VideoStreamRenderer;
import com.azure.android.communication.calling.VideoStreamRendererView;
import com.azure.android.communication.common.CommunicationIdentifier;
import com.azure.android.communication.common.CommunicationTokenCredential;
import com.azure.android.communication.common.CommunicationUserIdentifier;
import com.azure.android.communication.common.MicrosoftTeamsUserIdentifier;
import com.azure.android.communication.common.PhoneNumberIdentifier;
import com.azure.android.communication.common.UnknownIdentifier;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

import io.flutter.Log;

public class Implementations {
    private final CallClient callClient = new CallClient();
    private CallAgent callAgent;
    @NonNull
    private final ViewManager viewManager;
    @NonNull
    private final Context context;
    @Nullable
    private Activity activity;
    @Nullable
    private Call call;
    @Nullable
    private LocalVideoStream _localVideoStream;
    @Nullable
    private VideoDeviceInfo _currentCamera;

    final HashSet<String> joinedParticipants = new HashSet<>();
    final Map<Integer, StreamData> streamDataMap = new HashMap<>();

    private ParticipantsUpdatedListener remoteParticipantUpdatedListener;
    private PropertyChangedListener onStateChangedListener;
    private IncomingCall incomingCall;

    public Implementations(@NonNull Context context, @NonNull ViewManager viewManager) {
        this.context = context;
        this.viewManager = viewManager;
    }
    public void setActivity(@Nullable Activity activity) {
        this.activity = activity;
    }

    public void initialize(String userToken) {
        try {
            callAgent = createCallAgent(userToken);
            activity.setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);

            // TODO: expose handleIncomingCall as API
            handleIncomingCall();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private CallAgent createCallAgent(String userToken) throws Exception {
        try {
            CommunicationTokenCredential credential = new CommunicationTokenCredential(userToken);
            return callClient.createCallAgent(context, credential).get();
        } catch (Exception ex) {
            ex.printStackTrace();
            Toast.makeText(context, "Failed to create start call.", Toast.LENGTH_SHORT).show();
            throw new Exception("Failed to create agent: " + ex.getMessage());
        }
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


    public void startCall(String calleeId) {
        try {
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
            StartCallOptions options = new StartCallOptions();
            LocalVideoStream stream = getOrCreateLocalVideoStream();
            if (stream != null) {
                LocalVideoStream[] videoStreams = { stream };
                VideoOptions videoOptions = new VideoOptions(videoStreams);
                options.setVideoOptions(videoOptions);
                showPreview(stream);
            }
            Log.e("tag", "Start call with user id: " + callId);

            call = callAgent.startCall(
                    context,
                    Collections.singletonList(new CommunicationUserIdentifier(callId)),
                    options);

            remoteParticipantUpdatedListener = this::handleRemoteParticipantsUpdate;
            onStateChangedListener = this::handleCallOnStateChanged;
            call.addOnRemoteParticipantsUpdatedListener(remoteParticipantUpdatedListener);
            call.addOnStateChangedListener(onStateChangedListener);
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.e("tag", "failed to start call, " + ex.getMessage());
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

    private @Nullable LocalVideoStream getOrCreateLocalVideoStream() {
        if (_localVideoStream != null) {
            return _localVideoStream;
        }

        try {
            DeviceManager deviceManager = callClient.getDeviceManager(context).get();
            List<VideoDeviceInfo> cameras = deviceManager.getCameras();
            if (!cameras.isEmpty()) {
                _currentCamera = getNextAvailableCamera(deviceManager, null);
                _localVideoStream = new LocalVideoStream(_currentCamera, context);
                return _localVideoStream;
            }
            return null;
        } catch (Exception ex) {
            Log.d("tag", "Unable to get local video stream");
            return null;
        }
    }

    public void turnOnLocalVideo(Boolean show) {
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

    public void switchLocalVideoSource() {
        if (_localVideoStream != null) {
            try {
                DeviceManager deviceManager = callClient.getDeviceManager(context).get();
                _currentCamera = getNextAvailableCamera(deviceManager, _currentCamera);
                _localVideoStream.switchSource(_currentCamera).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }


    public void handleRemoteParticipantsUpdate(ParticipantsUpdatedEvent args) {
        handleAddedParticipants(args.getAddedParticipants());
        handleRemovedParticipants(args.getRemovedParticipants());
    }

    private void handleAddedParticipants(List<RemoteParticipant> participants) {
        for (RemoteParticipant participant : participants) {
            Log.d("tag", "Add participant: " + participant.getDisplayName());
            if(!joinedParticipants.contains(getId(participant))) {
                joinedParticipants.add(getId(participant));

                for (RemoteVideoStream stream : participant.getVideoStreams()) {
                    StreamData data = new StreamData(stream, null, null);
                    streamDataMap.put(stream.getId(), data);
                    startRenderingVideo(data);
                }
                participant.addOnVideoStreamsUpdatedListener(videoStreamsEventArgs -> videoStreamsUpdated(videoStreamsEventArgs));
            }

        }
    }

    private void videoStreamsUpdated(RemoteVideoStreamsEvent videoStreamsEventArgs) {
        Log.d("tag", "Video stream updated!");
        for(RemoteVideoStream stream : videoStreamsEventArgs.getAddedRemoteVideoStreams()) {
            StreamData data = new StreamData(stream, null, null);
            Log.d("tag", "videoStream added: " + stream.getId());
            streamDataMap.put(stream.getId(), data);
            startRenderingVideo(data);
        }

        for(RemoteVideoStream stream : videoStreamsEventArgs.getRemovedRemoteVideoStreams()) {
            stopRenderingVideo(stream);
        }
    }


    public String getId(final RemoteParticipant remoteParticipant) {
        final CommunicationIdentifier identifier = remoteParticipant.getIdentifier();
        if (identifier instanceof PhoneNumberIdentifier) {
            return ((PhoneNumberIdentifier) identifier).getPhoneNumber();
        } else if (identifier instanceof MicrosoftTeamsUserIdentifier) {
            return ((MicrosoftTeamsUserIdentifier) identifier).getUserId();
        } else if (identifier instanceof CommunicationUserIdentifier) {
            return ((CommunicationUserIdentifier) identifier).getId();
        } else {
            return ((UnknownIdentifier) identifier).getId();
        }
    }


    private void handleRemovedParticipants(List<RemoteParticipant> participants) {
        // TODO:
    }


    static class StreamData {
        RemoteVideoStream stream;
        VideoStreamRenderer renderer;
        VideoStreamRendererView rendererView;
        StreamData(RemoteVideoStream stream, VideoStreamRenderer renderer, VideoStreamRendererView rendererView) {
            this.stream = stream;
            this.renderer = renderer;
            this.rendererView = rendererView;
        }
    }

    void startRenderingVideo(StreamData data){
        if (data.renderer != null) {
            return;
        }
        // TODO: refactor this

        data.renderer = new VideoStreamRenderer(data.stream, context);
        data.renderer.addRendererListener(new RendererListener() {
            @Override
            public void onFirstFrameRendered() {
                String text = data.renderer.getSize().toString();
                Log.i("MainActivity", "Video rendering at: " + text);
            }

            @Override
            public void onRendererFailedToStart() {
                String text = "Video failed to render";
                Log.i("MainActivity", text);
            }
        });
        data.rendererView = data.renderer.createView(new CreateViewOptions(ScalingMode.FIT));
        data.rendererView.setTag(data.stream.getId());

        activity.runOnUiThread(() -> {
            viewManager.addRemoteVideoView(activity, data.rendererView);
        });
    }

    void stopRenderingVideo(RemoteVideoStream stream) {
        StreamData data = streamDataMap.get(stream.getId());
        if (data == null || data.renderer == null) {
            return;
        }

        viewManager.removeRemoteVideoView(activity, data.stream.getId());

        data.rendererView = null;
        // Dispose renderer
        data.renderer.dispose();
        data.renderer = null;
    }

    private void handleCallOnStateChanged(PropertyChangedEvent args) {
        if (call.getState() == CallState.CONNECTED) {
            Log.d("tag", "CallOnStateChanged: connected");
            // TODO:
//            runOnUiThread(() -> Toast.makeText(this, "Call is CONNECTED", Toast.LENGTH_SHORT).show());
            handleCallState();
        }
        if (call.getState() == CallState.DISCONNECTED) {
            // TODO:
//            runOnUiThread(() -> Toast.makeText(this, "Call is DISCONNECTED", Toast.LENGTH_SHORT).show());
//            if (previewRenderer != null) {
//                previewRenderer.dispose();
//            }
//            switchSourceButton.setVisibility(View.INVISIBLE);
        }
    }

    private void handleCallState() {
        handleAddedParticipants(call.getRemoteParticipants());
    }

    public void endOneToOneVideoCall() {
        try {
            call.hangUp().get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        // TODO: dispose view renderers
    }


    public void handleIncomingCall() {
        callAgent.addOnIncomingCallListener((incomingCall) -> {
            Log.d("tag", "Received incoming call !!!!!!!!!");
            this.incomingCall = incomingCall;
            Executors.newCachedThreadPool().submit(this::answerIncomingCall);
        });
    }

    private void answerIncomingCall() {
        Log.d("tag", "Answering incoming call !!!!!!!!!");
        if (incomingCall == null){
            Log.d("tag", "incoming call is null");
            return;
        }
        AcceptCallOptions acceptCallOptions = new AcceptCallOptions();
        LocalVideoStream stream = getOrCreateLocalVideoStream();
        acceptCallOptions.setVideoOptions(new VideoOptions(new LocalVideoStream[]{stream}));
        Log.d("tag", "Show local preview from incoming call");
        showPreview(stream);
        try {
            call = incomingCall.accept(context, acceptCallOptions).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        //Subscribe to events on updates of call state and remote participants
        remoteParticipantUpdatedListener = this::handleRemoteParticipantsUpdate;
        onStateChangedListener = this::handleCallOnStateChanged;
        call.addOnRemoteParticipantsUpdatedListener(remoteParticipantUpdatedListener);
        call.addOnStateChangedListener(onStateChangedListener);
    }
}
