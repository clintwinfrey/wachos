# wachos
Wachos is a Java GUI library that lets you write Java code once, and deploy that code on the web, desktop, or Android.

To use wachos:
 - Clone the repository
 - Clean/build the wachos library
 - Clean/build WachosTutorial
 - Windows Desktop App:
    * Download and install 7-Zip
    * Open the WinLauncher folder
    * Extract libcef.dll from libcef.7z, directly into the WinLauncher folder
    * Clean/build and run WinLauncher in an IDE such as NetBeans
    * If you're on Windows 8, do these steps for Win8Launcher instead of WinLauncher
 - Android:
    * Download and install Android Studio
    * Open AndroidBuilder with Android Studio
    * Clean and build, and then try the generated APK
         - You can clean/build in Android Studio
         - You can also clean/build in another IDE, as long as it has gradle
 - Web
    * Download the latest version of GlassFish or WildFly
    * Clean/build and run the WebLauncher app with your downloaded web server

To see the JavaDoc, you must generate JavaDoc from the wachos library.

Note that each instantiation is running the same WachosTutorial code.  If you change that code, it will change the desktop, Android, and web deployments accordingly.
