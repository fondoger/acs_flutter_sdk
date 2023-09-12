package com.microsoft.acsflutter.acsflutter;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.flutter.Log;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

/** AcsflutterPlugin */
public class AcsflutterPlugin implements FlutterPlugin, MethodCallHandler, ActivityAware {

  private Implementations implementations;
  @Nullable
  private ActivityPluginBinding pluginBinding;


  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private MethodChannel channel;

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    this.implementations = new Implementations(flutterPluginBinding.getApplicationContext());


    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "acsflutter");
    channel.setMethodCallHandler(this);
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    if (call.method.equals("getPlatformVersion")) {
      result.success("Android " + android.os.Build.VERSION.RELEASE);
    } else if (call.method.equals("getAllPermissions")) {
      // print log
      Log.d("tag", "getAllPermissions() called!");

      implementations.getAllPermissions();
      result.success("");
    } else if (call.method.equals("startCall")) {
      Log.d("tag", "startCall() called");
      String calleeId = call.argument("calleeId");
      implementations.startCall(calleeId);
      result.success("");
    } else if (call.method.equals("stopCall")) {
      Log.d("tag", "stopCall() called");
      implementations.stopCall();
      result.success("");
    } else if (call.method.equals("createAgent")) {
      result.success("");
    } else {
      result.notImplemented();
    }
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }

  @Override
  public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
    startListeningToActivity(binding.getActivity());
  }

  @Override
  public void onDetachedFromActivityForConfigChanges() {
    onDetachedFromActivity();
  }

  @Override
  public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
    onAttachedToActivity(binding);
  }

  @Override
  public void onDetachedFromActivity() {
    stopListeningToActivity();
  }

  private void startListeningToActivity(Activity activity) {
    if (implementations != null) {
      implementations.setActivity(activity);
    }
  }

  private void stopListeningToActivity() {
    if (implementations != null) {
      implementations.setActivity(null);
    }
  }
}
