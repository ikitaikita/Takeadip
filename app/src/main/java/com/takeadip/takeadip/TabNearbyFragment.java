package com.takeadip.takeadip;


import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.takeadip.takeadip.internal.AccessInterface;
import com.takeadip.takeadip.utils.cluster.MarkerCluster;
import com.takeadip.takeadip.utils.cluster.MarkerClusterer;
import com.takeadip.takeadip.internal.MyApplication;
import com.takeadip.takeadip.internal.Utils;
import com.takeadip.takeadip.model.Dip;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class TabNearbyFragment extends Fragment implements GoogleMap.OnInfoWindowClickListener {

    public static final String TAG = "TabNearbyFragment";


    //private MapFragment fm;
    private double visiblerad = 0; //radius is changing
    private MapView mMapView;
    private GoogleMap mMap = null;
    private Bundle mBundle;

    private Map<Marker, Dip> allMarkersMap = new HashMap<Marker, Dip>();
    ArrayList<Marker> mMarkers;
    ArrayList<Marker> mClusterMarkers;
    float mCurrentZoom;
    private boolean buttonShowPressed = false;


    //myapplication
    private MyApplication application;
    private ArrayList<Dip> l_dips = new ArrayList<Dip>();

    //GPS
    private static final int REQUEST_LOCATION = 0;
    private static String[] PERMISSIONS_LOCATION = {Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION};
    CustomLocationListener customLocationListener = new CustomLocationListener();
    private Location m_DeviceLocation = null;
    private LocationManager mLocationManager;
    boolean gps_enabled = false;
    boolean network_enabled = false;
    private double latitude = 42.598726;
    private double longitude = -5.567096;

    //results
    public final int RESULT_DIPS_ERROR = -1;
    public final int RESULT_DIPS_OK = 1;


    private Handler handler = new Handler(new ResultMessageCallback());

    private String menserror = "";
    private ProgressDialog pDialogGetDips, pDialogGetDipsFromSpinner;


    //Spinner
    private boolean filtering = false;
    private Spinner sp_filter;
    private ArrayAdapter<String> adapter; // adapter for Spinner
    private List<Dip> l_dips_filter = new ArrayList<Dip>();
    String selection;
    private Dip dip;
    //private String[]names = {"Todos","FP","PN","P","T"};


    public TabNearbyFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBundle = savedInstanceState;
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            MapsInitializer.initialize(TabNearbyFragment.this.getActivity());


        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
        application = (MyApplication) getActivity().getApplicationContext();
        if(application.getL_dips()!=null)
            if(application.getL_dips().size()==0)
                getDips();
            else l_dips = application.getL_dips();
        Log.e(TAG, "onCreate");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab_nearby, container, false);

        mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        CustomLocationListener customLocationListener = new CustomLocationListener();

        //Request Permissions for Android 6.0
        requestLocationPermission();
        if(m_DeviceLocation!=null)
        {
            Log.i(TAG, "tengo localizacion");

            latitude = m_DeviceLocation.getLatitude();
            longitude = m_DeviceLocation.getLongitude();
        }
        Log.i(TAG, "latitude: "+ String.valueOf(latitude));
        Log.i(TAG, "longitude: "+  String.valueOf(longitude));
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(TabNearbyFragment.this.getActivity());
        Log.i(TAG, "status: "+ String.valueOf(status));

        mMarkers = new ArrayList<Marker>();
        mClusterMarkers = new ArrayList<Marker>();
        LatLng coordinate = new LatLng(latitude, longitude);

        Log.i("coordinate: ", coordinate.toString());

        mMapView = (MapView) v.findViewById(R.id.map);
        // mMapView.onCreate(mBundle);
        mMapView.onCreate(savedInstanceState);
        mMap = mMapView.getMap();
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.setMyLocationEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinate,15));


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
                Dip p = null;

                // Getting view from the layout file info_window_layout
                View v = getActivity().getLayoutInflater().inflate(R.layout.mapwindowlayout, null);

                // Getting the position from the marker
                LatLng latLng = marker.getPosition();

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

                    txt_nameplace.setText(p.getName());
                    txt_address.setText(p.getAddress());
                    //txt_numberbeans.setText(p.getRating());


                    /*Bitmap bmp;
                    try {
                        bmp = BitmapFactory.decodeStream(new java.net.URL(Constants.urlpicturebeanplaces + p.getPhotoreference()).openStream());
                        if(bmp!=null)
                        {
                            //img_photo.setImageBitmap(bmp);
                            img_place.setImageBitmap(bmp);
                            //img_photo.setImageDrawable(Utils.roundImageDrawable(bmp, getActivity().getResources()));
                        }
                        else img_place.setBackgroundResource(R.drawable.addphoto);
                    } catch (MalformedURLException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    } catch (IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }*/
                }

                // Returning the view containing InfoWindow contents
                return v;

            }
        });
        mMap.setOnInfoWindowClickListener(this);

        //getDips();
        sp_filter = (Spinner) v.findViewById(R.id.sp_typedips);
        loadSpinnerFilter();






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
        if ((ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
                || (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)) {

            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example, if the request has been denied previously.
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) ||
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
                    m_DeviceLocation = net_loc;
                } else if (net_loc != null) {
                    m_DeviceLocation = gps_loc;
                }
            }
            return;
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
                    if(selection!="All")
                    {
                        l_dips_filter.clear();
                        l_dips_filter = Utils.getDipsFromSpinner(selection, l_dips);

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
        Log.i(TAG, "drawPointsOnMap");


        //mMap.clear();
        //mMarkers.clear();
        //mClusterMarkers.clear();

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude,longitude), 12));
        int i = 0;
        int iconoMapa=0;
        ArrayList<Marker> markerlist = new ArrayList<Marker>();
        List<Dip> l_dips_topaint = new ArrayList<Dip>();
        if(filtering) l_dips_topaint = l_dips_filter;
        else l_dips_topaint = l_dips;
        Log.i(TAG, l_dips_topaint.size()  + String.valueOf(l_dips_topaint.size()));
        if (l_dips_topaint != null) {

            if(l_dips_topaint.size()>0)
            {
                for (final Dip p : l_dips_topaint) {
                    if(p != null)
                    {
                        if(p.getLatitude() != null && p.getLongitude() != null)
                        {
                            LatLng markerPosition = new LatLng (Double.parseDouble(p.getLatitude()) ,Double.parseDouble(p.getLongitude()));
                            Log.i(TAG, "markerPosition: " + markerPosition.toString());

                            //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerPosition,10));
                            String descrip ="";
                            //String id = p.getID();
                            //descrip = p.getGuid();

                            //adding the marker on the map
                            Marker marker = mMap.addMarker(new MarkerOptions()
                                    .position(markerPosition)
                                    .title(p.getName())
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
        int i = 0;
        //	mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude,longitude), 12));

        ArrayList<Marker> markerlist = new ArrayList<Marker>();


        if (l_dips_filter != null) {

            if(l_dips_filter.size()>0)
            {
                for (final Dip p : l_dips_filter) {
                    if(p != null)
                    {
                        if(p.getLatitude() != null && p.getLongitude() != null)
                        {
                            LatLng markerPosition = new LatLng (Double.parseDouble(p.getLatitude()) ,Double.parseDouble(p.getLongitude()));
                            Log.i("markerPosition: ", markerPosition.toString());


                            //añade el marcador al mapa
                            Marker marker = mMap.addMarker(new MarkerOptions()
                                    .position(markerPosition)
                                    .title(p.getName())
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

                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                for (Marker marker : markerlist) {
                    builder.include(marker.getPosition());
                }
                int padding = 50;
                LatLngBounds bounds = builder.build();
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
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
    public void onInfoWindowClick(Marker marker) {
        Log.i(TAG, "Click On InfoWindow");
        Toast toast1 = Toast.makeText(getActivity().getApplicationContext(),
                        "onInfoWindowClick", Toast.LENGTH_SHORT);

        toast1.show();
       /* Dip p = null;
        if(placeId != null)
        {
            p = place_one;
            //onepoint = false;
        }

        else
        if( marker.getSnippet()!= null)
        {

            if(filtering)p = l_dips_filter.get(Integer.parseInt(marker.getSnippet()));
            else p = l_dips.get(Integer.parseInt(marker.getSnippet()));
        }

        if(p!=null)
        {
            Log.i("p.getName(): ", p.getName());
            placeprofilefragment = new PlaceProfileFragment3();

            Bundle bundles = new Bundle();
            bundles.putSerializable("place",p);

            placeprofilefragment.setArguments(bundles);
//


            android.support.v4.app.FragmentTransaction ft =  getActivity().getSupportFragmentManager().beginTransaction();

            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

            //ft.replace(android.R.id.content, frag);
            ft.add(android.R.id.content,placeprofilefragment);
            ft.addToBackStack(getTag());
            ft.commit();
        }*/
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

    private class ResultMessageCallback implements Handler.Callback {

        public boolean handleMessage(Message arg0) {

            //if(pDialog != null)pDialog.dismiss();

            switch (arg0.what) {



                case RESULT_DIPS_OK:
                    if(pDialogGetDips != null)pDialogGetDips.dismiss();

                    Log.i(TAG,"RESULT_DIPS_OK");

                    if(l_dips.size()>0)
                    {
                        drawPointsOnMap();
                        application.setL_dips(l_dips);
                    }
                    else{
                        Utils.showAlert(getActivity(), "","No hay datos para mostrar");
                    }



                    break;
                case  RESULT_DIPS_ERROR:
                    if(pDialogGetDips != null)pDialogGetDips.dismiss();
                    Log.i(TAG,"RESULT_DIPS_ERROR");
                    Utils.showAlert(getActivity(), "",menserror);

                    break;





            }

            return true; // lo marcamos como procesado
        }
    }
  /*  private void getPhotoReference(Dip d)
    {

        Thread thread = new Thread(new GetPhotoDip(d));
        thread.start();
    }
*/
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
                            /*for(int i = 0; i<l_dips.size(); i++)
                            {
                                getPhotoReference(l_dips.get(i));
                            }*/
                }
                else
                {
                    mensajeDevuelto = RESULT_DIPS_ERROR;


                }





            handler.sendEmptyMessage(mensajeDevuelto);
        }
    }
  /*  private class GetPhotoDip implements Runnable {
        private Dip thisdip;

        private GetPhotoDip(Dip d)
        {
            this.thisdip = d;
        }



        public void run() {

            int mensajeDevuelto = RESULT_PHOTO_OK;


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
                for(int i = 0; i<l_dips.size(); i++)
                {
                    getPhotoReference(l_dips.get(i));
                }
            }
            else
            {
                mensajeDevuelto = RESULT_DIPS_ERROR;


            }





            handler.sendEmptyMessage(mensajeDevuelto);
        }
    }*/


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
        mMapView.onLowMemory();
        super.onLowMemory();
    }


}
