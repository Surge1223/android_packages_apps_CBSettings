package com.cheekybastards.ftw;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.widget.Toast;

import com.cheekybastards.ftw.utils.CMDProcessor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProfileNotification extends Activity {

	public static final String PREFS_NAME = "MyPrefsFile";
	public static SharedPreferences prefs;
	public String checkedItem;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		loadprofile();
	}
	public void loadprofile() {
		List<String> profiles = readList (this, "profile");
		String[] profs = new String[profiles.size()];
		profs = profiles.toArray(profs);
		final String[] saved = profs;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Pick a profile");
		builder.setItems(saved, new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				checkedItem = saved[which];
				//Object checkedItem = lw.getAdapter().getItem(which);
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ProfileNotification.this);
				// set title
				alertDialogBuilder.setTitle("Profile informations");
				// set dialog message
				prefs = getSharedPreferences(PREFS_NAME, 0);
				final String max = prefs.getString(checkedItem+"_max", "0");
				final String min = prefs.getString(checkedItem+"_min", "0");
				final String gov = prefs.getString(checkedItem+"_gov", "0");
				final String IO = prefs.getString(checkedItem+"_IO", "0");

				alertDialogBuilder
				.setMessage(Html.fromHtml("<b>Profile Name</b> : " +checkedItem+
						"<br><b>Max freq</b> = " +max+
						"<br><b>Min freq</b> = " +min+ 
						"<br><b>Governor</b> = " +gov+ 
						"<br><b>IO Scheduler</b> = " +IO))
						.setCancelable(false)
						.setPositiveButton("Apply values", new DialogInterface.OnClickListener() {
							@SuppressWarnings("deprecation")
							@Override
							public void onClick(DialogInterface dialog, int id) {
								CMDProcessor.runSuCommand("busybox echo "+max+" > /sys/devices/system/cpu/cpu0/cpufreq/scaling_max_freq");
								CMDProcessor.runSuCommand("busybox echo "+max+" > /sys/module/cpu_tegra/parameters/cpu_user_cap");
								CMDProcessor.runSuCommand("busybox echo "+min+" > /sys/module/cpu_tegra/parameters/scaling_min_freq");
								CMDProcessor.runSuCommand("busybox echo "+gov+ " > /sys/devices/system/cpu/cpu0/cpufreq/scaling_governor");
								CMDProcessor.runSuCommand("busybox echo "+IO+ " > /sys/block/mmcblk0/queue/scheduler");
								Toast.makeText(ProfileNotification.this, "Values applied!" , Toast.LENGTH_SHORT).show();
								SharedPreferences.Editor editor = prefs.edit();
								editor.putString("sProf", checkedItem);
								editor.commit();
								finish();
							}
						})

						.setNeutralButton("Cancel",new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,int id) {
								dialog.cancel();
								finish();
							}
						});
				AlertDialog alertDialog = alertDialogBuilder.create();
				alertDialog.show();	
			}

		});
		AlertDialog load = builder.create();
		load.show();
	}
	
	public static List<String> readList (Context context, String prefix)
	{
		prefs = context.getSharedPreferences(PREFS_NAME, 0);

		int name = prefs.getInt(prefix+"_size", 0);

		List<String> data = new ArrayList<String>(name);
		for(int i=0; i<name; i++){
			data.add(prefs.getString(prefix+"_"+i, null));
			data.removeAll(Arrays.asList(new Object[]{null}));
			}
		return data;
	}
}
