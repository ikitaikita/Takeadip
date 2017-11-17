package com.takeadip.takeadip.internal;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.location.Location;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.takeadip.takeadip.R;
import com.takeadip.takeadip.data.model.DipsList;
import com.takeadip.takeadip.model.Dip;
import com.takeadip.takeadip.data.model.DipData;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * Created by vik on 23/05/2017.
 */

public class Utils {


    private Context context;

    public Utils(Context current){
        this.context = current;
    }

    /*private String all = context.getResources().getString(R.string.all);
    private String river_beach = context.getResources().getString(R.string.river_beach);
    private String naturist_beach = context.getResources().getString(R.string.naturist_beach);
    private String river_lap = context.getResources().getString(R.string.river_lap);
    private String thermal = context.getResources().getString(R.string.thermal);*/





    public static final int REQUEST_LOCATION = 0;
    public static String[] PERMISSIONS_LOCATION = {android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION};
    public static final String BASE_URL = "http://esunescandalo.com/chapuzones/";
    public static String[]names = {"All","River Beach & River Pool","Naturist Beach","River Lap","Thermal"};

    public static final String PF="PF"; //para piscinas naturales y playas fluviales
    public static final String PN="PN"; //para playas nudistas
    public static final String TE="TE"; //para termas
    public static final String PO="PO"; //para pozas
    public static final String PLAYASNUDISTAS = "Playa Nudista";
    public static final String PLAYAFLUVIAL = "Playa Fluvial/Piscina Natural";
    public static final String TERMAS = "Terma";
    public static final String POZAS = "Poza";
    //public static String[]names = {"All","PF","PN","P","TE"};


  /*  public static String[] getNames(Context context)
    {
        String[] return_names = null;


         String all = context.getResources().getString(R.string.all);
         String river_beach = context.getResources().getString(R.string.river_beach);
         String naturist_beach = context.getResources().getString(R.string.naturist_beach);
         String river_lap = context.getResources().getString(R.string.river_lap);
         String thermal = context.getResources().getString(R.string.thermal);
         String[]names = {all,river_beach,naturist_beach,river_lap,thermal};
        return return_names;

    }*/

    public static String getStringTypeDip(String typedip)
    {
        String return_string ="";
        switch (typedip)
        {
            case "River Beach & River Pool":
            return_string="PF";
            break;
            case "Naturist Beach":
                return_string="PN";
                break;
            case "River Lap":
                return_string="P";
                break;
            case "Thermal":
                return_string="TE";
                break;
            default:
                break;
        }
        return return_string;
    }

    public static String getTypeStringFromDip(String typedip)
    {
        String return_string ="";
        switch (typedip)
        {
            case "PF":
                return_string="River Beach & River Pool";

                break;
            case "PN":
                return_string="Naturist Beach";
                break;
            case "P":
                return_string="River Lap";
                break;
            case "TE":
                return_string="Thermal";
                break;
            default:
                break;
        }
        return return_string;
    }

    public static void showAlert(Context context, String title, String message)
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        if(!title.equals("")) alert.setTitle(title);
        alert.setMessage(message);
        alert.setCancelable(true);

        alert.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();

                    }
                });


        AlertDialog alertDialog = alert.create();
        alertDialog.show();
    }

    public static int getZoomLevel(Circle circle) {
        int zoomLevel=0;
        if (circle != null){
            double radius = circle.getRadius();
            double scale = radius / 500;
            zoomLevel =(int) (16 - Math.log(scale) / Math.log(2));
        }
        return zoomLevel;
    }

    public static Bitmap createDrawableFromView(View view) {

        view.setDrawingCacheEnabled(true);
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache(true);
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);

        return bitmap;
    }
    public static ArrayList<Dip> getDipsFromSpinner(String filter, ArrayList<Dip> l_dips)
    {
        ArrayList<Dip> l_filter = new ArrayList<Dip>();
        String new_stringFilter = Utils.getStringTypeDip(filter);

        for(Dip elem:l_dips)

        {
            Log.i("filter: ", elem.getType());
            if(elem.getType().equals(new_stringFilter))l_filter.add(elem);
        }
        return l_filter;
    }
    public static ArrayList<DipData> getDipsFromSpinner2(String filter, List<DipData> l_dips)
    {
        ArrayList<DipData> l_filter = new ArrayList<DipData>();
        String new_stringFilter = Utils.getStringTypeDip(filter);

        for(DipData elem:l_dips)

        {
            Log.i("filter: ", elem.getTipo());
            if(elem.getTipo().equals(new_stringFilter))l_filter.add(elem);
        }
        return l_filter;
    }

