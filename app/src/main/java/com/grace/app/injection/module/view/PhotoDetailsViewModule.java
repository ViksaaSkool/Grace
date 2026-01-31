package com.grace.app.injection.module.view;

import androidx.annotation.NonNull;

import com.grace.app.interactor.PhotoDetailsInteractor;
import com.grace.app.interactor.impl.PhotoDetailsInteractorImpl;
import com.grace.app.presenter.PhotoDetailsPresenter;
import com.grace.app.presenter.impl.PhotoDetailsPresenterImpl;
import com.grace.app.presenter.loader.PresenterFactory;

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
        return () -> new PhotoDetailsPresenterImpl(interactor);
    }
}
