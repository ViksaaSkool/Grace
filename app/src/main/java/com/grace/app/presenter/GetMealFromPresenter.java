package com.grace.app.presenter;

import android.view.View;

import com.grace.app.view.GetMealFromView;

/**
 * Created by varsovski on 24-Dec-16.
 */

public interface GetMealFromPresenter extends BasePresenter<GetMealFromView> {

    void setAnimationToView(View v);
}
