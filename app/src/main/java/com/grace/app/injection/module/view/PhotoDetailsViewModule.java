package com.grace.app.injection.module.view;

import android.support.annotation.NonNull;

import com.grace.app.interactor.PhotoDetailsInteractor;
import com.grace.app.interactor.impl.PhotoDetailsInteractorImpl;
import com.grace.app.presenter.loader.PresenterFactory;
import com.grace.app.presenter.PhotoDetailsPresenter;
import com.grace.app.presenter.impl.PhotoDetailsPresenterImpl;

import dagger.Module;
import dagger.Provides;

@Module
public final class PhotoDetailsViewModule {
    @Provides
    public PhotoDetailsInteractor provideInteractor() {
        return new PhotoDetailsInteractorImpl();
    }

    @Provides
    public PresenterFactory<PhotoDetailsPresenter> providePresenterFactory(@NonNull final PhotoDetailsInteractor interactor) {
        return new PresenterFactory<PhotoDetailsPresenter>() {
            @NonNull
            @Override
            public PhotoDetailsPresenter create() {
                return new PhotoDetailsPresenterImpl(interactor);
            }
        };
    }
}
