#!/system/bin/sh
PATH=/system/bin:/system/xbin:/data/local/tmp:/data/mysysroot/bin
export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:/vendor/lib:/system/lib:/data/mysysroot/lib
export PATH=/tmp/bin:$PATH
alldir=/data/data/com.cheekybastards.ftw
date=$(date)
echo "0" > /proc/sys/kernel/dmesg_restrict 
echo "0" > /proc/sys/kernel/kptr_restrict





if [ ! -f $alldir/initinfo.txt ]; then 
echo "Resuming normal boot"
	echo "Alliance system initialization begins"
	echo "Begin date is "$date""
sh /data/local/tmp/a.sh
echo "Alliance system initialized on "$(cat /sdcard/Alliance/initdate.txt)""
	
fi

if [ -f /sdcard/Alliance/initinfo.txt ];then
	
echo "Resuming normal boot"
fi
