package org.grid2osm.gisapp.event;

public final class SendDataTaskEvent {

	public final Integer httpStatus;

	public SendDataTaskEvent(Integer httpStatus) {
		this.httpStatus = httpStatus;
	}
}
