package com.takeadip.takeadip.di.component;

import com.takeadip.takeadip.ViewPagerActivity;
import com.takeadip.takeadip.di.PerActivity;
import com.takeadip.takeadip.di.module.ActivityModule;

import dagger.Component;

/**
 * Created by vik on 16/10/2017.
 */
@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {

    void inject(ViewPagerActivity mainActivity);
}
