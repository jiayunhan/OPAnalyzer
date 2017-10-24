apk_path=$1
apk_name=$2
apk_prefix=$3

echo $apk_path >> anzhi_result_2.txt
scp jackjia@rome.eecs.umich.edu:$apk_path .
adb install $apk_name
adb shell monkey -p $apk_prefix -c android.intent.category.LAUNCHER 1
sleep 8
adb shell "su -c 'busybox netstat -lnpt'" >>anzhi_result_2.txt
echo 'END' >> anzhi_result_2.txt
adb uninstall $apk_prefix 
rm *.apk

