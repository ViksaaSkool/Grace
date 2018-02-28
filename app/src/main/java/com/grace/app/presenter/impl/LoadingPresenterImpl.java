package com.grace.app.presenter.impl;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.grace.app.constants.Constants;
import com.grace.app.custom.BlessPhotoAsyncTask;
import com.grace.app.interactor.LoadingInteractor;
import com.grace.app.presenter.LoadingPresenter;
import com.grace.app.util.GracePhotoUtil;
import com.grace.app.util.LogUtil;
import com.grace.app.view.LoadingView;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

public final class LoadingPresenterImpl extends BasePresenterImpl<LoadingView> implements LoadingPresenter {
    /**
     * The interactor
     */
    @NonNull
    private final LoadingInteractor mInteractor;

    private boolean mIsMealCall = false;
    private BlessPhotoAsyncTask mBlessPhotoAsyncTask;
    private AsyncTask<Void, Void, String> mConvertPhotoAsyncTask;


    @Inject
    public LoadingPresenterImpl(@NonNull LoadingInteractor interactor) {
        mInteractor = interactor;
    }

    @Override
    public void onStart(boolean viewCreated) {
        super.onStart(viewCreated);
    }

    @Override
    public void onStop() {
        super.onStop();
    }


    @Override
    public void isPhotoOfMeal(String photoUri) {
        if (getView() != null) {
            if (photoUri != null && !photoUri.isEmpty()) {
                convertPhotoAndValidate(photoUri);
            } else {
                LogUtil.d(Constants.BLESS_TAG, "isPhotoOfMeal() | photoUri is NULL or EMPTY!");
                getView().onIsMealResponseFailure("photoUri is NULL or EMPTY!");
            }
        }
    }

    private void convertPhotoAndValidate(final String photoUri) {
        mConvertPhotoAsyncTask = new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {
                LogUtil.d(Constants.API_TAG, "convertPhotoAndValidate doInBackground() |");
                return GracePhotoUtil.convertPhotoToString(photoUri);
            }

            @Override
            protected void onPostExecute(String base64Photo) {
                super.onPostExecute(base64Photo);
                LogUtil.d(Constants.API_TAG, "convertPhotoAndValidate onPostExecute() | " +
                        "base64Photo = " + base64Photo);
                mInteractor.isPhotoOfMeal(base64Photo, getView());
                mIsMealCall = true;
            }
        };
        mConvertPhotoAsyncTask.execute();


    }

    @Override
    public void blessPhoto(String photoUri) {
        if (getView() != null) {
            if (photoUri != null && !photoUri.isEmpty()) {
                mBlessPhotoAsyncTask = new BlessPhotoAsyncTask(getView(), photoUri);
                mBlessPhotoAsyncTask.execute();
            } else {
                LogUtil.d(Constants.BLESS_TAG, "blessPhoto() | photoUri is NULL or EMPTY!");
                getView().onPhotoBlessed("");
            }
        }
    }


    @Override
    public void onPresenterDestroyed() {
        super.onPresenterDestroyed();
    }

    @Override
    public void onViewDetached() {
        super.onViewDetached();
        //if call is running, cancel it!
        if (mIsMealCall) {
            mInteractor.cancelCall();
        }

        //if task is running, cancel it!
        if (mBlessPhotoAsyncTask != null
                && mBlessPhotoAsyncTask.getStatus() == AsyncTask.Status.RUNNING) {
            mBlessPhotoAsyncTask.cancel(true);
            EventBus.getDefault().post(true);
        }

        //if task is running, cancel it!
        if (mConvertPhotoAsyncTask != null
                && mConvertPhotoAsyncTask.getStatus() == AsyncTask.Status.RUNNING) {
            mConvertPhotoAsyncTask.cancel(true);
            EventBus.getDefault().post(true);
        }

    }
}