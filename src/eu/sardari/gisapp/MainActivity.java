package eu.sardari.gisapp;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;

import android.support.v7.app.ActionBarActivity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.provider.MediaStore;

public class MainActivity extends ActionBarActivity implements GooglePlayServicesClient.ConnectionCallbacks, OnConnectionFailedListener, LocationListener {

	private static ArrayList<File> photoFiles;
	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	private LocationClient locationClient;
	private LocationRequest locationRequest;

	/** Create a file Uri for saving an image or video */
	private Uri getOutputMediaFileUri() {

		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

			// Store files in a private folder only accessible by the
			// application
			File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "camera");

			// Create the storage directory if it does not exist
			if (!mediaStorageDir.exists()) {
				if (!mediaStorageDir.mkdirs()) {
					Log.d("camera", "failed to create directory");
					return null;
				}
			}

			// Create a media file name
			String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
			File mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");

			return Uri.fromFile(mediaFile);
		}

		return null;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				// Image captured and saved to fileUri specified in the Intent
				takePhoto();
			} else if (resultCode == RESULT_CANCELED) {
				// User cancelled the image capture
			} else {
				// Image capture failed, advise user
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Button button = (Button) findViewById(R.id.takePhoto);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				photoFiles = new ArrayList<File>();
				takePhoto();
			}
		});
		button = (Button) findViewById(R.id.sendPhoto);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sendPhoto();
			}
		});

		locationClient = new LocationClient(this, this, this);
		locationRequest = LocationRequest.create();
		locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		locationRequest.setInterval(5000);
		locationRequest.setFastestInterval(1000);
	}

	private void sendPhoto() {

		if (photoFiles != null && !photoFiles.isEmpty() && servicesConnected()) {

			locationClient.requestLocationUpdates(locationRequest, this);
			Location location = locationClient.getLastLocation();

			if (location != null) {

				final ArrayList<File> files = photoFiles;
				Ion.with(this, "https://www.grid2osm.org/createPoi/").setMultipartParameter("accuracy", String.valueOf(location.getAccuracy()))
						.setMultipartParameter("altitude", String.valueOf(location.getAltitude())).setMultipartParameter("bearing", String.valueOf(location.getBearing()))
						.setMultipartParameter("latitude", String.valueOf(location.getLatitude())).setMultipartParameter("longitude", String.valueOf(location.getLongitude()))
						.setMultipartParameter("provider", String.valueOf(location.getProvider())).setMultipartParameter("time", String.valueOf(location.getTime())).asString().withResponse()
						.setCallback(new FutureCallback<Response<String>>() {
							@Override
							public void onCompleted(Exception e, Response<String> result) {

								File echoedFile = MainActivity.this.getFileStreamPath("echo");

								if (files != null && result != null) {
									String poiId = result.getResult();

									for (File file : files) {
										if (file.exists()) {
											Ion.with(MainActivity.this, "https://www.grid2osm.org/savePicture/").setMultipartParameter("id", poiId).setMultipartFile("image", file).write(echoedFile);
										}
									}
								}

							}
						});

			}

		}

		photoFiles = null;
	}

	private void takePhoto() {

		// create Intent to take a picture and return control to the calling
		// application
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		// create a file to save the image
		Uri fileUri = getOutputMediaFileUri();

		// set the image file name
		intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

		photoFiles.add(new File(fileUri.getPath()));

		// start the image capture Intent
		startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
	}

	private boolean servicesConnected() {
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

		if (ConnectionResult.SUCCESS == resultCode) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		locationClient.connect();
	}

	@Override
	protected void onStop() {
		locationClient.disconnect();
		super.onStop();
	}

	@Override
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnected(Bundle arg0) {
		// TODO Auto-generated method stub
		locationClient.requestLocationUpdates(locationRequest, this);
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub

	}
}
