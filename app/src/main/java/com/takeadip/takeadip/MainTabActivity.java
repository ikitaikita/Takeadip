package com.takeadip.takeadip;

import android.*;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentPagerAdapter;

import android.support.v4.view.ViewPager;

import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ShareActionProvider;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.model.LatLng;
import com.takeadip.takeadip.internal.AccessInterface;
import com.takeadip.takeadip.internal.MyApplication;
import com.takeadip.takeadip.internal.Utils;
import com.takeadip.takeadip.model.Dip;
import com.takeadip.takeadip.persistence.PersistenceSQL;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

public class MainTabActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */


    //CharSequence Titles[]={"Nearby","Map","Dips"};
    //int Numboftabs =3;


    //This is our tablayout
    private TabLayout tabLayout;

    public static final String TAG = "MainTabActivity";

    //This is our viewPager
    private ViewPager viewPager;

    //myapplication
    private MyApplication application;
    private ArrayList<Dip> l_dips = new ArrayList<Dip>();
    private ArrayList<Dip> l_dips_aux = new ArrayList<Dip>();
    private ArrayList<Dip> l_favourites = new ArrayList<Dip>();



    //results
    public final int RESULT_DIPS_ERROR = -1;
    public final int RESULT_DIPS_OK = 1;


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
    private LatLng startpoint;

    private String menserror = "Error";
    private ProgressDialog pDialogGetDips, pDialogLocation;

    //share menu
    private ShareActionProvider mShareActionProvider;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        application = (MyApplication) this.getApplicationContext();

        /*mLocationManager = (LocationManager) MainTabActivity.this.getSystemService(Context.LOCATION_SERVICE);

        //CustomLocationListener customLocationListener = new CustomLocationListener();

        //Request Permissions for Android 6.0
        requestLocationPermission();


        if(m_DeviceLocation!=null)
        {
            if(pDialogLocation != null)pDialogLocation.dismiss();
            Log.i(TAG, "tengo localizacion");

            latitude = m_DeviceLocation.getLatitude();
            longitude = m_DeviceLocation.getLongitude();
            if(application.getL_favourites()!=null)
                if(application.getL_favourites().size()==0)
                    getFavourites();

            if(application.getL_dips()!=null)
                if(application.getL_dips().size()==0)
                    getDips();
                else
                {
                    l_dips = application.getL_dips();
                    l_dips_aux = l_dips;
                    l_dips.clear();
                    l_dips = Utils.orderListByDistance(l_dips_aux, 0, startpoint);
                    application.setL_dips(l_dips);
                }


        }
        else
        {
            Log.i(TAG, "waiting location");


            //finish();
        }
        Log.i(TAG, "latitude: "+ String.valueOf(latitude));
        Log.i(TAG, "longitude: "+  String.valueOf(longitude));
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(MainTabActivity.this);
        Log.i(TAG, "status google: "+  String.valueOf(status));
*/

        //Adding toolbar to the activity
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Initializing the tablayout
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        int tabLayoutWidth = tabLayout.getWidth();

        DisplayMetrics metrics = new DisplayMetrics();
        MainTabActivity.this.getWindowManager().getDefaultDisplay().getMetrics(metrics);


        //Adding the tabs using addTab() method
        tabLayout.addTab(tabLayout.newTab().setText("Nearby").setTag("Nearby"));

        tabLayout.addTab(tabLayout.newTab().setText("Map"));
        tabLayout.addTab(tabLayout.newTab().setText("Dips"));
        //tabLayout.addTab(tabLayout.newTab().setText("Favourites"));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.mipmap.ic_action_favorite_border));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager = (ViewPager) findViewById(R.id.pager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());




        //Adding adapter to pager
        viewPager.setAdapter(adapter);





        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                Log.i(TAG,"onTabSelected" + tab.getText() + tab.getPosition() );
                /*if(tab.getPosition() == 0)
                {

                    Bundle args = new Bundle();

                    // Colocamos el String
                    args.putDouble("lati",latitude);
                    args.putDouble("longi",longitude);
                    FragmentManager fm = getFragmentManager();
                    Fragment currentFragment =fm.findFragmentByTag("Nearby");
                    currentFragment.setArguments(args);
                }*/
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
               // viewPager.setCurrentItem(tab.getPosition());
                Log.i(TAG,"onTabReselected"+  tab.getText() + tab.getPosition() );

            }
        });

        tabLayout.setupWithViewPager(viewPager); // para que las pestañas respondan al swipe, es decir, que se actualicen al arrastrar con el dedo
        tabLayout.getTabAt(3).setIcon(R.mipmap.ic_action_favorite_border);




    }

    private void getFavourites()
    {
        l_favourites = PersistenceSQL.getFavourites(this);
        application.setL_favourites(l_favourites);

    }

    private void requestLocationPermission() {

        gps_enabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        network_enabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        Location net_loc = null, gps_loc = null;
        //Check, if we already have permission
        if ((ActivityCompat.checkSelfPermission(MainTabActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
                || (ActivityCompat.checkSelfPermission(MainTabActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)) {

            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example, if the request has been denied previously.
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainTabActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(MainTabActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Display an AlertDialog with an explanation and a button to trigger the request.
                new AlertDialog.Builder(MainTabActivity.this)
                        .setMessage(getString(R.string.permission_location_explanation))
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                ActivityCompat
                                        .requestPermissions(MainTabActivity.this, PERMISSIONS_LOCATION,
                                                REQUEST_LOCATION);
                            }
                        }).show();
            } else {
                ActivityCompat.requestPermissions(MainTabActivity.this, PERMISSIONS_LOCATION,
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
                    startpoint = new LatLng(latitude,longitude);
                    application.setStartpoint(startpoint);

                }

                else
                {
                    Log.i(TAG, "chosen Location: "+ "NETWORK");
                    m_DeviceLocation = net_loc;
                    startpoint = new LatLng(latitude,longitude);
                    application.setStartpoint(startpoint);
                }


                // I used this just to get an idea (if both avail, its upto you which you want to take as I taken location with more accuracy)

            } else {

                if (gps_loc != null) {
                    m_DeviceLocation = net_loc;
                } else if (net_loc != null) {
                    m_DeviceLocation = gps_loc;
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
            application.setStartpoint(startpoint);



            //Location locAnte = distance_list[pos-1];


            mLocationManager.removeUpdates(this);
        }

        public void onProviderDisabled(String provider) {}

        public void onProviderEnabled(String provider) {}

        public void onStatusChanged(String provider,
                                    int status, Bundle extras) {}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_tab, menu);




   /*     // Locate MenuItem with ShareActionProvider
        MenuItem item = menu.findItem(R.id.menu_item_share);

        // Fetch and store ShareActionProvider
        mShareActionProvider = (ShareActionProvider) item.getActionProvider();*/

        // Return true to display menu
        return true;
    }

   /* // Call to update the share intent
    private void setShareIntent(Intent shareIntent) {
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
       /* if (id == R.id.action_settings) {
            return true;
        }*/
        if (id == R.id.action_aboutMe) {
            aboutMe();
        }


        return super.onOptionsItemSelected(item);
    }

    public void aboutMe(){
        Intent intent = new Intent(this, AboutMe.class);
        this.startActivity(intent);
    }

    private void getDips()
    {
        pDialogGetDips = ProgressDialog.show(MainTabActivity.this, getString(R.string.info), getString(R.string.loading));
        Thread thread = new Thread(new GetAllDips());
        thread.start();
    }

    private class GetAllDips implements Runnable {





        public void run() {
            Log.i(TAG, "GetAllDips");

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
                double distance = 0;
                //l_dips_aux = l_dips;

                //l_dips.clear();

                l_dips_aux = Utils.orderListByDistance(l_dips,distance, startpoint);
                Log.i("ordered l_dips : ", String.valueOf(l_dips_aux.size()));

                l_dips = l_dips_aux;
                application.setL_dips(l_dips);
                Log.i("l_dips: ", String.valueOf(l_dips.size()));

            }
            else
            {
                mensajeDevuelto = RESULT_DIPS_ERROR;


            }





            handler.sendEmptyMessage(mensajeDevuelto);
        }
    }


    private class ResultMessageCallback implements Handler.Callback {

        public boolean handleMessage(Message arg0) {

            //if(pDialog != null)pDialog.dismiss();
            Log.i(TAG, "ResultMessageCallback");

            switch (arg0.what) {



                case RESULT_DIPS_OK:
                    if(pDialogGetDips != null)pDialogGetDips.dismiss();

                    Log.i(TAG,"RESULT_DIPS_OK");

                    if(l_dips.size()>0)
                    {
                        //drawPointsOnMap();




                    }
                    else{
                        Utils.showAlert(MainTabActivity.this, "","No hay datos para mostrar");
                    }



                    break;
                case  RESULT_DIPS_ERROR:
                    if(pDialogGetDips != null)pDialogGetDips.dismiss();
                    Log.i(TAG,"RESULT_DIPS_ERROR");
                    Utils.showAlert(MainTabActivity.this, "",menserror);

                    break;






            }

            return true; // lo marcamos como procesado
        }
    }


}
