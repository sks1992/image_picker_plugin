import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:custom_image_picker/custom_image_picker_method_channel.dart';

void main() {
  MethodChannelCustomImagePicker platform = MethodChannelCustomImagePicker();
  const MethodChannel channel = MethodChannel('custom_image_picker');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });
}
