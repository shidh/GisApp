package org.grid2osm.gisapp;

public class SendDataTaskEvent {

	final Integer httpStatus;

	SendDataTaskEvent(Integer httpStatus) {
		this.httpStatus = httpStatus;
	}
}
