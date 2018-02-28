package com.grace.app.presenter.loader;

import android.support.annotation.NonNull;

import com.grace.app.presenter.BasePresenter;

/**
 * Factory to implement to create a presenter
 */
public interface PresenterFactory<T extends BasePresenter> {
    @NonNull
    T create();
}
