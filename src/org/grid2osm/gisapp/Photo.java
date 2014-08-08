package org.grid2osm.gisapp;

import java.io.File;

public class Photo {

	final Float accuracy;
	final Double altitude;
	final Float bearing;
	final double latitude;
	final double longitude;
	final File photoFile;
	final String provider;
	final long time;

	Photo(Float accuracy, Double altitude, Float bearing, double latitude,
			double longitude, File photoFile, String provider, long time) {
		this.accuracy = accuracy;
		this.altitude = altitude;
		this.bearing = bearing;
		this.latitude = latitude;
		this.longitude = longitude;
		this.photoFile = photoFile;
		this.provider = provider;
		this.time = time;
	}
}
