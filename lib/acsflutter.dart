import 'acsflutter_platform_interface.dart';

class Acsflutter {
  Future<String?> getPlatformVersion() {
    return AcsflutterPlatform.instance.getPlatformVersion();
  }

  /// Request each permission if it is not already granted.
  Future<String?> getAllPermissions() {
    return AcsflutterPlatform.instance.getAllPermissions();
  }

  Future<String?> createAgent() {
    return AcsflutterPlatform.instance.createAgent();
  }

  Future<String?> startCall(String calleeId) {
    return AcsflutterPlatform.instance.startCall(calleeId);
  }

  Future<String?> stopCall() {
    return AcsflutterPlatform.instance.stopCall();
  }
}
