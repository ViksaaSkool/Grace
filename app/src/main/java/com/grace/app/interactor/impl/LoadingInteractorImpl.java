package com.grace.app.interactor.impl;

import com.grace.app.GraceApplication;
import com.grace.app.constants.Constants;
import com.grace.app.injection.module.GraceApiModule;
import com.grace.app.interactor.LoadingInteractor;
import com.grace.app.model.GraceApiCall;
import com.grace.app.model.MealResponse;
import com.grace.app.util.LogUtil;
import com.grace.app.view.LoadingView;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public final class LoadingInteractorImpl implements LoadingInteractor {

    @Inject
    GraceApiModule.GraceInterface mGraceInterface;
    @Inject
    GraceApplication mGraceApplication;
    @Inject
    GraceApiCall mGraceApiCall;

    private boolean ongoing = false;

    @Inject
    public LoadingInteractorImpl() {
        GraceApplication.getAppComponent().inject(this);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void isPhotoOfMeal(String base64PhotoString, final LoadingView view) {

        RequestBody mealPhoto = RequestBody.create(MediaType.parse("multipart/form-data"), base64PhotoString);
        RequestBody type = RequestBody.create(MediaType.parse("multipart/form-data"), Constants.DATA_VALUE);
        RequestBody rawValue = RequestBody.create(MediaType.parse("multipart/form-data"), "0");

        mGraceApiCall.setGraceApicall(mGraceInterface.recogniseMeal(mealPhoto, type, rawValue));
        ongoing = true;
        mGraceApiCall.getGraceApicall().enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                ongoing = false;
                if (view != null) {
                    if (response.isSuccessful()) {
                        LogUtil.d(Constants.API_TAG, "isPhotoOfMeal() | onResponse() SUCCESS!");
                        MealResponse mealResponse = (MealResponse) response.body();
                        view.onIsMealResponseSuccess(mealResponse);
                    } else {
                        LogUtil.d(Constants.API_TAG, "isPhotoOfMeal() | onResponse() Failure?");
                        view.onIsMealResponseFailure(response.message());
                    }

                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                ongoing = false;
                if (view != null && !call.isCanceled()) {
                    LogUtil.d(Constants.API_TAG, "isPhotoOfMeal() | " +
                            "onFailure exception = " + t.getMessage());
                    view.onIsMealResponseFailure(t.getMessage());
                } else {
                    LogUtil.d(Constants.API_TAG, "isPhotoOfMeal() | " +
                            "call.isCanceled() = " + call.isCanceled());
                }

            }
        });
    }

    @Override
    public void cancelCall() {
        if (mGraceApiCall != null && mGraceApiCall.getGraceApicall() != null && ongoing) {
            mGraceApiCall.getGraceApicall().cancel();
            LogUtil.d(Constants.API_TAG, "LoadingInteractorImpl cancelCall() |");
            EventBus.getDefault().post(true);
        }

    }
}


