package com.grace.app.injection.componenet.view;

import com.grace.app.injection.module.view.PhotoDetailsViewModule;
import com.grace.app.injection.componenet.AppComponent;
import com.grace.app.injection.scope.ActivityScope;
import com.grace.app.view.activity.PhotoDetailsActivity;

import dagger.Component;

@ActivityScope
@Component(dependencies = AppComponent.class, modules = PhotoDetailsViewModule.class)
public interface PhotoDetailsViewComponent {
    void inject(PhotoDetailsActivity activity);
}