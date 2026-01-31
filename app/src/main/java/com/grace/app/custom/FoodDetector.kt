package com.grace.app.custom;

import androidx.annotation.NonNull;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class FoodDetector {

    // Keep this small & strict to avoid false positives.
    // You can expand it after you see real outputs from your users' images.
    private static final Set<String> FOOD_LABELS = new HashSet<>(Arrays.asList(
            "food", "dish", "cuisine", "meal", "dessert", "fruit", "vegetable",
            "burger", "pizza", "sandwich", "salad", "pasta", "sushi"
    ));

    private final ImageLabeler labeler;

    public FoodDetector() {
        ImageLabelerOptions options =
                new ImageLabelerOptions.Builder()
                        .setConfidenceThreshold(0.70f) // tune this
                        .build();
        labeler = ImageLabeling.getClient(options);
    }

    public interface Callback {
        void onResult(boolean isFood, @NonNull List<ImageLabel> labels);

        void onError(@NonNull Exception e);
    }

    public void isFood(@NonNull InputImage image, @NonNull Callback callback) {
        labeler.process(image)
                .addOnSuccessListener(labels -> {
                    boolean isFood = false;

                    for (ImageLabel l : labels) {
                        String text = l.getText().toLowerCase(Locale.US);

                        // direct match
                        if (FOOD_LABELS.contains(text)) {
                            isFood = true;
                            break;
                        }

                        // loose match (e.g., "fast food" or "food product")
                        for (String key : FOOD_LABELS) {
                            if (!key.isEmpty() && text.contains(key)) {
                                isFood = true;
                                break;
                            }
                        }
                        if (isFood) break;
                    }

                    callback.onResult(isFood, labels);
                })
                .addOnFailureListener(callback::onError);
    }
}