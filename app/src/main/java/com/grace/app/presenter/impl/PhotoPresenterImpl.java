package com.grace.app.presenter.impl;

import android.support.annotation.NonNull;

import com.grace.app.interactor.PhotoInteractor;
import com.grace.app.presenter.PhotoPresenter;
import com.grace.app.view.PhotoView;

import javax.inject.Inject;

/**
 * Created by varsovski on 25-Dec-16.
 */

public class PhotoPresenterImpl extends BasePresenterImpl<PhotoView> implements PhotoPresenter {

    /**
     * The interactor
     */
    @NonNull
    private final PhotoInteractor mInteractor;

    private boolean isFirstStart = true;

    // The view is available using the mView variable

    @Inject
    public PhotoPresenterImpl(@NonNull PhotoInteractor interactor) {
        mInteractor = interactor;
    }

    @Override
    public void onStart(boolean firstStart) {
        isFirstStart = firstStart;
        super.onStart(firstStart);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onPresenterDestroyed() {
        super.onPresenterDestroyed();
    }


}
