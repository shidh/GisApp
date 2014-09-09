package org.grid2osm.gisapp;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import org.apache.http.HttpStatus;
import org.grid2osm.gisapp.AsyncTask.CheckTokenTask;
import org.grid2osm.gisapp.AsyncTask.GetTokenTask;
import org.grid2osm.gisapp.AsyncTask.SendDataTask;
import org.grid2osm.gisapp.dialog.GpsSettingsDialog;
import org.grid2osm.gisapp.dialog.NetSettingsDialog;
import org.grid2osm.gisapp.dialog.PlayServicesDialog;
import org.grid2osm.gisapp.event.CheckTokenTaskFailureEvent;
import org.grid2osm.gisapp.event.GetTokenFinishedEvent;
import org.grid2osm.gisapp.event.GpsSettingsDialogNegativeClickEvent;
import org.grid2osm.gisapp.event.GpsSettingsDialogPositiveClickEvent;
import org.grid2osm.gisapp.event.NetSettingsDialogNegativeClickEvent;
import org.grid2osm.gisapp.event.NetSettingsDialogPositiveClickEvent;
import org.grid2osm.gisapp.event.PlayServicesDialogNegativeClickEvent;
import org.grid2osm.gisapp.event.SendDataTaskEvent;
import org.grid2osm.gisapp.event.SwipeBottomEvent;
import org.grid2osm.gisapp.event.SwipeLeftEvent;
import org.grid2osm.gisapp.event.SwipeRightEvent;
import org.grid2osm.gisapp.event.SwipeTopEvent;
import org.grid2osm.gisapp.event.TransferProgressChangedEvent;
import org.grid2osm.gisapp.retrofit.TransferProgressMultipartTypedOutput;
import org.grid2osm.gisapp.retrofit.TransferProgressTypedFile;
import org.grid2osm.gisapp.retrofit.TransferProgressTypedString;

import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

import de.greenrobot.event.EventBus;

