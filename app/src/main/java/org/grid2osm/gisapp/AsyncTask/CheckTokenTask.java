package org.grid2osm.gisapp.AsyncTask;

import org.grid2osm.gisapp.RestClientInterface;
import org.grid2osm.gisapp.event.CheckTokenTaskFailureEvent;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;

import com.squareup.okhttp.OkHttpClient;

import de.greenrobot.event.EventBus;

import android.os.AsyncTask;

public class CheckTokenTask extends AsyncTask<String, Void, Void> {

	private static final String REST_SERVER = "https://www.googleapis.com";

	@Override
	protected Void doInBackground(String... params) {

		OkHttpClient okHttpClient = new OkHttpClient();
		OkClient okClient = new OkClient(okHttpClient);
		RestAdapter restAdapter = new RestAdapter.Builder().setClient(okClient)
				.setEndpoint(REST_SERVER).build();
		RestClientInterface restClientInterface = restAdapter
				.create(RestClientInterface.class);

		Callback<Response> callback = new Callback<Response>() {

			@Override
			public void failure(RetrofitError error) {
				EventBus.getDefault().post(new CheckTokenTaskFailureEvent());
			}

			@Override
			public void success(Response response0, Response response1) {
			}
		};

		restClientInterface.checkToken(params[0], callback);

		return null;
	}
}
