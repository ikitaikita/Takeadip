package com.takeadip.takeadip.data;

import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.takeadip.takeadip.data.model.DipData;

import java.lang.reflect.Type;
import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by janisharali on 25/12/16.
 */

@Singleton
public class SharedPrefsHelper {

    public static String PREF_KEY_DIPS_LIST = "dips-list";

    private SharedPreferences mSharedPreferences;

    @Inject
    public SharedPrefsHelper(SharedPreferences sharedPreferences) {
        mSharedPreferences = sharedPreferences;
    }

    public void put(String key, String value) {
        mSharedPreferences.edit().putString(key, value).apply();
    }

    public void putDipsList (String key, ArrayList<DipData> diplist){
        Gson gson = new Gson();

        String json = gson.toJson(diplist);
        mSharedPreferences.edit().putString(key,json).apply();
    }

    public ArrayList<DipData> getDipsList(String key){
        Gson gson = new Gson();
        String json = mSharedPreferences.getString(key, null);
        Type type = new TypeToken<ArrayList<DipData>>() {}.getType();
        ArrayList<DipData> arrayList = gson.fromJson(json, type);
        return arrayList;
    }

    public void put(String key, int value) {
        mSharedPreferences.edit().putInt(key, value).apply();
    }


    public void deleteSavedData(String key) {
        mSharedPreferences.edit().remove(key).apply();
    }
}
