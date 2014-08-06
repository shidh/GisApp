package org.grid2osm.gisapp;

import org.grid2osm.gisapp.retrofit.TransferProgressMultipartTypedOutput;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.POST;

public interface RestClientInterface {

	@POST("/createPoi/")
	void createPoi(@Body TransferProgressMultipartTypedOutput data,
			Callback<Response> callback);
}
