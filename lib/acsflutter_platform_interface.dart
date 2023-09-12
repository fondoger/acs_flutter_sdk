import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'acsflutter_method_channel.dart';

abstract class AcsflutterPlatform extends PlatformInterface {
  /// Constructs a AcsflutterPlatform.
  AcsflutterPlatform() : super(token: _token);

  static final Object _token = Object();

  static AcsflutterPlatform _instance = MethodChannelAcsflutter();

  /// The default instance of [AcsflutterPlatform] to use.
  ///
  /// Defaults to [MethodChannelAcsflutter].
  static AcsflutterPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [AcsflutterPlatform] when
  /// they register themselves.
  static set instance(AcsflutterPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }

  Future<String?> getAllPermissions() {
    throw UnimplementedError('getAllPermissions() has not been implemented.');
  }

  Future<String?> createAgent() {
    throw UnimplementedError('createAgent() has not been implemented.');
  }

  Future<String?> startCall() {
    throw UnimplementedError('startCall() has not been implemented.');
  }
}
