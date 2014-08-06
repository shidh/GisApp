package org.grid2osm.gisapp;

public class GetTokenFinishedEvent {

	final String gToken;

	GetTokenFinishedEvent(String gToken) {
		this.gToken = gToken;
	}
}
