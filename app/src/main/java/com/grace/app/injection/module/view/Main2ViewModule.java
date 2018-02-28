package com.grace.app.injection.module.view;

import android.support.annotation.NonNull;

import com.grace.app.interactor.Main2Interactor;
import com.grace.app.interactor.impl.Main2InteractorImpl;
import com.grace.app.presenter.loader.PresenterFactory;
import com.grace.app.presenter.Main2Presenter;
import com.grace.app.presenter.impl.Main2PresenterImpl;

import dagger.Module;
import dagger.Provides;

@Module
public final class Main2ViewModule {
    @Provides
    public Main2Interactor provideInteractor() {
        return new Main2InteractorImpl();
    }

    @Provides
    public PresenterFactory<Main2Presenter> providePresenterFactory(@NonNull final Main2Interactor interactor) {
        return new PresenterFactory<Main2Presenter>() {
            @NonNull
            @Override
            public Main2Presenter create() {
                return new Main2PresenterImpl(interactor);
            }
        };
    }
}
