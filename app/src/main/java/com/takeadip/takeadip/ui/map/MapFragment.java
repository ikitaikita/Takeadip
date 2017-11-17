package com.takeadip.takeadip.ui.map;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.takeadip.takeadip.R;
import com.takeadip.takeadip.TabDipsFragment;
import com.takeadip.takeadip.TabMapFragment;
import com.takeadip.takeadip.data.model.DipData;
import com.takeadip.takeadip.internal.AccessInterface;
import com.takeadip.takeadip.internal.MyApplication;
import com.takeadip.takeadip.internal.Utils;
import com.takeadip.takeadip.model.Dip;
import com.takeadip.takeadip.utils.cluster.MarkerCluster;
import com.takeadip.takeadip.utils.cluster.MarkerClusterer;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by vik on 19/10/2017.
 */

public class MapFragment extends Fragment {

    public static final String TAG = "TabMapFragment";


    //private MapFragment fm;
    private double visiblerad = 0; //radius is changing
    private MapView mMapView;
    private GoogleMap mMap = null;
    private Bundle mBundle;

    private Map<Marker, DipData> allMarkersMap = new HashMap<Marker, DipData>();
    ArrayList<Marker> mMarkers;
    ArrayList<Marker> mClusterMarkers;
    float mCurrentZoom;
    private boolean buttonShowPressed = false;


    //myapplication
    private MyApplication application;
    private List<DipData> l_dips = new ArrayList<DipData>();

    //GPS

    CustomLocationListener customLocationListener = new CustomLocationListener();
    private Location m_DeviceLocation = null;
    private LocationManager mLocationManager;
    boolean gps_enabled = false;
    boolean network_enabled = false;
    private double latitude = 42.598726;
    private double longitude = -5.567096;




    //Spinner
    private boolean filtering = false;
    private Spinner sp_filter;
    private ArrayAdapter<String> adapter; // adapter for Spinner
    private List<DipData> l_dips_filter = new ArrayList<DipData>();
    String selection;
    //private Dip dip;
    //private String[]names = {"Todos","FP","PN","P","T"};


    public MapFragment() {
        // Required empty public constructor
    }
    public static MapFragment newInstance(ArrayList<DipData> diplist)
    {
        MapFragment myFragment = new MapFragment();
        //myFragment.l_dips = diplist;
        Bundle args = new Bundle();
        args.putSerializable("diplist", diplist);

        myFragment.setArguments(args);

        return myFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBundle = savedInstanceState;
        Log.e(TAG, "onCreate");
        //application = (MyApplication) getActivity().getApplicationContext();

        if (savedInstanceState != null) {
            l_dips = (ArrayList<DipData>)savedInstanceState.get("diplist");
            //mTitle = state.getString("mTitle");
        }
        mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        requestLocationPermission();
        //l_dips = application.getL_dips();

        Log.i("TAG " + TAG, "size l_dips:" + String.valueOf(l_dips.size()));
        /*StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);*/

        try {
            MapsInitializer.initialize(MapFragment.this.getActivity());


        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }

        /*if(application.getL_dips()!=null)
            if(application.getL_dips().size()==0)
                getDips();
            else l_dips = application.getL_dips();*/


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab_map, container, false);
        //loadList();


        if(m_DeviceLocation!=null)
        {
            Log.i(TAG, "tengo localizacion");

            latitude = m_DeviceLocation.getLatitude();
            longitude = m_DeviceLocation.getLongitude();


        }
        Log.i(TAG, "latitude: "+ String.valueOf(latitude));
        Log.i(TAG, "longitude: "+  String.valueOf(longitude));
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(MapFragment.this.getActivity());
        Log.i(TAG, "status: "+ String.valueOf(status));
        LatLng coordinate = new LatLng(latitude, longitude);

        Log.i("coordinate: ", coordinate.toString());

        mMarkers = new ArrayList<Marker>();
        mClusterMarkers = new ArrayList<Marker>();


