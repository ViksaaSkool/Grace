package com.grace.app.interactor;

import com.grace.app.view.LoadingView;

public interface LoadingInteractor extends BaseInteractor {

    void isPhotoOfMeal(String base64PhotoString, LoadingView view);

    void cancelCall();
}