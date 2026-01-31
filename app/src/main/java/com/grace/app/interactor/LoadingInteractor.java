package com.grace.app.interactor;

import android.graphics.Bitmap;

import com.grace.app.view.LoadingView;

public interface LoadingInteractor extends BaseInteractor {

    void isPhotoOfMeal(Bitmap image, LoadingView view);

    void cancelCall();
}