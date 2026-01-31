package com.grace.app.data

import android.content.Context
import com.google.mlkit.vision.common.InputImage
import com.grace.app.custom.BlessPhotoWorker
import com.grace.app.custom.FoodDetector
import com.grace.app.di.IoDispatcher
import com.grace.app.util.GracePhotoUtil
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MealRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val foodDetector: FoodDetector,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : MealRepository {
    override suspend fun isMeal(photoUri: String): Boolean = withContext(ioDispatcher) {
        val bitmap = GracePhotoUtil.getHandledBitmap(photoUri)
        val image = InputImage.fromBitmap(bitmap, 0)
        try {
            foodDetector.isFood(image)
        } finally {
            bitmap.recycle()
        }
    }

    override suspend fun blessPhoto(photoUri: String): String = withContext(ioDispatcher) {
        BlessPhotoWorker.bless(context, photoUri)
    }
}
