package org.grid2osm.gisapp;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.POST;
import retrofit.mime.MultipartTypedOutput;

public interface RestClientInterface {

	@POST("/createPoi/")
	void createPoi(@Body MultipartTypedOutput body, Callback<Response> callback);
}
