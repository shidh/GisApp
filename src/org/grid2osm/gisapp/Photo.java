package org.grid2osm.gisapp;

import android.location.Location;

public class Photo {

	final Float accuracy;
	final Double altitude;
	final Float bearing;
	final double latitude;
	final double longitude;
	final String provider;
	final long time;
	final String filePath;

	Photo(Location location, String filePath) {
		if (location.hasAccuracy()) {
			this.accuracy = location.getAccuracy();
		} else {
			this.accuracy = null;
		}
		if (location.hasAltitude()) {
			this.altitude = location.getAltitude();
		} else {
			this.altitude = null;
		}
		if (location.hasBearing()) {
			this.bearing = location.getBearing();
		} else {
			this.bearing = null;
		}
		this.latitude = location.getLatitude();
		this.longitude = location.getLongitude();
		this.provider = location.getProvider();
		this.time = location.getTime();
		this.filePath = filePath;
	}
}
