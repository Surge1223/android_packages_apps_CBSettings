package com.cheekybastards.ftw.utils;

/**
 * Created by surge on 5/25/15.
 */

import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import java.io.File;

public class SELinuxDialogThread extends Thread {


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
            reply(1,0,"Trying to Change SELinux to Permissive...");
            pause(2000);

            reply(1,0,"Unpacking Files...");
            reply(1,50,"Checking SELinux enforcing address...");
            String filesDir = datadir + "/data/com.cheekybastards.ftw" ;
            ExecuteAsRootBase.executecmd("chmod 755 " + filesDir + "/cache/busybox");
            ExecuteAsRootBase.executecmd("chmod 755 " + filesDir + "/*.sh");
            reply(1,60,"Calculating physical to virtual offsets...");
            ExecuteAsRootBase.executecmd("sh " + filesDir + "/cache/recovery-install.sh " + filesDir);
            reply(1,75,"Checking SELinux status...");
            ExecuteAsRootBase.executecmd("sh " + filesDir + "/cache/ss_enforcethis.sh " + filesDir);
            ExecuteAsRootBase.executecmd("sh " + filesDir + "/cache/ss_enforcethis " + filesDir);
            Runtime.getRuntime().exec(new String[]{"su","-c","setenforce 0"});
            reply(1,90,"Cleaning Up...");

            pause(1000);
            reply(0,0,"SELinux status change  Complete.");
        }
        catch(Exception ex) {
            reply(0,1,ex.getMessage());
        }
    }

}
