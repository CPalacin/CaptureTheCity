package com.whiteandc.capture.data;


import android.graphics.drawable.Drawable;

import com.google.android.gms.maps.model.LatLng;

public class Monument implements Comparable{
	
	private String name= null;
	private int[] photos= null;
	private boolean captured= false;
	private String capturedImg= null;
	private String description=  "";
    private LatLng latLng= null;
    private Drawable mainPicture = null;

	public Monument(String name, int[] photoList, boolean captured, String capturedImage, LatLng latLng, String description, Drawable mainPicture){
		this.name= name;
		this.photos= photoList;
		this.captured= captured;
		this.capturedImg= capturedImage;
        this.description = description;
        this.latLng= latLng;
		this.mainPicture = mainPicture;
	}

	public String getName() {
		return name;
	}

	@Override
	public int compareTo(Object another) {
		return name.compareTo(((Monument) another).name);
	}
	public int[] getPhotos() {
		return photos;
	}
	public void setPhotos(int[] photos) {
		this.photos = photos;
	}
	public boolean isCaptured() {
		return captured;
	}
	public void setIsCaptured(boolean b) {
		captured=b;
	}

	public String getCapturedImg() {
		return capturedImg;
	}

    public Drawable getMainPicture() {
        return mainPicture;
    }

    public void setMainPicture(Drawable mainPicture) {
        this.mainPicture = mainPicture;
    }

    public String getDescription() {
        return description;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

}
