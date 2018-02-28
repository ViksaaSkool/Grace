package com.grace.app.view;

import android.support.annotation.UiThread;

import com.grace.app.model.MealResponse;

@UiThread
public interface LoadingView {

    void onIsMealResponseSuccess(MealResponse mealResponse);

    void onIsMealResponseFailure(String error);

    void onPhotoBlessed(String uri);


}