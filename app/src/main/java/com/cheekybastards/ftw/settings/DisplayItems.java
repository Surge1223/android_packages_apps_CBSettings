package com.cheekybastards.ftw.settings;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.cheekybastards.ftw.R;

/**
 * The class DisplayItems displays a set of buttons allowing different tasks to
 * be chosen. Short presses on the buttons launch the tasks. Long presses open a
 * dialog box giving a description of the task and the option to execute it from
 * there.
 * 
 * @author Mike Guidry
 * 
 */

public class DisplayItems extends Activity {

	private static final String TAG = "THEMES"; // Debugging tag
	private static final int numberTasks = 2;
	private static final String launcherTitle = "Task Description";
	private static final int launcherIcon = R.drawable.ic_launcher;
	private int buttonPressed;
	private String[] taskDescription = new String[numberTasks];
	private int currentTheme;
	private boolean isLight;

	/**
	 * Method onCreate(Bundle savedInstanceState) is called when the activity is
	 * first created.
	 */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Following options to change the Theme must precede setContentView().

		toggleTheme();
		setContentView(R.layout.displayitems);
}
		
	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	
	/**
	 * Method showTask() creates a custom launch dialog popped up by a
	 * long-press on a button. This dialog presents a summary of the task to the
	 * user and has buttons to either launch the task or cancel the dialog.
	 * Which task to present is controlled by the value of the int
	 * buttonPressed, which is stored if a button is pressed or long-pressed.
	 * 
	 * In this example we use an older approach to launching a dialog using the
	 * AlertDialog class.  In MainActivity.java of this project we illustrate a
	 * similar task using the more modern DialogFragment class. See
	 * 
	 *    http://developer.android.com/guide/topics/ui/dialogs.html
	 *    
	 * for further discussion.
	 */

	private void showTask(String title, String message, int icon, Context context) {

		int alertTheme = R.style.HoloCustom;
		if(currentTheme == 2){
			alertTheme = R.style.HoloLightCustom;
		}

		AlertDialog alertDialog = new AlertDialog.Builder(context, alertTheme).create();

		alertDialog.setTitle(title);
		alertDialog.setMessage(message);
		alertDialog.setIcon(icon);

		int positive = AlertDialog.BUTTON_POSITIVE;
		int negative = AlertDialog.BUTTON_NEGATIVE;

		alertDialog.setButton(positive, "Select this Task",
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				launchTask();
			}
		});

		alertDialog.setButton(negative, "Cancel",
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// Can execute code here if desired.
			}
		});

		alertDialog.show();
	}

	/**
	 * Method launchTask() uses a switch statement to decide which task to
	 * launch. It is invoked either directly by a press of a button for the
	 * task, or indirectly by a long-press on the button, which launches the
	 * method createLaunchDialog() describing the task and presents a button
	 * that calls this method.
	 */

	private void launchTask() {
		switch (buttonPressed) {
		case 1: // Launch task 1
			Intent i = new Intent(this, TaskActivity1.class);
			startActivity(i);
			break;
		case 2: // Launch task 2
			Intent j = new Intent(this, TaskActivity2.class);
			startActivity(j);
		}
	}

	// Method to check SharedPreferences and set the current theme

	private void toggleTheme(){

		// Following options to change the Theme must precede setContentView().

		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		String lister = sharedPref.getString("list_preference", "1");

		currentTheme = Integer.parseInt(lister);
		if(currentTheme == 2){
			isLight = false;
		} else {
			isLight = true;
		}

		if(isLight) {
			setTheme(R.style.HoloCustom);
		} else {
			setTheme(R.style.HoloLightCustom);
		} 
	}

}
