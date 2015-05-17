package com.whiteandc.capture.data;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;


public class MonumentList {
	public static HashMap<String, Monument> monumentList = new HashMap<String, Monument>();
    private static String cityName = "Madrid";
	public static void addItem(Monument monument){
		if(!monumentList.containsKey(monument.getName())){
			monumentList.put(monument.getName(), monument);
		}else{
			Log.w("MonumentList", "Added duplicated monument this should not happens. The monument has not be added.");
		}
	}
	
	public static Iterator<Monument> getIterator() {
		return monumentList.values().iterator();
	}
	
	@SuppressWarnings("unchecked")
	public static ArrayList<Monument> getList(){
		ArrayList<Monument> list = new ArrayList<Monument>(monumentList.values());
		Collections.sort(list);
		return list;
	}
	
	public static Monument getMonument(String name){
		return monumentList.get(name);
	}


    public static String getCityName() {
        return cityName;
    }

    public static void setCityName(String cityName) {
        MonumentList.cityName = cityName;
    }

}

