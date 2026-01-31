package com.grace.app.custom

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.grace.app.R
import com.grace.app.constants.Constants
import com.grace.app.util.GracePhotoUtil
import com.grace.app.util.LogUtil
import java.io.FileOutputStream
import java.io.IOException

object BlessPhotoWorker {
    fun bless(context: Context, uri: String): String {
        LogUtil.d(Constants.BLESS_TAG, "BlessPhotoWorker bless() | photo = $uri")

        val bottomImage = GracePhotoUtil.getHandledBitmap(uri)

        val topImageSource = BitmapFactory.decodeResource(
            context.resources,
            R.drawable.watermark_blessed_transparent
        )
        val widthHeight =
            if (GracePhotoUtil.getResizeType(bottomImage) == Constants.PORTRAIT_RESIZE) {
                bottomImage.width / Constants.WATERMARK_DIMENSIONS_FACTOR
            } else {
                bottomImage.height / Constants.WATERMARK_DIMENSIONS_FACTOR
            }
        val topImage = GracePhotoUtil.getResizedBitmap(topImageSource, widthHeight, widthHeight)
        topImageSource.recycle()

        var blessedPhotoUri = ""
        try {
            val file = GracePhotoUtil.getOutputMediaFile(context, Constants.GRACE_BLESSED_PHOTO)
            if (file != null) {
                blessedPhotoUri = file.absolutePath
                FileOutputStream(blessedPhotoUri).use { outputStream ->
                    GracePhotoUtil.addBlessing(bottomImage, topImage, Constants.MARGIN_FACTOR)
                        .compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                }
            }
        } catch (e: IOException) {
            LogUtil.d(Constants.BLESS_TAG, "bless() | exception = ${e.message}")
            blessedPhotoUri = ""
        } finally {
            bottomImage.recycle()
            topImage.recycle()
        }

        return blessedPhotoUri
    }
}
