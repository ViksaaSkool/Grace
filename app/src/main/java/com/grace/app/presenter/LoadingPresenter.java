package com.grace.app.presenter;

import com.grace.app.view.LoadingView;

public interface LoadingPresenter extends BasePresenter<LoadingView> {

    void isPhotoOfMeal(String photoUri);

    void blessPhoto(String photoUri);
}