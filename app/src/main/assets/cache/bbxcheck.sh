#!/system/bin/sh
# By Hashcode
# edited for different use by Surge1223
PATH=/system/bin:/system/xbin:/sbin

INSTALLPATH=$1
BBX=$INSTALLPATH/busybox

vers=0
alt_boot_mode=0
SELinux=0
backup=0
busybox=0




ssbdir=$(ls $EMULATED_STORAGE_TARGET/$?/SS |grep backups)

if [ "$?" -ne 0 ]; then
backup=0
fi

export a=$(getenforce)
el=$a

if [ $el == "Enforcing" ]; then
SELinux=0
fi

if [ $el == "Permissive" ]; then
SELinux=1
fi

export b=$(which busybox)
eb=$b

if [ $eb == "/system/xbin/busybox" ]; then
busybox=2

else

    if [ $eb == "/system/bin/busybox" ]; then
    busybox=1

    else
        if [ $eb != "/system/*/busybox" ]; then
        busybox=0
        fi
    fi
fi

echo "$busybox:$SELinux:$backup"

