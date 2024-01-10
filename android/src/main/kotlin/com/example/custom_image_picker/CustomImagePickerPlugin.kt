package com.example.custom_image_picker

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.NonNull
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.PluginRegistry.ActivityResultListener
import io.flutter.plugin.common.PluginRegistry.RequestPermissionsResultListener
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


/** CustomImagePickerPlugin */
class CustomImagePickerPlugin : FlutterPlugin, MethodCallHandler, ActivityAware,
    ActivityResultListener,
    RequestPermissionsResultListener {
    private lateinit var channel: MethodChannel
    private lateinit var result: Result
    private lateinit var context: Context

    private var activity: Activity? = null
    private val openCameraPermission = 9991
    private val openCameraCode = 9992
    private val openFileManagerCode = 9993


    override fun onAttachedToEngine(
        @NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding
    ) {
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "custom_image_picker")
        channel.setMethodCallHandler(this)
        context = flutterPluginBinding.applicationContext
    }

    override fun onMethodCall(
        @NonNull call: MethodCall, @NonNull result: Result
    ) {
        this.result = result
        val code = call.argument<String>("code")
        if (call.method == "getImage") {
            when (code) {
                "C" -> checkPermissionAndOpenCamera()
                "G" -> getImageFromFileManager()
            }
        } else {
            result.notImplemented()
        }
    }

    private fun checkPermissionAndOpenCamera() {
        if (!checkPermission()) {
            requestPermission()
        } else {
            getImageFromCamera()
        }
    }

    private fun checkPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            activity!!, arrayOf(Manifest.permission.CAMERA),
            openCameraPermission
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ): Boolean {
        if (requestCode == openCameraPermission) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getImageFromCamera()
                return true
            }
        }
        requestPermission()
        return false
    }

    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    private fun getImageFromCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        activity?.startActivityForResult(cameraIntent, openCameraCode)
    }

    private fun getImageFromFileManager() {
        val packageManagerIntent = Intent()
        packageManagerIntent.action = Intent.ACTION_GET_CONTENT
        packageManagerIntent.type = "image/*";
        activity?.startActivityForResult(
            Intent.createChooser(
                packageManagerIntent,
                "Select Image"
            ), openFileManagerCode
        )
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        activity = binding.activity
        binding.addRequestPermissionsResultListener(this)
        binding.addActivityResultListener(this)

    }

    override fun onDetachedFromActivityForConfigChanges() {
        activity = null
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        activity = binding.activity
        binding.addRequestPermissionsResultListener(this)
        binding.addActivityResultListener(this)

    }

    override fun onDetachedFromActivity() {
        activity = null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == openCameraCode) {
                if (data != null) {
                    val bitmap: Bitmap = data.extras?.get("data") as Bitmap
                    val imagePath = saveBitmapToStorage(bitmap)
                    if (imagePath == null) {
                        result.error("", "No Image Selected.", "")
                        return true
                    }
                    File(imagePath).inputStream().use {
                        val imageBytes = it.readBytes()
                        result.success(imageBytes)
                    }
                    return true
                } else {
                    result.error("URI_ERROR", "Failed to get the photo URI", null)
                    return true
                }
            }
            if (requestCode == openFileManagerCode) {
                if (data != null) {
                    val uri: Uri? = data.data
                    if (uri == null) {
                        result.error("", "No Image Selected.", "")
                        return true
                    }
                    val bitmap =
                        MediaStore.Images.Media.getBitmap(
                            activity?.contentResolver, uri
                        )
                    val stream = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                    val imageBytes = stream.toByteArray()
                    result.success(imageBytes)

                    return true
                } else {
                    result.error("URI_ERROR", "Failed to get the photo URI", null)
                    return true
                }
            }
        }
        return false
    }

    private fun saveBitmapToStorage(bitmap: Bitmap): String? {
        val storageDir = activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        if (!storageDir!!.exists()) storageDir.mkdirs()
        Log.d("STORAGE_DIRECTORY", "storageDir = $storageDir")
        return try {
            val imageFileName = "IMG_${System.currentTimeMillis()}.jpg"
            val imageFile = File(storageDir, imageFileName)
            FileOutputStream(imageFile).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
            }
            imageFile.absolutePath
        } catch (ex: IOException) {
            null
        }
    }
}