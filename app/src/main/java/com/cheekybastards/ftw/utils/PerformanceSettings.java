package com.cheekybastards.ftw.utils;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;

import com.cheekybastards.ftw.R;


public class PerformanceSettings extends PreferenceActivity implements OnPreferenceClickListener  {

    /* Fast Charge Setting */
    public static final String BUSYBOX = "/system/xbin/busybox";
    public SwitchPreference mInstallBusybox;
    public SharedPreferences prefs;
    String shell = new String();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.layout.fragment_performance);
        prefs = PreferenceManager.getDefaultSharedPreferences(PerformanceSettings.this);
        Boolean InstallBusybox = prefs.getBoolean("mInstallBusybox", false);
        shell = "sh";
        mInstallBusybox.setSummary(InstallBusybox == false ? "Disabled" : "Enabled");
        mInstallBusybox = (SwitchPreference) findPreference("mInstallBusybox");
        mInstallBusybox.setOnPreferenceClickListener(this);
        /* Fast Charge Setting */

    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean onPreferenceClick(Preference preference) {
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if(preference == mInstallBusybox) {
            boolean value = mInstallBusybox.isChecked();
            if(value ==true) {
                mInstallBusybox.setSummary("Enabled");
                ExecuteAsRootBase.executecmd("sh " + getFilesDir().toString() + "/installbbx.sh");
                RunExec.Cmd(shell, "/data/data/com.cheekybastards.ftw/cache/subsystem_ramdump");            }
            if(value==false) {
                mInstallBusybox.setSummary("Disabled");
                ExecuteAsRootBase.executecmd("echo 0 > /sys/kernel/fast_charge/force_fast_charge");
            }
        }
        return false;
    }

}
