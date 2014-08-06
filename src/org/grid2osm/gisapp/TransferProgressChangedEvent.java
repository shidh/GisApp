package org.grid2osm.gisapp;

public class TransferProgressChangedEvent {

	final long additionalTransferSize;

	TransferProgressChangedEvent(long additionalTransferSize) {
		this.additionalTransferSize = additionalTransferSize;
	}
}
