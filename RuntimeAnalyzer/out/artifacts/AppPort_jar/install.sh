apk_path=$1
apk_name=$2
apk_prefix=$3


echo $apk_path >> category_result.txt
adb install "/home/jackjia/Documents/run_time_apks/category/category_app/"$apk_name
adb shell monkey -p $apk_prefix -c android.intent.category.LAUNCHER 1
sleep 8
adb shell "su -c 'busybox netstat -lnpt'" >>category_result.txt
echo 'END' >> category_result.txt
adb uninstall $apk_prefix 

