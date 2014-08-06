package org.grid2osm.gisapp.dialog;

import org.grid2osm.gisapp.R;
import org.grid2osm.gisapp.event.PlayServicesDialogNegativeClickEvent;

import de.greenrobot.event.EventBus;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class PlayServicesDialog extends DialogFragment {

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		// Build the dialog and set up the button click handlers
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage(R.string.play_message)
				.setTitle(R.string.play_title)
				.setNegativeButton(R.string.close,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								// Send the negative button event back to the
								// host activity
								EventBus.getDefault()
										.post(new PlayServicesDialogNegativeClickEvent());
							}
						});
		// Create the AlertDialog object and return it
		return builder.create();
	}

}
