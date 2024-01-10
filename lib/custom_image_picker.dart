import 'dart:typed_data';

import 'package:flutter/material.dart';

import 'custom_image_picker_platform_interface.dart';

class CustomImagePicker {

  Future<Uint8List?> getImage(
    BuildContext context,
  ) async {
    Uint8List? selectedImage;
    await showDialog(
        context: context,
        builder: (BuildContext context) {
          return AlertDialog(
            title: const Text("Choose"),
            content: Row(
              mainAxisAlignment: MainAxisAlignment.spaceEvenly,
              children: [
                SizedBox(
                  height: MediaQuery.of(context).size.height * 0.1,
                  child: InkWell(
                    onTap: () async {
                      selectedImage = await CustomImagePickerPlatform.instance
                          .getImage("C");
                      if (context.mounted) {
                        Navigator.pop(context);
                      }
                    },
                    child: Column(
                      children: const [
                        Padding(
                          padding: EdgeInsets.all(4.0),
                          child: Icon(
                            Icons.photo_camera,
                            color: Colors.grey,
                            size: 35,
                          ),
                        ),
                        Text(
                          "Camera",
                          style: TextStyle(color: Colors.grey),
                        )
                      ],
                    ),
                  ),
                ),
                SizedBox(
                  height: MediaQuery.of(context).size.height * 0.1,
                  child: InkWell(
                    onTap: () async {
                      selectedImage = await CustomImagePickerPlatform.instance
                          .getImage("G");
                      if (context.mounted) {
                        Navigator.pop(context);
                      }
                    },
                    child: Column(
                      children: const [
                        Padding(
                          padding: EdgeInsets.all(4.0),
                          child: Icon(
                            Icons.image,
                            color: Colors.grey,
                            size: 35,
                          ),
                        ),
                        Text(
                          "Gallery",
                          style: TextStyle(color: Colors.grey),
                        )
                      ],
                    ),
                  ),
                ),
              ],
            ),
            actions: [
              TextButton(
                child: const Text(
                  "CLOSE",
                  style: TextStyle(color: Colors.black),
                ),
                onPressed: () {
                  Navigator.pop(context);
                },
              )
            ],
          );
        });
    return selectedImage;
  }
}
