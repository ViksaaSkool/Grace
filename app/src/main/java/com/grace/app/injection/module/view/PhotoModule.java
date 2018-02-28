package com.grace.app.injection.module.view;

import android.support.annotation.NonNull;

import com.grace.app.interactor.PhotoInteractor;
import com.grace.app.interactor.impl.PhotoInteractorImpl;
import com.grace.app.presenter.PhotoPresenter;
import com.grace.app.presenter.impl.PhotoPresenterImpl;
import com.grace.app.presenter.loader.PresenterFactory;

import dagger.Module;
import dagger.Provides;

/**
 * Created by varsovski on 25-Dec-16.
 */

@Module
public class PhotoModule {

    @Provides
    public PhotoInteractor provideInteractor() {
        return new PhotoInteractorImpl();
    }

    @Provides
    public PresenterFactory<PhotoPresenter> providePresenterFactory(@NonNull final PhotoInteractor interactor) {
        return new PresenterFactory<PhotoPresenter>() {
            @NonNull
            @Override
            public PhotoPresenter create() {
                return new PhotoPresenterImpl(interactor);
            }
        };
    }
}
