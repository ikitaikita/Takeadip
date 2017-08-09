package com.takeadip.takeadip;


import android.*;
import android.Manifest;
import android.animation.Animator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.Pair;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.takeadip.takeadip.internal.AccessInterface;
import com.takeadip.takeadip.internal.ImageDownloaderTaskUser;
import com.takeadip.takeadip.internal.MyApplication;
import com.takeadip.takeadip.internal.Utils;
import com.takeadip.takeadip.model.Dip;
import com.takeadip.takeadip.persistence.PersistenceSQL;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class TabDipsFragment extends Fragment {

    private static final String TAG = "TabDipsFragment";


    private ListView lv_dips;
    //myapplication
    private MyApplication application;
    private ArrayList<Dip> l_dips = new ArrayList<Dip>();
    private ArrayList<Dip> l_favourites= new ArrayList<Dip>();


    //results
    public final int RESULT_DIPS_ERROR = -1;
    public final int RESULT_DIPS_OK = 1;
    public final int GET_SPINNER_OK = 2;
    public final int GET_SPINNER_ERROR = -2;

    private Handler handler = new Handler(new ResultMessageCallback());

    //GPS
    private static final int REQUEST_LOCATION = 0;
    private static String[] PERMISSIONS_LOCATION = {android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION};
    CustomLocationListener customLocationListener = new CustomLocationListener();
    private Location m_DeviceLocation = null;
    private LocationManager mLocationManager;
    boolean gps_enabled = false;
    boolean network_enabled = false;
    private double latitude = 42.598726;
    private double longitude = -5.567096;
    private LatLng startpoint = null;

    private String menserror = "";
    private ProgressDialog pDialogGetDips, pDialogGetDipsFromSpinner;


    //Spinner
    private boolean filtering = false;
    private Spinner sp_filter;
    private ArrayAdapter<String> adapter; // adapter for Spinner
    private ArrayList<Dip> l_dips_filter = new ArrayList<Dip>();
    String selection;
    private Dip dip;
    ///private String[]names = {"Todos","FP","PN","P","T"};



    //CHILD FRAGMENT
    Fragment detaildipfragment;

    //new
    private RelativeLayout layout;
    private MyRenderer selectedRenderer;

    public TabDipsFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //mBundle = savedInstanceState;
        application = (MyApplication) getActivity().getApplicationContext();
        mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        requestLocationPermission();
        //loadList();

        Log.e(TAG, "onCreate");

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab_dips,container,false);

       /* if(application.getL_dips()!=null)
            if(application.getL_dips().size()==0)
                getDips();
            else l_dips = application.getL_dips();*/




        if(m_DeviceLocation!=null)
        {

            Log.i(TAG, "tengo localizacion");

            latitude = m_DeviceLocation.getLatitude();
            longitude = m_DeviceLocation.getLongitude();
            /*if(application.getL_favourites()!=null)
                if(application.getL_favourites().size()==0)
                    getFavourites();*/

            if(application.getL_dips()!=null)
                if(application.getL_dips().size()==0)
                    getDips();
                else
                {
                    loadList();
                }


        }
        else
        {
            Utils.showAlert(getContext(), "info", "You need to configure your GPS signal");
            Log.i(TAG, "waiting location");


            //finish();
        }
        Log.i(TAG, "latitude: "+ String.valueOf(latitude));
        Log.i(TAG, "longitude: "+  String.valueOf(longitude));
       /* int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(MainTabActivity.this);
        Log.i(TAG, "status google: "+  String.valueOf(status));*/

        Log.i(TAG, "l_dips_size: " + String.valueOf(l_dips.size()));
        sp_filter = (Spinner) v.findViewById(R.id.sp_typedips);
        loadSpinnerFilter();


        layout = (RelativeLayout)v.findViewById(R.id.layout);
        lv_dips = (ListView)v.findViewById(R.id.list);

        lv_dips.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                int fixedpos = arg2;

                //FriendsProfileFragment friendsprofilefragment = new FriendsProfileFragment();
                detaildipfragment = new DetailDipFragment();
                //OwnProfileFragment frag = new OwnProfileFragment();

                Bundle bundles = new Bundle();
                Dip dip =  l_dips.get(fixedpos);

                // ensure your object has not null
                if (dip != null) {
                    bundles.putSerializable("dip", dip);
                    // Log.e("friend", "is valid");
                } else {
                    Log.e("dip", "is null");
                }
                detaildipfragment.setArguments(bundles);
                //getDarkerbackground to call childfragment

                  /*  fadeBackground = v.findViewById(R.id.fadeBackground);
                    fadeBackground.setVisibility(View.VISIBLE);
                    fadeBackground.animate().alpha(0.7f);*/
                //getDarkerbackground to call childfragment

                android.support.v4.app.FragmentTransaction ft =  getActivity().getSupportFragmentManager().beginTransaction();


                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

                //ft.replace(android.R.id.content, frag);
                ft.add(android.R.id.content,detaildipfragment);
                ft.addToBackStack(getTag());
                ft.commit();


            }
        });

        lv_dips.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final int pos =  position;
                Dip dip =  l_dips.get(pos);
                AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
                if(!PersistenceSQL.isFavourite(dip.getDip_id(), getContext()))
                {
                    builder1.setMessage(getResources().getString(R.string.addfavourites));
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    int fixedpos = pos;
                                    Dip dip =  l_dips.get(fixedpos);
                                    addTofavourites(dip, getContext());
                                    lv_dips.setAdapter(new Adapter(getActivity(),R.layout.list_itemdiplist, l_dips));
                                    dialog.cancel();
                                }
                            });

                    builder1.setNegativeButton(
                            "No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                }else
                {
                    builder1.setMessage(getResources().getString(R.string.removefavourites));
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    int fixedpos = pos;
                                    Dip dip =  l_dips.get(fixedpos);
                                    removeFromfavourites(dip);
                                    lv_dips.setAdapter(new Adapter(getActivity(),R.layout.list_itemdiplist, l_dips));
                                    dialog.cancel();
                                }
                            });

                    builder1.setNegativeButton(
                            "No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                }



                AlertDialog alert11 = builder1.create();
                alert11.show();


                return true;
            }
        });





      /*  lv_dips.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // find out where the clicked view sits in relationship to the
                // parent container
                int fixedpos = position;
                Dip dip =  l_dips.get(fixedpos);

                int t = view.getTop() + lv_dips.getTop();
                int l = view.getLeft() + lv_dips.getLeft();

                // create a copy of the listview and add it to the parent
                // container
                // at the same location it was in the listview
                selectedRenderer = new MyRenderer(getContext(),dip);
                RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(view.getWidth(), view
                        .getHeight());
                rlp.topMargin = t;
                rlp.leftMargin = l;



                layout.addView(selectedRenderer, rlp);
                view.setVisibility(View.INVISIBLE);

                // animate out the listView
                Animation outAni = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f,
                        Animation.RELATIVE_TO_SELF, -1f, Animation.RELATIVE_TO_SELF, 0f,
                        Animation.RELATIVE_TO_SELF, 0f);
                outAni.setDuration(1000);
                outAni.setFillAfter(true);
                outAni.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                        ScaleAnimation scaleAni = new ScaleAnimation(1f,
                                1f, 1f, 2f,
                                Animation.RELATIVE_TO_SELF, 0.5f,
                                Animation.RELATIVE_TO_SELF, 0.5f);
                        scaleAni.setDuration(400);
                        scaleAni.setFillAfter(true);


                        selectedRenderer.startAnimation(scaleAni);
                        selectedRenderer.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                selectedRenderer.clearAnimation();
                                lv_dips.clearAnimation();
                                layout.removeView(selectedRenderer);
                                //selectedRenderer.setVisibility(View.INVISIBLE);
                                lv_dips.setAdapter(new MyAdapter(getActivity(),R.layout.list_itemdiplist, l_dips));

                            }
                        });


                    }
                });

                lv_dips.startAnimation(outAni);
            }

        });*/

        //l_favourites = application.getL_favourites();
        return v;
    }
    private void getDips()
    {
        pDialogGetDips = ProgressDialog.show(getActivity(), getString(R.string.info), getString(R.string.loading));
        Thread thread = new Thread(new GetAllDips());
        thread.start();
    }
    private class GetAllDips implements Runnable {



        public void run() {

            int mensajeDevuelto = RESULT_DIPS_OK;


            JSONObject jsonresponse = null;

            //String url = "places/"+latitude+"/"+longitude+"?"+"token="+ application.getTokenapp();
            String url = AccessInterface.URL_GETALLDIPS;
            jsonresponse = AccessInterface.sendJSON(url, null, "GET");
            if(jsonresponse!=null)
            {
                //  if(jsonresponse.has("code")){

                //String code = jsonresponse.getString("code");


                l_dips = AccessInterface.getListDips(jsonresponse);

                Log.i(TAG, "l_dips.size: "+ String.valueOf(l_dips.size()));
            }
            else
            {
                mensajeDevuelto = RESULT_DIPS_ERROR;


            }





            handler.sendEmptyMessage(mensajeDevuelto);
        }
    }

    private void requestLocationPermission() {

        gps_enabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        network_enabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        Location net_loc = null, gps_loc = null;
        //Check, if we already have permission
        if ((ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
                || (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)) {

            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example, if the request has been denied previously.
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Display an AlertDialog with an explanation and a button to trigger the request.
                new AlertDialog.Builder(getActivity())
                        .setMessage(getString(R.string.permission_location_explanation))
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                ActivityCompat
                                        .requestPermissions(getActivity(), PERMISSIONS_LOCATION,
                                                REQUEST_LOCATION);
                            }
                        }).show();
            } else {
                ActivityCompat.requestPermissions(getActivity(), PERMISSIONS_LOCATION,
                        REQUEST_LOCATION);
            }
        } else {
            //We already got the permissions, to proceed normally
            //Only proceed to start the App, if initialization is finished
            if (gps_enabled) {

                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 0, customLocationListener);
                gps_loc = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }

            if (network_enabled)
            {
                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 0, customLocationListener);
                net_loc = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
            if (gps_loc != null && net_loc != null) {

                if (gps_loc.getAccuracy() >= net_loc.getAccuracy())
                {
                    Log.i(TAG, "chosen Location: "+ "GPS");
                    m_DeviceLocation = gps_loc;
                }

                else
                {
                    Log.i(TAG, "chosen Location: "+ "NETWORK");
                    m_DeviceLocation = net_loc;

                }


                // I used this just to get an idea (if both avail, its upto you which you want to take as I taken location with more accuracy)

            } else {

                if (gps_loc != null) {
                    m_DeviceLocation = gps_loc;
                } else if (net_loc != null) {
                    m_DeviceLocation = net_loc;
                }
            }
            return;
        }
    }

    private class CustomLocationListener implements LocationListener {

        public void onLocationChanged(Location argLocation) {
            Log.i("++++++++++","CustomLocationListener");
            m_DeviceLocation = argLocation;
            latitude = m_DeviceLocation.getLatitude();
            longitude = m_DeviceLocation.getLongitude();

            startpoint = new LatLng(latitude,longitude);



            //Location locAnte = distance_list[pos-1];


            mLocationManager.removeUpdates(this);
        }

        public void onProviderDisabled(String provider) {}

        public void onProviderEnabled(String provider) {}

        public void onStatusChanged(String provider,
                                    int status, Bundle extras) {}
    }

    private void addTofavourites(Dip dip, Context context)
    {
        PersistenceSQL.insertDip(context, dip);
        Utils.showAlert(context, "", "favourite dip already added");
        l_favourites = PersistenceSQL.getFavourites(getContext());
        application.setL_favourites(l_favourites);

        /*l_favourites = application.getL_favourites();
        if(!l_favourites.contains(dip))l_favourites.add(dip);
        application.setL_favourites(l_favourites);*/

    }
    private void removeFromfavourites(Dip dip)
    {
        PersistenceSQL.deleteDip(dip.getDip_id(), getContext());
        Utils.showAlert(getContext(), "", "dip already deleted");
        l_favourites = PersistenceSQL.getFavourites(getContext());
        application.setL_favourites(l_favourites);




    }
    /* public class MyRenderer extends RelativeLayout {

         public TextView textdipView, typedipView;

         public ImageView imageView;

        public MyRenderer(Context context, Dip dip) {
             super(context);
             setPadding(40, 40, 40, 40);
             //setBackgroundColor(0xFFFF0000);


             RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(
                     RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
             rlp.addRule(CENTER_IN_PARENT);

             textdipView = new TextView(context);
             typedipView = new TextView(context);
             textdipView.setText(dip.getName());
             typedipView.setText(dip.getType());

             addView(textdipView, rlp);

             addView(typedipView, rlp);
             rlp.addRule(RelativeLayout.BELOW, typedipView.getId());

         }

     }*/
    public class MyRenderer extends LinearLayout {

        private Dip mydip ;
        public TextView textdipView, typedipView, descdipView;


        public ImageView imageView;

        public MyRenderer(Context context, Dip dip) {

            super(context);
            mydip = dip;
            setPadding(10, 10, 10, 10);

            //setBackgroundColor(0xFFFF0000);
            LinearLayout.LayoutParams rlp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            setOrientation(LinearLayout.VERTICAL);



            String sringtypedip =Utils.getTypeStringFromDip(dip.getType());

            textdipView = new TextView(context);
            typedipView = new TextView(context);
            descdipView = new TextView(context);
            imageView = new ImageView(context);
            textdipView.setText(dip.getName());
            textdipView.setTextColor(getResources().getColor(R.color.colorPrimary));
            typedipView.setText(sringtypedip);
            //typedipView.setText(dip.getType());
            typedipView.setTextColor(getResources().getColor(R.color.colorAccent));
            descdipView.setText(dip.getDescription());
            descdipView.setTextColor(getResources().getColor(R.color.colorPrimaryDark));


            addView(textdipView, rlp);
            addView(typedipView, rlp);


            //new ImageDownloaderTaskUser(imageView).execute(AccessInterface.URL_GETPHOTO );
            //addView(imageView, rlp);


        }

    }


    private void loadSpinnerFilter()
    {
        //ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.recentsearches_array, android.R.layout.simple_spinner_item);
        //adapter=ArrayAdapter.createFromResource(getActivity(), R.array.filter_array, android.R.layout.simple_spinner_item);
        adapter =new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, Utils.names);
        adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);
        sp_filter.setAdapter(adapter);

        sp_filter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // TODO Auto-generated method stub


                //Toast.makeText(parent.getContext(), "OnItemSelectedListener : " + parent.getItemAtPosition(position).toString(),Toast.LENGTH_SHORT).show();

                selection =  parent.getItemAtPosition(position).toString();
                if(selection != "All")
                {
                    Log.i("selection: ",selection);
                    l_dips_filter.clear();
                    l_dips_filter = Utils.getDipsFromSpinner(selection, l_dips);
                    lv_dips.setAdapter(new Adapter(getActivity(),R.layout.list_itemdiplist, l_dips_filter));

                }else
                {

                    lv_dips.setAdapter(new Adapter(getActivity(),R.layout.list_itemdiplist, l_dips));
                }


            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TOO Auto-generated method stub
                Toast toast1 =Toast.makeText(getActivity(),"no selection", Toast.LENGTH_SHORT);
                toast1.show();
            }

        });


    }

    private class LoadListAsyncTask extends AsyncTask<Void, Void, List<Dip>> {

        @Override
        protected void onPreExecute() {
            // start loading animation maybe?
            //adapter.clear(); // clear "old" entries (optional)
        }

        @Override
        protected List<Dip> doInBackground(Void... params) {
            // everything in here gets executed in a separate thread
            l_dips =  application.getL_dips();

            Collections.sort(l_dips, new Comparator<Dip>() {
                @Override
                public int compare(Dip d1, Dip d2) {
                    // Aqui esta el truco, ahora comparamos p2 con p1 y no al reves como antes
                    return d1.getProvince().compareTo(d2.getProvince());                }
            });
            return l_dips;
        }

        @Override
        protected void onPostExecute(List<Dip> items) {
            // stop the loading animation or something
            lv_dips.setAdapter(new Adapter(getActivity(),R.layout.list_itemdiplist, l_dips));
        }
    }
   /* private void getDips()
    {
        pDialogGetDips = ProgressDialog.show(getActivity(), getString(R.string.info), getString(R.string.loading));
        Thread thread = new Thread(new GetAllDips());
        thread.start();
    }*/

    private void loadList()
    {
        LoadListAsyncTask gfl = new LoadListAsyncTask();
        gfl.execute();
    }
