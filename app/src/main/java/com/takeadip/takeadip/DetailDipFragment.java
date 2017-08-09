package com.takeadip.takeadip;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.LikeView;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.takeadip.takeadip.internal.AccessInterface;
import com.takeadip.takeadip.internal.DirectionsJSONParser;
import com.takeadip.takeadip.internal.MyApplication;
import com.takeadip.takeadip.internal.Utils;
import com.takeadip.takeadip.model.Dip;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.view.View.OnClickListener;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailDipFragment extends Fragment implements OnClickListener {
    private static final String TAG = "DetailDipFragment";

    private Dip dip = null;
    private MyApplication application;

    private TextView txt_dip;
    private TextView txt_typedip;
    Button btn_fab, btn_share, btn_facebook;



    private MapView mMapView;
    private GoogleMap mMap = null;

    //layDD
    private RelativeLayout lay_dd;
    private TextView txt_distance_val;
    private TextView txt_duration_val;
    //private List<HashMap<String, String>> ldd ;
    String distance="";
    String duration="";

    //private ImageView img_photo;
    //private TextView txt_desc;




    private double latitude = 42.598726;
    private double longitude = -5.567096;


    public DetailDipFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            MapsInitializer.initialize(DetailDipFragment.this.getActivity());


        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
        Log.e(TAG, "onCreate");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail_dip, container, false);
        application = (MyApplication)getActivity(). getApplicationContext();

        Bundle bundle = getArguments();
        if(bundle != null)
        {
            dip= (Dip) bundle.getSerializable("dip");

        }
        /*requestLocationPermission();

        if(m_DeviceLocation!=null)
        {
            Log.i(TAG, "tengo localizacion");

            latitude = m_DeviceLocation.getLatitude();
            longitude = m_DeviceLocation.getLongitude();
        }*/

       /* LikeView likeView = (LikeView)rootView.findViewById(R.id.like_view);
        likeView.setObjectIdAndType(
                "https://www.facebook.com/FacebookDevelopers",
                LikeView.ObjectType.PAGE);*/

     /*   LikeView likeView = (LikeView) rootView.findViewById(R.id.likeView);
        likeView.setLikeViewStyle(LikeView.Style.STANDARD);
        likeView.setAuxiliaryViewPosition(LikeView.AuxiliaryViewPosition.INLINE);
        likeView.setObjectIdAndType(
                "http://inthecheesefactory.com/blog/understand-android-activity-launchmode/en",
                LikeView.ObjectType.OPEN_GRAPH);*/


        btn_fab = (Button)rootView. findViewById(R.id.btn_fab);
        btn_fab.setOnClickListener(this);
        btn_share =(Button) rootView. findViewById(R.id.btn_share);
        btn_share.setOnClickListener(this);
        btn_facebook = (Button)rootView. findViewById(R.id.btn_facebook);
        btn_facebook.setOnClickListener(this);
        txt_dip = (TextView)rootView.findViewById(R.id.txt_dip);

        txt_typedip = (TextView)rootView.findViewById(R.id.txt_typedip);
        //layDD
        lay_dd = (RelativeLayout)rootView.findViewById(R.id.lay_dd);
        txt_duration_val = (TextView) rootView.findViewById(R.id.txt_duration_val);
        txt_distance_val = (TextView) rootView.findViewById(R.id.txt_distance_val);
        //img_photo = (ImageView)rootView.findViewById(R.id.img_photo);
        mMapView = (MapView) rootView.findViewById(R.id.map);
        // mMapView.onCreate(mBundle);
        mMapView.onCreate(savedInstanceState);
        mMap = mMapView.getMap();
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.setMyLocationEnabled(true);
        //mMap.setMyLocationEnabled(true);




        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL); //establecemos tipo de mapa

        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);




        if(dip != null)

        {

            txt_dip.setText(dip.getName());
            String type_string = Utils.getTypeStringFromDip(dip.getType());
            Log.i("555555: ", type_string);
            txt_typedip.setText(type_string);
            LatLng coordinate = new LatLng(Double.valueOf(dip.getLatitude()),Double.valueOf( dip.getLongitude()));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinate,12));
            drawOnePointOnMap();


            //new ImageDownloaderTaskUser(img_photo).execute(AccessInterface.URL_GETPHOTO );

        }

        return rootView;
    }




    private void drawOnePointOnMap() {
        Log.i("", "drawOnePointOnMap");


        if(dip.getLatitude() != null && dip.getLongitude() != null)
        {
            LatLng markerPosition = new LatLng (Double.parseDouble(dip.getLatitude()) ,Double.parseDouble(dip.getLongitude()));
            Log.i("markerPosition: ", markerPosition.toString());

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerPosition,15));
            String descrip ="";
            //String id = p.getID();
            //descrip = p.getGuid();

            //añade el marcador al mapa
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(markerPosition)
                    .title(dip.getName())
                    .snippet(dip.getAddress())
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_map_marker_outline_black_24dp))

                    .anchor(0.5f, 0.5f));

            marker.showInfoWindow();
        }





