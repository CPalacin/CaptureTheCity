package com.whiteandc.capture.fragments.map;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.IntentSender;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.whiteandc.capture.Assets;
import com.whiteandc.capture.MonumentsActivity;
import com.whiteandc.capture.R;
import com.whiteandc.capture.data.Monument;
import com.whiteandc.capture.data.MonumentList;
import com.whiteandc.capture.maps.GMapV2Direction;

import org.w3c.dom.Document;

import java.util.ArrayList;

/**
 * Created by Blanca on 25/04/2015.
 */
public class FragmentMapDetail extends MapFragment implements MonumentsActivity.LocationUpdateListener {
    private LatLng toPosition;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        GoogleMap map= getMap();
        final MonumentsActivity monumentsActivity = (MonumentsActivity) getActivity();
        String monumentId = monumentsActivity.getCurrentMonumentId();

        Monument currentMonument= MonumentList.getMonument(monumentId);
        toPosition= currentMonument.getLatLng();
        Marker marker = map.addMarker(new MarkerOptions().position(toPosition)
                .title(monumentId));

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(toPosition, 16));
        map.setMyLocationEnabled(true);

        onLocationChanged(monumentsActivity.getLastLocation());
        return view;
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("FragmentMapDetail", "location: "+location);
        if(location!=null) {
            // Getting latitude of the current location
            double latitude = location.getLatitude();

            // Getting longitude of the current location
            double longitude = location.getLongitude();

            // Creating a LatLng object for the current location
            LatLng fromPosition = new LatLng(latitude, longitude);
            Log.d("FragmentMapDetail", fromPosition.toString());

            new DrawRouteTask(fromPosition, toPosition, getMap())
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    private static class DrawRouteTask extends AsyncTask<Void, Void, ArrayList<LatLng>> {
        private LatLng fromPosition;
        private LatLng toPosition;
        private GoogleMap map;

        public DrawRouteTask(LatLng fromPosition, LatLng toPosition, GoogleMap map) {
            this.fromPosition = fromPosition;
            this.toPosition = toPosition;
            this.map = map;
        }

        @Override
        protected ArrayList<LatLng> doInBackground(Void... params) {
            GMapV2Direction md = new GMapV2Direction();

            Document doc = md.getDocument(fromPosition, toPosition, GMapV2Direction.MODE_WALKING);
            Log.i("DrawRouteTask", "doc: " + doc.toString());
            ArrayList<LatLng> directionPoint = md.getDirection(doc);

            return directionPoint;
        }

        @Override
        protected void onPostExecute(ArrayList<LatLng> directionPoint) {
            super.onPostExecute(directionPoint);
            if (map != null) {
                Log.i("DrawRouteTask", "directionPoint: " + directionPoint.toString());
                PolylineOptions rectLine = new PolylineOptions().width(5).color(Color.BLUE);

                for (int i = 0; i < directionPoint.size(); i++) {
                    Log.i("DrawRouteTask", "directionPoint "+i+": " +directionPoint.get(i));
                    rectLine.add(directionPoint.get(i));
                }

                map.addPolyline(rectLine);
            }
        }
    }
}
