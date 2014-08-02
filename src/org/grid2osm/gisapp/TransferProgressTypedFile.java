package org.grid2osm.gisapp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import retrofit.mime.TypedFile;

public class TransferProgressTypedFile extends TypedFile {

	private static final int BUFFER_SIZE = 4096;
	private final TransferProgressListener listener;

	public TransferProgressTypedFile(String mimeType, File file,
			TransferProgressListener listener) {
		super(mimeType, file);
		this.listener = listener;
	}

	@Override
	public void writeTo(OutputStream out) throws IOException {
		byte[] buffer = new byte[BUFFER_SIZE];
		FileInputStream in = new FileInputStream(super.file());
		try {
			int read;
			while ((read = in.read(buffer)) != -1) {
				listener.transferred(read);
				out.write(buffer, 0, read);
			}
		} finally {
			in.close();
		}
	}
}