/*    public static Bitmap loadContactPhoto(ContentResolver cr, long  id) {
        Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, id);
        InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(cr, uri);
        if (input == null) {
            return null;
        }
        return BitmapFactory.decodeStream(input);
    }*/

    public static double calculationByDistanceKM(LatLng StartP, LatLng EndP) {
        int Radius = 6371;// radius Earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));
        Log.i("Radius Value", "" + valueResult + "   KM  " + kmInDec
                + " Meter   " + meterInDec);

        return Radius * c;
    }


    public static double calculationDistanceMetres()
    {
        Location selected_location=new Location("locationA");
        selected_location.setLatitude(17.372102);
        selected_location.setLongitude(78.484196);
        Location near_locations=new Location("locationA");
        near_locations.setLatitude(17.375775);
        near_locations.setLongitude(78.469218);

        double distance=selected_location.distanceTo(near_locations);
        return distance;
    }
    public static ArrayList<Dip> orderListByDistance(List<Dip> list_dips, double distance, LatLng startpoint)
    {
        Log.i("list_dips.size: " , String.valueOf(list_dips.size()));
        ArrayList<Dip> new_list = new ArrayList<Dip> ();
        //LatLng startpoint = new LatLng(latitude,longitude);
        Log.i("startpoint: " , startpoint.toString());
        LatLng endpoint;
        double newdistance;
        Iterator<Dip> it1 = list_dips.iterator();
        while (it1.hasNext()){
            Dip tmp = it1.next();
            endpoint = new LatLng(Double.parseDouble(tmp.getLatitude()),Double.parseDouble(tmp.getLongitude()));
            Log.i("startpoint: " , startpoint.toString());
            newdistance = Utils.calculationByDistanceKM(startpoint, endpoint);
            Log.i("newdistance: " , String.valueOf(newdistance));


            //Log.i(TAG, "orderListByDistance, distance: " +String.valueOf(newdistance));

            tmp.setDistance(Math.ceil(newdistance));
            new_list.add(tmp);
            Log.i("new item added: ", tmp.getName() + " " + String.valueOf(tmp.getDistance()));
        }
        Collections.sort(new_list, new Comparator<Dip>() {
            @Override
            public int compare(Dip d1, Dip d2) {
                return d1.getDistance().compareTo(d2.getDistance());
            }
        });

        return new_list;


    }
    public static ArrayList<DipData> orderListByDistance2(List<DipData> list_dips, double distance, LatLng startpoint)
    {
        //List<DipData> list  = list_dips.getDatos();
        Log.i("list_dips.size: " , String.valueOf(list_dips.size()));
        ArrayList<DipData> new_list = new ArrayList<DipData> ();
        //LatLng startpoint = new LatLng(latitude,longitude);
        Log.i("startpoint: " , startpoint.toString());
        LatLng endpoint;
        double newdistance;
        Iterator<DipData> it1 = list_dips.iterator();
        while (it1.hasNext()){
            DipData tmp = it1.next();
            endpoint = new LatLng(Double.parseDouble(tmp.getLatitud()),Double.parseDouble(tmp.getLongitud()));
            Log.i("startpoint: " , startpoint.toString());
            newdistance = Utils.calculationByDistanceKM(startpoint, endpoint);
            Log.i("newdistance: " , String.valueOf(newdistance));


            //Log.i(TAG, "orderListByDistance, distance: " +String.valueOf(newdistance));

            tmp.setDistance(Math.ceil(newdistance));
            new_list.add(tmp);
            Log.i("new item added: ", tmp.getNombre() + " " + String.valueOf(tmp.getDistance()));
        }
        Collections.sort(new_list, new Comparator<DipData>() {
            @Override
            public int compare(DipData d1, DipData d2) {
                return d1.getDistance().compareTo(d2.getDistance());
            }
        });

        return new_list;


    }
    public static ArrayList<Dip> orderListByProvince(ArrayList<Dip> list_dips)
    {

        Collections.sort(list_dips, new Comparator<Dip>() {
            @Override
            public int compare(Dip d1, Dip d2) {
                return d1.getProvince().compareTo(d2.getProvince());
            }
        });

        return list_dips;


    }

    public static ArrayList<DipData> orderListByProvince2(ArrayList<DipData> list_dips)
    {

        Collections.sort(list_dips, new Comparator<DipData>() {
            @Override
            public int compare(DipData d1, DipData d2) {
                return d1.getProvincia().compareTo(d2.getProvincia());
            }
        });

        return list_dips;


    }
}
