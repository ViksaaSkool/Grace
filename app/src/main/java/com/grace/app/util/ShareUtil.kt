package com.grace.app.util

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.grace.app.R
import java.io.File

object ShareUtil {
    fun shareBlessedPhoto(context: Context, blessedPhotoUri: String) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "image/jpg"
            val photoFile = File(blessedPhotoUri)
            val photoUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                photoFile
            )
            putExtra(Intent.EXTRA_STREAM, photoUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(
            Intent.createChooser(
                shareIntent,
                context.getString(R.string.share_meal_text)
            )
        )
    }
}
