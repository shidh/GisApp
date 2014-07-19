package org.grid2osm.gisapp;

import java.io.IOException;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;

import android.os.AsyncTask;

public class GetUserNameTask extends AsyncTask<Void, Void, String> {

	// Used to store the token in the main thread
	public interface GetUserNameTaskInterface {
		public void onGetUserNameTaskFinished(String token);
	}

	MainActivity mActivity;
	String mScope;

	String mEmail;

	GetUserNameTask(MainActivity activity, String name, String scope) {
		this.mActivity = activity;
		this.mScope = scope;
		this.mEmail = name;
	}

	/*
	 * Executes the asynchronous job. This runs when you call execute() on the
	 * AsyncTask instance.
	 */
	@Override
	protected String doInBackground(Void... params) {
		String token = null;
		try {
			token = fetchToken();
		} catch (IOException e) {
			// The fetchToken() method handles Google-specific exceptions,
			// so this indicates something went wrong at a higher level.
			// TIP: Check for network connectivity before starting the
			// AsyncTask.
		}
		return token;
	}

	/*
	 * Gets an authentication token from Google and handles any
	 * GoogleAuthException that may occur.
	 */
	protected String fetchToken() throws IOException {
		try {
			return GoogleAuthUtil.getToken(mActivity, mEmail, mScope);
		} catch (UserRecoverableAuthException userRecoverableException) {
			// GooglePlayServices.apk is either old, disabled, or not present
			// so we need to show the user some UI in the activity to recover.
			mActivity.handleException(userRecoverableException);
		} catch (GoogleAuthException fatalException) {
			// Some other type of unrecoverable exception has occurred.
			// Report and log the error as appropriate for your app.
		}
		return null;
	}

	@Override
	protected void onPostExecute(String result) {
		mActivity.onGetUserNameTaskFinished(result);
	}
}
