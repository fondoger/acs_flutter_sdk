import 'acsflutter_platform_interface.dart';

class Acsflutter {
  Future<String?> initialize(String userToken) {
    return AcsflutterPlatform.instance.initialize(userToken);
  }

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

  Future<String?> startOneToOneVideoCall(String calleeId) {
    return AcsflutterPlatform.instance.startOneToOneVideoCall(calleeId);
  }

  Future<String?> turnOnLocalVideo(bool show) {
    return AcsflutterPlatform.instance.turnOnLocalVideo(show);
  }
}
