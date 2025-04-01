package com.github.cloudwebrtc.flutter_callkeep;

import android.app.Activity;
import android.content.Context;
import androidx.annotation.NonNull;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.wazo.callkeep.CallKeepModule;

/** FlutterCallkeepPlugin */
public class FlutterCallkeepPlugin implements FlutterPlugin, MethodCallHandler, ActivityAware {
  private MethodChannel channel;
  private CallKeepModule callKeep;

  private void setActivity(@NonNull Activity activity) {
    if (callKeep != null) {
      callKeep.setActivity(activity);
    }
  }

  private void startListening(final Context context, BinaryMessenger messenger) {
    channel = new MethodChannel(messenger, "FlutterCallKeep.Method");
    channel.setMethodCallHandler(this);
    callKeep = new CallKeepModule(context, messenger);
  }

  private void stopListening() {
    if (channel != null) {
      channel.setMethodCallHandler(null);
      channel = null;
    }
    if (callKeep != null) {
      callKeep.dispose();
      callKeep = null;
    }
  }

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    startListening(flutterPluginBinding.getApplicationContext(), flutterPluginBinding.getBinaryMessenger());
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    if (callKeep != null && !callKeep.handleMethodCall(call, result)) {
      result.notImplemented();
    }
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    stopListening();
  }

  @Override
  public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
    setActivity(binding.getActivity());
  }

  @Override
  public void onDetachedFromActivityForConfigChanges() {
    setActivity(null);
  }

  @Override
  public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
    setActivity(binding.getActivity());
  }

  @Override
  public void onDetachedFromActivity() {
    setActivity(null);
  }
}
