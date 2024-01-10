import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'custom_image_picker_platform_interface.dart';

/// An implementation of [CustomImagePickerPlatform] that uses method channels.
class MethodChannelCustomImagePicker extends CustomImagePickerPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('custom_image_picker');

  @override
  Future<Uint8List?> getImage(String sourceCode) async {
    final imagePath = await methodChannel
        .invokeMethod<Uint8List>('getImage', {"code": sourceCode});
    return imagePath;
  }
}
