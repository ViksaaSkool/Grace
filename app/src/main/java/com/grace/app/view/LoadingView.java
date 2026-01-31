package com.grace.app.view;

import androidx.annotation.UiThread;

import com.grace.app.model.MealResponse;

@UiThread
public interface LoadingView {

    void onIsMealResponseSuccess(MealResponse mealResponse);

    void onIsMealResponseFailure(String error);

    void onPhotoBlessed(String uri);


}