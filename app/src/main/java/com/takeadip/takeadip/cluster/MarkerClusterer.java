package com.takeadip.takeadip.cluster;

import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;

/**
 * Created by vik on 23/05/2017.
 */

public class MarkerClusterer {

    private GoogleMap mMap;
    private Marker[] mMarkers;
    private int mGridSize;
    private boolean mAverageCenter;
    private ArrayList<MarkerCluster> mMarkerClusters;

    public MarkerClusterer(GoogleMap map, Marker[] markers, int gridSize, boolean averageCenter) {

        mMap = map;
        mMarkers = markers;
        mGridSize = gridSize;
        mAverageCenter = averageCenter;
        mMarkerClusters = new ArrayList<MarkerCluster>();
    }
    public ArrayList<MarkerCluster> createMarkerClusters() {
        LatLngBounds mapBounds = new LatLngBounds(mMap.getProjection()
                .getVisibleRegion().latLngBounds.southwest, mMap
                .getProjection().getVisibleRegion().latLngBounds.northeast);
        LatLngBounds bounds = new MapUtils().getExtendedBounds(mMap, mGridSize,
                mapBounds);

        for (Marker marker : mMarkers) {
            if (isMarkerInBounds(marker, bounds)) {
                Log.i("marker:", marker.getTitle());
                addToClosestCluster(marker);
            }
        }

        return mMarkerClusters;
    }

    private void addToClosestCluster(Marker marker) {
        //double distance = 100000;
        double distance = 10000000;
        MarkerCluster clusterToAddTo = null;



        for (MarkerCluster markerCluster : mMarkerClusters) {
            if (markerCluster.center != null) {
                double d = MapUtils.distanceBetweenPoints(markerCluster.center,
                        marker.getPosition());
                Log.i("distance d: " , String.valueOf(d));
                if (d < distance) {
                    distance = d;
                    clusterToAddTo = markerCluster;
                }
            }
        }

        if (clusterToAddTo != null
                && isMarkerInBounds(marker, clusterToAddTo.bounds)) {
            clusterToAddTo.addMarker(marker);
        } else {
            MarkerCluster markerCluster = new MarkerCluster(mMap, mGridSize,
                    mAverageCenter);
            markerCluster.addMarker(marker);
            mMarkerClusters.add(markerCluster);
        }
    }

    private boolean isMarkerInBounds(Marker marker, LatLngBounds bounds) {
        return bounds.contains(marker.getPosition());
    }

}
