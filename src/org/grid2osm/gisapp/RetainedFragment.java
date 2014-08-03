package org.grid2osm.gisapp;

import java.io.File;
import java.util.ArrayList;

import android.app.Fragment;
import android.os.Bundle;

/*
 * https://developer.android.com/guide/topics/resources/runtime-changes.html#RetainingAnObject
 */
public class RetainedFragment extends Fragment {

	/*
	 * Complex data objects from MainActivity, that we want to retain. Take a
	 * look at MainActivity to read the object's description. If you want to
	 * retain simple attributes, use the SharedPreferences.
	 */
	private GetTokenTask getTokenTask;
	private File imageViewFile;
	private File photoFile;
	private ArrayList<File> photoFiles;

	public GetTokenTask getGetTokenTask() {
		return getTokenTask;
	}

	public File getImageViewFile() {
		return imageViewFile;
	}

	public File getPhotoFile() {
		return photoFile;
	}

	public ArrayList<File> getPhotoFiles() {
		return photoFiles;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// retain this fragment
		setRetainInstance(true);
	}

	public void setGetTokenTask(GetTokenTask getTokenTask) {
		this.getTokenTask = getTokenTask;
	}

	public void setImageViewFile(File imageViewFile) {
		this.imageViewFile = imageViewFile;
	}

	public void setPhotoFile(File photoFile) {
		this.photoFile = photoFile;
	}

	public void setPhotoFiles(ArrayList<File> photoFiles) {
		this.photoFiles = photoFiles;
	}
}
