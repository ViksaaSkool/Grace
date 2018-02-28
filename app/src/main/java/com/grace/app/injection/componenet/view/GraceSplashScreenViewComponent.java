package com.grace.app.injection.componenet.view;

import com.grace.app.injection.module.view.GraceSplashScreenViewModule;
import com.grace.app.injection.componenet.AppComponent;
import com.grace.app.injection.scope.ActivityScope;
import com.grace.app.view.activity.GraceSplashScreenActivity;

import dagger.Component;

@ActivityScope
@Component(dependencies = AppComponent.class, modules = GraceSplashScreenViewModule.class)
public interface GraceSplashScreenViewComponent {
    void inject(GraceSplashScreenActivity activity);
}