package com.whiteandc.capture.fragments.map;

import android.app.Fragment;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;

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
import com.whiteandc.capture.MonumentsActivity;
import com.whiteandc.capture.data.Monument;
import com.whiteandc.capture.data.MonumentList;
import com.whiteandc.capture.maps.GMapV2Direction;

import org.w3c.dom.Document;

import java.util.ArrayList;

/**
 * Created by Blanca on 25/04/2015.
 */
public class FragmentMapDetail extends MapFragment {


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        GoogleMap map= getMap();
        String monumentId= ((MonumentsActivity) getActivity()).getCurrentMonumentId();

        // If monumentId is null the
        if(monumentId != null){

        }

        Monument currentMonument= MonumentList.getMonument(monumentId);
        LatLng latLng= currentMonument.getLatLng();
        Marker marker = map.addMarker(new MarkerOptions().position(latLng)
                .title(monumentId));


        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
        drawPath(map, latLng);
        //map.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
        return view;
    }

    private void drawPath(GoogleMap map, LatLng latLng) {
        // Enabling MyLocation Layer of Google Map
        map.setMyLocationEnabled(true);

        // Getting LocationManager object from System Service LOCATION_SERVICE
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(getActivity().LOCATION_SERVICE);

        // Creating a criteria object to retrieve provider
        Criteria criteria = new Criteria();

        // Getting the name of the best provider
        String provider = locationManager.getBestProvider(criteria, true);

        // Getting Current Location
        Location location = locationManager.getLastKnownLocation(provider);

        if(location!=null) {
            // Getting latitude of the current location
            double latitude = location.getLatitude();

            // Getting longitude of the current location
            double longitude = location.getLongitude();

            // Creating a LatLng object for the current location
            LatLng fromPosition = new LatLng(latitude, longitude);
            Log.d("FragmentMapDetail", fromPosition.toString());
            GMapV2Direction md = new GMapV2Direction();

            Document doc = md.getDocument(fromPosition, latLng, GMapV2Direction.MODE_DRIVING);
            Log.d("FragmentMapDetail", doc.toString());
            ArrayList<LatLng> directionPoint = md.getDirection(doc);
            PolylineOptions rectLine = new PolylineOptions().width(3).color(android.R.color.holo_red_light);

            for (int i = 0; i < directionPoint.size(); i++) {
                rectLine.add(directionPoint.get(i));
            }

            map.addPolyline(rectLine);
        }
    }
}
