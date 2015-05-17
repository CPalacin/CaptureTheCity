package com.whiteandc.capture.util;

import android.util.Log;

/**
 * Created by crubio on 5/16/2015.
 */
public class StringUtil {

    public static String cropString(String string, int cropIndex){
        String res = string;
        int cont = 0;

        while (cropIndex+cont < string.length() &&
               string.charAt(cropIndex + cont) != ' ' &&
               string.charAt(cropIndex + cont) != '.'){
            cont++;
        }

        if(cropIndex+cont < string.length()){
            res = res.substring(0,cropIndex+cont);
        }
        return res;
    }
}
