package com.grace.app.presenter.impl;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.grace.app.constants.Constants;
import com.grace.app.custom.BlessPhotoWorker;
import com.grace.app.interactor.LoadingInteractor;
import com.grace.app.presenter.LoadingPresenter;
import com.grace.app.util.GracePhotoUtil;
import com.grace.app.util.LogUtil;
import com.grace.app.view.LoadingView;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.inject.Inject;

public final class LoadingPresenterImpl extends BasePresenterImpl<LoadingView> implements LoadingPresenter {
    /**
     * The interactor
     */
    @NonNull
    private final LoadingInteractor mInteractor;

    private boolean mIsMealCall = false;
    private Future<?> mBlessPhotoTask;
    private Future<?> mConvertPhotoTask;
    private final ExecutorService mExecutor = Executors.newSingleThreadExecutor();
    private final Handler mMainHandler = new Handler(Looper.getMainLooper());


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
        mConvertPhotoTask = mExecutor.submit(() -> {
            LogUtil.d(Constants.API_TAG, "convertPhotoAndValidate doInBackground() |");
            final Bitmap imageBitmap = GracePhotoUtil.getHandledBitmap(photoUri);
            mMainHandler.post(() -> {
                mInteractor.isPhotoOfMeal(imageBitmap, getView());
            });
        });
    }

    @Override
    public void blessPhoto(String photoUri) {
        if (getView() != null) {
            if (photoUri != null && !photoUri.isEmpty()) {
                mBlessPhotoTask = mExecutor.submit(new Runnable() {
                    @Override
                    public void run() {
                        final String result = BlessPhotoWorker.bless(photoUri);
                        mMainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                LogUtil.d(Constants.BLESS_TAG, "BlessPhotoWorker onPostExecute() | result = " + result);
                                if (getView() != null) {
                                    getView().onPhotoBlessed(result);
                                }
                            }
                        });
                    }
                });
            } else {
                LogUtil.d(Constants.BLESS_TAG, "blessPhoto() | photoUri is NULL or EMPTY!");
                getView().onPhotoBlessed("");
            }
        }
    }


    @Override
    public void onPresenterDestroyed() {
        mExecutor.shutdownNow();
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
        if (mBlessPhotoTask != null && !mBlessPhotoTask.isDone()) {
            mBlessPhotoTask.cancel(true);
            EventBus.getDefault().post(true);
        }

        //if task is running, cancel it!
        if (mConvertPhotoTask != null && !mConvertPhotoTask.isDone()) {
            mConvertPhotoTask.cancel(true);
            EventBus.getDefault().post(true);
        }

    }
}
