package org.grid2osm.gisapp;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.http.HttpStatus;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;
import retrofit.mime.MultipartTypedOutput;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedString;

import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.squareup.okhttp.OkHttpClient;

import android.accounts.AccountManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements LocationListener,
		GetTokenTask.GetTokenTaskInterface,
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener,
		GpsSettingsDialog.GpsSettingsListener,
		NetSettingsDialog.NetSettingsListener,
		PlayServicesDialog.PlayServicesListener {

	// Attributes for persistent storage
	private static final String STORAGE_GMAIL = "org.grid2osm.gisapp.gMail";
	private static final String STORAGE_GTOKEN = "org.grid2osm.gisapp.gToken";
	private static final String STORAGE_PREFS = "org.grid2osm.gisapp.storagePrefs";
	private Editor storageEditor;
	private SharedPreferences storagePrefs;

	// Attributes for starting the intent and used by onActivityResult
	private static final int INTENT_ENABLE_GPS = 1000;
	private static final int INTENT_ENABLE_NET = 1001;
	private static final int INTENT_PICK_ACCOUNT = 1002;
	private static final int INTENT_RECOVER_FROM_AUTH_ERROR = 1003;
	private static final int INTENT_RECOVER_FROM_PLAY_SERVICES_ERROR = 1004;
	private static final int INTENT_TAKE_PHOTO = 1005;

	// Attributes used to locate the user
	private static final int LOCALIZATION_UPPER_LIMIT = 5000;
	private static final int LOCALIZATION_LOWER_LIMIT = 1000;
	private LocationClient locationClient;
	private LocationRequest locationRequest;

	// Attributes used by the REST client
	private static final String REST_SERVER = "http://www.play.localdomain";
	private static final String SCOPE = "audience:server:client_id:889611969164-ujvohn299csu833avfmcsun3k6fna30s.apps.googleusercontent.com";
	private String gToken;
	private RestClientInterface restClientInterface;

	// List holding the photos temporarily
	private ArrayList<TypedFile> photoFiles;

	// Users's Google mail address
	private String gMail;

	// The photo file where the camera stores the taken photo temporarily
	private File photoFile;

	// Synchronous or asynchronous token request
	private static final int TIME_4_TOKEN_SYNC_REQUEST = 5000;
	private Boolean isSynchronous;

	// Make the photo accessible in the gallery
	private void addPhotoToGallery() {
		Intent mediaScanIntent = new Intent(
				Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		Uri contentUri = Uri.fromFile(photoFile);
		mediaScanIntent.setData(contentUri);
		this.sendBroadcast(mediaScanIntent);
	}

	// Add file to photoFiles to be able to send them later on
	private void addPhotoToListAndGallery() {
		String photoFileUri = Uri.fromFile(photoFile).toString();
		String mimeType = null;
		String extension = MimeTypeMap.getFileExtensionFromUrl(photoFileUri);
		if (extension != null) {
			MimeTypeMap mime = MimeTypeMap.getSingleton();
			mimeType = mime.getMimeTypeFromExtension(extension);
			photoFiles.add(new TypedFile(mimeType, photoFile));

			// Add the photo to the gallery
			addPhotoToGallery();
		} else {
			Toast.makeText(this,
					R.string.problem_add_photo_to_list_and_gallery,
					Toast.LENGTH_LONG).show();
		}
	}

	// Create a file for saving the photo
	private void createPhotoFile() {

		try {
			String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
					Locale.getDefault()).format(new Date());
			String photoFileName = getString(R.string.app_name) + "_"
					+ timeStamp;
			File storageDir = Environment
					.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
			photoFile = new File(storageDir.getPath(), photoFileName + ".jpg");
		} catch (Exception e) {
			Toast.makeText(this, R.string.problem_create_file,
					Toast.LENGTH_LONG).show();
		}
	}

	/*
	 * Attempts to retrieve the username. If the account is not yet known,
	 * invoke the picker. Once the account is known, start an instance of the
	 * AsyncTask to get the authentication token and work with it.
	 */
	private void getUsername() {
		if (gMail == null) {
			pickUserAccount();
		} else if (netIsEnabled()) {
			if (isSynchronous != null && isSynchronous) {
				isSynchronous = false;
				try {
					new GetTokenTask(this, gMail, SCOPE).execute().get(
							TIME_4_TOKEN_SYNC_REQUEST, TimeUnit.MILLISECONDS);
				} catch (InterruptedException e) {
					Toast.makeText(this, R.string.problem_get_token,
							Toast.LENGTH_LONG).show();
				} catch (ExecutionException e) {
					Toast.makeText(this, R.string.problem_get_token,
							Toast.LENGTH_LONG).show();
				} catch (TimeoutException e) {
					Toast.makeText(this, R.string.problem_get_token,
							Toast.LENGTH_LONG).show();
				}
			} else {
				new GetTokenTask(this, gMail, SCOPE).execute();
			}
		}
	}

	// Check whether the GPS sensor is activated
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
							INTENT_RECOVER_FROM_PLAY_SERVICES_ERROR);
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
							INTENT_RECOVER_FROM_PLAY_SERVICES_ERROR);
				}
			}
		});
	}

	// Checks whether the device currently has a network connection
	private boolean netIsEnabled() {
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null) {
			return networkInfo.isConnected();
		} else {
			return false;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == INTENT_ENABLE_GPS) {
			if (!gpsIsEnabled()) {
				Toast.makeText(this, R.string.problem_no_gps,
						Toast.LENGTH_SHORT).show();
				finish();
			}
		} else if (requestCode == INTENT_ENABLE_NET) {
			if (!netIsEnabled()) {
				Toast.makeText(this, R.string.problem_no_net,
						Toast.LENGTH_SHORT).show();
				finish();
			}
		} else if (requestCode == INTENT_PICK_ACCOUNT) {
			// Receiving a result from the AccountPicker
			if (resultCode == RESULT_OK) {
				gMail = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
				storageEditor.putString(STORAGE_GMAIL, gMail);
				storageEditor.commit();

				// With the account name acquired, go get the auth token
				getUsername();
			} else if (resultCode == RESULT_CANCELED) {
				/*
				 * The account picker dialog closed without selecting an
				 * account. Notify users that they must pick an account to
				 * proceed.
				 */
				Toast.makeText(this, R.string.problem_no_account,
						Toast.LENGTH_SHORT).show();
				finish();
			}
		} else if ((requestCode == INTENT_RECOVER_FROM_AUTH_ERROR || requestCode == INTENT_RECOVER_FROM_PLAY_SERVICES_ERROR)
				&& resultCode == RESULT_OK) {
			/*
			 * Receiving a result that follows a GoogleAuthException, try auth
			 * again
			 */
			getUsername();
		} else if (requestCode == INTENT_TAKE_PHOTO) {
			if (resultCode == RESULT_OK) {
				/*
				 * Add the photo to the photo list to send it later on and add
				 * it to the gallery
				 */
				addPhotoToListAndGallery();

				// Photo taken and saved; allow the user to take another one
				takePhoto();
			} else if (resultCode == RESULT_CANCELED) {
				// User cancelled the photo capture
			}
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

	/*
	 * Called by Location Services when the request to connect the client
	 * failed. At this point, the apps shows a message and quits.
	 */
	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {

		Toast.makeText(this, R.string.problem_no_localization,
				Toast.LENGTH_SHORT).show();
		finish();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Create a new global location parameters object
		locationRequest = LocationRequest.create();

		// Set the update interval ceiling in milliseconds
		locationRequest.setFastestInterval(LOCALIZATION_LOWER_LIMIT);

		// Set the update interval in milliseconds
		locationRequest.setInterval(LOCALIZATION_UPPER_LIMIT);

		// Use high accuracy
		locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

		/*
		 * Create a new location client, using the enclosing class to handle
		 * callbacks
		 */
		locationClient = new LocationClient(this, this, this);

		// Open Shared Preferences
		storagePrefs = getSharedPreferences(STORAGE_PREFS, Context.MODE_PRIVATE);

		// Get an editor
		storageEditor = storagePrefs.edit();

		// Initialize the REST adapter
		OkHttpClient okHttpClient = new OkHttpClient();
		OkClient okClient = new OkClient(okHttpClient);
		RestAdapter restAdapter = new RestAdapter.Builder().setClient(okClient)
				.setEndpoint(REST_SERVER).build();
		restClientInterface = restAdapter.create(RestClientInterface.class);

		// Button for sending the POI data and photos
		Button button = (Button) findViewById(R.id.sendData);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sendData();
			}
		});

		// Button for taking photos via intent
		button = (Button) findViewById(R.id.takePhoto);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (photoFiles == null) {
					photoFiles = new ArrayList<TypedFile>();
				}
				takePhoto();
			}
		});

		// Account chooser button
		button = (Button) findViewById(R.id.logoutIn);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				gMail = null;
				getUsername();
			}
		});
	}

	@Override
	public void onDisconnected() {

	}

	@Override
	public void onGetTokenTaskFinished(String token) {
		gToken = token;
		storageEditor.putString(STORAGE_GTOKEN, gToken);
		storageEditor.commit();
	}

	@Override
	public void onGpsSettingsDialogNegativeClick(DialogFragment dialog) {
		// User touched the dialog's negative button
		if (!gpsIsEnabled()) {
			Toast.makeText(this, R.string.problem_no_gps, Toast.LENGTH_SHORT)
					.show();
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
		startActivityForResult(intent, INTENT_ENABLE_GPS);
	}

	@Override
	public void onLocationChanged(Location location) {

	}

	@Override
	public void onNetSettingsDialogNegativeClick(DialogFragment dialog) {
		// User touched the dialog's negative button
		if (!netIsEnabled()) {
			Toast.makeText(this, R.string.problem_no_net, Toast.LENGTH_SHORT)
					.show();
			finish();
		}
	}

	@Override
	public void onNetSettingsDialogPositiveClick(DialogFragment dialog) {
		// User touched the dialog's positive button
		Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
		startActivityForResult(intent, INTENT_ENABLE_NET);
	}

	@Override
	public void onPlayServicesDialogNegativeClick(DialogFragment dialog) {
		Toast.makeText(this, R.string.problem_no_play, Toast.LENGTH_SHORT)
				.show();
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

		// Check for the availability of GPS
		else if (!gpsIsEnabled()) {
			GpsSettingsDialog gpsSettings = new GpsSettingsDialog();
			gpsSettings.show(getSupportFragmentManager(), "gpsSettings");
		}

		// Check for network connectivity.
		else if (!netIsEnabled()) {
			NetSettingsDialog netSettings = new NetSettingsDialog();
			netSettings.show(getSupportFragmentManager(), "netSettings");
		}

		/*
		 * All requirements are met. So, restore the data of the previous
		 * session, if available, and start localization.
		 */
		else {

			// Restore gMail and gToken from persistent storage
			if (storagePrefs.contains(STORAGE_GMAIL)) {
				gMail = storagePrefs.getString(STORAGE_GMAIL, null);
			}
			if (storagePrefs.contains(STORAGE_GTOKEN)) {
				gToken = storagePrefs.getString(STORAGE_GTOKEN, null);
			}
			if (gMail == null || gToken == null) {
				getUsername();
			}

			/*
			 * Connect the client. Don't re-start any requests here; instead,
			 * wait for onResume()
			 */
			if (!locationClient.isConnected()) {
				locationClient.connect();
			}
		}
	}

	/*
	 * Called when the Activity is no longer visible at all. Stop updates and
	 * disconnect.
	 */
	@Override
	protected void onStop() {

		// If the client is connected
		if (locationClient.isConnected()) {
			stopPeriodicUpdates();
		}

		// After disconnect() is called, the client is considered "dead".
		locationClient.disconnect();

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
		startActivityForResult(intent, INTENT_PICK_ACCOUNT);
	}

	// Chech whether the Google play services are available
	private boolean playIsAvailable() {
		if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS) {
			return true;
		} else {
			return false;
		}
	}

	// Send the data to the backend server
	private void sendData() {

		Location location = locationClient.getLastLocation();

		if (location != null && netIsEnabled()) {

			Callback<Response> callback = new Callback<Response>() {

				@Override
				public void failure(RetrofitError error) {
					if (error == null || error.getResponse() == null) {
						Toast.makeText(MainActivity.this, R.string.problem_no_server_connection, Toast.LENGTH_SHORT)
						.show();
					} else if (error.getResponse().getStatus() == HttpStatus.SC_UNAUTHORIZED) {
						isSynchronous = true;
						getUsername();
						sendData();
					}
				}

				@Override
				public void success(Response response0, Response response1) {

					/*
					 * The poi data and photos were sent successfully.
					 * Therefore, we create a new photo list.
					 */
					photoFiles = new ArrayList<TypedFile>();
				}
			};

			MultipartTypedOutput body = new MultipartTypedOutput();
			body.addPart("accuracy",
					new TypedString(String.valueOf(location.getAccuracy())));
			body.addPart("altitude",
					new TypedString(String.valueOf(location.getAltitude())));
			body.addPart("bearing",
					new TypedString(String.valueOf(location.getBearing())));
			body.addPart("latitude",
					new TypedString(String.valueOf(location.getLatitude())));
			body.addPart("longitude",
					new TypedString(String.valueOf(location.getLongitude())));
			body.addPart("provider", new TypedString(location.getProvider()));
			body.addPart("time",
					new TypedString(String.valueOf(location.getTime())));
			body.addPart("token", new TypedString(gToken));
			if (photoFiles != null && !photoFiles.isEmpty()) {
				int index = 0;
				for (TypedFile photoFile : photoFiles) {
					body.addPart("photo" + index, photoFile);
					index++;
				}
			}
			restClientInterface.createPoi(body, callback);
		}
	}

	/**
	 * In response to a request to start updates, send a request to Location
	 * Services
	 */
	private void startPeriodicUpdates() {

		locationClient.requestLocationUpdates(locationRequest, this);
	}

	/**
	 * In response to a request to stop updates, send a request to Location
	 * Services
	 */
	private void stopPeriodicUpdates() {

		locationClient.removeLocationUpdates(this);
	}

	// Take a photo using an intent
	private void takePhoto() {

		// Create an intent to take a picture
		Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		// Start the image capture Intent
		if (takePhotoIntent.resolveActivity(getPackageManager()) != null) {

			// Create a file to save the photo
			createPhotoFile();

			// Continue only if the file was successfully created
			if (photoFile != null) {

				// set the image file name
				takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT,
						Uri.fromFile(photoFile));

				startActivityForResult(takePhotoIntent, INTENT_TAKE_PHOTO);
			}
		}
	}
}
