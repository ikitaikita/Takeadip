package com.takeadip.takeadip;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.takeadip.takeadip.data.model.DipData;
import com.takeadip.takeadip.data.model.DipsList;
import com.takeadip.takeadip.internal.Utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Created by vik on 20/10/2017.
 */

public class ViewPagerActivityPresenter implements ViewPagerActivityContract{

    public static final String CHARSET_NAME = "UTF-8";

    private final ViewPagerActivityContract.View mDipsView;
    private final DipsService service;

    private ArrayList<DipData> mList_dips_aux;
    //private List<DipData> dipslist = new ArrayList<>();

    private LatLng mStartpoint;

    public ViewPagerActivityPresenter (ViewPagerActivityContract.View dipsView, DipsService service)
    {
        this.mDipsView = dipsView;
        this.service = service;
        mStartpoint = new LatLng(42.598726, -5.567096);

    }

    public void initDataSet ()
    {

        service.getDips("mostrar_chapuzones").enqueue( new Callback<DipsList>()
        {
            @Override
            public void onResponse (Call<DipsList> call, Response<DipsList> response)
            {
                if ( response.isSuccessful() )
                {

                    //mList_dips_aux = Utils.orderListByDistance2(response.body().getDatos(),0, mStartpoint);
                    /*mList_dips_aux = Utils.orderListByDistance2(response.body().getDatos(),0, mStartpoint);
                    Log.i( "mList_dips_aux size: ", String.valueOf(mList_dips_aux.size()) );*/
                    mDipsView.setDips((response.body().getDatos()));
                    Timber.i( "Dips data was loaded from API." );
                }
            }

            @Override
            public void onFailure (Call<DipsList> call, Throwable t)
            {
                mDipsView.showErrorMessage("Unable to load the dips data from API.");
                Timber.e( t, "Unable to load the dips data from API." );
            }
        } );
    }

    public void initDataSet2(InputStream is) {
        String json = null;

        try {
            json = getJsonData(is);
        } catch (Exception e) {
            Timber.e("an exception occurred", e);
        }

        if (json != null) {
            ArrayList<DipData> dips = new Gson().fromJson(new StringReader(json), new TypeToken<List<DipData>>() {
            }.getType());

            mDipsView.setDips(dips);
        }
    }

    private String getJsonData(InputStream is) throws Exception {
        Writer writer = new StringWriter();
        try {
            Reader reader = new BufferedReader(new InputStreamReader(is, CHARSET_NAME));
            int n;
            char[] buffer = new char[1024];
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        } finally {
            if (is != null) {
                is.close();
            }
        }

        return writer.toString();
    }





}
