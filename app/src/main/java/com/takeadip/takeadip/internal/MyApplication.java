package com.takeadip.takeadip.internal;

import android.app.Application;
import android.content.Context;

import com.google.android.gms.maps.model.LatLng;
import com.takeadip.takeadip.R;
import com.takeadip.takeadip.model.Dip;

import java.util.ArrayList;

/**
 * Created by vik on 23/05/2017.
 */

public class MyApplication extends Application {

    private ArrayList<Dip> l_dips = new ArrayList<Dip>();
    private ArrayList<Dip> l_favourites = new ArrayList<Dip>();

    private LatLng startpoint = null;

    public static final String PF="PF"; //para piscinas naturales y playas fluviales
    public static final String PN="PN"; //para playas nudistas
    public static final String TE="TE"; //para termas
    public static final String PO="PO"; //para pozas
    public static final String PLAYASNUDISTAS = "Playa Nudista";
    public static final String PLAYAFLUVIAL = "Playa Fluvial/Piscina Natural";
    public static final String TERMAS = "Terma";
    public static final String POZAS = "Poza";

    public ArrayList<Dip> getL_dips() {
        return l_dips;
    }
    public ArrayList<Dip> getL_favourites() {
        return l_favourites;
    }

    public void setL_dips(ArrayList<Dip> l_dips) {
        this.l_dips = l_dips;
    }
    public void setL_favourites(ArrayList<Dip> l_favourites) {
        this.l_favourites = l_favourites;
    }
    public void setStartpoint (LatLng start)
    {
        startpoint = start;

    }
    public LatLng getStartpoint ()
    {
        return startpoint;
    }

/*    private String all = getResources().getString(R.string.all);
    private String river_beach = getResources().getString(R.string.river_beach);
    private String naturist_beach = getResources().getString(R.string.naturist_beach);
    private String river_lap = getResources().getString(R.string.river_lap);
    private String thermal = getResources().getString(R.string.thermal);
    public  String[]names = {all,river_beach,naturist_beach,river_lap,thermal};

    public String[] getNames() {
        return names;
    }*/

}
