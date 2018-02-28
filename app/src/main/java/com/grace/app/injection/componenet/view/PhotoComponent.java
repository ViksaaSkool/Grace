package com.grace.app.injection.componenet.view;

import com.grace.app.injection.componenet.AppComponent;
import com.grace.app.injection.module.view.PhotoModule;
import com.grace.app.injection.scope.FragmentScope;
import com.grace.app.view.fragment.PhotoFragment;

import dagger.Component;

/**
 * Created by varsovski on 25-Dec-16.
 */

@FragmentScope
@Component(dependencies = AppComponent.class, modules = PhotoModule.class)
public interface PhotoComponent {
    void inject(PhotoFragment fragment);
}
