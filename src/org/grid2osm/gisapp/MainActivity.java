package org.grid2osm.gisapp;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;

public class MainActivity extends FragmentActivity implements LocationListener,
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener,
		GpsSettingsDialog.GpsSettingsListener,
		PlayServicesDialog.PlayServicesListener {

	// Object that holds accuracy and frequency parameters
	private LocationRequest mLocationRequest;

	// Current instantiation of the location client
	private LocationClient mLocationClient;

	// Check whether the gpg sensor is activated
	private boolean gpsIsEnabled() {
		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}

	/*
	 * Called by Location Services when the request to connect the client
	 * finishes successfully. At this point, you can request the current
	 * location or start periodic updates
	 */
	@Override
	public void onConnected(Bundle bundle) {

		startPeriodicUpdates();
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Create a new global location parameters object
		mLocationRequest = LocationRequest.create();

		// Set the update interval ceiling in milliseconds
		mLocationRequest.setFastestInterval(1000);

		// Set the update interval in milliseconds
		mLocationRequest.setInterval(5000);

		// Use high accuracy
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

		// Create a new location client, using the enclosing class to handle
		// callbacks
		mLocationClient = new LocationClient(this, this, this);
	}

	@Override
	public void onDisconnected() {

	}

	@Override
	public void onGpsSettingsDialogNegativeClick(DialogFragment dialog) {
		// User touched the dialog's negative button
		if (!gpsIsEnabled()) {
			finish();
		}
	}

	/*
	 * The dialog fragment receives a reference to this Activity through the
	 * Fragment.onAttach() callback, which it uses to call the following methods
	 * defined by the NoticeDialogFragment.NoticeDialogListener interface
	 */
	@Override
	public void onGpsSettingsDialogPositiveClick(DialogFragment dialog) {
		// User touched the dialog's positive button
		Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		startActivity(intent);
	}

	@Override
	public void onLocationChanged(Location location) {

	}

	@Override
	public void onPlayServicesDialogNegativeClick(DialogFragment dialog) {
		finish();
	}

	// Called when the Activity is restarted, even before it becomes visible.
	@Override
	protected void onStart() {

		super.onStart();

		// Check for the Google play services on the device
		if (!playIsAvailable()) {
			PlayServicesDialog playServices = new PlayServicesDialog();
			playServices.show(getSupportFragmentManager(), "playServices");
		}

		/*
		 * The activity is either being restarted or started for the first time
		 * so this is where we should make sure that GPS is enabled
		 */
		if (!gpsIsEnabled()) {

			/*
			 * Create a dialog here that requests the user to enable GPS, and
			 * use an intent with the
			 * android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS action
			 * to take the user to the Settings screen to enable GPS when they
			 * click "OK"
			 */
			GpsSettingsDialog gpsSettings = new GpsSettingsDialog();
			gpsSettings.show(getSupportFragmentManager(), "gpsSettings");
		}

		/*
		 * Connect the client. Don't re-start any requests here; instead, wait
		 * for onResume()
		 */
		mLocationClient.connect();
	}

	/*
	 * Called when the Activity is no longer visible at all. Stop updates and
	 * disconnect.
	 */
	@Override
	protected void onStop() {

		// If the client is connected
		if (mLocationClient.isConnected()) {
			stopPeriodicUpdates();
		}

		// After disconnect() is called, the client is considered "dead".
		mLocationClient.disconnect();

		super.onStop();
	}

	// Chech whether the Google play services are available
	private boolean playIsAvailable() {
		if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * In response to a request to start updates, send a request to Location
	 * Services
	 */
	private void startPeriodicUpdates() {

		mLocationClient.requestLocationUpdates(mLocationRequest, this);
	}

	/**
	 * In response to a request to stop updates, send a request to Location
	 * Services
	 */
	private void stopPeriodicUpdates() {

		mLocationClient.removeLocationUpdates(this);
	}
}
