package org.grid2osm.gisapp;

import java.util.ArrayList;

import android.location.Location;

public class Poi {

	final ArrayList<Location> locationTrace;
	final ArrayList<Photo> photos;

	Poi(ArrayList<Location> locationTrace, ArrayList<Photo> photos) {
		this.locationTrace = locationTrace;
		this.photos = photos;
	}
}
