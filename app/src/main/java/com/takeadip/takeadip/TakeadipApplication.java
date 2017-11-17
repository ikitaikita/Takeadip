package com.takeadip.takeadip;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.takeadip.takeadip.data.DataManager;
import com.takeadip.takeadip.di.component.ActivityComponent;
import com.takeadip.takeadip.di.component.ApplicationComponent;
import com.takeadip.takeadip.di.component.DaggerApplicationComponent;
import com.takeadip.takeadip.di.module.ApplicationModule;

import javax.inject.Inject;

/**
 * Created by vik on 16/10/2017.
 */

public class TakeadipApplication extends Application {

    protected ApplicationComponent applicationComponent;
    private static TakeadipApplication instance;



    @Inject
    DataManager dataManager;

    public static TakeadipApplication get(Context context) {
        return (TakeadipApplication) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        applicationComponent = DaggerApplicationComponent
                .builder()
                .applicationModule(new ApplicationModule(this))
                .build();
        applicationComponent.inject(this);
    }

    public ApplicationComponent getComponent(){
        return applicationComponent;
    }

    public static TakeadipApplication getInstance ()
    {
        return instance;
    }

    public static boolean hasNetwork ()
    {
        return instance.checkIfHasNetwork();
    }
    public boolean checkIfHasNetwork()
    {
        ConnectivityManager cm = (ConnectivityManager) getSystemService( Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }



}
