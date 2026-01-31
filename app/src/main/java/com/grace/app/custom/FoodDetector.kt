package com.grace.app.custom

import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeler
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class FoodDetector(
    confidenceThreshold: Float = 0.70f
) {
    private val labeler: ImageLabeler = ImageLabeling.getClient(
        ImageLabelerOptions.Builder()
            .setConfidenceThreshold(confidenceThreshold)
            .build()
    )

    suspend fun isFood(image: InputImage): Boolean = suspendCancellableCoroutine { continuation ->
        labeler.process(image)
            .addOnSuccessListener { labels ->
                if (continuation.isActive) {
                    continuation.resume(
                        labels.any { label ->
                            val text = label.text.lowercase(Locale.US)
                            FOOD_LABELS.any { key -> text == key || text.contains(key) }
                        }
                    )
                }
            }
            .addOnFailureListener { error ->
                if (continuation.isActive) {
                    continuation.resumeWithException(error)
                }
            }
    }

    companion object {
        private val FOOD_LABELS = setOf(
            "food",
            "dish",
            "cuisine",
            "meal",
            "dessert",
            "fruit",
            "vegetable",
            "burger",
            "pizza",
            "sandwich",
            "salad",
            "pasta",
            "sushi"
        )
    }
}
