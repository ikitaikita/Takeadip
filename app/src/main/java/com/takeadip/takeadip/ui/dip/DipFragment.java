package com.takeadip.takeadip.ui.dip;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.takeadip.takeadip.R;
import com.takeadip.takeadip.data.model.DipData;
import com.takeadip.takeadip.internal.Utils;

/**
 * Created by vik on 17/10/2017.
 */

public class DipFragment extends Fragment implements DipContract.View , View.OnClickListener{

    private static final String DIP_ID_TAG = "DetailDipFragment";
    private static final String BUNDLE_DIP = "dip";

    private DipData mDip;

    public static DipFragment newInstance(DipData dip) {
        Bundle args = new Bundle();
        args.putSerializable(BUNDLE_DIP, dip);



        DipFragment fragment = new DipFragment();
        fragment.setArguments(args);

        return fragment;
    }

    private DipContract.Presenter mPresenter;

    private TextView txt_dip;
    private TextView txt_typedip;
    Button btn_fab, btn_share, btn_facebook;



    private MapView mMapView;
    private GoogleMap mMap = null;

    public DipFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {

        Bundle bundle = getArguments();
        if(bundle != null)
        {
            mDip= (DipData) bundle.getSerializable("dip");

        }
        // Inflate the layout for this fragment

        mPresenter = new DipPresenter(mDip);

        View v = inflater.inflate(R.layout.fragment_detail_dip, container, false);
        txt_dip = (TextView) v.findViewById(R.id.txt_dip);
        txt_typedip = (TextView) v.findViewById(R.id.txt_typedip);
        Button btn_fab = (Button)v.findViewById(R.id.btn_fab);
        Button btn_share = (Button)v.findViewById(R.id.btn_share);
        Button btn_facebook = (Button)v.findViewById(R.id.btn_facebook);
        btn_fab.setOnClickListener(this);
        btn_share.setOnClickListener(this);
        btn_facebook.setOnClickListener(this);

        mMapView = (MapView) v.findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);



        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        mPresenter.onAttachView(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mPresenter.onDetachView();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void displayDip(@NonNull final DipData dip) {

        txt_dip.setText(dip.getNombre());
        txt_typedip.setText(dip.getTipo());

    }

    @Override
    public void displayMap() {


        mMap = mMapView.getMap();
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.setMyLocationEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL); //establecemos tipo de mapa
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);

    }

    @Override
    public void drawPointOnMap(DipData dip) {
        if(dip.getLatitud() != null && dip.getLongitud() != null)
        {
            LatLng markerPosition =
                    new LatLng (Double.parseDouble(dip.getLatitud())
                            ,Double.parseDouble(dip.getLongitud()));
            Log.i("markerPosition: ", markerPosition.toString());

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerPosition,15));
            String descrip ="";
            //String id = p.getID();
            //descrip = p.getGuid();

            //añade el marcador al mapa
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(markerPosition)
                    .title(dip.getNombre())
                    .snippet(dip.getDireccion())
                    .icon(BitmapDescriptorFactory
                            .fromResource(R.mipmap.ic_map_marker_outline_black_24dp))

                    .anchor(0.5f, 0.5f));

            marker.showInfoWindow();
        }
    }

    @Override
    public void showErrorMessage() {
        Toast.makeText( getActivity(), R.string.dip_not_available, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btn_fab: seekLocation(mDip);

            case R.id.btn_share: shareDip(mDip);

            case R.id.btn_facebook: setupFacebookShareIntent(mDip);

            break;

        }
    }


    private void seekLocation(DipData dip) {
        Uri gmmIntentUri =
                Uri.parse("google.navigation:q=" +dip.getLatitud()
                        + "," + dip.getLongitud() + "");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }

    private void shareDip(DipData dip){
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareSubject =  getResources().getString(R.string.share_subject);
        String uri
                = "http://maps.google.com/maps?q=" +dip.getLatitud()+","+dip.getLongitud();

        String shareBody =
                getResources().getString(R.string.share_body)
                        + " "
                        + dip.getNombre()
                        + " "
                        + getResources().getString(R.string.share_whereis)
                        + " "
                        + dip.getDireccion()
                        + " "
                        + uri
                        + " "
                        + getResources().getString(R.string.hope_like);
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,shareSubject);
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    private void setupFacebookShareIntent(DipData dip) {

        String uri = "http://maps.google.com/maps?q=" +dip.getLatitud()+","+dip.getLongitud();
        String body = dip.getNombre() + " " + Utils.getStringTypeDip(dip.getTipo());

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
}
