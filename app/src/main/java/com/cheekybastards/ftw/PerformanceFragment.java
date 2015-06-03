package com.cheekybastards.ftw;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.cheekybastards.ftw.utils.BuildPropEditor;
import com.cheekybastards.ftw.utils.ExecuteAsRootBase;
import com.cheekybastards.ftw.utils.FixPermsDialogThread;
import com.cheekybastards.ftw.utils.InstallBusyboxDialogThread;
import com.cheekybastards.ftw.utils.SELinuxDialogThread;

/**
 * Created by surge on 5/25/15.
 */

public class PerformanceFragment extends PreferenceFragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    /* Performance Settings */
    public static final String BUSYBOX = "/system/xbin/busybox";
    public final String TAG = this.getClass().getSimpleName();
    public SwitchPreference mInstallBusybox;
    public Button buttonInstallbbx = null;
    public Button buttonBuildprop = null;
    public Button buttonFixPerms = null;
    public Button buttonCpuSettings = null;
    public Button buttonSelinuxChange = null;
    public Button restore_backup = null;
    public Button backup_button = null;
    public SwitchPreference mFixPerms;
    public SharedPreferences prefs;
    Handler myHandler = new Handler();
    SwipeRefreshLayout swipeLayout;
    String shell = new String();
    String su = new String();
    private SwipeRefreshLayout swipe_container;
    private SELinuxDialogThread SELinuxDialogThread = null;
    private InstallBusyboxDialogThread InstallBusyboxDialogThread = null;
    private FixPermsDialogThread FixPermsDialogThread = null;
    private ProgressDialog pDialog = null;
    private Boolean rootCheck = false;
    private Boolean SELinux = false;
    private Boolean Permissive = false;
    private Boolean Busybox = false;
    private Boolean Busybox2 = false;
    private Boolean backup = false;
    private TextView statusText = null;
    private TextView messageText = null;
    private SwipeRefreshLayout swipeContainer;
    private ScrollView scrollView;
    private CardView cardView;
    private TextView lollipopSs;
    private TextView textView2;
    private TextView textSystemStatus;
    private TextView textSelinuxStatus;
    private CardView cardView2;
    private TextView helpTextManager;
    private TextView textView1;

        protected void setupControls() {
        /* Setup button */
        //  buttonRebootWriteProtect.setVisibility(View.GONE);
                /* Check Version */
        buttonBuildprop.setEnabled(true);
        buttonInstallbbx.setEnabled(true);
        buttonFixPerms.setEnabled(true);
        buttonCpuSettings.setEnabled(true);
        buttonSelinuxChange.setEnabled(true);
        try {
            //  buttonRebootWriteProtect.setVisibility(View.GONE);
            if (rootCheck) {
                /* Check Version */
                String inText = ExecuteAsRootBase.executecmd("sh " + "/data/data/com.cheekybastards.ftw/cache/bbxcheck.sh ");

                String parts[] = inText.split(":");
                String vers_s = null;
                float vers = 0;
                if (parts != null) {
                    if (parts.length >= 1) {
                        if (parts[0].equals("1"))
                            Busybox = true;
                    } else if (parts[0].equals("2")) {
                        Busybox2 = true;
                    } else if (parts[0].equals("0")) {
                        Busybox = false;
                    }
                }
                if (parts.length >= 2) {


                    if (parts[1].equals("0")) {
                        SELinux = true;
                    } else if (parts[1].equals("1")) {
                        SELinux = false;
                        Permissive = true;


                    }


                    //noinspection StatementWithEmptyBody
                    if (vers == 0) {

                    }

                /* setMessage(check); */
                    textSelinuxStatus.setText("SELinux: Enforcing");

                    if (SELinux) {
                        textSelinuxStatus.setText("SELinux: Enforcing");
                        buttonSelinuxChange.setEnabled(true);

                    } else if (Permissive) {
                        textSelinuxStatus.setText("SELinux: Permissive");
                        buttonSelinuxChange.setEnabled(false);
                    }
                    if (Busybox) {
                        buttonInstallbbx.setEnabled(true);
                        textSystemStatus.setText("Busybox: Installed in /system/bin");

                    }
                    if (Busybox2) {
                        buttonInstallbbx.setEnabled(true);
                        textSystemStatus.setText("Busybox: Installed in /system/xbin");

                    }
                }

            }

        } finally {
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_performance, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        shell = "sh";
        su = "su";
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        scrollView = (ScrollView) view.findViewById(R.id.scrollView);
        cardView = (CardView) view.findViewById(R.id.card_view);
        lollipopSs = (TextView) view.findViewById(R.id.lollipop_ss);
        textView2 = (TextView) view.findViewById(R.id.textView2);
        textSystemStatus = (TextView) view.findViewById(R.id.textSystemStatus);
        textSelinuxStatus = (TextView) view.findViewById(R.id.textSelinuxStatus);
        statusText = (TextView) view.findViewById(R.id.textSelinuxStatus);
        messageText = (TextView) view.findViewById(R.id.textView2);
        helpTextManager = (TextView) view.findViewById(R.id.help_text_manager);
        textView1 = (TextView) view.findViewById(R.id.textView1);
        view.findViewById(R.id.buttonInstallbbx).setOnClickListener(this);
        view.findViewById(R.id.buttonBuildprop).setOnClickListener(this);
        view.findViewById(R.id.buttonFixPerms).setOnClickListener(this);
        view.findViewById(R.id.buttonCpuSettings).setOnClickListener(this);
        view.findViewById(R.id.buttonSelinuxChange).setOnClickListener(this);
        view.findViewById(R.id.backup_button).setOnClickListener(this);
        view.findViewById(R.id.restore_backup).setOnClickListener(this);
        cardView2 = (CardView) view.findViewById(R.id.card_view2);
        buttonBuildprop = (Button) view.findViewById(R.id.buttonBuildprop);
        buttonInstallbbx = (Button) view.findViewById(R.id.buttonInstallbbx);
        buttonFixPerms = (Button) view.findViewById(R.id.buttonFixPerms);
        buttonCpuSettings = (Button) view.findViewById(R.id.buttonCpuSettings);
        buttonSelinuxChange = (Button) view.findViewById(R.id.buttonSelinuxChange);
        rootCheck = ExecuteAsRootBase.canRunRootCommands();
        buttonCpuSettings.setOnClickListener(this);
        buttonInstallbbx.setOnClickListener(this);
        buttonFixPerms.setOnClickListener(this);
        buttonBuildprop.setOnClickListener(this);
        buttonSelinuxChange.setOnClickListener(this);
      //  restore_backup.setOnClickListener(this);
      //  backup_button.setOnClickListener(this);
        swipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorScheme(android.R.color.darker_gray, android.R.color.tertiary_text_dark, android.R.color.black);
    }


    /*
        public class libload {
            // Declare native method (and make it public to expose it directly)
            public  native int chmod(int base);

            // Provide additional functionality, that "extends" the native method
            public int to4(int base)
            {
                int sq = squared(base);
                return squared(sq);
            }

            // Load library
            static {
                System.loadLibrary("square");
            }
        } */
    @Override
    public void onRefresh() {
        // TODO Auto-generated method stub
        new Handler().postDelayed(new Runnable() {
                                      @Override
                                      public void run() {
                                          swipeLayout.setRefreshing(false);
                                      }
                                  },
                10);
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.buttonBuildprop:
                Intent i = new Intent(getActivity(), BuildPropEditor.class);
                startActivity(i);
                break;

            case R.id.buttonInstallbbx:
                try {
                    if (rootCheck) {
                        pDialog = new ProgressDialog(v.getRootView().getContext());
                        pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                        pDialog.setMessage("Checking Permission Status...");
                        pDialog.setCancelable(false);
                        pDialog.show();
                        pDialog.getCurrentFocus();

                        InstallBusyboxDialogThread = new InstallBusyboxDialogThread();
                        InstallBusyboxDialogThread.LOGTAG = "Installing Busybox";
                        InstallBusyboxDialogThread.handler = new Handler() {
                            @Override
                            public void handleMessage(Message msg) {
                                if (msg.arg1 == 0) {
                                    pDialog.dismiss();
                                    pDialog = null;
                                    Toast.makeText(buttonInstallbbx.getRootView().getContext(), (String) msg.obj, Toast.LENGTH_LONG).show();
                                    setupControls();
                                } else {
                                    pDialog.setProgress(msg.arg2);
                                    pDialog.setMessage((String) msg.obj);
                                }
                            }
                        };
                        InstallBusyboxDialogThread.start();
                    }
                } catch (Exception ex) {
                    messageText.setText(ex.getMessage());
                }

                break;

            case R.id.buttonFixPerms:
                try {
                    if (rootCheck) {

                        pDialog = new ProgressDialog(v.getRootView().getContext());
                        pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                        pDialog.setMessage("Checking Permission Status...");
                        pDialog.setCancelable(false);
                        pDialog.show();
                        pDialog.getCurrentFocus();

                        FixPermsDialogThread = new FixPermsDialogThread();
                        FixPermsDialogThread.LOGTAG = "Fix Perms";
                        FixPermsDialogThread.handler = new Handler() {
                            @Override
                            public void handleMessage(Message msg) {
                                if (msg.arg1 == 0) {
                                    pDialog.dismiss();
                                    pDialog = null;
                                    Toast.makeText(buttonFixPerms.getRootView().getContext(), (String) msg.obj, Toast.LENGTH_LONG).show();
                                    setupControls();
                                } else {
                                    pDialog.setProgress(msg.arg2);
                                    pDialog.setMessage((String) msg.obj);
                                }
                            }
                        };
                        FixPermsDialogThread.start();
                    }
                } catch (Exception ex) {
                    messageText.setText(ex.getMessage());
                }
                break;

            case R.id.buttonCpuSettings:
                cpusettings();
                break;

            case R.id.buttonSelinuxChange:
                try {
                    if (rootCheck) {

                        pDialog = new ProgressDialog(v.getRootView().getContext());
                        pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                        pDialog.setMessage("Atempting to change SELinux.Status...");
                        pDialog.setCancelable(false);
                        pDialog.show();
                        pDialog.getCurrentFocus();
                        SELinuxDialogThread = new SELinuxDialogThread();
                        SELinuxDialogThread.LOGTAG = "Alliance Control";
                        SELinuxDialogThread.handler = new Handler() {
                            @Override
                            public void handleMessage(Message msg) {
                                if (msg.arg1 == 0) {
                                    pDialog.dismiss();
                                    pDialog = null;
                                    Toast.makeText(buttonSelinuxChange.getRootView().getContext(), (String) msg.obj, Toast.LENGTH_LONG).show();
                                    setupControls();
                                } else {
                                    pDialog.setProgress(msg.arg2);
                                    pDialog.setMessage((String) msg.obj);
                                }
                            }
                        };

                        SELinuxDialogThread.start();
                    }
                } catch (Exception ex) {
                    messageText.setText(ex.getMessage());
                }
                break;
        }
    }


    public void cpusettings() {

        try {

            Intent intent = new Intent(getActivity(), CpuFragment.class);
            startActivity(intent);
        } catch (Exception ex) {
            System.out.println("Cpu Settings");
        }

    }


}
