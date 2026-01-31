package com.grace.app.injection.module.view;

import androidx.annotation.NonNull;

import com.grace.app.interactor.GetMealFromInteractor;
import com.grace.app.interactor.impl.GetMealFromInteractorImpl;
import com.grace.app.presenter.GetMealFromPresenter;
import com.grace.app.presenter.impl.GetMealFromPresenterImpl;
import com.grace.app.presenter.loader.PresenterFactory;

import dagger.Module;
import dagger.Provides;

/**
 * Created by varsovski on 24-Dec-16.
 */
@Module
public class GetMealFromModule {

    @Provides
    public GetMealFromInteractor provideInteractor() {
        return new GetMealFromInteractorImpl();
    }

    @Provides
    public PresenterFactory<GetMealFromPresenter> providePresenterFactory(@NonNull final GetMealFromInteractor interactor) {
        return () -> new GetMealFromPresenterImpl(interactor);
    }
}
