package org.grid2osm.gisapp.dialog;

import org.grid2osm.gisapp.R;
import org.grid2osm.gisapp.event.GpsSettingsDialogNegativeClickEvent;
import org.grid2osm.gisapp.event.GpsSettingsDialogPositiveClickEvent;

import de.greenrobot.event.EventBus;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class GpsSettingsDialog extends DialogFragment {

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		// Build the dialog and set up the button click handlers
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage(R.string.gps_message)
				.setTitle(R.string.gps_title)
				.setPositiveButton(R.string.settings,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								// Send the positive button event back to the
								// host activity
								EventBus.getDefault()
										.post(new GpsSettingsDialogPositiveClickEvent());
							}
						})
				.setNegativeButton(R.string.cancel,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								// Send the negative button event back to the
								// host activity
								EventBus.getDefault()
										.post(new GpsSettingsDialogNegativeClickEvent());
							}
						});
		// Create the AlertDialog object and return it
		return builder.create();
	}

}
