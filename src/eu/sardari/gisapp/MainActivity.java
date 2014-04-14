package eu.sardari.gisapp;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.provider.MediaStore;

public class MainActivity extends ActionBarActivity {

	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;

	/** Create a file Uri for saving an image or video */
	private Uri getOutputMediaFileUri() {

		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

			// Store files in a private folder only accessible by the
			// application
			File mediaStorageDir = new File(
					getExternalFilesDir(Environment.DIRECTORY_PICTURES),
					"camera");

			// Create the storage directory if it does not exist
			if (!mediaStorageDir.exists()) {
				if (!mediaStorageDir.mkdirs()) {
					Log.d("camera", "failed to create directory");
					return null;
				}
			}

			// Create a media file name
			String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
					.format(new Date());
			File mediaFile = new File(mediaStorageDir.getPath()
					+ File.separator + "IMG_" + timeStamp + ".jpg");

			return Uri.fromFile(mediaFile);
		}

		return null;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				// Image captured and saved to fileUri specified in the Intent
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

		// create Intent to take a picture and return control to the calling
		// application
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		// create a file to save the image
		Uri fileUri = getOutputMediaFileUri();

		// set the image file name
		intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

		// start the image capture Intent
		startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
	}

}
