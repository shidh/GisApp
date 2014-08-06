package org.grid2osm.gisapp.retrofit;

import java.io.IOException;
import java.io.OutputStream;

import org.grid2osm.gisapp.event.TransferProgressChangedEvent;

import de.greenrobot.event.EventBus;

import retrofit.mime.TypedByteArray;

public class TransferProgressTypedByteArray extends TypedByteArray {

	public TransferProgressTypedByteArray(String mimeType, byte[] bytes) {
		super(mimeType, bytes);
	}

	@Override
	public void writeTo(OutputStream out) throws IOException {
		EventBus.getDefault().post(
				new TransferProgressChangedEvent(this.getBytes().length));
		out.write(this.getBytes());
	}

}
