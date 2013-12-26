#! /bin/bash

ant release
adb uninstall com.pplive.liveplatform
adb install bin/LivePlatform-release.apk
