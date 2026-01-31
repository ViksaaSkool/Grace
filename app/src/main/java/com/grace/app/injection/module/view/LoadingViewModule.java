package com.grace.app.injection.module.view;

import androidx.annotation.NonNull;

import com.grace.app.interactor.LoadingInteractor;
import com.grace.app.interactor.impl.LoadingInteractorImpl;
import com.grace.app.presenter.LoadingPresenter;
import com.grace.app.presenter.impl.LoadingPresenterImpl;
import com.grace.app.presenter.loader.PresenterFactory;

import dagger.Module;
import dagger.Provides;

@Module
public final class LoadingViewModule {
    @Provides
    public LoadingInteractor provideInteractor() {
        return new LoadingInteractorImpl();
    }

    @Provides
    public PresenterFactory<LoadingPresenter> providePresenterFactory(@NonNull final LoadingInteractor interactor) {
        return () -> new LoadingPresenterImpl(interactor);
    }
}
