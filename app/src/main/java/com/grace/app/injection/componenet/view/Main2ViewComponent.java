package com.grace.app.injection.componenet.view;

import com.grace.app.injection.componenet.AppComponent;
import com.grace.app.injection.module.view.Main2ViewModule;
import com.grace.app.injection.scope.ActivityScope;
import com.grace.app.view.activity.Main2Activity;

import dagger.Component;

@ActivityScope
@Component(dependencies = AppComponent.class, modules = Main2ViewModule.class)
public interface Main2ViewComponent {
    void inject(Main2Activity activity);
}