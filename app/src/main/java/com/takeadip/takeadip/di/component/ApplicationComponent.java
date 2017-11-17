package com.takeadip.takeadip.di.component;

import android.app.Application;
import android.content.Context;

import com.takeadip.takeadip.TakeadipApplication;
import com.takeadip.takeadip.data.DataManager;
import com.takeadip.takeadip.data.DbHelper;
import com.takeadip.takeadip.data.SharedPrefsHelper;
import com.takeadip.takeadip.di.ApplicationContext;
import com.takeadip.takeadip.di.module.ApplicationModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by vik on 16/10/2017.
 */

@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {

    void inject(TakeadipApplication takeadipApplication);

    @ApplicationContext
    Context getContext();

    Application getApplication();

    DataManager getDataManager();

    DbHelper getDbHelper();

    SharedPrefsHelper getPreferenceHelper();
}
