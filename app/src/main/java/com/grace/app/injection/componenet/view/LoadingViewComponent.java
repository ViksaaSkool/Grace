package com.grace.app.injection.componenet.view;

import com.grace.app.injection.module.view.LoadingViewModule;
import com.grace.app.injection.componenet.AppComponent;
import com.grace.app.injection.scope.FragmentScope;
import com.grace.app.view.fragment.LoadingFragment;

import dagger.Component;

@FragmentScope
@Component(dependencies = AppComponent.class, modules = LoadingViewModule.class)
public interface LoadingViewComponent {
    void inject(LoadingFragment fragment);
}