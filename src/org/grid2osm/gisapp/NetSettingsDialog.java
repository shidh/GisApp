package org.grid2osm.gisapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class NetSettingsDialog extends DialogFragment {

	/*
	 * The activity that creates an instance of this dialog fragment must
	 * implement this interface in order to receive event callbacks. Each method
	 * passes the DialogFragment in case the host needs to query it.
	 */
	public interface NetSettingsListener {
		public void onNetSettingsDialogNegativeClick(DialogFragment dialog);

		public void onNetSettingsDialogPositiveClick(DialogFragment dialog);
	}

	// Use this instance of the interface to deliver action events
	NetSettingsListener mListener;

	// Override the Fragment.onAttach() method to instantiate the
	// NetSettingsListener
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// Verify that the host activity implements the callback interface
		try {
			// Instantiate the NetSettingsListener so we can send events to the
			// host
			mListener = (NetSettingsListener) activity;
		} catch (ClassCastException e) {
			// The activity doesn't implement the interface, throw exception
			throw new ClassCastException(activity.toString()
					+ " must implement NetSettingsListener");
		}
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		// Build the dialog and set up the button click handlers
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage(R.string.net_message)
				.setTitle(R.string.net_title)
				.setPositiveButton(R.string.settings,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								// Send the positive button event back to the
								// host activity
								mListener
										.onNetSettingsDialogPositiveClick(NetSettingsDialog.this);
							}
						})
				.setNegativeButton(R.string.cancel,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								// Send the negative button event back to the
								// host activity
								mListener
										.onNetSettingsDialogNegativeClick(NetSettingsDialog.this);
							}
						});
		// Create the AlertDialog object and return it
		return builder.create();
	}

}
