package org.grid2osm.gisapp.event;

public final class TransferProgressChangedEvent {

	public final long additionalTransferSize;

	public TransferProgressChangedEvent(long additionalTransferSize) {
		this.additionalTransferSize = additionalTransferSize;
	}
}
