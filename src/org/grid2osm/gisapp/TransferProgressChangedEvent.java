package org.grid2osm.gisapp;

public class TransferProgressChangedEvent {

	public final long additionalTransferSize;

	public TransferProgressChangedEvent(long additionalTransferSize) {
		this.additionalTransferSize = additionalTransferSize;
	}
}
