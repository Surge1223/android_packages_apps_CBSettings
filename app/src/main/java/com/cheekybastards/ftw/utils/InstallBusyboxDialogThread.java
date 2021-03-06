package com.cheekybastards.ftw.utils;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import java.io.File;

/**
 * Created by surge on 5/25/15.
 */
public class InstallBusyboxDialogThread extends Thread {


    public Handler handler = null;
    public String packageCodePath = "";
    public File mAppRoot = null;
    public String LOGTAG = "";
    File datadir = Environment.getDataDirectory();

    protected void pause(int milli) {
        try {
            Thread.sleep(milli);
        }
        catch(Exception ex) { }
    }

    protected void reply(int arg1, int arg2, String text) {
        Message msg = new Message();
        msg.arg1 = arg1;
        msg.arg2 = arg2;
        msg.obj = (Object)text;
        if (handler != null) { handler.sendMessage(msg); }

    }
    @Override
    public void run() {
        try {

            reply(1, 0, "Checking for Busybox...");
            pause(2000);
            reply(1, 0, "Removing Old Busybox...");
            reply(1, 50, "Installing Busybox...");
            String filesDir = datadir + "/data/com.cheekybastards.ftw" ;
            ExecuteAsRootBase.executecmd("chmod 755 " + filesDir + "/cache/*.sh");
            reply(1, 60, "Settings Symlinks...");
            ExecuteAsRootBase.executecmd("sh " + filesDir + "/cache/bbx.sh");
            reply(1,75,"Fixing File Permissions...");

            reply(1, 90, "Cleaning Up...");

            pause(1000);
            reply(0,0,"Installing Busybox Complete.");
        }
        catch(Exception ex) {
            reply(0,1,ex.getMessage());
        }
    }

}


