package org.grid2osm.gisapp;

import java.io.File;
import java.util.ArrayList;

import android.app.Fragment;
import android.location.Location;
import android.os.Bundle;

/*
 * https://developer.android.com/guide/topics/resources/runtime-changes.html#RetainingAnObject
 */
public class RetainedFragment extends Fragment {

	/*
	 * Complex data objects from MainActivity, that we want to retain. Take a
	 * look at MainActivity to read the object's description. If you want to
	 * retain simple attributes, use the SharedPreferences.
	 */
	private Photo imageViewPhoto;
	private ArrayList<Location> locationTrace;
	private ArrayList<Photo> poiPhotos;
	private ArrayList<Poi> pois;

	Photo getImageViewPhoto() {
		return imageViewPhoto;
	}

	public ArrayList<Location> getLocationTrace() {
		return locationTrace;
	}

	ArrayList<Photo> getPoiPhotos() {
		return poiPhotos;
	}

	ArrayList<Poi> getPois() {
		return pois;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// retain this fragment
		setRetainInstance(true);
	}

	void setImageViewPhoto(Photo imageViewPhoto) {
		this.imageViewPhoto = imageViewPhoto;
	}

	public void setLocationTrace(ArrayList<Location> locationTrace) {
		this.locationTrace = locationTrace;
	}

	void setPoiPhotos(ArrayList<Photo> poiPhotos) {
		this.poiPhotos = poiPhotos;
	}

	void setPois(ArrayList<Poi> pois) {
		this.pois = pois;
	}
}