        mMapView = (MapView) v.findViewById(R.id.map);
        // mMapView.onCreate(mBundle);
        mMapView.onCreate(savedInstanceState);
        mMap = mMapView.getMap();
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.setMyLocationEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinate,5));



        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL); //establecemos tipo de mapa

        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);

        mMap.setOnCameraChangeListener(
                new GoogleMap.OnCameraChangeListener() {
                    @Override
                    public void onCameraChange(
                            CameraPosition newPosition) {
                        // is clustered?
                        if (mCurrentZoom != newPosition.zoom) {
                            // create cluster markers for new position
                            Log.i(TAG, "getMaxZoomLevel: "+String.valueOf(mMap.getMaxZoomLevel()));
                            Log.i(TAG, "getMinZoomLevel(): "+ String.valueOf(mMap.getMinZoomLevel()));
                            recreateClusterMarkers();
                            // redraw map
                            redrawMap();
                        }
                        mCurrentZoom = newPosition.zoom;
                    }
                });
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            // Use default InfoWindow frame
            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            // Defines the contents of the InfoWindow
            @Override
            public View getInfoContents(Marker marker) {
                DipData p = null;

                // Getting view from the layout file info_window_layout
                View v = getActivity().getLayoutInflater().inflate(R.layout.mapwindowlayout, null);

                // Getting the position from the marker
                //LatLng latLng = marker.getPosition();

                // Getting reference to the ImageView to set picture of place
                //ImageView img_place = (ImageView)v.findViewById(R.id.img_place);




                // Getting reference to the TextView to set latitude
                TextView txt_nameplace = (TextView) v.findViewById(R.id.txt_nameplace);
                TextView txt_address = (TextView) v.findViewById(R.id.txt_address);
/*

                if(placeId != null)
                {
                    p = place_one;
                    //onepoint = false;
                }

                else*/
                if( marker.getSnippet()!= null)
                {
                    if(l_dips_filter.size()>0)p = l_dips_filter.get(Integer.parseInt(marker.getSnippet()));
                    else p = l_dips.get(Integer.parseInt(marker.getSnippet()));
                }
                if(p!=null)
                {

                    txt_nameplace.setText(p.getNombre());
                    txt_address.setText(p.getDireccion());



                }

                // Returning the view containing InfoWindow contents
                return v;

            }
        });
        //mMap.setOnInfoWindowClickListener(this);


        sp_filter = (Spinner) v.findViewById(R.id.sp_typedips);
        loadSpinnerFilter();
        setClickOnSpinner();
        //drawPointsOnMap();






        return v;
    }


    /**
     * Request the Location Permission
     */
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
                    ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Display an AlertDialog with an explanation and a button to trigger the request.
                new AlertDialog.Builder(getActivity())
                        .setMessage(getString(R.string.permission_location_explanation))
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                ActivityCompat
                                        .requestPermissions(getActivity(), Utils.PERMISSIONS_LOCATION,
                                                Utils.REQUEST_LOCATION);
                            }
                        }).show();
            } else {
                ActivityCompat.requestPermissions(getActivity(), Utils.PERMISSIONS_LOCATION,
                        Utils.REQUEST_LOCATION);
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



            //Location locAnte = distance_list[pos-1];


            mLocationManager.removeUpdates(this);
        }

        public void onProviderDisabled(String provider) {}

        public void onProviderEnabled(String provider) {}

        public void onStatusChanged(String provider,
                                    int status, Bundle extras) {}
    }


    private void loadSpinnerFilter()
    {
        //ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.recentsearches_array, android.R.layout.simple_spinner_item);
        //adapter=ArrayAdapter.createFromResource(getActivity(), R.array.filter_array, android.R.layout.simple_spinner_item);
        adapter =new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, Utils.names);
        adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);
        sp_filter.setAdapter(adapter);

    }
    private void setClickOnSpinner()
    {
        sp_filter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // TODO Auto-generated method stub


                //Toast.makeText(parent.getContext(), "OnItemSelectedListener : " + parent.getItemAtPosition(position).toString(),Toast.LENGTH_SHORT).show();

                selection =  parent.getItemAtPosition(position).toString();
                if(selection!="All")
                {
                    l_dips_filter.clear();
                    l_dips_filter = Utils.getDipsFromSpinner2(selection, l_dips);

                    drawPointsOnMapFromSpinner();

                }else
                {
                    l_dips_filter.clear();
                    mMap.clear();

                    drawPointsOnMap();
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
    private void recreateClusterMarkers() {
        // remove cluster markers from map
        for (Marker marker : mClusterMarkers) {
            marker.remove();
        }
        // clear cluster markers list
        mClusterMarkers.clear();
        // create mew cluster markers
        createClusterMarkers();
        Log.i(TAG, "number mClusterMarkers: " + String.valueOf(mClusterMarkers.size()));
    }
    private void createClusterMarkers() {

        if (mClusterMarkers.size() == 0) {
            // set cluster parameters
            //int gridSize = 100;
            int gridSize = mMarkers.size();
            Log.i(TAG,"gridSize: "+ String.valueOf(gridSize));
            boolean averageCenter = false;
            // create clusters
            Marker[] markers = mMarkers.toArray(new Marker[mMarkers.size()]);
            Log.i(TAG, "markers Size: "+ String.valueOf(markers.length));
            ArrayList<MarkerCluster> markerClusters = new MarkerClusterer(
                    mMap, markers, gridSize, averageCenter)
                    .createMarkerClusters();
            Log.i(TAG , "markerClusters Size: "+ String.valueOf(markerClusters.size()));

            // create cluster markers
            for (MarkerCluster cluster : markerClusters) {
                int markerCount = cluster.markers.size();
                if (markerCount == 1) {
                    mClusterMarkers.add(cluster.markers.get(0));
                } else {
                    // get marker view and set text
                    View markerView = getActivity().getLayoutInflater().inflate(
                            R.layout.cluster_marker_view, null);
                    ((TextView) markerView.findViewById(R.id.marker_count))
                            .setText(String.valueOf(markerCount));

                    // create cluster marker
                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(cluster.center)
                            .icon(BitmapDescriptorFactory
                                    .fromBitmap(Utils.createDrawableFromView(markerView)))
                            .visible(false);
                    Marker clusterMarker = mMap.addMarker(
                            markerOptions);
                    // add to list
                    mClusterMarkers.add(clusterMarker);
                }
            }
        }
    }


    private void drawPointsOnMap() {
        Log.i("TAG " + TAG, "drawPointsOnMap");


        //mMap.clear();
        //mMarkers.clear();
        //mClusterMarkers.clear();

        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude,longitude), 8));
        int i = 0;
        int iconoMapa=0;
        //ArrayList<Marker> markerlist = new ArrayList<Marker>();
        List<DipData> l_dips_topaint;
        if(filtering) l_dips_topaint = l_dips_filter;
        else l_dips_topaint = l_dips;
        Log.i(TAG, l_dips_topaint.size()  + String.valueOf(l_dips_topaint.size()));
        if (l_dips_topaint != null) {

            if(l_dips_topaint.size()>0)
            {
                for (final DipData p : l_dips_topaint) {
                    if(p != null)
                    {
                        if(p.getLatitud() != null && p.getLongitud() != null)
                        {
                            LatLng markerPosition = new LatLng (Double.parseDouble(p.getLatitud()) ,Double.parseDouble(p.getLongitud()));
                            Log.i(TAG, "markerPosition: " + markerPosition.toString());

                            //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerPosition,10));
                            String descrip ="";
                            //String id = p.getID();
                            //descrip = p.getGuid();

                            //adding the marker on the map
                            Marker marker = mMap.addMarker(new MarkerOptions()
                                    .position(markerPosition)
                                    .title(p.getNombre())
                                    .snippet(""+i)

                                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_map_marker_outline_black_24dp))

                                    .anchor(0.5f, 0.5f));
                            allMarkersMap.put(marker, p);
                            mMarkers.add(marker);
                            //markerlist.add(marker);
                            i++;


                        }

                    }



//							mMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener(){
//								@Override
//								 public void onInfoWindowClick(Marker marker) {
//									Place res = allMarkersMap.get(marker);
//									Intent intent = new Intent(getActivity(), MapPopupActivity.class);
//									//Store res = l_stores.get(Integer.parseInt(marker.getSnippet()));
//									//Store res = l_stores.get(Integer.parseInt(marker.getSnippet()));
//									intent.putExtra("store",res);
//									startActivity(intent);
//								 }
//							});

                }
                Log.i(TAG, "lat: " + String.valueOf(latitude));
                Log.i(TAG, "long: " + String.valueOf(longitude));

                Circle circle = mMap.addCircle(new CircleOptions().center(new LatLng(latitude, longitude)).radius(5000).strokeColor(Color.RED));

                circle.setVisible(false);
                Utils.getZoomLevel(circle);


                mCurrentZoom = mMap.getCameraPosition().zoom;
                //createClusterMarkers();

                // redrawMap();

