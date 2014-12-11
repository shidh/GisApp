package org.grid2osm.gisapp.retrofit;

import java.io.UnsupportedEncodingException;

public class TransferProgressTypedString extends TransferProgressTypedByteArray {

	private static byte[] convertToBytes(String string) {
		try {
			return string.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	public TransferProgressTypedString(String string) {
		super("text/plain; charset=UTF-8", convertToBytes(string));
	}
}
