package com.whiteandc.capture.fragments.map;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.whiteandc.capture.MonumentsActivity;
import com.whiteandc.capture.data.Monument;
import com.whiteandc.capture.data.MonumentList;

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


        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20));
        //map.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
        return view;
    }
}
