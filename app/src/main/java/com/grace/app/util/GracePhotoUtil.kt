package com.grace.app.util

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Environment
import android.util.Base64
import android.webkit.MimeTypeMap
import com.grace.app.constants.Constants
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date

object GracePhotoUtil {
    fun getOutputMediaFile(context: Context, photoName: String): File? {
        var baseDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        if (baseDir == null) {
            baseDir = context.cacheDir
        }
        val mediaStorageDir = File(baseDir, Constants.APP_FOLDER)
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                LogUtil.d(Constants.CAMERA_TAG, "getOutputMediaFile() | failed to create directory")
                return null
            }
        }
        @SuppressLint("SimpleDateFormat")
        val timeStamp = SimpleDateFormat(Constants.DATE_PHOTO_FORMAT).format(Date())
        val mediaFile = File(
            mediaStorageDir.path + File.separator + photoName + timeStamp + Constants.GRACE_PHOTO_JPG
        )

        LogUtil.d(
            Constants.CAMERA_TAG,
            "getOutputMediaFile() | file path = ${mediaFile.absolutePath}"
        )
        return mediaFile
    }

    fun getSelectedPhotoPath(context: Context, selectedPhoto: Uri?): String {
        if (selectedPhoto == null) return ""

        var extension = "jpg"
        val mimeType = context.contentResolver.getType(selectedPhoto)
        if (mimeType != null) {
            val resolvedExt = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
            if (!resolvedExt.isNullOrEmpty()) extension = resolvedExt
        }

        var baseDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        if (baseDir == null) {
            baseDir = context.cacheDir
        }
        val outputFile = File(baseDir, "picked_${System.currentTimeMillis()}.$extension")

        return try {
            context.contentResolver.openInputStream(selectedPhoto).use { inputStream ->
                if (inputStream == null) return ""
                FileOutputStream(outputFile).use { outputStream ->
                    copyStreams(inputStream, outputStream)
                }
            }
            outputFile.absolutePath
        } catch (e: IOException) {
            LogUtil.d(Constants.CAMERA_TAG, "getSelectedPhotoPath() | exception = ${e.message}")
            ""
        }
    }

    fun addBlessing(mealPhoto: Bitmap, watermark: Bitmap, marginFactor: Int): Bitmap {
        val canvas = Canvas(mealPhoto)
        canvas.drawBitmap(mealPhoto, Matrix(), null)
        canvas.drawBitmap(
            watermark,
            watermark.width.toFloat() / marginFactor,
            mealPhoto.height - (watermark.height + watermark.height.toFloat() / marginFactor),
            null
        )
        return mealPhoto
    }

    private fun getOrientation(photoPath: String): Int {
        return try {
            val exif = ExifInterface(photoPath)
            val orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED
            )
            LogUtil.d(
                Constants.BLESS_TAG,
                "getOrientation() | orientation is: ${printOrientation(orientation)}"
            )
            orientation
        } catch (e: IOException) {
            LogUtil.d(Constants.BLESS_TAG, "getOrientation() | exception = ${e.message}")
            ExifInterface.ORIENTATION_UNDEFINED
        }
    }

    fun getHandledBitmap(photoPath: String): Bitmap {
        val options = BitmapFactory.Options().apply { inMutable = true }
        return when (getOrientation(photoPath)) {
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> flip(
                BitmapFactory.decodeFile(photoPath, options),
                ExifInterface.ORIENTATION_FLIP_HORIZONTAL
            )

            ExifInterface.ORIENTATION_ROTATE_180 -> rotateBitmap(
                BitmapFactory.decodeFile(photoPath, options),
                180f
            )

            ExifInterface.ORIENTATION_FLIP_VERTICAL -> flip(
                BitmapFactory.decodeFile(photoPath, options),
                ExifInterface.ORIENTATION_FLIP_VERTICAL
            )

            ExifInterface.ORIENTATION_ROTATE_90 -> rotateBitmap(
                BitmapFactory.decodeFile(photoPath, options),
                90f
            )

            ExifInterface.ORIENTATION_ROTATE_270 -> rotateBitmap(
                BitmapFactory.decodeFile(photoPath, options),
                270f
            )

            else -> BitmapFactory.decodeFile(photoPath, options)
        }
    }

    fun getResizedBitmap(bitmap: Bitmap, newWidth: Int, newHeight: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val scaleWidth = newWidth.toFloat() / width
        val scaleHeight = newHeight.toFloat() / height
        val matrix = Matrix().apply { postScale(scaleWidth, scaleHeight) }
        val resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false)
        bitmap.recycle()
        return resizedBitmap
    }

    fun getResizedBitmap(bitmap: Bitmap, scale: Int): Bitmap {
        val scaleWidth = bitmap.width / scale
        val scaleHeight = bitmap.height / scale
        return Bitmap.createScaledBitmap(bitmap, scaleWidth, scaleHeight, true)
    }

    fun getResizeType(mealPhoto: Bitmap): Int {
        return if (mealPhoto.width > mealPhoto.height) Constants.LANDSCAPE_RESIZE else Constants.PORTRAIT_RESIZE
    }

    fun getPhotoSizeInMB(photoUri: String): Float {
        return File(photoUri).length() / (1024f * 1024f)
    }

    fun convertPhotoToString(photoPath: String): String {
        val photoMB = getPhotoSizeInMB(photoPath)
        val scaleF = when {
            photoMB > 6f -> 18
            photoMB > 3f -> 9
            photoMB > 1f -> 6
            else -> 3
        }

        val bitmap = getResizedBitmap(BitmapFactory.decodeFile(photoPath), scaleF)
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)

        val imageType = File(photoPath).name.substring(File(photoPath).name.lastIndexOf("."))
        return "data:image/$imageType;base64," + Base64.encodeToString(
            byteArrayOutputStream.toByteArray(),
            Base64.DEFAULT
        )
    }

    private fun rotateBitmap(source: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }

    private fun flip(src: Bitmap, type: Int): Bitmap {
        val matrix = Matrix()
        when (type) {
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.preScale(1.0f, -1.0f)
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.preScale(-1.0f, 1.0f)
            else -> return src
        }
        return Bitmap.createBitmap(src, 0, 0, src.width, src.height, matrix, true)
    }

    private fun printOrientation(orientationTag: Int): String {
        return when (orientationTag) {
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> "ORIENTATION_FLIP_HORIZONTAL"
            ExifInterface.ORIENTATION_ROTATE_180 -> "ORIENTATION_ROTATE_180"
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> "ORIENTATION_FLIP_VERTICAL"
            ExifInterface.ORIENTATION_TRANSPOSE -> "ORIENTATION_TRANSPOSE"
            ExifInterface.ORIENTATION_ROTATE_90 -> "ORIENTATION_ROTATE_90"
            ExifInterface.ORIENTATION_TRANSVERSE -> "ORIENTATION_TRANSVERSE"
            ExifInterface.ORIENTATION_ROTATE_270 -> "ORIENTATION_ROTATE_270"
            else -> "ORIENTATION_UNDEFINED"
        }
    }

    private fun copyStreams(inputStream: InputStream, outputStream: OutputStream) {
        val buffer = ByteArray(8 * 1024)
        var bytesRead: Int
        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
            outputStream.write(buffer, 0, bytesRead)
        }
        outputStream.flush()
    }
}
