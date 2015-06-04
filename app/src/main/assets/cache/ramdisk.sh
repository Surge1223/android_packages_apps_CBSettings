#!/sbin/sh


mkdir -p /pure/ramdisk
cd pure/ramdisk
cp ../boot.img boot.img
unmkbootimg -i ../boot
gunzip -c ../ramdisk.cpio.gz | cpio -i


mount /system 2> /dev/null
mount /cache 2> /dev/null
mount /data 2> /dev/null

find . | cpio -o -H newc | gzip > ../new-ramdisk.cpio.gz
cd /pure
rm -rf ramdisk
