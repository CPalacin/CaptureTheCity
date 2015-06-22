package com.whiteandc.capture.fragments.map;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.whiteandc.capture.R;
import com.whiteandc.capture.data.MonumentList;
import com.whiteandc.capture.data.MonumentLoader;


/**
 * Created by Blanca on 21-01-2015.
 */
public class FragmentMap extends MapFragment implements OnMapReadyCallback {

    static final LatLng MADRID = new LatLng(40.427505, -3.705286);

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        getMapAsync(this);

        //googleMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
        return view;
    }

    private void addMarkers(GoogleMap map) { //TODO maybe we can iterate over the list
        addOneMarker(map, getResources().getString(R.string.title_fuente_cibeles));
        addOneMarker(map, getResources().getString(R.string.title_puerta_alcala));
        addOneMarker(map, getResources().getString(R.string.title_calle_alcala));
        addOneMarker(map, getResources().getString(R.string.title_cat_almudena));
        addOneMarker(map, getResources().getString(R.string.title_templo_debod));
        addOneMarker(map, getResources().getString(R.string.title_palacio_real));
        addOneMarker(map, getResources().getString(R.string.title_plaza_mayor));
        addOneMarker(map, getResources().getString(R.string.title_retiro));
        addOneMarker(map, getResources().getString(R.string.title_sol));
        addOneMarker(map, getResources().getString(R.string.title_palacion_com));
        addOneMarker(map, getResources().getString(R.string.title_teatro));
        addOneMarker(map, getResources().getString(R.string.title_teleferico));
    }

    private void addOneMarker(GoogleMap map, String monument) {
        LatLng latLng= MonumentList.getMonument(monument).getLatLng();
        Marker monumentMarker = map.addMarker(new MarkerOptions().position(latLng)
                .title(monument));
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        addMarkers(googleMap);

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(MADRID, 14));
    }
}