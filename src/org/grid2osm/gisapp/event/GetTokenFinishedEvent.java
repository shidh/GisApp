package org.grid2osm.gisapp.event;

public final class GetTokenFinishedEvent {

	public final String gToken;

	public GetTokenFinishedEvent(String gToken) {
		this.gToken = gToken;
	}
}