/*    private class GetAllDips implements Runnable {



        public void run() {

            int mensajeDevuelto = RESULT_DIPS_OK;


            JSONObject jsonresponse = null;

            //String url = "places/"+latitude+"/"+longitude+"?"+"token="+ application.getTokenapp();
            String url = AccessInterface.URL_GETALLDIPS;
            jsonresponse = AccessInterface.sendJSON(url, null, "GET");
            if(jsonresponse!=null)
            {
                //  if(jsonresponse.has("code")){

                //String code = jsonresponse.getString("code");


                l_dips = AccessInterface.getListDips(jsonresponse);
                Log.i(TAG, "l_dips.size: "+ String.valueOf(l_dips.size()));
            }
            else
            {
                mensajeDevuelto = RESULT_DIPS_ERROR;


            }





            handler.sendEmptyMessage(mensajeDevuelto);
        }
    }*/

    private class ResultMessageCallback implements Handler.Callback {

        public boolean handleMessage(android.os.Message arg0) {



            switch (arg0.what) {


                case RESULT_DIPS_OK:

                    if(pDialogGetDips != null)pDialogGetDips.dismiss();
                    if(l_dips.size()>0)
                    {
                        application.setL_dips(l_dips);

                        //lv_dips.setAdapter(new Adapter(getActivity(),R.layout.list_itemdiplist, l_dips));
                        lv_dips.setAdapter(new Adapter(getActivity(),R.layout.list_itemdiplist, l_dips));
                        //Toast.makeText(getActivity(),"Success", Toast.LENGTH_LONG)	.show();
                    }
                    //Toast.makeText(LoginActivity.this,"Success", Toast.LENGTH_LONG)	.show();

                    else Utils.showAlert(getActivity(), "","No Dips to show");



                    break;
                case  RESULT_DIPS_ERROR:
                    Utils.showAlert(getActivity(), "","error downloading dips");

                    break;

                case GET_SPINNER_OK:

                    Utils.showAlert(getActivity(), "", "Spinner OK");


                    //lv_dips.setAdapter(new Adapter(getActivity(),R.layout.list_itemdiplist, l_dips).notifyDataSetChanged(););


                    //Toast.makeText(getActivity(),"Success", Toast.LENGTH_LONG)	.show();

                    break;
                case  GET_SPINNER_ERROR:
                    Utils.showAlert(getActivity(), "","error");

                    break;


            }

            return true; // lo marcamos como procesado
        }

    }

   /* public class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return 10;
        }

        @Override
        public String getItem(int position) {
            return "Hello World " + position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            MyRenderer renderer;
            if (convertView != null)
                renderer = (MyRenderer) convertView;
            else
                renderer = new MyRenderer(getActivity());
            renderer.textView.setText(getItem(position));
            return renderer;
        }
    }*/