import android.accounts.AccountManager;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
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
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity implements
		LocationListener, GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener {

	// Attributes for persistent storage
	private static final String STORAGE_ACCOUNTPICKERISOPEN = "org.grid2osm.gisapp.accountPickerIsOpen";
	private static final String STORAGE_ACCUMULATEDTRANSFERSIZE = "org.grid2osm.gisapp.accumulatedTransferSize";
	private static final String STORAGE_GESTURESENABLED = "org.grid2osm.gisapp.gesturesEnabled";
	private static final String STORAGE_GMAIL = "org.grid2osm.gisapp.gMail";
	private static final String STORAGE_GTOKEN = "org.grid2osm.gisapp.gToken";
	private static final String STORAGE_IMAGEVIEWINDEX = "org.grid2osm.gisapp.imageViewIndex";
	private static final String STORAGE_LOCATIONTRACEENABLED = "org.grid2osm.gisapp.locationTraceEnabled";
	private static final String STORAGE_PHOTOFILEPATH = "org.grid2osm.gisapp.photoFilePath";
	private static final String STORAGE_PREFS = "org.grid2osm.gisapp.storagePrefs";
	private static final String STORAGE_PROGRESSBARVISIBILITY = "org.grid2osm.gisapp.progressBar.visibility";
	private static final String STORAGE_PROGRESSCIRCLEVISIBILITY = "org.grid2osm.gisapp.progressCircle.visibility";
	private static final String STORAGE_RESUMESEND = "org.grid2osm.gisapp.resumeSend";
	private static final String STORAGE_TAKEANOTHERPHOTO = "org.grid2osm.gisapp.takeAnotherPhoto";
	private static final String STORAGE_TOTALTRANSFERSIZE = "org.grid2osm.gisapp.totalTransferSize";
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
	private ArrayList<CustomLocation> poiLocationTrace;
	private Boolean locationTraceEnabled;

	// Attributes used by the REST client
	private static final String SCOPE = "audience:server:client_id:889611969164-ujvohn299csu833avfmcsun3k6fna30s.apps.googleusercontent.com";
	private String gToken;
	private ProgressBar progressBar;
	private ProgressBar progressCircle;
	private Long accumulatedTransferSize;
	private Long totalTransferSize;

	// List holding information on all POI that will be sent later on to the
	// backend server
	private ArrayList<Poi> pois;

	// List holding the photos of a POI temporarily for storing them later on in
	// attribute "pois"
	private ArrayList<Photo> poiPhotos;

	// The photo file where the camera stores the taken photo temporarily to
	// create an entry in "poiPhotos"
	private String photoFilePath;

	// Current photo on the imageView
	private int imageViewIndex;

	// Users's Google mail address
	private String gMail;

	// Attributes for gesture recognition
	private GestureDetectorCompat gestureDetector;
	private Boolean gesturesEnabled;

	// Attributes for the imageView
	private ImageView imageView;
	private TextView cameraTextView;
	private TextView deleteTextView;
	private TextView previewTextView;
	private TextView sendTextView;

	// The view where all GUI items are placed on.
	private View rootView;

	// Fragment used to retain complex objects
	private RetainedFragment retainedFragment;

	/*
	 * If the activity was registered in onCreate(), don't register in
	 * onResume();
	 */
	private boolean skipRegisterOnNextResume;

	// Keeps the app from opening multiple dialogs at the same time
	private Boolean accountPickerIsOpen;
	private ArrayList<DialogFragment> dialogs;

	/*
	 * After the intent to take a picture finishes we need to wait for
	 * onConnected() and get location information thereafter in order to save
	 * the data.
	 */
	private Boolean takeAnotherPhoto;

	// Menu item with counter for POI items
	private MenuItem poiMenuItem;

	// Resume sending after refreshing the Google token
	private Boolean resumeSend;

	// Add file to photoFiles to be able to send them later on
	private void addPhotoToListAndGallery() {

		Location location = locationClient.getLastLocation();

		Photo photo = new Photo(location, photoFilePath);

		// Add the photo to the list
		poiPhotos.add(photo);
		
		imageViewIndex = poiPhotos.indexOf(photo);

		// Add the photo to the gallery
		Intent mediaScanIntent = new Intent(
				Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		Uri contentUri = Uri.fromFile(new File(photo.filePath));
		mediaScanIntent.setData(contentUri);
		this.sendBroadcast(mediaScanIntent);
	}

	private void checkSmartphoneSettings() {

		// Check for the Google play services on the device
		if (!playIsAvailable()) {
			PlayServicesDialog playServices = new PlayServicesDialog();
			dialogs.add(playServices);
			playServices.show(getSupportFragmentManager(), "playServices");
		}

		// Check for the availability of GPS
		else if (!gpsIsEnabled()) {
			GpsSettingsDialog gpsSettings = new GpsSettingsDialog();
			dialogs.add(gpsSettings);
			gpsSettings.show(getSupportFragmentManager(), "gpsSettings");
		}
	}

	private void clearImageView() {
		imageView.setImageResource(R.drawable.ic_swipe);
		cameraTextView.setVisibility(View.VISIBLE);
		deleteTextView.setVisibility(View.VISIBLE);
		previewTextView.setVisibility(View.VISIBLE);
		sendTextView.setVisibility(View.VISIBLE);
		rootView.setBackgroundColor(Color.WHITE);
		imageViewIndex = 0;
		poiPhotos = new ArrayList<Photo>();
		poiLocationTrace = new ArrayList<CustomLocation>();
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
			photoFilePath = new File(storageDir.getPath(), photoFileName + ".jpg").getPath();
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
			new GetTokenTask(this, gMail, SCOPE).execute();
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

	private void initPrimitiveStorage() {

		if (storagePrefs == null) {
			// Open Shared Preferences
			storagePrefs = getSharedPreferences(STORAGE_PREFS,
					Context.MODE_PRIVATE);

			// Get an editor
			storageEditor = storagePrefs.edit();
		} else if (storageEditor == null) {
			// Get an editor
			storageEditor = storagePrefs.edit();
		}
	}

	private void initRetainedFragment() {

		// Find the retained fragment on activity restarts
		FragmentManager fragmentManager = getFragmentManager();
		retainedFragment = (RetainedFragment) fragmentManager
				.findFragmentByTag("data");

		// Create the fragment and data the first time
		if (retainedFragment == null) {
			// add the fragment
			retainedFragment = new RetainedFragment();
			fragmentManager.beginTransaction().add(retainedFragment, "data")
					.commit();
		}
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
			}
		} else if (requestCode == INTENT_PICK_ACCOUNT) {
			accountPickerIsOpen = false;

			// Receiving a result from the AccountPicker
			if (resultCode == RESULT_OK) {
				gMail = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);

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
				takeAnotherPhoto = true;
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

		if (takeAnotherPhoto != null && takeAnotherPhoto) {
			takeAnotherPhoto = false;

			/*
			 * Add the photo to the photo list to send it later on and add it to
			 * the gallery
			 */
			addPhotoToListAndGallery();

			// After adding the first photo to the list, enable location
			// tracing.
			if (poiPhotos.size() == 1) {
				locationTraceEnabled = true;
			}

			// Photo taken and saved; allow the user to take another one
			takePhoto();
		}
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

		// Initialize the views
		imageView = (ImageView) findViewById(R.id.imageView);
		cameraTextView = (TextView) findViewById(R.id.camera);
		deleteTextView = (TextView) findViewById(R.id.delete);
		previewTextView = (TextView) findViewById(R.id.preview);
		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		progressCircle = (ProgressBar) findViewById(R.id.progressCircle);
		rootView = findViewById(android.R.id.content);
		sendTextView = (TextView) findViewById(R.id.send);

		EventBus.getDefault().register(this);
		skipRegisterOnNextResume = true;

		// Restore complex objects
		restoreRetainedObjects();

		// Restore primitive attributes
		restorePrimitiveAttributes();

		/*
		 * Create a new location client, using the enclosing class to handle
		 * callbacks
		 */
		locationClient = new LocationClient(this, this, this);

		// Enable gesture recognition
		gestureDetector = new GestureDetectorCompat(this, new SwipeGesture());
		gesturesEnabled = true;

		// Initialise the dialogs list
		dialogs = new ArrayList<DialogFragment>();

		// Initialize the pois
		if (pois == null) {
			pois = new ArrayList<Poi>();
		}

		// Initialize the poiPhotos
		if (poiPhotos == null) {
			poiPhotos = new ArrayList<Photo>();
		}

		// Initialize the trace
		if (poiLocationTrace == null) {
			poiLocationTrace = new ArrayList<CustomLocation>();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		poiMenuItem = menu.findItem(R.id.poi);

		// Restore the POI counter
		updatePoiMenuItemButton();

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		// Store the data in the fragment
		saveRetainedObjects();

		// Store primitive data
		savePrimitiveAttributes();
	}

	@Override
	public void onDisconnected() {

	}

	public void onEventMainThread(CheckTokenTaskFailureEvent event) {
		getUsername();
	}

	public void onEventMainThread(GetTokenFinishedEvent event) {
		gToken = event.gToken;
		if (resumeSend != null && resumeSend) {
			resumeSend = false;
			progressCircle.setVisibility(View.GONE);
			sendData();
		}
	}

	public void onEventMainThread(GpsSettingsDialogNegativeClickEvent event) {
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
	public void onEventMainThread(GpsSettingsDialogPositiveClickEvent event) {
		// User touched the dialog's positive button
		Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		startActivityForResult(intent, INTENT_ENABLE_GPS);
	}

	public void onEventMainThread(NetSettingsDialogNegativeClickEvent event) {
		// User touched the dialog's negative button
		if (!netIsEnabled()) {
			Toast.makeText(this, R.string.problem_no_net, Toast.LENGTH_SHORT)
					.show();
		}
	}

	public void onEventMainThread(NetSettingsDialogPositiveClickEvent event) {
		// User touched the dialog's positive button
		Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
		startActivityForResult(intent, INTENT_ENABLE_NET);
	}

	public void onEventMainThread(PlayServicesDialogNegativeClickEvent event) {
		Toast.makeText(this, R.string.problem_no_play, Toast.LENGTH_SHORT)
				.show();
		finish();
	}

	public void onEventMainThread(SendDataTaskEvent event) {
		if (event.httpStatus == null) {
			progressBar.setVisibility(View.GONE);
			gesturesEnabled = true;
			Toast.makeText(MainActivity.this,
					R.string.problem_no_server_connection, Toast.LENGTH_SHORT)
					.show();
		} else if (event.httpStatus.equals(HttpStatus.SC_UNAUTHORIZED)) {
			progressBar.setVisibility(View.GONE);
			progressCircle.setVisibility(View.VISIBLE);
			Toast.makeText(this, R.string.problem_token_expired,
					Toast.LENGTH_SHORT).show();
			resumeSend = true;
			getUsername();
		} else if (event.httpStatus.equals(HttpStatus.SC_OK)) {
			/*
			 * The poi data and photos were sent successfully. Therefore, we
			 * clear the imageView, delete the current imageView file and create
			 * a new photo list.
			 */
			pois.remove(0);
			updatePoiMenuItemButton();
			if (pois.isEmpty()) {
				progressBar.setVisibility(View.GONE);
				gesturesEnabled = true;
				clearImageView();
			} else {
				sendData();
			}
		} else {
			progressBar.setVisibility(View.GONE);
			gesturesEnabled = true;
			Toast.makeText(this, R.string.problem_send_data, Toast.LENGTH_LONG)
					.show();
		}
	}

	public void onEventMainThread(SwipeBottomEvent event) {
		if (gesturesEnabled) {
			if (poiPhotos != null && !poiPhotos.isEmpty()) {
				if (poiPhotos.size() == 1) {
					clearImageView();
				} else {
					Photo imageViewPhoto = poiPhotos.get(imageViewIndex);
					setupImageView(true);
					if (imageViewPhoto != null) {
						poiPhotos.remove(imageViewPhoto);
					}
				}
			}
		}
	}

	public void onEventMainThread(SwipeLeftEvent event) {
		if (gesturesEnabled) {
			takePhoto();
		}
	}

	public void onEventMainThread(SwipeRightEvent event) {
		if (gesturesEnabled) {
			setupImageView(true);
		}
	}

	public void onEventMainThread(SwipeTopEvent event) {
		if (gesturesEnabled) {
			if (poiPhotos != null && !poiPhotos.isEmpty()) {
				locationTraceEnabled = false;
				savePoi();
			} else if (pois != null && !pois.isEmpty()) {
				// Check for network connectivity.
				if (netIsEnabled()) {
					new CheckTokenTask().execute(gToken);
					sendData();
				} else {
					NetSettingsDialog netSettings = new NetSettingsDialog();
					netSettings
							.show(getSupportFragmentManager(), "netSettings");
				}
			} else {
				Toast.makeText(this, R.string.problem_no_photo,
						Toast.LENGTH_LONG).show();
			}
		}
	}

	public void onEventMainThread(TransferProgressChangedEvent event) {
		accumulatedTransferSize += event.additionalTransferSize;
		progressBar
				.setProgress((int) (accumulatedTransferSize * 100 / totalTransferSize));
	}

	@Override
	public void onLocationChanged(Location location) {
		if (locationTraceEnabled != null && locationTraceEnabled) {
			CustomLocation customLocation = new CustomLocation(location);
			poiLocationTrace.add(customLocation);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		/*
		 * Handle action bar item clicks here. The action bar will automatically
		 * handle clicks on the Home/Up button, so long as you specify a parent
		 * activity in AndroidManifest.xml.
		 */
		switch (item.getItemId()) {
		case R.id.changeAccount:
			gMail = null;
			getUsername();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onPause() {

		/*
		 * Dismiss all dialogs. If the conditions are met they open again after
		 * the app comes in the foreground.
		 */
		for (DialogFragment dialog : dialogs) {
			dialog.dismiss();
		}
		EventBus.getDefault().unregister(this);
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (skipRegisterOnNextResume) {
			// registered in onCreate, skip registration in this run
			skipRegisterOnNextResume = false;
		} else {
			EventBus.getDefault().register(this);
		}

		// Restore the progress bar
		if (progressBar.getVisibility() == View.VISIBLE) {
			EventBus.getDefault().post(new TransferProgressChangedEvent(0));
		}
	}

	// Called when the Activity is restarted, even before it becomes visible.
	@Override
	protected void onStart() {

		super.onStart();

		checkSmartphoneSettings();

		// Ask for user's mail address and/or token if not available
		if (gMail == null || gToken == null) {
			getUsername();
		}

		/*
		 * Connect the client. Don't re-start any requests here; instead, wait
		 * for onResume()
		 */
		if (!locationClient.isConnected()) {
			locationClient.connect();
		}

		setupImageView(false);
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

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		this.gestureDetector.onTouchEvent(event);
		return super.onTouchEvent(event);
	}

	/*
	 * Starts an activity in Google Play Services so the user can pick an
	 * account
	 */
	private void pickUserAccount() {
		if (accountPickerIsOpen == null || !accountPickerIsOpen) {
			accountPickerIsOpen = true;
			String[] accountTypes = new String[] { "com.google" };
			Intent intent = AccountPicker.newChooseAccountIntent(null, null,
					accountTypes, false, null, null, null, null);
			startActivityForResult(intent, INTENT_PICK_ACCOUNT);
		}
	}

	// Check whether the Google play services are available
	private boolean playIsAvailable() {
		if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS) {
			return true;
		} else {
			return false;
		}
	}

	private void restorePrimitiveAttributes() {

		initPrimitiveStorage();

		// Restore simple attributes from persistent storage
		if (storagePrefs.contains(STORAGE_ACCOUNTPICKERISOPEN)) {
			accountPickerIsOpen = storagePrefs.getBoolean(
					STORAGE_ACCOUNTPICKERISOPEN, false);
		}
		if (storagePrefs.contains(STORAGE_ACCUMULATEDTRANSFERSIZE)) {
			accumulatedTransferSize = storagePrefs.getLong(
					STORAGE_ACCUMULATEDTRANSFERSIZE, 0L);
		}
		if (storagePrefs.contains(STORAGE_GESTURESENABLED)) {
			gesturesEnabled = storagePrefs.getBoolean(STORAGE_GESTURESENABLED,
					true);
		}
		if (storagePrefs.contains(STORAGE_GMAIL)) {
			gMail = storagePrefs.getString(STORAGE_GMAIL, null);
		}
		if (storagePrefs.contains(STORAGE_GTOKEN)) {
			gToken = storagePrefs.getString(STORAGE_GTOKEN, null);
		}
		if (storagePrefs.contains(STORAGE_IMAGEVIEWINDEX)) {
			imageViewIndex = storagePrefs.getInt(STORAGE_IMAGEVIEWINDEX, 0);
		}
		if (storagePrefs.contains(STORAGE_LOCATIONTRACEENABLED)) {
			locationTraceEnabled = storagePrefs.getBoolean(
					STORAGE_LOCATIONTRACEENABLED, false);
		}
		if (storagePrefs.contains(STORAGE_PHOTOFILEPATH)) {
			photoFilePath = storagePrefs.getString(STORAGE_PHOTOFILEPATH, null);
		}
		if (storagePrefs.contains(STORAGE_PROGRESSBARVISIBILITY)) {
			progressBar.setVisibility(storagePrefs.getInt(
					STORAGE_PROGRESSBARVISIBILITY, View.GONE));
		}
		if (storagePrefs.contains(STORAGE_PROGRESSCIRCLEVISIBILITY)) {
			progressCircle.setVisibility(storagePrefs.getInt(
					STORAGE_PROGRESSCIRCLEVISIBILITY, View.GONE));
		}
		if (storagePrefs.contains(STORAGE_RESUMESEND)) {
			resumeSend = storagePrefs.getBoolean(STORAGE_RESUMESEND, false);
		}
		if (storagePrefs.contains(STORAGE_TAKEANOTHERPHOTO)) {
			takeAnotherPhoto = storagePrefs.getBoolean(STORAGE_TAKEANOTHERPHOTO, false);
		}
		if (storagePrefs.contains(STORAGE_TOTALTRANSFERSIZE)) {
			totalTransferSize = storagePrefs.getLong(STORAGE_TOTALTRANSFERSIZE,
					0L);
		}
	}

	private void restoreRetainedObjects() {

		if (retainedFragment == null) {
			initRetainedFragment();
		}

		poiPhotos = retainedFragment.getPoiPhotos();
		pois = retainedFragment.getPois();
		poiLocationTrace = retainedFragment.getPoiLocationTrace();
	}

	private void savePoi() {
		pois.add(new Poi(poiLocationTrace, poiPhotos));
		updatePoiMenuItemButton();
		clearImageView();
	}

	private void savePrimitiveAttributes() {

		initPrimitiveStorage();

		if (accountPickerIsOpen != null) {
			storageEditor.putBoolean(STORAGE_ACCOUNTPICKERISOPEN,
					accountPickerIsOpen);
		}
		if (accumulatedTransferSize != null) {
			storageEditor.putLong(STORAGE_ACCUMULATEDTRANSFERSIZE,
					accumulatedTransferSize);
		}
		if (gesturesEnabled != null) {
			storageEditor.putBoolean(STORAGE_GESTURESENABLED, gesturesEnabled);
		}
		if (gMail != null) {
			storageEditor.putString(STORAGE_GMAIL, gMail);
		}
		if (gToken != null) {
			storageEditor.putString(STORAGE_GTOKEN, gToken);
		}
		storageEditor.putInt(STORAGE_IMAGEVIEWINDEX, imageViewIndex);
		if (locationTraceEnabled != null) {
			storageEditor.putBoolean(STORAGE_LOCATIONTRACEENABLED,
					locationTraceEnabled);
		}
		if (photoFilePath != null) {
			storageEditor.putString(STORAGE_PHOTOFILEPATH, photoFilePath);
		}
		if (resumeSend != null) {
			storageEditor.putBoolean(STORAGE_RESUMESEND, resumeSend);
		}
		storageEditor.putInt(STORAGE_PROGRESSBARVISIBILITY,
				progressBar.getVisibility());
		storageEditor.putInt(STORAGE_PROGRESSCIRCLEVISIBILITY,
				progressCircle.getVisibility());
		if (takeAnotherPhoto != null) {
			storageEditor.putBoolean(STORAGE_TAKEANOTHERPHOTO, takeAnotherPhoto);
		}
		if (totalTransferSize != null) {
			storageEditor.putLong(STORAGE_TOTALTRANSFERSIZE, totalTransferSize);
		}
		storageEditor.commit();
	}

	private void saveRetainedObjects() {

		if (retainedFragment == null) {
			initRetainedFragment();
		}

		retainedFragment.setPoiPhotos(poiPhotos);
		retainedFragment.setPois(pois);
		retainedFragment.setPoiLocationTrace(poiLocationTrace);
	}

	// Send the data to the backend server
	private void sendData() {

		gesturesEnabled = false;
		accumulatedTransferSize = 0L;
		progressBar.setProgress((int) (long) accumulatedTransferSize);
		progressBar.setVisibility(View.VISIBLE);

		TransferProgressMultipartTypedOutput data = new TransferProgressMultipartTypedOutput();
		data.addPart("token", new TransferProgressTypedString(gToken));

		if (pois != null && !pois.isEmpty()) {
			Poi poi = pois.get(0);
			int index = 0;

			for (CustomLocation location : poi.locationTrace) {
				if (location.accuracy != null) {
					data.addPart(
							"trace_" + index + "_accuracy",
							new TransferProgressTypedString(String
									.valueOf(location.accuracy)));
				}
				if (location.altitude != null) {
					data.addPart(
							"trace_" + index + "_altitude",
							new TransferProgressTypedString(String
									.valueOf(location.altitude)));
				}
				if (location.bearing != null) {
					data.addPart(
							"trace_" + index + "_bearing",
							new TransferProgressTypedString(String
									.valueOf(location.bearing)));
				}
				data.addPart(
						"trace_" + index + "_latitude",
						new TransferProgressTypedString(String.valueOf(location
								.latitude)));
				data.addPart(
						"trace_" + index + "_longitude",
						new TransferProgressTypedString(String.valueOf(location
								.longitude)));
				data.addPart("trace_" + index + "_provider",
						new TransferProgressTypedString(location.provider));
				data.addPart(
						"trace_" + index + "_time",
						new TransferProgressTypedString(String.valueOf(location
								.time)));
				index++;
			}
			index = 0;

			for (Photo photo : poi.photos) {

				if (photo.accuracy != null) {
					data.addPart(
							"photo_" + index + "_accuracy",
							new TransferProgressTypedString(String
									.valueOf(photo.accuracy)));
				}
				if (photo.altitude != null) {
					data.addPart(
							"photo_" + index + "_altitude",
							new TransferProgressTypedString(String
									.valueOf(photo.altitude)));
				}
				if (photo.bearing != null) {
					data.addPart(
							"photo_" + index + "_bearing",
							new TransferProgressTypedString(String
									.valueOf(photo.bearing)));
				}
				data.addPart(
						"photo_" + index + "_latitude",
						new TransferProgressTypedString(String.valueOf(photo
								.latitude)));
				data.addPart(
						"photo_" + index + "_longitude",
						new TransferProgressTypedString(String.valueOf(photo
								.longitude)));
				data.addPart("photo_" + index + "_provider",
						new TransferProgressTypedString(photo.provider));
				data.addPart(
						"photo_" + index + "_time",
						new TransferProgressTypedString(String.valueOf(photo
								.time)));

				String photoFileUri = Uri.fromFile(new File(photo.filePath)).toString();
				String mimeType = null;
				String extension = MimeTypeMap
						.getFileExtensionFromUrl(photoFileUri);
				MimeTypeMap mime = MimeTypeMap.getSingleton();
				mimeType = mime.getMimeTypeFromExtension(extension);
				TransferProgressTypedFile file = new TransferProgressTypedFile(
						mimeType, new File(photo.filePath));
				data.addPart("photo_" + index + "_file", file);
				index++;
			}
		}

		totalTransferSize = data.length();

		new SendDataTask().execute(data);
	}

	// Place a photo in the imageView if available
	private void setupImageView(boolean choosePreviousPhoto) {
		if (poiPhotos != null && !poiPhotos.isEmpty()) {
			int newIndex;
			if (choosePreviousPhoto && imageViewIndex > 0) {
				newIndex = imageViewIndex - 1;
			} else {
				newIndex = poiPhotos.size() - 1;
			}
			Photo imageViewPhoto = poiPhotos.get(newIndex);
			cameraTextView.setVisibility(View.GONE);
			deleteTextView.setVisibility(View.GONE);
			previewTextView.setVisibility(View.GONE);
			sendTextView.setVisibility(View.GONE);
			Uri photoUri = Uri.fromFile(new File(imageViewPhoto.filePath));
			imageView.setImageURI(photoUri);
			rootView.setBackgroundColor(Color.BLACK);
			imageViewIndex = newIndex;
		} else {
			rootView.setBackgroundColor(Color.WHITE);
		}
	}

	/*
	 * In response to a request to start updates, send a request to Location
	 * Services
	 */
	private void startPeriodicUpdates() {

		// Create a new location parameters object
		LocationRequest locationRequest = LocationRequest.create();

		// Set the update interval ceiling in milliseconds
		locationRequest.setFastestInterval(LOCALIZATION_LOWER_LIMIT);

		// Set the update interval in milliseconds
		locationRequest.setInterval(LOCALIZATION_UPPER_LIMIT);

		// Use high accuracy
		locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

		locationClient.requestLocationUpdates(locationRequest, this);
	}

	/*
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
			if (photoFilePath != null) {

				// Set the image file name
				takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT,
						Uri.fromFile(new File(photoFilePath)));

				startActivityForResult(takePhotoIntent, INTENT_TAKE_PHOTO);
			}
		}
	}

	private void updatePoiMenuItemButton() {
		Button poiMenuItemButton = (Button) poiMenuItem.getActionView()
				.findViewById(R.id.poiText);

		if (pois.isEmpty()) {
			poiMenuItem.setVisible(false);
		} else {
			poiMenuItem.setVisible(true);
			poiMenuItemButton.setText(String.valueOf(pois.size()));
		}
	}
}
