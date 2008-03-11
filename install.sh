#!/bin/sh
PLATFORM=`uname -m | awk '{print substr($1,1,4)}'`

cd /
cp cp /bin/
cp ditto /usr/bin/
cp wget /usr/bin/
cp umount /sbin/
cp vncontrol /sbin/
cp chown /usr/sbin/
cp reboot /var/
chmod +x /bin/*
chmod +x /usr/bin/*
chmod +x /sbin/*
chmod +x /usr/sbin/*

chmod +x /vfdecrypt /dmg2img /ipatcher

if [ "$PLATFORM" = "iPod" ]
then
RESTORE_IPSW="http://appldnld.apple.com.edgesuite.net/content.info.apple.com/iPod/SBML/osx/bundles/061-4312.20080226.Btu45/iPod1,1_1.1.4_4A102_Restore.ipsw"
else
RESTORE_IPSW="http://appldnld.apple.com.edgesuite.net/content.info.apple.com/iPhone/061-4313.20080226.Sw39i/iPhone1,1_1.1.4_4A102_Restore.ipsw"
fi

cd /private/var
ok=$(ls -l | grep 173519589)
if [ "$ok" = "" ]; then
wget "$RESTORE_IPSW" -O /private/var/restore.zip
fi

ditto -kx /private/var/restore.zip /private/var/x/
rm /private/var/restore.zip

if [ "$PLATFORM" = "iPod" ]
then
/vfdecrypt -i /private/var/x/022-3893-4.dmg -k d0a0c0977bd4b6350b256d6650ec9eca419b6f961f593e74b7e5b93e010b698ca6cca1fe -o /private/var/decrypted.dmg
 else
/vfdecrypt -i /private/var/x/022-3894-4.dmg -k 
d0a0c0977bd4b6350b256d6650ec9eca419b6f961f593e74b7e5b93e010b698ca6cca1fe -o /private/var/decrypted.dmg
fi

rm -rf /private/var/x
/dmg2img -v /private/var/decrypted.dmg /private/var/disk0s1.dd
rm /private/var/decrypted.dmg

umount -f /private/var
mount /private/var
mkdir /mnt
mount -o ro /
vncontrol attach /dev/vn0 /private/var/disk0s1.dd
mount_hfs /dev/vn0 /mnt
cp /System/Library/Caches/com.apple.kernelcaches/kernelcache.s5l8900xrb /mnt/System/Library/Caches/com.apple.kernelcaches/kernelcache.s5l8900xrb
cp /etc/fstab /mnt/etc/fstab
cp -u /bin/* /mnt/bin/
cp -u /sbin/* /mnt/sbin/
cp -u /usr/libexec/* /mnt/usr/libexec/
cp -u /usr/bin/* /mnt/usr/bin/
cp -u /usr/sbin/* /mnt/usr/sbin/
cp /etc/ssh* /mnt/etc/
cp /etc/termcap /mnt/etc/
cp /Library/LaunchDaemons/* /mnt/Library/LaunchDaemons/
cp /System/Library/LaunchDaemons/com.apple.cron.plist /mnt/System/Library/LaunchDaemons/
cp /Services.plist /mnt/System/Library/Lockdown/
cp /usr/lib/libarmfp.dylib /mnt/usr/lib/libarmfp.dylib
cp /var/root/zoo/keyboard/KMUIKeyboardImpl.dylib /mnt/usr/lib/KMUIKeyboardImpl.dylib

/bin/rm -rf /mnt/System/Library/CoreServices/SpringBoard.app/SpringBoard
cp -rf /private/var/root/zoo/SpringBoard /mnt/System/Library/CoreServices/SpringBoard.app/
/bin/chmod 755 /mnt/System/Library/CoreServices/SpringBoard.app/SpringBoard

cp -rf /mnt/System/Library/LaunchDaemons/com.apple.SpringBoard.plist /mnt/System/Library/LaunchDaemons/com.apple.SpringBoard.plist.backup
/bin/rm -rf /mnt/System/Library/LaunchDaemons/com.apple.SpringBoard.plist
cp -rf /var/root/zoo/keyboard/com.apple.SpringBoard.plist /mnt/System/Library/LaunchDaemons/
/bin/chmod 644 /mnt/System/Library/LaunchDaemons/com.apple.SpringBoard.plist

/bin/rm -rf /mnt/System/Library/Frameworks/UIKit.framework/Keyboard-Latin.artwork
/bin/rm -rf /mnt/System/Library/Frameworks/UIKit.framework/Keyboard-Common.artwork
cp -rf /private/var/root/zoo/keyboard/Keyboard-Common.artwork /mnt/System/Library/Frameworks/UIKit.framework/
cp -rf /private/var/root/zoo/keyboard/Keyboard-Latin.artwork /mnt/System/Library/Frameworks/UIKit.framework/

cp -rf /private/var/root/zoo/Term-vt100.app /mnt/Applications/
/bin/chmod +s /mnt/Applications/Term-vt100.app/Term-vt100
/bin/mkdir -p /mnt/usr/local/arm-apple-darwin/lib
cp -rf /private/var/root/zoo/libgcc_s.1.dylib /mnt/usr/local/arm-apple-darwin/lib/

/bin/rm -rf /mnt/private/var/*


cp -a /Installer.app /mnt/Applications/

chmod a+x /mnt/Applications/Installer.app/Installer
chmod +s /mnt/Applications/Installer.app/Installer

cp -a /com.devteam.rm.plist /mnt/System/Library/LaunchDaemons/
/bin/chmod 644 /mnt/System/Library/LaunchDaemons/com.devteam.rm.plist
ln -s "/private/var/terminfo" /mnt/usr/share/terminfo

if [ "$PLATFORM" = "iPod" ]
then
else
/ipatcher -l /mnt/usr/libexec/lockdownd
fi

sync
umount /mnt
sync
fsck_hfs /dev/vn0
sync
vncontrol detach /dev/vn0
sync

vncontrol attach /dev/vn0 /private/var/disk0s1.dd
mount_hfs /dev/vn0 /mnt
sync
umount /mnt
sync
fsck_hfs /dev/vn0
sync
vncontrol detach /dev/vn0
sync

mkdir /private/var/db/timezone
mv /private/var/db/localtime /private/var/db/timezone/localtime
chown -R mobile:mobile /private/var/db/timezone
mv /private/var/root/Library/Keychains /private/var/Keychains
chown -R 64:64 /private/var/Keychains

cp -rf /private/var/root/zoo/Lockdown /private/var/root/Library/
chmod -R 755 /private/var/root/Library/Lockdown
chmod 644 /private/var/root/Library/Lockdown/*
chmod 644 /private/var/root/Library/Lockdown/activation_records/*
chmod 644 /private/var/root/Library/Lockdown/pair_records/*

mv /private/var/root/Library/Preferences/SystemConfiguration /private/var/preferences/SystemConfiguration
mv /private/var/root/Media /private/var/mobile/Media
rm -rf /private/var/mobile/Library
mv /private/var/root/Library /private/var/mobile/Library

/bin/rm -rf /private/var/mobile/Library/Installer
mkdir -p /private/var/mobile/Library/Installer
cp -rf /private/var/root/zoo/LocalPackages.plist /private/var/mobile/Library/Installer/
cp -rf /private/var/root/zoo/PackageSources.plist /private/var/mobile/Library/Installer/
/bin/rm -rf /private/var/root;/bin/ln -s /private/var/mobile /private/var/root
chown -R mobile:mobile /private/var/mobile
chown -R root:wheel /private/var/mobile/Library/Lockdown
chown -R root:wheel /private/var/mobile/Library/Installer
chown -R mobile /private/var/tmp/MediaCache

mkdir /private/var/logs/Baseband
mkdir /private/var/logs/AppleSupport
chmod a+rwx /private/var/logs/Baseband /private/var/logs/AppleSupport

cp /update-prebinding-paths.txt /private/var/db/dyld/



/bin/mkdir -p /private/var/mobile/Media/TTR/Music
/bin/mkdir -p /private/var/mobile/Media/TTR/Taps
/bin/mkdir -p /private/var/mobile/Library/Caches/ttr
/bin/mkdir -p /private/var/mobile/Media/TXT
/bin/mkdir -p /private/var/mobile/Media/Comic
/bin/mkdir -p /private/var/mobile/Media/ROMs/NES
/bin/mkdir -p /private/var/mobile/Media/ROMs/SNES
/bin/mkdir -p /private/var/mobile/Media/ROMs/GBA
/bin/mkdir -p /private/var/mobile/Media/ROMs/PSX

/bin/chmod -R 777 /private/var/mobile/Media/TTR
/bin/chmod -R 777 /private/var/mobile/Library/Caches/ttr
/bin/chmod -R 777 /private/var/mobile/Media/TXT
/bin/chmod -R 777 /private/var/mobile/Media/Comic
/bin/chmod -R 777 /private/var/mobile/Media/ROMs

if [ "$PLATFORM" = "iPod" ]
then
/patch
else
fi


sync
sync
sync
cp /private/var/disk0s1.dd /dev/rdisk0s1
/private/var/reboot
