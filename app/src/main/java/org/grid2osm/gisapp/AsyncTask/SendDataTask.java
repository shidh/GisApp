package org.grid2osm.gisapp.AsyncTask;

import java.util.concurrent.TimeUnit;

import org.grid2osm.gisapp.RestClientInterface;
import org.grid2osm.gisapp.event.SendDataTaskEvent;
import org.grid2osm.gisapp.retrofit.TransferProgressMultipartTypedOutput;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;

import com.squareup.okhttp.OkHttpClient;

import de.greenrobot.event.EventBus;

import android.os.AsyncTask;

public class SendDataTask extends
		AsyncTask<TransferProgressMultipartTypedOutput, Void, Void> {

	private static final String REST_SERVER = "https://www.grid2osm.org";

	@Override
	protected Void doInBackground(
			TransferProgressMultipartTypedOutput... params) {

		OkHttpClient okHttpClient = new OkHttpClient();
		okHttpClient.setWriteTimeout(3000, TimeUnit.MILLISECONDS);
		OkClient okClient = new OkClient(okHttpClient);
		RestAdapter restAdapter = new RestAdapter.Builder().setClient(okClient)
				.setEndpoint(REST_SERVER).build();
		RestClientInterface restClientInterface = restAdapter
				.create(RestClientInterface.class);

		Callback<Response> callback = new Callback<Response>() {

			@Override
			public void failure(RetrofitError error) {
				if (error == null || error.getResponse() == null) {

					EventBus.getDefault().post(new SendDataTaskEvent(null));
				} else {
					EventBus.getDefault().post(
							new SendDataTaskEvent(error.getResponse()
									.getStatus()));
				}
			}

			@Override
			public void success(Response response0, Response response1) {

				EventBus.getDefault().post(
						new SendDataTaskEvent(response0.getStatus()));
			}
		};

		restClientInterface.createPoi(params[0], callback);

		return null;
	}
}