//						LatLngBounds.Builder builder = new LatLngBounds.Builder();
//						for (Marker marker : markerlist) {
//						    builder.include(marker.getPosition());
//						}
//						int padding = 50;
//						LatLngBounds bounds = builder.build();
//						mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
            }



            //mMap.fitBounds(bounds);
        }

    }
    private void drawPointsOnMapFromSpinner() {
        Log.i(TAG, "drawPointsOnMapFromSpinner");

        mMap.clear();
        Log.i("mMarkers Sp before: ", String.valueOf(mMarkers.size()));
        Log.i("mClusterMarkers bef: ", String.valueOf(mClusterMarkers.size()));
        Log.i("allMarkersMap before: ", String.valueOf(mMarkers.size()));
        mMarkers.clear();
        mClusterMarkers.clear();
        allMarkersMap.clear();
        Log.i("mMarkers Spinner: ", String.valueOf(mMarkers.size()));
        Log.i("mClusterMarkers : ", String.valueOf(mClusterMarkers.size()));
        Log.i("allMarkersMap: ", String.valueOf(mMarkers.size()));
        //	mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude,longitude), 12));
        int i = 0;
        int iconoMapa=0;
        ArrayList<Marker> markerlist = new ArrayList<Marker>();


        if (l_dips_filter != null) {

            if(l_dips_filter.size()>0)
            {
                for (final DipData p : l_dips_filter) {
                    if(p != null)
                    {
                        if(p.getLatitud() != null && p.getLongitud() != null)
                        {
                            LatLng markerPosition = new LatLng (Double.parseDouble(p.getLatitud()) ,Double.parseDouble(p.getLongitud()));
                            Log.i("markerPosition: ", markerPosition.toString());
                            Log.i("place: ", p.getNombre());

                            //añade el marcador al mapa
                            Marker marker = mMap.addMarker(new MarkerOptions()
                                    .position(markerPosition)
                                    .title(p.getNombre())
                                    .snippet(""+i)

                                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_map_marker_outline_black_24dp))

                                    .anchor(0.5f, 0.5f));
                            allMarkersMap.put(marker, p);
                            mMarkers.add(marker);
                            markerlist.add(marker);
                            i++;


                        }

                    }



//							mMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener(){
//								@Override
//								 public void onInfoWindowClick(Marker marker) {
//									Place res = allMarkersMap.get(marker);
//									Intent intent = new Intent(getActivity(), MapPopupActivity.class);
//									//Store res = l_stores.get(Integer.parseInt(marker.getSnippet()));
//									//Store res = l_stores.get(Integer.parseInt(marker.getSnippet()));
//									intent.putExtra("store",res);
//									startActivity(intent);
//								 }
//							});

                }
//						Log.i("latitude in drwoPoint: ", String.valueOf(latitude));
//						Log.i("longitude in drwoPoint: ", String.valueOf(longitude));
//						if(!searching){
//							Circle circle = mMap.addCircle(new CircleOptions().center(new LatLng(latitude, longitude)).radius(5000).strokeColor(Color.RED));
//
//					        circle.setVisible(false);
//					        getZoomLevel(circle);
//						}
//
//				        mCurrentZoom = mMap.getCameraPosition().zoom;
//				        createClusterMarkers();
//
//						redrawMap();

               /* LatLngBounds.Builder builder = new LatLngBounds.Builder();
                for (Marker marker : markerlist) {
                    builder.include(marker.getPosition());
                }
                int padding = 50;
                LatLngBounds bounds = builder.build();
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));*/
            }



            //mMap.fitBounds(bounds);
        }

    }

    private void redrawMap() {

        // hide all markers
        for (Marker marker : mMarkers) {
            Log.i(TAG, "title marker mMarker: " + marker.getTitle());
            marker.setVisible(false);
        }
        for (Marker marker : mClusterMarkers) {
            marker.setVisible(true);
            Log.i(TAG, "title marker mClusterMarkers: " + marker.getTitle());

        }
        // show markers
//				for (Marker marker : mClusterMarkers) {
//					marker.setVisible(true);
//				}
//				if (mIsClustered) {
//					for (Marker marker : mClusterMarkers) {
//						marker.setVisible(true);
//					}
//				} else {
//					for (Marker marker : mMarkers) {
//						marker.setVisible(true);
//					}
//				}
    }






    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy");
        mMapView.onDestroy();
        super.onDestroy();
    }
    @Override
    public void onLowMemory() {
        Log.i(TAG, "onLowMemory");
        mMapView.onLowMemory();
        super.onLowMemory();
    }






}
