
rem this script assumes the gradle build already ran:
rem gradle build:build

cd C:\projects\AllelonProject\Allelon\build\outputs\apk

"C:\Program Files\Java\jdk1.7.0_17\bin\jarsigner" -verbose -sigalg SHA1withRSA -digestalg SHA1 -keystore C:\Users\bogdan\Dropbox\keystore.jks Allelon-release-unsigned.apk "allelon radio"

del C:\projects\AllelonProject\Allelon\build\outputs\apk\Allelon-release-aligned.apk

C:\programs\adt-bundle-windows-x86_64-20130917\sdk\build-tools\21.1.2\zipalign.exe 4 Allelon-release-unsigned.apk Allelon-release-aligned.apk

echo Final binary is: Allelon-release-aligned.apk

pause
