package org.tracking;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class SelfTracker extends Activity {
	private Toast mToast;
	private static final int PREFERENCES_ID = Menu.FIRST;

	// prefs
	private static final Integer MIN_INTERVAL = 10;
	private Integer interval = 0;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// Button clicks
		Button button = (Button) findViewById(R.id.take_picture);
		button.setOnClickListener(mTakePictureListener);
		button = (Button) findViewById(R.id.start_repeating);
		button.setOnClickListener(mStartRepeatingListener);
		button = (Button) findViewById(R.id.stop_repeating);
		button.setOnClickListener(mStopRepeatingListener);
	}

	@Override
	protected void onStart() {
		super.onStart();
		Integer intervalBefore = interval;
		getPrefs();
		// restart if something changed
		if (!intervalBefore.equals(interval)) {
			stopAlarm();
			startAlarm();
			Toast.makeText(SelfTracker.this, "Timer restarted because interval changed.", Toast.LENGTH_SHORT).show();
		}
	}

	private void getPrefs() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		String time = prefs.getString("interval", "").trim();
		try {
			interval = new Integer(time);
		} catch (NumberFormatException e) {
			if (mToast != null) {
				mToast.cancel();
			}
			mToast = Toast.makeText(SelfTracker.this, "Problem with interval, could not get it from preferences...",
					Toast.LENGTH_SHORT);
			mToast.show();
		}
		if (interval < MIN_INTERVAL) {
			if (mToast != null) {
				mToast.cancel();
			}
			mToast = Toast.makeText(SelfTracker.this, "Interval too short, this will not work !", Toast.LENGTH_SHORT);
			mToast.show();
		}
	}

	private void startAlarm() {
		// When the alarm goes off, we want to broadcast an Intent to our
		// BroadcastReceiver. Here we make an Intent with an explicit class
		// name to have our own receiver (which has been published in
		// AndroidManifest.xml) instantiated and called, and then create an
		// IntentSender to have the intent executed as a broadcast.
		// Note that unlike above, this IntentSender is configured to
		// allow itself to be sent multiple times.
		Intent intent = new Intent(SelfTracker.this, RepeatingAlarm.class);
		PendingIntent sender = PendingIntent.getBroadcast(SelfTracker.this, 0, intent, 0);

		// We want the alarm to go off 30 seconds from now.
		long firstTime = SystemClock.elapsedRealtime();
		firstTime += 15 * 1000;

		// Schedule the alarm!
		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
		if (interval >= MIN_INTERVAL) {
			am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime, interval * 1000, sender);

			// Tell the user about what we did.
			if (mToast != null) {
				mToast.cancel();
			}
			mToast = Toast.makeText(SelfTracker.this, R.string.repeating_scheduled, Toast.LENGTH_LONG);
			mToast.show();
		}
	}

	private void stopAlarm() {
		// Create the same intent, and thus a matching IntentSender, for
		// the one that was scheduled.
		Intent intent = new Intent(SelfTracker.this, RepeatingAlarm.class);
		PendingIntent sender = PendingIntent.getBroadcast(SelfTracker.this, 0, intent, 0);

		// And cancel the alarm.
		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
		am.cancel(sender);

		// Tell the user about what we did.
		if (mToast != null) {
			mToast.cancel();
		}
		mToast = Toast.makeText(SelfTracker.this, R.string.repeating_unscheduled, Toast.LENGTH_LONG);
		mToast.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, PREFERENCES_ID, 2, R.string.preferences).setIcon(android.R.drawable.ic_menu_preferences);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case PREFERENCES_ID:
			Intent preferencesActivity = new Intent(SelfTracker.this, Preferences.class);
			SelfTracker.this.startActivity(preferencesActivity);
			return true;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	private OnClickListener mTakePictureListener = new OnClickListener() {
		public void onClick(View v) {
			Intent myIntent = new Intent(SelfTracker.this, TakePicture.class);
			SelfTracker.this.startActivity(myIntent);
			myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		}
	};

	private OnClickListener mStartRepeatingListener = new OnClickListener() {
		public void onClick(View v) {
			startAlarm();
		}
	};
	private OnClickListener mStopRepeatingListener = new OnClickListener() {
		public void onClick(View v) {
			stopAlarm();
		}
	};
}