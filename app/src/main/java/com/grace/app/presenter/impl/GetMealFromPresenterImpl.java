package com.grace.app.presenter.impl;

import android.support.annotation.NonNull;
import android.view.View;

import com.grace.app.interactor.GetMealFromInteractor;
import com.grace.app.presenter.GetMealFromPresenter;
import com.grace.app.view.GetMealFromView;

import javax.inject.Inject;

/**
 * Created by varsovski on 24-Dec-16.
 */

public class GetMealFromPresenterImpl extends BasePresenterImpl<GetMealFromView> implements GetMealFromPresenter {

    /**
     * The interactor
     */
    @NonNull
    private final GetMealFromInteractor mInteractor;

    // The view is available using the mView variable

    @Inject
    public GetMealFromPresenterImpl(@NonNull GetMealFromInteractor interactor) {
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

    @Override
    public void setAnimationToView(final View v) {
    }
}

