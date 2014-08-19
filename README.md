iTriage, 
an Android application for performing triage on emergency room patients, and a database for storing patient data.
A quick video demonstration of the app can be seen at: http://youtu.be/2cuMCeIWvAU.
Authors

c2adamge: Alex Adam,
c2brunel: Alec Brunelle,
g3spence: Spencer Elliott,
g3gohnic: Nicholas Goh,

How to run using adb

In terminal, browse to the directory where your Android SDK is installed. This directory contains directories add-ons, platform-tools, tools, etc.
Start your emulator by running tools/emulator -avd <avd_name> where <avd_name> is the name of an Android Virtual Device. (Run tools/android list avd to see a list of your AVDs.)
Open a new terminal window in your Android SDK directory.
Run platform-tools/adb install <path_to_apk> where <path_to_apk> is the path to iTriage.apk. If iTriage is already installed, run platform-tools/adb install -r <path_to_sdk> to reinstall.
On your virtual device, open iTriage from the app drawer.
How to run using JetBrains IntelliJ (Recommended)

Note: This has only been tested with the Android 4.4 SDK.

Open IntelliJ,
click 'Open Project',
cpen PIII/trunk/iTriageProject,
click run -> edit configurations...,
click '+' -> android application,
set name and module to iTriage,
click OK,
run -> run 'iTriage'.

How to run using Eclipse ADT,

Note: This has only been tested with the Android 4.4 SDK.

Open Eclipse,
set workspace to PIII/trunk/iTriageProject,
file -> import... -> android -> existing projects into workspace,
set the root directory to PIII/trunk/iTriageProject,
click finish, then wait for project to build automatically,
run -> run -> android application,
NOTE: If you experience difficulties, clean the project first

Bonus Marks:

Register user function is working; offers both nurse and physician creation and features a (semi-)secure protection, using an admin password (PraiseTheTsar),
various aesthetic choices such as:
spinner,
intuitive patient sorting interface,
cards UI
