package com.cheekybastards.ftw.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;

import com.cheekybastards.ftw.CpuFragment;
import com.cheekybastards.ftw.R;
import com.cheekybastards.ftw.utils.RunExec;

public class BackupFragment extends PreferenceFragment implements OnPreferenceClickListener  {

    /* Performance Settings */
    public static final String BUSYBOX = "/system/xbin/busybox";
    public SwitchPreference mInstallBusybox;
    public SwitchPreference mFixPerms;
    public SharedPreferences prefs;
    String shell = new String();
    String su = new String();
    private boolean InstallBusybox = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.layout.fragment_backup);
        Boolean InstallBusybox = false;
        shell = "sh";
        su = "su";
        mInstallBusybox = (SwitchPreference) findPreference("mInstallBusybox");
        mInstallBusybox.setOnPreferenceClickListener(this);
        mFixPerms = (SwitchPreference) findPreference("mFixPerms");
        mFixPerms.setOnPreferenceClickListener(this);
        Preference button = (Preference)getPreferenceManager().findPreference("button");
        if (button != null) {
            button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference arg0) {
                    Intent intent = new Intent(getActivity(), CpuFragment.class);
                    startActivity(intent);
                    return true;
                }
            });
        }

    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean onPreferenceClick(Preference preference) {
        if(preference == mInstallBusybox) {
            boolean value = mInstallBusybox.isChecked();
            if(value ==true) {
                mInstallBusybox.setSummary("Install Busybox");
                RunExec.Cmd(su, "/data/data/com.cheekybastards.ftw/cache/bbx.sh");}
            System.out.println("Installing Busybox");
            if(value==false) {
                mInstallBusybox.setSummary("Do not install Busybox");
                RunExec.Cmd(shell, "/data/data/com.cheekybastards.ftw/cache/test.sh");}
            System.out.println("Executing test check");

        }
        if(preference == mFixPerms) {
            boolean value = mFixPerms.isChecked();
            if(value ==true) {
                mFixPerms.setSummary("Fix Perms on boot");
                RunExec.Cmd(su, "/data/data/com.cheekybastards.ftw/cache/FixPerms.sh");}
            System.out.println("Fixed Perms");
            if(value==false) {
                mFixPerms.setSummary("Do not Fix Perms on boot");
                RunExec.Cmd(shell, "/data/data/com.cheekybastards.ftw/cache/noperms.sh");}
            System.out.println("Selected not to fix Perms");

        }
        return false;
    }

}