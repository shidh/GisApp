package org.grid2osm.gisapp;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.Multipart;
import retrofit.http.Part;
import retrofit.http.POST;
import retrofit.mime.TypedFile;

public interface RestClientInterface {

	@FormUrlEncoded
	@POST("/createPoi/")
	void createPoi(@Field("accuracy") Float accuracy,
			@Field("altitude") Double altitude,
			@Field("bearing") Float bearing,
			@Field("latitude") Double latitude,
			@Field("longitude") Double longitude,
			@Field("provider") String provider, @Field("time") Long time,
			@Field("token") String token, Callback<Boolean> callback);

	@Multipart
	@POST("/savePhoto/")
	void savePhoto(@Part("photo") TypedFile photo, @Part("poiId") Long poiId,
			@Part("token") String token, Callback<Boolean> callback);
}
