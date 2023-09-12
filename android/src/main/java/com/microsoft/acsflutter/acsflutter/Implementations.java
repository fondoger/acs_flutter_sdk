package com.microsoft.acsflutter.acsflutter;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import android.content.Context;
import android.media.AudioManager;
import android.widget.Toast;

import com.azure.android.communication.calling.Call;
import com.azure.android.communication.calling.CallAgent;
import com.azure.android.communication.calling.CallClient;
import com.azure.android.communication.calling.HangUpOptions;
import com.azure.android.communication.calling.StartCallOptions;
import com.azure.android.communication.common.CommunicationTokenCredential;
import com.azure.android.communication.common.CommunicationUserIdentifier;

import java.util.ArrayList;

public class Implementations {

    @NonNull
    private final Context context;
    @Nullable
    private Activity activity;
    @Nullable
    private Call call;


    public Implementations(@NonNull Context context) {
        this.context = context;
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

    public void startCall(String calleeId) {
        // Create Agent
        String userToken = "eyJhbGciOiJSUzI1NiIsImtpZCI6IjVFODQ4MjE0Qzc3MDczQUU1QzJCREU1Q0NENTQ0ODlEREYyQzRDODQiLCJ4NXQiOiJYb1NDRk1kd2M2NWNLOTVjelZSSW5kOHNUSVEiLCJ0eXAiOiJKV1QifQ.eyJza3lwZWlkIjoiYWNzOjc1ZTQ3YzVjLTVhYTMtNDMxNC1hZDAxLWEzNjc0YzZkYTJjNF8wMDAwMDAxYi0yN2Q3LTZmNjEtMDJjMy01OTNhMGQwMDE0OTEiLCJzY3AiOjE3OTIsImNzaSI6IjE2OTQ0OTgxNDciLCJleHAiOjE2OTQ1ODQ1NDcsInJnbiI6ImFtZXIiLCJhY3NTY29wZSI6InZvaXAiLCJyZXNvdXJjZUlkIjoiNzVlNDdjNWMtNWFhMy00MzE0LWFkMDEtYTM2NzRjNmRhMmM0IiwicmVzb3VyY2VMb2NhdGlvbiI6InVuaXRlZHN0YXRlcyIsImlhdCI6MTY5NDQ5ODE0N30.aA2K0jVAZJIzdKc4yneVcs0xB1uRs1Uii5kqGUGtRXNn8X8ZsqnNwQzQJUWei9tKbxHZdPKQ3GClMLPEvlHCvcpMSEWm8c4i4B2BTqYg8Z06tnoLEvxjCW_yKKfwYWLobgD2Ym5pCHVkc_GZSAxttWWGCzOoZKFbNOQ0Ldzvlp_9amG1x7qWGCx7qo_KF1NzULyBVLr3XROefLYjPv_Rx08jypaPJDxeNXilUV_gEq0dl548QH5ztG8_aoqHt4gzJYybo4DDcEsC-UOZuqS7WBbm4YgCPT2x9nXYSvLvorscfKSKl5lteBOVj_WcVVQ4wyI60hB9WUGlh6T-7eBeKA";

        try {
            activity.setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);

            // create agent
            CommunicationTokenCredential credential = new CommunicationTokenCredential(userToken);
            CallAgent callAgent = new CallClient().createCallAgent(context, credential).get();

            // start call
            StartCallOptions options = new StartCallOptions();
            call = callAgent.startCall(context,
                    new CommunicationUserIdentifier[] {new CommunicationUserIdentifier(calleeId)},
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
}
