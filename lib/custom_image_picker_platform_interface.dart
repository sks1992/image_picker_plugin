import 'dart:typed_data';

import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'custom_image_picker_method_channel.dart';

abstract class CustomImagePickerPlatform extends PlatformInterface {
  /// Constructs a CustomImagePickerPlatform.
  CustomImagePickerPlatform() : super(token: _token);

  static final Object _token = Object();

  static CustomImagePickerPlatform _instance = MethodChannelCustomImagePicker();

  /// The default instance of [CustomImagePickerPlatform] to use.
  ///
  /// Defaults to [MethodChannelCustomImagePicker].
  static CustomImagePickerPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [CustomImagePickerPlatform] when
  /// they register themselves.
  static set instance(CustomImagePickerPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<Uint8List?> getImage(String sourceCode) {
    throw UnimplementedError('capturePhoto() has not been implemented.');
  }
}
