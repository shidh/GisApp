package org.grid2osm.gisapp;

import java.io.File;

import android.location.Location;

public class Photo {

	final Location location;
	final File file;

	Photo(Location location, File file) {
		this.location = location;
		this.file = file;
	}
}
