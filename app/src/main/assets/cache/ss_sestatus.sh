#!/system/bin/sh
# By Surge1223
PATH=/system/bin:/system/xbin:/sbin

selinux=/system/bin/getenforce
per=/data/data/com.cheekybastards.ftw/cache/per
enf=/data/data/com.cheekybastards.ftw/cache/enf
status=/data/data/com.cheekybastards.ftw/cache/status

touch $per
touch $enf
rm $per
rm $enf

if [ ! -z "$($selinux |grep 'Permissive')" ]; then 
echo "0"
echo "per" >$status
touch $per
chmod 777 $per
chmod 777 $status


 else
   if [ ! -z "$($selinux |grep 'Enforcing')" ]; then
       echo "1"
       echo "enf" >$status
       touch $enf
       chmod 777 $enf
       chmod 777 $status
       
   fi
fi

strings $status |grep "per" 
a=$?
el=$a

if [ $el == "0" ]; then 
touch $per
chmod 777 $per
fi

if [ $el == "1". ]; then  
touch $enf
chmod 777 $enf
fi



