package com.grace.app.interactor.impl;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.grace.app.GraceApplication;
import com.grace.app.constants.Constants;
import com.grace.app.custom.FoodDetector;
import com.grace.app.interactor.LoadingInteractor;
import com.grace.app.model.MealResponse;
import com.grace.app.model.Status;
import com.grace.app.util.LogUtil;
import com.grace.app.view.LoadingView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import javax.inject.Inject;

public final class LoadingInteractorImpl implements LoadingInteractor {


    @Inject
    FoodDetector mFoodDetector;

    private boolean ongoing = false;

    @Inject
    public LoadingInteractorImpl() {
        GraceApplication.getAppComponent().inject(this);
    }


    @Override
    public void isPhotoOfMeal(Bitmap bitmap, final LoadingView view) {

        MealResponse mealResponse = new MealResponse();
        mealResponse.setStatus(new Status());

        InputImage image = InputImage.fromBitmap(bitmap, 0);

        mFoodDetector.isFood(image, new FoodDetector.Callback() {
            @Override
            public void onResult(boolean isFood, @NonNull List<ImageLabel> labels) {
                mealResponse.setIsMeal(isFood);
                view.onIsMealResponseSuccess(mealResponse);
            }

            @Override
            public void onError(@NonNull Exception e) {
                view.onIsMealResponseFailure("No food detected!");
            }
        });
    }

    @Override
    public void cancelCall() {
        if (ongoing) {
            LogUtil.d(Constants.API_TAG, "LoadingInteractorImpl cancelCall() |");
            EventBus.getDefault().post(true);
        }

    }
}


