package org.grid2osm.gisapp;

import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

import android.accounts.AccountManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements LocationListener,
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener,
		GpsSettingsDialog.GpsSettingsListener,
		PlayServicesDialog.PlayServicesListener {

	// Key for storing the "mEmail" flag in shared preferences
	public static final String KEY_MEMAIL = "org.grid2osm.mEmail";

	static final int REQUEST_CODE_PICK_ACCOUNT = 1000;
	static final int REQUEST_CODE_RECOVER_FROM_AUTH_ERROR = 1001;
	static final int REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR = 1002;

	// Users's mail address
	private String mEmail;

	// Scope of data obtained through the Google API
	private static final String SCOPE = "audience:server:client_id:889611969164-ujvohn299csu833avfmcsun3k6fna30s.apps.googleusercontent.com";

	// Object that holds accuracy and frequency parameters
	private LocationRequest mLocationRequest;

	// Current instantiation of the location client
	private LocationClient mLocationClient;

	// Name of the prefs file
	public static final String PREFS_NAME = "org.grid2osm.mPrefs";

	// Handle to SharedPreferences for this app
	SharedPreferences mPrefs;

	// Handle to a SharedPreferences editor
	SharedPreferences.Editor mEditor;

	/*
	 * Attempts to retrieve the username. If the account is not yet known,
	 * invoke the picker. Once the account is known, start an instance of the
	 * AsyncTask to get the auth token and do work with it.
	 */
	private void getUsername() {
		if (mEmail == null) {
			pickUserAccount();
		} else {
			if (isDeviceOnline()) {
				new GetUserNameTask(MainActivity.this, mEmail, SCOPE).execute();
			} else {
				Toast.makeText(this, R.string.not_online, Toast.LENGTH_LONG)
						.show();
			}
		}
	}

	// Check whether the gpg sensor is activated
	private boolean gpsIsEnabled() {
		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}

	/*
	 * This method is a hook for background threads and async tasks that need to
	 * provide the user a response UI when an exception occurs.
	 */
	public void handleException(final Exception e) {
		/*
		 * Because this call comes from the AsyncTask, we must ensure that the
		 * following code instead executes on the UI thread.
		 */
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (e instanceof GooglePlayServicesAvailabilityException) {
					/*
					 * The Google Play services APK is old, disabled, or not
					 * present. Show a dialog created by Google Play services
					 * that allows the user to update the APK
					 */
					int statusCode = ((GooglePlayServicesAvailabilityException) e)
							.getConnectionStatusCode();
					Dialog dialog = GooglePlayServicesUtil.getErrorDialog(
							statusCode, MainActivity.this,
							REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR);
					dialog.show();
				} else if (e instanceof UserRecoverableAuthException) {
					/*
					 * Unable to authenticate, such as when the user has not yet
					 * granted the app access to the account, but the user can
					 * fix this. Forward the user to an activity in Google Play
					 * services.
					 */
					Intent intent = ((UserRecoverableAuthException) e)
							.getIntent();
					startActivityForResult(intent,
							REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR);
				}
			}
		});
	}

	// Checks whether the device currently has a network connection
	private boolean isDeviceOnline() {
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			return true;
		}
		return false;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE_PICK_ACCOUNT) {
			// Receiving a result from the AccountPicker
			if (resultCode == RESULT_OK) {
				mEmail = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
				mEditor.putString(KEY_MEMAIL, mEmail);
				mEditor.commit();
				
				// With the account name acquired, go get the auth token
				getUsername();
			} else if (resultCode == RESULT_CANCELED) {
				/*
				 * The account picker dialog closed without selecting an
				 * account. Notify users that they must pick an account to
				 * proceed.
				 */
				Toast.makeText(this, R.string.pick_account, Toast.LENGTH_SHORT)
						.show();
				finish();
			}
		} else if ((requestCode == REQUEST_CODE_RECOVER_FROM_AUTH_ERROR || requestCode == REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR)
				&& resultCode == RESULT_OK) {
			// Receiving a result that follows a GoogleAuthException, try auth
			// again
			getUsername();
		}
		super.onActivityResult(requestCode, resultCode, data);
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

		finish();
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

		// Open Shared Preferences
		mPrefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

		// Get an editor
		mEditor = mPrefs.edit();
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
		if (!mLocationClient.isConnected()) {
			mLocationClient.connect();
		}
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

	/**
	 * Starts an activity in Google Play Services so the user can pick an
	 * account
	 */
	private void pickUserAccount() {
		String[] accountTypes = new String[] { "com.google" };
		Intent intent = AccountPicker.newChooseAccountIntent(null, null,
				accountTypes, false, null, null, null, null);
		startActivityForResult(intent, REQUEST_CODE_PICK_ACCOUNT);
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
