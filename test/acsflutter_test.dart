import 'package:flutter_test/flutter_test.dart';
import 'package:acsflutter/acsflutter.dart';
import 'package:acsflutter/acsflutter_platform_interface.dart';
import 'package:acsflutter/acsflutter_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockAcsflutterPlatform with MockPlatformInterfaceMixin implements AcsflutterPlatform {
  @override
  Future<String?> initialize(String userToken) => Future.value('42');

  @override
  Future<String?> getPlatformVersion() => Future.value('42');

  @override
  Future<String?> getAllPermissions() => Future.value('42');

  @override
  Future<String?> createAgent() => Future.value('42');

  @override
  Future<String?> startCall(String calleeId) => Future.value('42');

  @override
  Future<String?> stopCall() => Future.value('42');

  @override
  Future<String?> startOneToOneVideoCall(String calleeId) => Future.value('42');

  @override
  Future<String?> turnOnLocalVideo(bool show) => Future.value('42');
}

void main() {
  final AcsflutterPlatform initialPlatform = AcsflutterPlatform.instance;

  test('$MethodChannelAcsflutter is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelAcsflutter>());
  });

  test('getPlatformVersion', () async {
    Acsflutter acsflutterPlugin = Acsflutter();
    MockAcsflutterPlatform fakePlatform = MockAcsflutterPlatform();
    AcsflutterPlatform.instance = fakePlatform;

    expect(await acsflutterPlugin.getPlatformVersion(), '42');
  });
}
