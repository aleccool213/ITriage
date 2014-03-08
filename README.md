iTriage
An Android application for performing triage on emergency room patients, and a database for storing patient data.

Authors

c2adamge: Alex Adam
c2brunel: Alec Brunelle
g3spence: Spencer Elliott
g3gohnic: Nicholas Goh

How to run using adb

In terminal, browse to the directory where your Android SDK is installed. This directory contains directories add-ons, platform-tools, tools, etc.
Start your emulator by running tools/emulator -avd <avd_name> where <avd_name> is the name of an Android Virtual Device. (Run tools/android list avd to see a list of your AVDs.)
Open a new terminal window in your Android SDK directory.
Run platform-tools/adb install <path_to_apk> where <path_to_apk> is the path to iTriage.apk. If iTriage is already installed, run platform-tools/adb install -r <path_to_sdk> to reinstall.
On your virtual device, open iTriage from the app drawer.
How to run using JetBrains IntelliJ (Recommended)

Note: This has only been tested with the Android 4.4 SDK.

Open IntelliJ
Click 'Open Project'
Open PIII/trunk/iTriageProject
Click Run -> Edit Configurations...
Click '+' -> Android Application
Set Name and Module to iTriage
Click OK
Run -> Run 'iTriage'
How to run using Eclipse ADT

Note: This has only been tested with the Android 4.4 SDK.

Open Eclipse
Set workspace to PIII/trunk/iTriageProject
File -> Import... -> Android -> Existing Projects into Workspace
Set the root directory to PIII/trunk/iTriageProject
Click Finish, then wait for project to build automatically
Run -> Run -> Android Application
NOTE: If you experience difficulties, clean the project first
Bonus Marks

Register user function is working; offers both nurse and physician creation and features a (semi-)secure protection using an admin password (PraiseTheTsar)
Various aesthetic choices such as:
Spinner
Intuitive patient sorting interface
(Pseudo-)cards UI
