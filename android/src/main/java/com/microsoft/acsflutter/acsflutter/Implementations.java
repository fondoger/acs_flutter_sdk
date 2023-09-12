package com.microsoft.acsflutter.acsflutter;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import android.content.Context;

import java.util.ArrayList;

public class Implementations {

    @NonNull
    private final Context context;
    @Nullable
    private Activity activity;


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
}
