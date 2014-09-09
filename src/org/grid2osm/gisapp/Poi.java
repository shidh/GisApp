package org.grid2osm.gisapp;

import java.util.ArrayList;

public class Poi {

	final ArrayList<CustomLocation> locationTrace;
	final ArrayList<Photo> photos;

	Poi(ArrayList<CustomLocation> locationTrace, ArrayList<Photo> photos) {
		this.locationTrace = locationTrace;
		this.photos = photos;
	}
}
