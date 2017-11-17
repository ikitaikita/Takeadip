package com.takeadip.takeadip.di.module;

import android.app.Activity;
import android.content.Context;

import com.takeadip.takeadip.di.ActivityContext;

import dagger.Module;
import dagger.Provides;

/**
 * Created by vik on 16/10/2017.
 */
@Module
public class ActivityModule {

    private Activity mActivity;

    public ActivityModule(Activity activity) {
        mActivity = activity;
    }

    @Provides
    @ActivityContext
    Context provideContext() {
        return mActivity;
    }

    @Provides
    Activity provideActivity() {
        return mActivity;
    }
}
