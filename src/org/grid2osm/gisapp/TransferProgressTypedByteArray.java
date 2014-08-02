package org.grid2osm.gisapp;

import java.io.IOException;
import java.io.OutputStream;

import retrofit.mime.TypedByteArray;

public class TransferProgressTypedByteArray extends TypedByteArray {

	private final TransferProgressListener listener;

	public TransferProgressTypedByteArray(String mimeType, byte[] bytes,
			TransferProgressListener listener) {
		super(mimeType, bytes);
		this.listener = listener;
	}

	@Override
	public void writeTo(OutputStream out) throws IOException {
		listener.transferred(this.getBytes().length);
		out.write(this.getBytes());
	}

}
