package com.grace.app.presenter.impl;

import android.support.annotation.NonNull;

import com.grace.app.presenter.GraceSplashScreenPresenter;
import com.grace.app.view.GraceSplashScreenView;
import com.grace.app.interactor.GraceSplashScreenInteractor;

import javax.inject.Inject;

public final class GraceSplashScreenPresenterImpl extends BasePresenterImpl<GraceSplashScreenView> implements GraceSplashScreenPresenter {
    /**
     * The interactor
     */
    @NonNull
    private final GraceSplashScreenInteractor mInteractor;

    // The view is available using the mView variable

    @Inject
    public GraceSplashScreenPresenterImpl(@NonNull GraceSplashScreenInteractor interactor) {
        mInteractor = interactor;
    }

    @Override
    public void onStart(boolean viewCreated) {
        super.onStart(viewCreated);

        // Your code here. Your view is available using mView and will not be null until next onStop()
    }

    @Override
    public void onStop() {
        // Your code here, mView will be null after this method until next onStart()

        super.onStop();
    }

    @Override
    public void onPresenterDestroyed() {
        /*
         * Your code here. After this method, your presenter (and view) will be completely destroyed
         * so make sure to cancel any HTTP call or database connection
         */

        super.onPresenterDestroyed();
    }
}