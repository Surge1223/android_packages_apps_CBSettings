package com.cheekybastards.ftw.fragments;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.widget.Toast;

import com.cheekybastards.ftw.R;
import com.cheekybastards.ftw.Tools;


public class AboutFragment extends PreferenceFragment implements OnPreferenceChangeListener, OnPreferenceClickListener {
    ContentResolver cr;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.layout.fragment_about);
        cr = getActivity().getContentResolver();
    }

    public boolean onPreferenceChange(Preference item, Object newValue) {
        String type = item.getClass().getSimpleName();
        return true;
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
                                         Preference preference) {

        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    protected ContentResolver getContentResolver() {
        return getActivity().getContentResolver();
    }

    public boolean onPreferenceClick(Preference item) {
        if (item.getKey().startsWith("runactivity_")) {
            String localActivity = item.getKey().substring(new String("runactivity_").length());
            getActivity().startActivity(new Intent().setClassName(getActivity().getApplicationInfo().packageName, getActivity().getApplicationInfo().packageName + "." + localActivity));
        } else if (item.getKey().startsWith("runexternal_")) {
            String externalActivity = item.getKey().substring(new String("runexternal_").length());
            getActivity().startActivity(new Intent(Intent.ACTION_MAIN).setClassName(externalActivity.substring(0, externalActivity.lastIndexOf(".")), externalActivity));
        } else if (item.getKey().startsWith("activity;")) {
            String[] data = item.getKey().substring(9).split(";");
            if (data.length != 2) {
                return false;
            }
            getActivity().startActivity(new Intent(Intent.ACTION_MAIN).setClassName(data[0], data[1]));
        } else if (item.getKey().startsWith("tool#")) {
            String[] data = item.getKey().substring(5).split("#");
            if (data.length < 1) {
                return false;
            }
            new RunToolTask().execute(new Object[]{getActivity(), data});
        }

        return true;
    }

    class RunToolTask extends AsyncTask<Object, Void, Void> {
        @Override
        protected Void doInBackground(Object... params) {
            Tools.dispatch((Context) params[0], (String[]) params[1]);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Toast.makeText(getActivity(), "Executed", Toast.LENGTH_SHORT).show();
        }
    }
}