import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:azure_communication_services_rtc/acsflutter.dart';
import 'package:azure_communication_services_rtc/views/local_video_preview_view.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';
  final _acsflutterPlugin = Acsflutter();
  final _textController = TextEditingController(text: "8:echo123");
  final _oneToOneVideoCalleeIdController = TextEditingController();
  final _userTokenController = TextEditingController();

  @override
  void initState() {
    super.initState();
    initPlatformState();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    String platformVersion;
    // Platform messages may fail, so we use a try/catch PlatformException.
    // We also handle the message potentially returning null.
    try {
      platformVersion = await _acsflutterPlugin.getPlatformVersion() ?? 'Unknown platform version';
    } on PlatformException {
      platformVersion = 'Failed to get platform version.';
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    setState(() {
      _platformVersion = platformVersion;
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
          appBar: AppBar(
            title: const Text('Plugin example app'),
          ),
          body: SingleChildScrollView(
            child: Padding(
              padding: const EdgeInsets.all(16.0),
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Text('Running on: $_platformVersion\n'),
                  Row(
                    children: [
                      TextButton(
                        onPressed: () async {
                          await _acsflutterPlugin.getAllPermissions();
                          // userID: 8:acs:75e47c5c-5aa3-4314-ad01-a3674c6da2c4_0000001b-2cf5-d2c3-e3c7-593a0d00910b
                          await _acsflutterPlugin.initialize(
                              "eyJhbGciOiJSUzI1NiIsImtpZCI6IjVFODQ4MjE0Qzc3MDczQUU1QzJCREU1Q0NENTQ0ODlEREYyQzRDODQiLCJ4NXQiOiJYb1NDRk1kd2M2NWNLOTVjelZSSW5kOHNUSVEiLCJ0eXAiOiJKV1QifQ.eyJza3lwZWlkIjoiYWNzOjc1ZTQ3YzVjLTVhYTMtNDMxNC1hZDAxLWEzNjc0YzZkYTJjNF8wMDAwMDAxYi0yY2Y1LWQyYzMtZTNjNy01OTNhMGQwMDkxMGIiLCJzY3AiOjE3OTIsImNzaSI6IjE2OTQ1ODQwMjQiLCJleHAiOjE2OTQ2NzA0MjQsInJnbiI6ImFtZXIiLCJhY3NTY29wZSI6InZvaXAiLCJyZXNvdXJjZUlkIjoiNzVlNDdjNWMtNWFhMy00MzE0LWFkMDEtYTM2NzRjNmRhMmM0IiwicmVzb3VyY2VMb2NhdGlvbiI6InVuaXRlZHN0YXRlcyIsImlhdCI6MTY5NDU4NDAyNH0.OahjeZ8Snwd2XTjC-MgX_Rw-drYYHToq823OvF3fV9b17GKZv4IrsVKUSkyO4a9ZPNytplC8GCEMULJkD7rkHpenZmzBkD_DDhrF3Rs-NTSnXteFxQUFGShQ76U7kZuLSgdnzUDnzG2T_tZ0Aa_wvU5230t4wk1IiYIfF3gr7SUC447JQZEIiT41zxX51bZTfVO6I9bikRvmC2y6ZVQHsjAgRMt9SCTuK5nDDIlntpK5irvT7294k9x1CXRHe3C7qbkULJSegRHbNdbaoi9GeogEhD3rtpm1lZPRgJsa5GLY8HcQ_t9XrsY23wylvdB5vefU30F2WJ19EKBLVx7wcg");
                          _oneToOneVideoCalleeIdController.text = "8:acs:75e47c5c-5aa3-4314-ad01-a3674c6da2c4_0000001b-2cd7-e6aa-ec8d-08482200b241";
                        },
                        child: Text("Initialize User A"),
                      ),
                      TextButton(
                        onPressed: () async {
                          await _acsflutterPlugin.getAllPermissions();
                          // userID: 8:acs:75e47c5c-5aa3-4314-ad01-a3674c6da2c4_0000001b-2cd7-e6aa-ec8d-08482200b241
                          await _acsflutterPlugin.initialize(
                              "eyJhbGciOiJSUzI1NiIsImtpZCI6IjVFODQ4MjE0Qzc3MDczQUU1QzJCREU1Q0NENTQ0ODlEREYyQzRDODQiLCJ4NXQiOiJYb1NDRk1kd2M2NWNLOTVjelZSSW5kOHNUSVEiLCJ0eXAiOiJKV1QifQ.eyJza3lwZWlkIjoiYWNzOjc1ZTQ3YzVjLTVhYTMtNDMxNC1hZDAxLWEzNjc0YzZkYTJjNF8wMDAwMDAxYi0yY2Q3LWU2YWEtZWM4ZC0wODQ4MjIwMGIyNDEiLCJzY3AiOjE3OTIsImNzaSI6IjE2OTQ1ODIwNjMiLCJleHAiOjE2OTQ2Njg0NjMsInJnbiI6ImFtZXIiLCJhY3NTY29wZSI6InZvaXAiLCJyZXNvdXJjZUlkIjoiNzVlNDdjNWMtNWFhMy00MzE0LWFkMDEtYTM2NzRjNmRhMmM0IiwicmVzb3VyY2VMb2NhdGlvbiI6InVuaXRlZHN0YXRlcyIsImlhdCI6MTY5NDU4MjA2M30.e3UWW4ORsLxWBw5fk7n2a92J9r2Haw-k55GZNkKLeIdASrbmW5_do1IL9pCu3M9wgP__8DreV41s4AnbLd9BJR-D6lN--eR7LuaaZMQnyDcsHK89BsCb7fAwsPTvJbrQzE04PeS-daMkwkuD68PCrd8_FxPC-Z5wGicmL3ZSsNZKpPcpG4x8sTwQcRYO5t5lrkSqDQDhdjByeUGNupHOi-Xrp414P7tem5TsVRNmyzsD5BefxICFibJqb3nH-e9dFdtHtSt9rXJYH1XnUFx2HvIPsjYojZ4HWkmbekm1zdyshdLm3ozwS68RQvLrgLq5xw3qr00zhcVFcYSupjsOCw");
                          _oneToOneVideoCalleeIdController.text = "8:acs:75e47c5c-5aa3-4314-ad01-a3674c6da2c4_0000001b-2cf5-d2c3-e3c7-593a0d00910b";
                        },
                        child: Text("Initialize User B"),
                      ),
                    ],
                  ),
                  Card(
                    child: Column(
                      children: [
                        Text("Voice Call"),
                        TextFormField(
                          controller: _textController,
                          decoration: const InputDecoration(
                            border: UnderlineInputBorder(),
                            labelText: 'Callee ID',
                          ),
                        ),
                        TextButton(
                          onPressed: () async {
                            await _acsflutterPlugin.startCall(_textController.text);
                          },
                          child: const Text("Call"),
                        ),
                        TextButton(
                          onPressed: () async {
                            await _acsflutterPlugin.stopCall();
                          },
                          child: const Text("Stop"),
                        ),
                      ],
                    ),
                  ),
                  Card(
                    child: Column(
                      children: [
                        Text("Video Call"),
                        TextFormField(
                          controller: _oneToOneVideoCalleeIdController,
                          decoration: const InputDecoration(
                            border: UnderlineInputBorder(),
                            labelText: 'Callee ID',
                          ),
                        ),
                        Row(
                          children: [
                            TextButton(
                              onPressed: () async {
                                await _acsflutterPlugin.startOneToOneVideoCall(_oneToOneVideoCalleeIdController.text);
                              },
                              child: Text("Call"),
                            ),
                            TextButton(
                              onPressed: () async {
                                await _acsflutterPlugin.turnOnLocalVideo(true);
                              },
                              child: Text("Show Video"),
                            ),
                            TextButton(
                              onPressed: () async {
                                await _acsflutterPlugin.turnOnLocalVideo(false);
                              },
                              child: Text("Hide Video"),
                            ),
                            TextButton(
                              onPressed: () {},
                              child: Text("Hang Up"),
                            ),
                          ],
                        ),
                        const SizedBox(
                          height: 150,
                          child: LocalVideoPreviewView(viewKey: "remoteVideoView"),
                        ),
                        const SizedBox(
                          height: 150,
                          child: LocalVideoPreviewView(viewKey: "localVideoView"),
                        ),
                      ],
                    ),
                  )
                ],
              ),
            ),
          )),
    );
  }
}
