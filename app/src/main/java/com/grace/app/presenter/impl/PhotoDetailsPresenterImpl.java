package com.grace.app.presenter.impl;

import android.support.annotation.NonNull;

import com.grace.app.presenter.PhotoDetailsPresenter;
import com.grace.app.view.PhotoDetailsView;
import com.grace.app.interactor.PhotoDetailsInteractor;

import javax.inject.Inject;

public final class PhotoDetailsPresenterImpl extends BasePresenterImpl<PhotoDetailsView> implements PhotoDetailsPresenter {
    /**
     * The interactor
     */
    @NonNull
    private final PhotoDetailsInteractor mInteractor;

    // The view is available using the mView variable

    @Inject
    public PhotoDetailsPresenterImpl(@NonNull PhotoDetailsInteractor interactor) {
        mInteractor = interactor;
    }

    @Override
    public void onStart(boolean firstStart) {
        super.onStart(firstStart);

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