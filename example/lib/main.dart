import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:acsflutter/acsflutter.dart';

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
          body: Padding(
            padding: const EdgeInsets.all(16.0),
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                Text('Running on: $_platformVersion\n'),
                TextFormField(
                  controller: _textController,
                  decoration: const InputDecoration(
                    border: UnderlineInputBorder(),
                    labelText: 'Callee ID',
                  ),
                ),
                TextButton(
                  onPressed: () async {
                    await _acsflutterPlugin.getAllPermissions();

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
          )),
    );
  }
}
