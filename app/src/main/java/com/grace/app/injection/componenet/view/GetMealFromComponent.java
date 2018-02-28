package com.grace.app.injection.componenet.view;

import com.grace.app.injection.componenet.AppComponent;
import com.grace.app.injection.module.view.GetMealFromModule;
import com.grace.app.injection.scope.FragmentScope;
import com.grace.app.view.fragment.GetMealFromFragment;

import dagger.Component;

/**
 * Created by varsovski on 24-Dec-16.
 */

@FragmentScope
@Component(dependencies = AppComponent.class, modules = GetMealFromModule.class)
public interface GetMealFromComponent {
    void inject(GetMealFromFragment fragment);
}
