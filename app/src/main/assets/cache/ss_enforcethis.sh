#!/system/bin/sh
# By Surge1223
PATH=/system/bin:/system/xbin

mount -o remount,rw /system
mount -o remount,rw /
mount -o remount,rw rootfs
mkdir /system/etc/init.d
touch /system/etc/init.d/enforcethis



#cp /data/data/com.cheekybastards.ftw/files/sepolicy /data/security/sepolicy


cp /data/security/sepolicy /system/etc/safestrap
setprop selinux.reload_policy 1

busybox sleep 1

busybox sysctl -w kernel.kptr_restrict=0 >/dev/null

kvar_addr_hex=`busybox cat /proc/kallsyms \
| busybox grep ".*\ selinux\_enforcing$" \
| busybox cut -f 1 -d " "`

echo $kvar_addr_hex


kvar_addr=`busybox printf '%d\n' "0x$kvar_addr_hex"`busybox dd of=/dev/kmem seek=$kvar_addr bs=1 count=1 conv=notrunc 2>/dev/null

export a=$(getenforce)
el=$a


if [ $el == "Enforcing" ]; then
echo "Enforcing"
touch /data/local/tmp/nofun
fi
busybox sleep 1

if [ $el == "Permissive" ]; then
echo "Permissive"
touch /data/local/tmp/suckitselinux
fi

echo "setenforce 0"> /system/etc/init.d/permissive


chmod 777 /system/etc/init.d/permissive
setenforce 0
