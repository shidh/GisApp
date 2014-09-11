package org.grid2osm.gisapp;

import android.location.Location;

public class CustomLocation {

	final Float accuracy;
	final Double altitude;
	final Float bearing;
	final double latitude;
	final double longitude;
	final String provider;
	final long time;

	public CustomLocation(Float accuracy, Double altitude, Float bearing,
			double latitude, double longitude, String provider, long time) {
		this.accuracy = accuracy;
		this.altitude = altitude;
		this.bearing = bearing;
		this.latitude = latitude;
		this.longitude = longitude;
		this.provider = provider;
		this.time = time;
	}

	public CustomLocation(Location location) {
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
	}
}
