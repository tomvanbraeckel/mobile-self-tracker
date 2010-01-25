package org.tracking;

import java.io.File;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.WindowManager;

/**
 * This dialog activity pops up and asks you to take a picture.
 * 
 * @author tom
 * 
 */
public class TakePicture extends Activity {
	private static final int DIALOG_YES_NO_MESSAGE = 1;

	/**
	 * Initialization of the Activity after it is first created. Must at least
	 * call {@link android.app.Activity#setContentView setContentView()} to
	 * describe what is to be displayed in the screen.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// Be sure to call the super class.
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND, WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
		showDialog(DIALOG_YES_NO_MESSAGE);

	}

	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_YES_NO_MESSAGE:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Take a picture ?").setCancelable(false).setPositiveButton("Yes",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							File sdcard = Environment.getExternalStorageDirectory();
							File dir = new File(sdcard.getAbsolutePath() + "/images/SelfTracker");
							// create directory for images
							dir.mkdirs();
							Calendar c = Calendar.getInstance();
							File file = new File(dir, c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1) + "-"
									+ c.get(Calendar.DAY_OF_MONTH) + " " + c.get(Calendar.HOUR_OF_DAY) + "."
									+ c.get(Calendar.MINUTE) + "." + c.get(Calendar.SECOND) + ".jpg");
							// ask camera to take a picture
							Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
							intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
							startActivity(intent);
							finish(); // remove from history stack, free up all
										// stuff
						}
					}).setNegativeButton("No", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
					finish(); // remove from history stack, free up all stuff
				}
			});
			return builder.create();
		}
		return null;
	}
}