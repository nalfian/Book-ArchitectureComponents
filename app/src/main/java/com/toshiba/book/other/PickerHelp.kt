package com.toshiba.book.other

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.AssetFileDescriptor
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Parcelable
import android.provider.MediaStore
import android.support.media.ExifInterface
import android.util.Log
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.util.ArrayList

object PickerHelp {
    private val DEFAULT_IMAGE_QUALITY = 400
    private val TEMP_IMAGE_NAME = "tempImage"

    fun getPickImages(context: Context): Intent? {
        var intentList: MutableList<Intent> = ArrayList()
        val pickIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        val takePhoto = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        takePhoto.putExtra("return-data", true)
        takePhoto.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(gettempfile(context)))
        intentList = addIntentToList(context, intentList, pickIntent)
        intentList = addIntentToList(context, intentList, takePhoto)
        var chooserIntent: Intent? = null

        if (intentList.size > 0) {
            chooserIntent = Intent.createChooser(intentList.removeAt(intentList.size - 1), "Pick Images")
            chooserIntent!!.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentList.toTypedArray<Parcelable>())
        }

        return chooserIntent
    }

    private fun addIntentToList(context: Context, intentList: MutableList<Intent>, intent: Intent): MutableList<Intent> {
        val resolveInfo = context.packageManager.queryIntentActivities(intent, 0)
        for (r in resolveInfo) {
            val pacKageName = r.activityInfo.packageName
            val targetIntent = Intent(intent)
            targetIntent.setPackage(pacKageName)
            intentList.add(targetIntent)
            Log.d("2311", "Intent: " + intent.action + " package: " + pacKageName)
        }

        return intentList
    }

    fun getImageResult(context: Context, resCode: Int, imageReturnIntent: Intent?): Bitmap? {
        Log.d("2311", "getImageFromResult, resultCode: $resCode")
        var bm: Bitmap? = null
        val images = gettempfile(context)
        if (resCode == Activity.RESULT_OK) {
            val selectImages: Uri?
            val isCamera = imageReturnIntent == null || imageReturnIntent.data == null || imageReturnIntent.data!!.toString().contains(images.toString())
            if (isCamera) { // from camera
                selectImages = Uri.fromFile(images)
            } else {
                selectImages = imageReturnIntent!!.data
            }

            Log.d("2311", "selectedImage: " + selectImages!!)

            bm = getImageResize(context, selectImages)
            val rotation = getRotation(context, selectImages, isCamera)
            bm = rotate(bm, rotation)

        }

        return bm
    }

    private fun gettempfile(context: Context): File {
        val imageFile = File(context.externalCacheDir, TEMP_IMAGE_NAME)
        imageFile.parentFile.mkdirs()
        return imageFile
    }

    fun getImageResize(context: Context, selectImages: Uri): Bitmap {
        var bm: Bitmap
        val sampleSizes = intArrayOf(5, 3, 2, 1)
        var i = 0
        do {
            bm = decodeBitmap(context, selectImages, sampleSizes[i])
            Log.d("2311", "resizer: new bitmap width = " + bm.width)
            i++
        } while (bm.width < DEFAULT_IMAGE_QUALITY && i < sampleSizes.size)

        return bm
    }

    private fun decodeBitmap(context: Context, selectImages: Uri, sampleSize: Int): Bitmap {
        val options = BitmapFactory.Options()
        options.inSampleSize = sampleSize
        var fileDescriptor: AssetFileDescriptor? = null
        try {
            fileDescriptor = context.contentResolver.openAssetFileDescriptor(selectImages, "r")
        } catch (ex: FileNotFoundException) {
            ex.printStackTrace()
        }

        assert(fileDescriptor != null)
        val actualyUsableBitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor!!.fileDescriptor, null, options)
        Log.d("2311", options.inSampleSize.toString() + " sample method bitmap ... " +
                actualyUsableBitmap.width + " " + actualyUsableBitmap.height)

        return actualyUsableBitmap
    }

    private fun getRotation(context: Context, selectImages: Uri, isCamera: Boolean): Int {
        val rotation: Int
        if (isCamera) {
            rotation = getRotationFromCamera(context, selectImages)
        } else {
            rotation = getRotationFromGallery(context, selectImages)
        }

        return rotation
    }

    private fun getRotationFromGallery(context: Context, selectImages: Uri): Int {
        var result = 0
        val column = arrayOf(MediaStore.Images.Media.ORIENTATION)
        var cursor: Cursor? = null
        try {
            cursor = context.contentResolver.query(selectImages, column, null, null, null)
            if (cursor != null && cursor.moveToFirst()) {
                val orientationColumnIndex = cursor.getColumnIndex(column[0])
                result = cursor.getInt(orientationColumnIndex)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor?.close()
        }

        return result
    }

    private fun getRotationFromCamera(context: Context, selectImages: Uri): Int {
        var rotate = 0
        try {
            context.contentResolver.notifyChange(selectImages, null)
            val exif = ExifInterface(selectImages.path)
            val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_270 -> rotate = 270
                ExifInterface.ORIENTATION_ROTATE_180 -> rotate = 180
                ExifInterface.ORIENTATION_ROTATE_90 -> rotate = 90
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return rotate
    }

    private fun rotate(bm: Bitmap?, rotation: Int): Bitmap? {
        if (rotation != 0) {
            val matrix = Matrix()
            matrix.postRotate(rotation.toFloat())
            return Bitmap.createBitmap(bm!!, 0, 0, bm.width, bm.height, matrix, true)
        }
        return bm
    }
}