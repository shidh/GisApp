package org.grid2osm.gisapp.retrofit;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.grid2osm.gisapp.event.TransferProgressChangedEvent;

import de.greenrobot.event.EventBus;

import retrofit.mime.TypedFile;

public class TransferProgressTypedFile extends TypedFile {

	private static final int BUFFER_SIZE = 4096;

	public TransferProgressTypedFile(String mimeType, File file) {
		super(mimeType, file);
	}

	@Override
	public void writeTo(OutputStream out) throws IOException {
		byte[] buffer = new byte[BUFFER_SIZE];
		FileInputStream in = new FileInputStream(super.file());
		try {
			int read;
			while ((read = in.read(buffer)) != -1) {
				EventBus.getDefault().post(
						new TransferProgressChangedEvent(read));
				out.write(buffer, 0, read);
			}
		} finally {
			in.close();
		}
	}
}
