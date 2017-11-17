package com.takeadip.takeadip.ui;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.takeadip.takeadip.DipsService;
import com.takeadip.takeadip.data.model.DipData;
import com.takeadip.takeadip.data.model.DipsList;
import com.takeadip.takeadip.internal.Utils;
import com.takeadip.takeadip.ui.DipListContract;
import com.takeadip.takeadip.ui.OnDipClickListener;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Created by vik on 17/10/2017.
 */

public class DipListPresenter implements DipListContract.Presenter, OnDipClickListener {

    private DipListContract.View mDipsView;
    private DipListContract.Navigator navigator;



    private List<DipData>  mList_dips_aux;
    private List<DipData> dipslist = new ArrayList<>();

    private LatLng mStartpoint;


    @Override
    public void onAttachView(final DipListContract.View view) {
        this.mDipsView = view;

        // Usually this call goes asynchronous, but for this example it doesn't matter
        dipslist = view.getDips();
        view.displayDips(dipslist, this);
    }







    @Override
    public void onDetachView() {
        mDipsView = null;
        navigator = null;

    }

    @Override
    public void setNavigator(@NonNull DipListContract.Navigator navigator) {
        this.navigator = navigator;

    }


    @Override
    public void onDipClick(DipData dip) {
        //DipData dip = mList_dips_aux.get(position);
        navigator.openDip(dip);
    }

   /* @Override
    public void onDipLongClick(android.view.View v, int position) {
        DipData dip = mList_dips_aux.get(position);
        Log.i("Long Click","to add to favourite");
    }*/
}
