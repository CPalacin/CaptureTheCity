package com.whiteandc.capture.data;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.whiteandc.capture.R;

import java.util.Locale;


public class MonumentLoader {
    private static final String CLASS = "MonumentLoader";

    public static void loadMonuments(SharedPreferences sharedpreferences, Activity activity) {

        addMonument(sharedpreferences, resToString(R.string.title_fuente_cibeles, activity),
                new int[]{R.drawable.cibeles_480, R.drawable.test1}, new LatLng(40.419385, -3.692996),
                resToString(R.string.fuente_cibeles, activity));

        addMonument(sharedpreferences, resToString(R.string.title_puerta_alcala, activity),
                new int[]{R.drawable.alcala2}, new LatLng(40.419992, -3.688640),
                resToString(R.string.puerta_alcala, activity));

        addMonument(sharedpreferences, resToString(R.string.title_calle_alcala, activity),
                new int[]{R.drawable.calle_alcala}, new LatLng(40.418659, -3.697051),
                resToString(R.string.calle_alacala, activity));

        addMonument(sharedpreferences, resToString(R.string.title_cat_almudena, activity),
                new int[]{R.drawable.catedral_almudena},
                new LatLng(40.415621, -3.714648),
                resToString(R.string.catedral_almudena, activity));

        addMonument(sharedpreferences, resToString(R.string.title_templo_debod, activity),
                new int[]{R.drawable.templo_debod}, new LatLng(40.424034, -3.717801),
                resToString(R.string.templo_debod, activity));

        addMonument(sharedpreferences, resToString(R.string.title_palacio_real, activity),
                new int[]{R.drawable.palacio_real, R.drawable.palacio_real1,
                        R.drawable.palacio_real2, R.drawable.palacio_real3,
                        R.drawable.palacio_real4},
                new LatLng(40.417967, -3.714291), resToString(R.string.palacio_real, activity));

        addMonument(sharedpreferences, resToString(R.string.title_plaza_mayor, activity),
                new int[]{R.drawable.plaza_mayor, R.drawable.plaza_mayor1,
                        R.drawable.plaza_mayor2, R.drawable.plaza_mayor3,
                        R.drawable.plaza_mayor4, R.drawable.plaza_mayor5},
                new LatLng(40.415520, -3.707417), resToString(R.string.plaza_mayor, activity));

        addMonument(sharedpreferences, resToString(R.string.title_retiro, activity),
                new int[]{R.drawable.retiro, R.drawable.retiro1,
                        R.drawable.retiro2, R.drawable.retiro3,
                        R.drawable.retiro4, R.drawable.retiro5,
                        R.drawable.retiro6, R.drawable.retiro7,
                        R.drawable.retiro8, R.drawable.retiro9,
                        R.drawable.retiro10, R.drawable.retiro11,
                        R.drawable.retiro12, R.drawable.retiro13,
                        R.drawable.retiro14},
                new LatLng(40.415249, -3.684521), resToString(R.string.retiro, activity));

        addMonument(sharedpreferences, resToString(R.string.title_sol, activity),
                new int[]{R.drawable.puerta_del_sol, R.drawable.puerta_del_sol1,
                        R.drawable.puerta_del_sol2, R.drawable.puerta_del_sol3,
                        R.drawable.puerta_del_sol4, R.drawable.puerta_del_sol5,
                        R.drawable.puerta_del_sol6, R.drawable.puerta_del_sol7,
                        R.drawable.puerta_del_sol8},
                new LatLng(40.416935, -3.703539), resToString(R.string.sol, activity));

        addMonument(sharedpreferences, resToString(R.string.title_palacion_com, activity),
                new int[]{R.drawable.palacio_de_comunicaciones, R.drawable.palacio_de_comunicaciones1,
                        R.drawable.palacio_de_comunicaciones2, R.drawable.palacio_de_comunicaciones3,
                        R.drawable.palacio_de_comunicaciones4, R.drawable.palacio_de_comunicaciones5,
                        R.drawable.palacio_de_comunicaciones6, R.drawable.palacio_de_comunicaciones7},
                new LatLng(40.418891, -3.692039), resToString(R.string.palacio_comunicaciones, activity));

        addMonument(sharedpreferences, resToString(R.string.title_teatro, activity),
                new int[]{R.drawable.teatro_real, R.drawable.teatro_real1,
                        R.drawable.teatro_real2, R.drawable.teatro_real3,
                        R.drawable.teatro_real4, R.drawable.teatro_real5,
                        R.drawable.teatro_real6, R.drawable.teatro_real7,
                        R.drawable.teatro_real8},
                new LatLng(40.418450, -3.710599), resToString(R.string.teatro, activity));

        addMonument(sharedpreferences, resToString(R.string.title_teleferico, activity),
                new int[]{R.drawable.teleferico, R.drawable.teleferico1,
                        R.drawable.teleferico3, R.drawable.teleferico4},
                new LatLng(40.425269, -3.717168), resToString(R.string.teleferico, activity));

        //addMonument(sharedpreferences, "Test", new int[]{R.drawable.test1,R.drawable.test2});
    }

    private static void addMonument(SharedPreferences sharedpreferences, String name, int[] photos, LatLng latLng, String description) {
        boolean captured = false;
        if (sharedpreferences.contains(name)) {
            Log.i(CLASS, "Obteniendo valor de key: " + name);
            captured = sharedpreferences.getBoolean(name, false);
        } else {
            Log.i(CLASS, "NO existe " + name + " en SharedPreferences, creando key");
            Editor editor = sharedpreferences.edit();
            editor.putBoolean(name, false);
            editor.apply();
        }
        Log.i(CLASS, "Anadiendo a la lista monumento: " + name + ((captured) ? " capturado" : " NO capturado"));
        MonumentList.addItem(new Monument(name, photos, captured, "IMG_20140603_201814.jpg", latLng, description));
    }

    private static String resToString(int res, Activity activity) {
        return activity.getResources().getString(res);
    }

}
