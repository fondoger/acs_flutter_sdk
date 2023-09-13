import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'acsflutter_platform_interface.dart';

/// An implementation of [AcsflutterPlatform] that uses method channels.
class MethodChannelAcsflutter extends AcsflutterPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('acsflutter');

  @override
  Future<String?> initialize(String userToken) async {
    final version = await methodChannel.invokeMethod<String>('initialize', {
      "userToken": userToken,
    });
    return version;
  }

  @override
  Future<String?> getPlatformVersion() async {
    final version = await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }

  @override
  Future<String?> getAllPermissions() async {
    final version = await methodChannel.invokeMethod<String>('getAllPermissions');
    return version;
  }

  @override
  Future<String?> createAgent() async {
    final version = await methodChannel.invokeMethod<String>('createAgent');
    return version;
  }

  @override
  Future<String?> startCall(String calleeId) async {
    final version = await methodChannel.invokeMethod<String>('startCall', {
      "calleeId": calleeId,
    });
    return version;
  }

  @override
  Future<String?> stopCall() async {
    final version = await methodChannel.invokeMethod<String>('stopCall');
    return version;
  }

  @override
  Future<String?> startOneToOneVideoCall(String calleeId) async {
    final version = await methodChannel.invokeMethod<String>('startOneToOneVideoCall', {
      "calleeId": calleeId,
    });
    return version;
  }

  @override
  Future<String?> turnOnLocalVideo(bool show) async {
    final version = await methodChannel.invokeMethod<String>('turnOnLocalVideo', {
      "show": show,
    });
    return version;
  }
}
