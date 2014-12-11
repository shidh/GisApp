package org.grid2osm.gisapp;

import org.grid2osm.gisapp.retrofit.TransferProgressMultipartTypedOutput;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

public interface RestClientInterface {

	@GET("/oauth2/v1/tokeninfo")
	void checkToken(@Query("id_token") String token, Callback<Response> callback);

	@POST("/createPoi/")
	void createPoi(@Body TransferProgressMultipartTypedOutput data,
			Callback<Response> callback);
}
