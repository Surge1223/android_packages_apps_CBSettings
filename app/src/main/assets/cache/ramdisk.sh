#!/sbin/sh

# get file descriptor for output (TWRP)
[ $OUTFD != "" ] || OUTFD=$(ps | grep -v "grep" | grep -o -E "updater(.*)" | cut -d " " -f 3)

# functions to send output to recovery
progress() {
  if [ $OUTFD != "" ]; then
    echo "progress ${1} ${2} " 1>&$OUTFD;
  fi;
}

set_progress() {
  if [ $OUTFD != "" ]; then
    echo "set_progress ${1} " 1>&$OUTFD;
  fi;
}

ui_print() {
  if [ $OUTFD != "" ]; then
    echo "ui_print ${1} " 1>&$OUTFD;
    echo "ui_print " 1>&$OUTFD;
  else
    echo "${1}";
  fi;
}

mkdir -p /pure/ramdisk
cd pure/ramdisk
cp ../boot.img boot.img
unmkbootimg -i ../boot
gunzip -c ../ramdisk.cpio.gz | cpio -i


sed -i 's/ro.adb.secure=1/ro.adb.secure=0/' default.prop

sed -i 's/ro.secure=1/ro.secure=0/' default.prop

sed -i 's/write \/proc\/sys\/kernel\/dmesg_restrict 1/write \/proc\/sys\/kernel\/dmesg_restrict 0/' init.rc


ui_print "Checking for correct file system"

mount /system 2> /dev/null
mount /cache 2> /dev/null
mount /data 2> /dev/null

find . | cpio -o -H newc | gzip > ../new-ramdisk.cpio.gz
cd /pure
rm -rf ramdisk