//						mMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener(){
//							@Override
//							 public void onInfoWindowClick(Marker marker) {
//								Place res = allMarkersMap.get(marker);
//								Intent intent = new Intent(getActivity(), MapPopupActivity.class);
//								//Store res = l_stores.get(Integer.parseInt(marker.getSnippet()));
//								//Store res = l_stores.get(Integer.parseInt(marker.getSnippet()));
//								intent.putExtra("store",res);
//								startActivity(intent);
//							 }
//						});


    }

    class ImageDownloaderTaskUser extends AsyncTask<String, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewReference;

        public ImageDownloaderTaskUser(ImageView imageView) {
            imageViewReference = new WeakReference<ImageView>(imageView);
        }

        @Override
        protected Bitmap doInBackground(String... params) {

            return downloadBitmapUser(params[0]);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (isCancelled()) {
                bitmap = null;
            }

            if (imageViewReference != null) {
                ImageView imageView = imageViewReference.get();
                if (imageView != null) {
                    if (bitmap != null) {
                        imageView.setImageBitmap(bitmap);

                        //imageView.setImageDrawable(Utils.roundImageDrawable(bitmap, getActivity().getResources()));

                    } else {
                        Drawable placeholder = imageView.getContext().getResources().getDrawable(R.drawable.comodin);
                        imageView.setImageDrawable(placeholder);
                    }
                }
            }
        }
    }
    private Bitmap downloadBitmapUser(String url) {
        HttpURLConnection urlConnection = null;
        try {
            URL uri = new URL(url);
            urlConnection = (HttpURLConnection) uri.openConnection();
            int statusCode = urlConnection.getResponseCode();
            if (statusCode != 200) {
                return null;
            }

            InputStream inputStream = urlConnection.getInputStream();
            if (inputStream != null) {
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                //Bitmap bitmap2 = Utils.getRoundedShape(bitmap);
                return bitmap;
            }
        } catch (Exception e) {
            urlConnection.disconnect();
            Log.w("ImageDownloader", "Error downloading image from " + url);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return null;
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
        mMapView.onLowMemory();
        super.onLowMemory();
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {

            /*case R.id.txt_dip:
            {
                *//*String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?saddr=%f,%f(%s)&daddr=%f,%f (%s)", 42.598726, -5.567096, "Home Sweet Home", dip.getLatitude(), dip.getLongitude(), "Where the party is at");
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                intent.setPackage("com.google.android.apps.maps");
                startActivity(intent);*//*

                //handle multiple view click events
                //private double latitude = 42.598726;
                //private double longitude = -5.567096;
                LatLng origin = new LatLng(42.598726, -5.567096);
                LatLng dest = new LatLng(Double.parseDouble(dip.getLatitude()), Double.parseDouble(dip.getLongitude()));

                // Getting URL to the Google Directions API
                String url = getDirectionsUrl(origin, dest);
                DownloadTask downloadTask = new DownloadTask();

                // Start downloading json data from Google Directions API
                downloadTask.execute(url);

            }
            break;*/
            case R.id.btn_fab:
            {
                /*String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?saddr=%f,%f(%s)&daddr=%f,%f (%s)", 42.598726, -5.567096, "Home Sweet Home", dip.getLatitude(), dip.getLongitude(), "Where the party is at");
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                intent.setPackage("com.google.android.apps.maps");
                startActivity(intent);*/

                //handle multiple view click events
                //private double latitude = 42.598726;
                //private double longitude = -5.567096;


                /*Location myLocation = mMap.getMyLocation();
                Log.i("KKKKKK: ", myLocation.toString());
                LatLng origin = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                LatLng dest = new LatLng(Double.parseDouble(dip.getLatitude()), Double.parseDouble(dip.getLongitude()));

                // Getting URL to the Google Directions API
                String url = getDirectionsUrl(origin, dest);
                DownloadTask downloadTask = new DownloadTask();

                // Start downloading json data from Google Directions API
                downloadTask.execute(url);*/



                seekLocation(dip);


            }
            break;

            case R.id.btn_share:
            {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareSubject =  getResources().getString(R.string.share_subject);
                String uri = "http://maps.google.com/maps?q=" +dip.getLatitude()+","+dip.getLongitude();

                String shareBody =getResources().getString(R.string.share_body)+ " " + dip.getName() + " " + getResources().getString(R.string.share_whereis)+ " " + dip.getAddress() + " " + uri + " "  + getResources().getString(R.string.hope_like);
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,shareSubject);
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));


                //setupFacebookShareIntent();
            }

            case R.id.btn_facebook:
            {
                setupFacebookShareIntent();
            }
            break;

        }
    }

    public void seekLocation(Dip dip)
    {
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" +dip.getLatitude() + "," + dip.getLongitude() + "");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }

    public void setupFacebookShareIntent() {

        String uri = "http://maps.google.com/maps?q=" +dip.getLatitude()+","+dip.getLongitude();
        String body = dip.getName() + " " + Utils.getStringTypeDip(dip.getType());

        ShareDialog shareDialog;
        FacebookSdk.sdkInitialize(getContext());
        shareDialog = new ShareDialog(this);

        ShareLinkContent linkContent = new ShareLinkContent.Builder()
                //.setContentTitle("Title")
                .setContentTitle(getResources().getString(R.string.share_body))
                //.setContentDescription("\"ddddddddddd\"")
                .setContentDescription(body)
                //.setContentUrl(Uri.parse("http://someurl.com/here"))
                .setContentUrl(Uri.parse(uri))

                .build();

        shareDialog.show(linkContent, ShareDialog.Mode.FEED);

        LoginManager.getInstance().logOut();
    }
    private class DownloadTask extends AsyncTask<String, Void, String>  {

        @Override
        protected void onPreExecute() {
            // start loading animation maybe?
            //adapter.clear(); // clear "old" entries (optional)
        }

        @Override
        protected String doInBackground(String... url) {

            String data = "";

            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();


            parserTask.execute(result);

        }
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String,String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                routes = parser.parse(jObject);
                distance = parser.getDistance();
                duration = parser.getDuration();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

           /* ldd = result.get(0);
            for (int i = 0; i < ldd.size(); i++) {

                HashMap dd = ldd.get(i);
                distance = (String)dd.get("distance");
                duration = (String)dd.get("duration");
            }*/


            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList();
                lineOptions = new PolylineOptions();

                List<HashMap<String, String>> path = result.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap point = path.get(j);

                    double lat = Double.parseDouble((String)point.get("lat"));
                    double lng = Double.parseDouble((String)point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                lineOptions.addAll(points);
                lineOptions.width(12);
                lineOptions.color(Color.BLUE);
                lineOptions.geodesic(true);

            }

// Drawing polyline in the Google Map for the i-th route
            mMap.addPolyline(lineOptions);
            addDistanceDuration();
        }
    }

    private void addDistanceDuration()
    {
        lay_dd.setVisibility(View.VISIBLE);
        txt_distance_val.setText(distance);
        txt_duration_val.setText(duration);
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=true";
        String language ="language=en";
        String mode = "units=metric";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + language +  "&" + mode;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "http://maps.google.com/maps/api/directions/json?" + parameters;


        return url;
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

}
