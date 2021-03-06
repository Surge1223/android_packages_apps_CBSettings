#!/system/bin/sh
PATH=/system/bin:/system/xbin:/data/local/tmp:/data/toolchain/bin
export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:/vendor/lib:/system/lib:/data/toolchain/lib
export PATH=/tmp/bin:$PATH
alldir=/data/data/com.cheekybastards.ftw
date=$(date)
echo "0" > /proc/sys/kernel/dmesg_restrict 
echo "0" > /proc/sys/kernel/kptr_restrict

mount -o remount,rw /
mount -o remount,rw / /
mount -o remount,rw /system
mount -o remount,rw /data
ROOT_BIN=/bin

sb=/system/bin/busybox
xb=/system/xbin/busybox
rm $sb
cp $alldir/busybox $xb
cp $alldir/busybox $sb
chmod 04755 $xb
chown root.shell $xb
chmod 04755 $sb
chown root.shell $sb
setenforce 0

$xb ln -sf $xb $sb
for sym in `$xb --list`; do ln -s $xb /system/bin/$sym; done

chmod 755 /sbin/adbd
chown root.root /sbin/adbd
chmod 755 $sb
chown root.shell $sb
$sb chmod 777 /system/etc/init.d/*
chmod 0755 /sbin/adbd
chown root.root /sbin/adbd
chmod 755 /system/lib/modules
chmod 644 /system/lib/modules/*.ko

mkdir -p /sdcard/PureNexus
cp -R $alldir/ /sdcard/PureNexus/

SYSROOT=/data/mysysroot
if [ ! -L "$ROOT_BIN" ]; then
    /system/xbin/busybox mount -t rootfs -o remount,rw rootfs;
    ln -s /system/bin /bin
    /system/xbin/busybox mount -t rootfs -o remount,ro rootfs;
fi

export PATH=$PATH:/data/toolchain/bin

echo "<1>Cheeky system: changing defaults" > /dev/kmsg
			# set loglevel
			sed -i "s/loglevel 3/loglevel 8/" /init.rc
			# adjust stock init.target.rc file to include /sbin/fixboot.sh
#			$sb sed -i "s/mount_all fstab.qcom/exec \/sbin\/fixboot.sh\n    mount_all fstab.qcom/" /init.target.rc

			# adjust init.<hardware>.rc for where to mount /sdcard
#			$sb sed -i "s/service sdcard \/system\/bin\/sdcard \/data\/media/service sdcard \/system\/bin\/sdcard \/datamedia\/media/" /init.qcom.rc
			
			# give adbd root permissions
			sed -i 's/ro.adb.secure=1/ro.adb.secure=0/' default.prop

			# make adb insecure
   sed -i 's/ro.secure=1/ro.secure=0/' default.prop

			# set dmesg log level
   sed -i 's/write \/proc\/sys\/kernel\/dmesg_restrict 1/write \/proc\/sys\/kernel\/dmesg_restrict 0/' init.rc

			# reset permissions
			$sb chmod -R 755 /sbin/*
			$sb chmod 755 /init
			$sb chmod 755 /charger
			$sb chmod 755 /modload.sh
			$sb chmod 644 /default.prop
			$sb chmod 755 /*.rc
			$sb chmod 755 /*.sh
			



echo "<1>Cheeky system: creating log" > /dev/kmsg
mkdir -p /sdcard/log
dmesg >/sdcard/log/startup.log
cat /proc/kallsyms >/sdcard/log/kallsyms.txt

$sb mount -o remount,ro /
$sb mount -o remount,ro / /
$sb mount -o remount,ro /system
$sb mount -o remount,ro /data

$sb sync
echo "done" >/sdcard/log/initinfo.txt
 busybox date >/sdcard/log/initdate.txt
rm $alldir/cache/subsystem_ramdump