/*
    public class MyAdapter extends ArrayAdapter<Dip>  {

        private ArrayList<Dip> items;
        //private DecimalFormat df = new DecimalFormat("0.00");

        public MyAdapter(Context context, int textViewResourceId,
                       List<Dip> items) {
            super(context, textViewResourceId, items);
            this.items = (ArrayList<Dip>) items;

        }
        *//*@Override
        public int getCount() {
            return 10;
        }*//*



        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;

            if (v == null) {
                LayoutInflater vi = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.list_itemdiplist, null);
            }

            final Dip p = items.get(position);


            if (p != null) {





                final TextView txt_nameplace = (TextView) v.findViewById(R.id.txt_nameplace);

                //final TextView txt_address = (TextView) v.findViewById(R.id.txt_address);
                final ImageView img_photo = (ImageView)v.findViewById(R.id.img_photo);

                final ImageView img_fav = (ImageView) v.findViewById(R.id.img_fav);


                String name = p.getName()+ ", ";
                String address = p.getProvince();
                Log.i("province: ", address);


                int posstartname = 0;
                int posendname = name.length();
                //Log.i("posendname: ", String.valueOf(posendname));
                //int length_hasbean = hasbeanplace.length();

                Spannable wordtoSpan = new SpannableString(name +  address);
                wordtoSpan.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorPrimary)), posstartname, posendname, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                wordtoSpan.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorAccent)), posendname, wordtoSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                txt_nameplace.setText(wordtoSpan);
                //txt_nameplace.setText(name);
                //txt_address.setText(address);

                //txt_address.setText(p.getAddress());
                if(PersistenceSQL.isFavourite(p.getDip_id(), getContext()))
                {
                    img_fav.setVisibility(View.VISIBLE);
                }else img_fav.setVisibility(View.INVISIBLE);



            }



            return v;
        }
    }*/
    private class Adapter extends ArrayAdapter<Dip> {

        private ArrayList<Dip> items;
        //private DecimalFormat df = new DecimalFormat("0.00");

        public Adapter(Context context, int textViewResourceId,
                       List<Dip> items) {
            super(context, textViewResourceId, items);
            this.items = (ArrayList<Dip>) items;

        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;

            if (v == null) {
                LayoutInflater vi = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.list_itemdiplist, null);
            }

            final Dip p = items.get(position);


            if (p != null) {



                final TextView txt_nameplace = (TextView) v.findViewById(R.id.txt_nameplace);

                //final TextView txt_address = (TextView) v.findViewById(R.id.txt_address);
                //final ImageView img_photo = (ImageView)v.findViewById(R.id.img_photo);
                final ImageView img_fav = (ImageView) v.findViewById(R.id.img_fav);


                String name = p.getName()+ ", ";
                String address = p.getProvince();



                int posstartname = 0;
                int posendname = name.length();
                //Log.i("posendname: ", String.valueOf(posendname));
                //int length_hasbean = hasbeanplace.length();

                Spannable wordtoSpan = new SpannableString(name +  address);
                wordtoSpan.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorPrimary)), posstartname, posendname, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                wordtoSpan.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorAccent)), posendname, wordtoSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                txt_nameplace.setText(wordtoSpan);
                //txt_nameplace.setText(name);
                //txt_address.setText(address);

                //txt_address.setText(p.getAddress());

              /*  if (img_photo != null) {

                    if(p.getPhotoreference()!= null)
                    {
                        Bitmap bmp;
                        try {
                            Log.i("p.getPhotoreference(): ", p.getPhotoreference());
                            bmp = BitmapFactory.decodeStream(new java.net.URL(Constants.urlpicturebeanplaces + p.getPhotoreference()).openStream());
                            if(bmp!=null)img_photo.setImageBitmap(bmp);
                            else
                            {
                                bmp =  BitmapFactory.decodeStream(new java.net.URL(p.getPicture()).openStream());
                                if(bmp!=null)img_photo.setImageBitmap(bmp);
                                else img_photo.setBackgroundResource(R.drawable.addphoto);
                            }
                        } catch (MalformedURLException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        } catch (IOException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }

                    }else img_photo.setBackgroundResource(R.drawable.addphoto);
                }*/

                if(PersistenceSQL.isFavourite(p.getDip_id(), getContext()))
                {
                    img_fav.setVisibility(View.VISIBLE);
                }else img_fav.setVisibility(View.INVISIBLE);


            }

            return v;

        }

    }

    @Override
    public void onDestroyView() {

        try{
            FragmentTransaction ft =  getActivity().getSupportFragmentManager().beginTransaction();
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
            if (detaildipfragment.isAdded())
            {
                Log.i("detaildipfragment", "isAdded");
                ft.hide(detaildipfragment);


            }

            ft.commit();
//				        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
//
//				        transaction.remove(friendsprofilefragment);
//
//				        transaction.commit();
        }catch(Exception e){
            Log.e("error: ", e.toString());
        }

        super.onDestroyView();
     /*   fadeBackground.animate().alpha(0.0f).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                // As soon as the animation is finished we set the visiblity again back to GONE
                fadeBackground.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });*/
    }

}
