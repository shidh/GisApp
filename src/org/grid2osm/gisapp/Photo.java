package org.grid2osm.gisapp;

import java.io.File;

import android.location.Location;

public class Photo {

	final CustomLocation location;
	final File file;

	Photo(Location location, File file) {
		this.location = new CustomLocation(location);
		this.file = file;
	}
}
