package org.grid2osm.gisapp;

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
	private ArrayList<CustomLocation> poiLocationTrace;
	private ArrayList<Photo> poiPhotos;
	private ArrayList<Poi> pois;

	public ArrayList<CustomLocation> getPoiLocationTrace() {
		return poiLocationTrace;
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

	public void setPoiLocationTrace(ArrayList<CustomLocation> locationTrace) {
		this.poiLocationTrace = locationTrace;
	}

	void setPoiPhotos(ArrayList<Photo> poiPhotos) {
		this.poiPhotos = poiPhotos;
	}

	void setPois(ArrayList<Poi> pois) {
		this.pois = pois;
	}
}
