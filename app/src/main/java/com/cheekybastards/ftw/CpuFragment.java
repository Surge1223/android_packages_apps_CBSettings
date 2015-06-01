package com.cheekybastards.ftw;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.cheekybastards.ftw.R;
import com.cheekybastards.ftw.utils.CMDProcessor;
import com.cheekybastards.ftw.utils.ExecuteAsRootBase;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CpuFragment extends PreferenceActivity implements OnPreferenceChangeListener,
OnPreferenceClickListener {

	public File freqfile;
	public File iofile;
	public File govfile;
	public String s1;
	public String s;
	public String s3;
	public String CurrentIOScheduler;
	public String[] frequencies;
	public String[] governors;
	public String[] IOSchedulers;
	public ListPreference cpumax;
	public ListPreference cpumin;
	public ListPreference governor;
	public ListPreference scheduler;
	public Preference saveprof;
	public Preference loadprof;
	public Preference eraseprof;
	public static final String PREFS_NAME = "MyPrefsFile";
	public SwitchPreference smartdimmer;
	public SwitchPreference smartaggr;
	public SwitchPreference fast;
	//public Preference tunegov;
	public SharedPreferences prefs;
	public static SharedPreferences prefs1;
	public CheckBoxPreference cpuboot;
	public CheckBoxPreference notif;
	public String bootsmart = "echo 0 > /sys/devices/tegradc.0/smartdimmer/enable";
	public String bootaggr = "echo 1 > /sys/devices/tegradc.0/smartdimmer/aggressiveness";
	public Boolean nPersist = false;
	public NotificationManager notificationManager;
	public String checkedItem;
	public Notification.Builder noti;
	public SensorManager mSensorManager;
	public Sensor mProximity;
	public PowerManager  manager;
	//public Activity act;
    private Context mContext;
	
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
        mContext = this;
       // setTheme(Preferences.getTheme(mContext));
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.cpumain);
		//act = getActivity();
		prefs = PreferenceManager.getDefaultSharedPreferences(CpuFragment.this);
		

		cpumax = (ListPreference) findPreference("maxcpu");
		cpumax.setOnPreferenceChangeListener(this);
		cpumin = (ListPreference) findPreference("mincpu");
		cpumin.setOnPreferenceChangeListener(this);
		governor = (ListPreference) findPreference("governors");
		governor.setOnPreferenceChangeListener(this);
		//tunegov = (Preference) findPreference("tunegov");
		//tunegov.setOnPreferenceClickListener(this);
		scheduler = (ListPreference) findPreference("scheduler");
		scheduler.setOnPreferenceChangeListener(this);
		smartdimmer = (SwitchPreference) findPreference("smartdimmer");
		smartdimmer.setOnPreferenceClickListener(this);
		smartaggr = (SwitchPreference) findPreference("smartaggr");
		smartaggr.setOnPreferenceClickListener(this);
		saveprof = (Preference) findPreference("saveprof");
		saveprof.setOnPreferenceClickListener(this);
		loadprof = (Preference) findPreference("loadprof");
		loadprof.setOnPreferenceClickListener(this);
		eraseprof = (Preference) findPreference("eraseprof");
		eraseprof.setOnPreferenceClickListener(this);
		cpuboot = (CheckBoxPreference) findPreference("cpuboot");
		cpuboot.setOnPreferenceClickListener(this);
		notif = (CheckBoxPreference) findPreference("notif");
		notif.setOnPreferenceClickListener(this);
		fast = (SwitchPreference) findPreference("fast");
		fast.setOnPreferenceClickListener(this);
		
		freqfile = new File("/sys/devices/system/cpu/cpu0/cpufreq/scaling_available_frequencies");
		FileInputStream fin1 = null;
		try {
			fin1 = new FileInputStream(freqfile);
			byte fileContent[] = new byte[(int)freqfile.length()];
			fin1.read(fileContent);
			s1 = new String(fileContent);
		}
		catch (FileNotFoundException e1) {
			System.out.println("File not found" + e1);
		}
		catch (IOException ioe1) {
			System.out.println("Exception while reading file " + ioe1);
		}
		finally {
			try {
				if (fin1 != null) {
					fin1.close();
				}
			}
			catch (IOException ioe1) {
				System.out.println("Error while closing stream: " + ioe1);
			}
		}

		govfile = new File("/sys/devices/system/cpu/cpu0/cpufreq/scaling_available_governors");
		FileInputStream fin = null;
		try {
			fin = new FileInputStream(govfile);
			byte fileContent[] = new byte[(int)govfile.length()];
			fin.read(fileContent);
			s = new String(fileContent);
		}
		catch (FileNotFoundException e) {
		}
		catch (IOException ioe) {
			System.out.println("Exception while reading file " + ioe);
		}
		finally {
			try {
				fin.close();
			}
			catch (IOException ioe) {
				System.out.println("Error while closing stream: " + ioe);
			}
		}

		iofile = new File("/sys/block/mmcblk0/queue/scheduler"); 

		FileInputStream fin2 = null;
		try {
			fin2 = new FileInputStream(iofile);
			byte fileContent[] = new byte[(int)iofile.length()];
			fin2.read(fileContent);
			s3 = new String(fileContent);
		}
		catch (FileNotFoundException e) {
			System.out.println("File not found" + e);
		}
		catch (IOException ioe) {
			System.out.println("Exception while reading file " + ioe);
		}
		finally {
			try {
				if (fin2 != null) {
					fin2.close();
				}
			}
			catch (IOException ioe) {
				System.out.println("Error while closing stream: " + ioe);
			}
		} 
		IOSchedulers = s3.replace("[", "").replace("]", "").split(" ");
		int bropen = s3.indexOf("[");
		int brclose = s3.lastIndexOf("]");
		CurrentIOScheduler = s3.substring(bropen + 1, brclose);

		frequencies = s1.split(" ");
		governors = s.split(" ");

		cpumax.setEntryValues(frequencies);
		cpumax.setEntries(frequencies);
		cpumin.setEntryValues(frequencies);
		cpumin.setEntries(frequencies);
		governor.setEntryValues(governors);
		governor.setEntries(governors);
		scheduler.setEntryValues(IOSchedulers);
		scheduler.setEntries(IOSchedulers);

		String Value = prefs.getString("mincpu", "Select min frequency");
		cpumin.setSummary(Value+"Hz");
		String mValue = prefs.getString("maxcpu", "Select max frequency");
		cpumax.setSummary(mValue+"Hz");
		String gValue = prefs.getString("governor", "Select Governor");
		governor.setSummary(gValue);
		//tunegov.setSummary(gValue);
		scheduler.setSummary(CurrentIOScheduler);
		Boolean dSmart = prefs.getBoolean("smartdimmer", false);
		Boolean aSmart = prefs.getBoolean("smartaggr", false);
		smartaggr.setSummary(aSmart == false ? "Disabled" : "Enabled");
		smartdimmer.setSummary(dSmart == false ? "Disabled" : "Enabled");
		Boolean afast = prefs.getBoolean("fast", false);
		fast.setSummary(afast == false ? "Disabled" : "Enabled");
		
		prefs1 = this.getSharedPreferences(PREFS_NAME, 0);
		
		if (notif.isChecked()) {
			nPersist=true;
			profnotification();
		}


	}
	

	@SuppressWarnings("deprecation")
	@Override
	public boolean onPreferenceChange(Preference pref, Object newvalue) {
		// TODO Auto-generated method stub
		if (pref == cpumax) {
			SharedPreferences.Editor editor = prefs.edit();
			String val = newvalue.toString();
			editor.putString("maxcpu", val);
			editor.commit();
			prefs = PreferenceManager.getDefaultSharedPreferences(this);
			String Value = prefs.getString("maxcpu", "1200000");
			Toast.makeText(this,"Selected Value: " +Value, Toast.LENGTH_SHORT).show();
			cpumax.setSummary(Value + "Hz");
			CMDProcessor.runSuCommand("busybox echo "+ Value + " > /sys/devices/system/cpu/cpu0/cpufreq/scaling_max_freq");
			CMDProcessor.runSuCommand("busybox echo " + Value + " > /sys/module/cpu_tegra/parameters/cpu_user_cap");
		}

		if (pref == cpumin) {
			SharedPreferences.Editor editor = prefs.edit();
			String val = newvalue.toString();
			editor.putString("mincpu", val);
			editor.commit();
			prefs = PreferenceManager.getDefaultSharedPreferences(this);
			String Value = prefs.getString("mincpu", "51000");
			Toast.makeText(this,"Selected Value: " +Value, Toast.LENGTH_SHORT).show();
			cpumin.setSummary(Value + "Hz");
			CMDProcessor.runSuCommand("busybox echo "+ Value + " > /sys/devices/system/cpu/cpu0/cpufreq/scaling_min_freq");
		}
		if (pref == governor) {
			SharedPreferences.Editor editor = prefs.edit();
			String val = newvalue.toString();
			editor.putString("governor", val);
			editor.commit();
			prefs = PreferenceManager.getDefaultSharedPreferences(this);
			String Value = prefs.getString("governor", "Select Governor");
			Toast.makeText(this,"Selected Value: " +Value, Toast.LENGTH_SHORT).show();
			governor.setSummary(Value);
			//tunegov.setSummary(Value);
			CMDProcessor.runSuCommand("busybox echo "+ Value + " > /sys/devices/system/cpu/cpu0/cpufreq/scaling_governor");
		}
		if (pref == scheduler) {
			SharedPreferences.Editor editor = prefs.edit();
			String val = newvalue.toString();
			editor.putString("scheduler", val);
			editor.commit();
			prefs = PreferenceManager.getDefaultSharedPreferences(this);
			String Value = prefs.getString("scheduler", CurrentIOScheduler);
			Toast.makeText(this,"Selected Value: " +Value, Toast.LENGTH_SHORT).show();
			scheduler.setSummary(Value);
			CMDProcessor.runSuCommand("busybox echo "+ Value + " > /sys/block/mmcblk0/queue/scheduler");
		}


		return false;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onPreferenceClick(Preference pref) {
		// TODO Auto-generated method stub
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		String governor = prefs.getString("governor", "Select Governor");
		if (pref == smartdimmer) {
			boolean value = smartdimmer.isChecked();
			if (value == true) {
				bootsmart = "echo 1 > /sys/devices/tegradc.0/smartdimmer/enable";
				smartdimmer.setSummary("Enabled");
				CMDProcessor.runSuCommand("busybox echo '1' > /sys/devices/tegradc.0/smartdimmer/enable");
				Log.d("???????", "ENABLED");
			}
			else if (value == false){
				bootsmart = "echo 0 > /sys/devices/tegradc.0/smartdimmer/enable";
				smartdimmer.setSummary("Disabled");
				CMDProcessor.runSuCommand("busybox echo '0' > /sys/devices/tegradc.0/smartdimmer/enable");
				Log.d("???????", "DISABLED");
			}
		}
		if (pref == smartaggr) {
			boolean value = smartaggr.isChecked();
			if (value == true) {
				bootaggr = "echo # > /sys/devices/tegradc.0/smartdimmer/aggressiveness";
				smartaggr.setSummary("Enabled");
				CMDProcessor.runSuCommand("busybox echo # > /sys/devices/tegradc.0/smartdimmer/aggressiveness");
				Log.d("???????", "ENABLED");
			}
			else if (value == false){
				bootaggr = "echo 1 > /sys/devices/tegradc.0/smartdimmer/aggressiveness";
				smartaggr.setSummary("Disabled");
				CMDProcessor.runSuCommand("busybox echo 1 > /sys/devices/tegradc.0/smartdimmer/aggressiveness");
				Log.d("???????", "DISABLED");
			}
		}
		if (pref == saveprof){
			saveprofile();
		}
		if (pref == loadprof) {
			loadprofile();
		}
		if (pref == eraseprof){
			manageprofiles();
		}
		
		if (pref == cpuboot) {
			boolean value = cpuboot.isChecked();
			if (value == true) {
				String cpumax = prefs.getString("maxcpu", "0");
				String cpumin = prefs.getString("mincpu", "0");
				String gov = prefs.getString("governor", "0");
				String IOSelected = prefs.getString("scheduler","0");
				String bootmax = "echo "+ cpumax + " > /sys/devices/system/cpu/cpu0/cpufreq/scaling_max_freq";
				String bootmin = "echo "+ cpumin + " > /sys/devices/system/cpu/cpu0/cpufreq/scaling_min_freq";
				String bootgov = "echo "+ gov + " > /sys/devices/system/cpu/cpu0/cpufreq/scaling_governor";
				String bootio = "echo "+ IOSelected + " > /sys/block/mmcblk0/queue/scheduler";
				String bootcap = "echo " + cpumax + " > /sys/module/cpu_tegra/parameters/cpu_user_cap";
				CMDProcessor.runSuCommand("busybox mount -o remount,rw /system");
				CMDProcessor.runSuCommand("busybox echo '#!/system/bin/sh' > system/etc/init.d/98bootcpu");
				CMDProcessor.runSuCommand("busybox echo 'sleep 60' >> system/etc/init.d/98bootcpu");
				CMDProcessor.runSuCommand("busybox echo '#Max freq' >> system/etc/init.d/98bootcpu");
				CMDProcessor.runSuCommand("busybox echo '" + bootmax + "' >> system/etc/init.d/98bootcpu");
				CMDProcessor.runSuCommand("busybox echo '#Min freq' >> system/etc/init.d/98bootcpu");
				CMDProcessor.runSuCommand("busybox echo '" + bootmin + "' >> system/etc/init.d/98bootcpu");
				CMDProcessor.runSuCommand("busybox echo '#User Cap' >> system/etc/init.d/98bootcpu");
				CMDProcessor.runSuCommand("busybox echo '" + bootcap + "' >> system/etc/init.d/98bootcpu");
				CMDProcessor.runSuCommand("busybox echo '#Governor' >> system/etc/init.d/98bootcpu");
				CMDProcessor.runSuCommand("busybox echo '" + bootgov + "' >> system/etc/init.d/98bootcpu");
				CMDProcessor.runSuCommand("busybox echo '#I/O Scheduler' >> system/etc/init.d/98bootcpu");
				CMDProcessor.runSuCommand("busybox echo '" + bootio + "' >> system/etc/init.d/98bootcpu");
				CMDProcessor.runSuCommand("busybox echo '#Smartdimmer' >> system/etc/init.d/98bootcpu");
				CMDProcessor.runSuCommand("busybox echo '" + bootsmart + "' >> system/etc/init.d/98bootcpu");
				CMDProcessor.runSuCommand("busybox echo '#Smartdimmer aggressiveness' >> system/etc/init.d/98bootcpu");
				CMDProcessor.runSuCommand("busybox echo '" + bootaggr + "' >> system/etc/init.d/98bootcpu");
				CMDProcessor.runSuCommand("busybox chmod 755 /system/etc/init.d/98bootcpu");
				CMDProcessor.runSuCommand("busybox mount -o remount,ro /system");
			}
			if (value == false) {
				CMDProcessor.runSuCommand("busybox mount -o remount,rw /system");
				CMDProcessor.runSuCommand("busybox rm -f /system/etc/init.d/98bootcpu");
				CMDProcessor.runSuCommand("busybox mount -o remount,ro /system");
			}
		}
		if (pref == notif) {
			boolean value = notif.isChecked();
			if (value == true) {
				nPersist = true;
				profnotification();
			}
			if (value == false) {
				nPersist = false;
				notificationManager = (NotificationManager) 
						this.getSystemService(Context.NOTIFICATION_SERVICE);
				notificationManager.cancelAll();
			}
		}
		if(pref == fast) {
			boolean value = fast.isChecked();
			if(value ==true) {
				fast.setSummary("Enabled");
				CMDProcessor.runSuCommand("busybox echo 1 > /sys/kernel/fast_charge/force_fast_charge");
			}
			if(value==false) {
				fast.setSummary("Disabled");
				ExecuteAsRootBase.executecmd("echo 0 > /sys/kernel/fast_charge/force_fast_charge");
			}
		}

		return false;
	}

	private void changegov(){
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		// set title
		alertDialogBuilder.setTitle("Warning!");
		// set dialog message
		alertDialogBuilder
		.setMessage("Please apply the governor before tune it!")
		.setCancelable(false)
		.setNeutralButton("Got It!",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				dialog.cancel();
			}
		});
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();	
	}

	private void notuning(){
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		// set title
		alertDialogBuilder.setTitle("Information");
		// set dialog message
		alertDialogBuilder
		.setMessage("No interfaces available for this Governor!")
		.setCancelable(false)
		.setNeutralButton("Got It!",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				dialog.cancel();
			}
		});
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();	
	}

	public void saveprofile() {
		AlertDialog.Builder saveprofile = new AlertDialog.Builder(this);
		LayoutInflater inflater = this.getLayoutInflater();
		View V = inflater.inflate(R.layout.dialog_save, null);
		saveprofile.setView(V);
		final EditText name = (EditText) V.findViewById(R.id.name);
		saveprofile
		.setTitle("Save profile")
		.setPositiveButton("save", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				String pName = name.getText().toString();
				Toast.makeText(CpuFragment.this,"profile " +name.getText()+ " saved", Toast.LENGTH_SHORT).show();
				List<String> profiles = new ArrayList<String>();
				profiles.add(pName);
				writeList(CpuFragment.this, profiles, "profile");
				SharedPreferences.Editor editor = prefs.edit();
				String cpumax = prefs.getString("maxcpu", "0");
				String cpumin = prefs.getString("mincpu", "0");
				String governor = prefs.getString("governor", "0");
				String IOSelected = prefs.getString("scheduler","0");
				editor.putString(pName+"_max", cpumax);
				editor.putString(pName+"_min", cpumin);
				editor.putString(pName+"_gov", governor);
				editor.putString(pName+"_IO", IOSelected);
				editor.commit();
			}
		})
		.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});      
		AlertDialog save = saveprofile.create();
		save.show();

	}
	
	public void loadprofile() {
		List<String> profiles = readList (CpuFragment.this, "profile");
		String[] profs = new String[profiles.size()];
		profs = profiles.toArray(profs);
		final String[] saved = profs;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Pick a profile");
		builder.setItems(saved, new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				checkedItem = saved[which];
				//Object checkedItem = lw.getAdapter().getItem(which);
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CpuFragment.this);
				// set title
				alertDialogBuilder.setTitle("Profile informations");
				// set dialog message
				prefs1 = CpuFragment.this.getSharedPreferences(PREFS_NAME, 0);
				final String max = prefs1.getString(checkedItem+"_max", "0");
				final String min = prefs1.getString(checkedItem+"_min", "0");
				final String gov = prefs1.getString(checkedItem+"_gov", "0");
				final String IO = prefs1.getString(checkedItem+"_IO", "0");

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
								cpumax.setValue(max);
								cpumin.setValue(min);
								governor.setValue(gov);
								scheduler.setValue(IO);
							Toast.makeText(CpuFragment.this, "Values applied!" , Toast.LENGTH_SHORT).show();
								SharedPreferences.Editor editor = prefs1.edit();
								editor.putString("sProf", checkedItem);
								editor.commit();
								//noti.setContentText("Current profile: " +checkedItem);
								
							}
						})

						.setNeutralButton("Cancel",new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,int id) {
								dialog.cancel();
							}
						});
				AlertDialog alertDialog = alertDialogBuilder.create();
				alertDialog.show();	
			}

		});
		AlertDialog load = builder.create();
		load.show();
	}

	public void manageprofiles() {
		List<String> profiles = readList (this, "profile");
		String[] profs = new String[profiles.size()];
		profs = profiles.toArray(profs);
		final String[] saved = profs;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Erase profiles");
		builder.setItems(saved, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				String checkedItem = saved[which];
				Toast.makeText(CpuFragment.this,"Selected:  "+checkedItem, Toast.LENGTH_SHORT).show();
				prefs1 = CpuFragment.this.getSharedPreferences(PREFS_NAME, 0);
				int aSize = (prefs1.getInt("profile_size", 0))-1;
				SharedPreferences.Editor editor = prefs1.edit();
				editor.remove(checkedItem+"_max");
				editor.remove(checkedItem+"_min");
				editor.remove(checkedItem+"_gov");
				editor.remove(checkedItem+"_IO");
				editor.remove(checkedItem+"_maxpos");
				editor.remove(checkedItem+"_minpos");
				editor.remove(checkedItem+"_govpos");
				editor.remove(checkedItem+"_IOpos");
				editor.putInt("profile_size", aSize);
				editor.remove("profile_"+which);
				editor.remove("sProf");
				editor.commit();
				Toast.makeText(CpuFragment.this, "profile "+checkedItem+ " Erased" , Toast.LENGTH_SHORT).show();
			}

		})
		.setNeutralButton("Cancel",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				dialog.cancel();
			}
		});
		AlertDialog manage = builder.create();
		manage.show();
	}
	
	public static void writeList(Context context, List<String> list, String prefix)
	{
		prefs1 = context.getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = prefs1.edit();

		int name = prefs1.getInt(prefix+"_size", 0);

		// clear the previous data if exists
		//for(int i=0; i<name; i++)
		//  editor.remove(prefix+"_"+i);

		// write the current list
		for(int i=0; i<list.size(); i++)
			editor.putString(prefix+"_"+(name), list.get(i));

		editor.putInt(prefix+"_size", name+1);
		editor.commit();
	}

	public static List<String> readList (Context context, String prefix)
	{
		prefs1 = context.getSharedPreferences(PREFS_NAME, 0);

		int name = prefs1.getInt(prefix+"_size", 0);

		List<String> data = new ArrayList<String>(name);
		for(int i=0; i<name; i++){
			data.add(prefs1.getString(prefix+"_"+i, null));
			data.removeAll(Arrays.asList(new Object[]{null}));
		}
		return data;
	}
	
	public void profnotification() {

		Intent intent = new Intent(this, ProfileNotification.class);
		PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

		// Build notification
		// Actions are just fake
		String active = prefs1.getString("sProf", "No profile Active");
		noti = new Notification.Builder(this);
		noti.setContentTitle("Change profile");
		noti.setContentText("Current profile: " +active);
		noti.setSmallIcon(R.drawable.ic_launcher);
		noti.setContentIntent(pIntent);
		noti.setOngoing(nPersist);
		Notification notification = noti.build();
		notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

		notificationManager.notify(001, notification); 
	}
	
}
