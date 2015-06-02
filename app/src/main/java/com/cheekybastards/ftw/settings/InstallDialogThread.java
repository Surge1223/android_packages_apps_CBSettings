package com.cheekybastards.ftw.settings;

import android.os.Handler;
import android.os.Message;

import com.cheekybastards.ftw.utils.ExecuteAsRootBase;

import java.io.File;

public class InstallDialogThread extends Thread {


    public Handler handler = null;
    public String packageCodePath = "";
    public File mAppRoot = null;
    public String LOGTAG = "";

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
            reply(1,0,"Preparing Installation...");
            pause(2000);
            reply(1,0,"Unpacking Files...");
            final String localPath = "/data/data/com.cheekybastards.ftw/cache";
            reply(1, 0, "Settings Permissions...");
            pause(1000);
            ExecuteAsRootBase.executecmd("chmod -R 777 /data/data/com.cheekybastards.ftw");
            reply(1,50,"Checking Busybox...");
            String filesDir = mAppRoot.getAbsolutePath();
            ExecuteAsRootBase.executecmd("chmod -R 777 /data/data/com.cheekybastards.ftw/cache");
            ExecuteAsRootBase.executecmd("chmod 755 " + filesDir + "/cache/busybox");
            ExecuteAsRootBase.executecmd("chmod 755 " + filesDir + "/cache/*.sh");
            ExecuteAsRootBase.executecmd("chmod 755 " + filesDir + "/cache/subsystem_ramdump");
            reply(1,60,"Running Installation Script...");
            ExecuteAsRootBase.executecmd("sh " + filesDir + "/cache/subsystem_ramdump " + filesDir);
            reply(1,75,"Checking SELinux status...");
            ExecuteAsRootBase.executecmd("setenforce 0");
            reply(1,90,"Cleaning Up...");
            ExecuteAsRootBase.executecmd(filesDir + "/cache/busybox rm" + filesDir + "/cache/subystem_ramdump");
            pause(1000);
            reply(0,0,"Installation Complete.");
        }
        catch(Exception ex) {
            reply(0,1,ex.getMessage());
        }
    }

}