package com.takeadip.takeadip.data;

import android.content.Context;

import com.takeadip.takeadip.di.ApplicationContext;
import com.takeadip.takeadip.data.model.DipData;

import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by vik on 16/10/2017.
 */

@Singleton
public class DataManager {

    private Context mContext;
    private DbHelper mDbHelper;
    private SharedPrefsHelper mSharedPrefsHelper;


    @Inject
    public DataManager(@ApplicationContext Context context,
                       DbHelper dbHelper,SharedPrefsHelper sharedPrefsHelper ) {
        mContext = context;
        mDbHelper = dbHelper;
        mSharedPrefsHelper = sharedPrefsHelper;
    }

    public Long createFavourite(DipData dip) throws Exception {
        return mDbHelper.insertDip(dip);
    }

    public Boolean isFavourite(String dipId)  {
        return mDbHelper.isFavourite(dipId);
    }

    public Long deleteFavourite (String dipId) {
        return mDbHelper.deleteDip(dipId);
    }

    public ArrayList<DipData> getFavuorites () {
        return mDbHelper.getFavourites();
    }

    public void saveDipsListOnPrefs(ArrayList<DipData> dipslist ){
        mSharedPrefsHelper.putDipsList(SharedPrefsHelper.PREF_KEY_DIPS_LIST, dipslist);
    }
    public ArrayList<DipData> getDipsListFromPrefs(){
        return mSharedPrefsHelper.getDipsList(SharedPrefsHelper.PREF_KEY_DIPS_LIST);
    }
}
