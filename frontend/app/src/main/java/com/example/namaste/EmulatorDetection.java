package com.example.namaste;

import android.os.Build;

public class EmulatorDetection {
    public boolean inEmulator() {
        if (Build.MANUFACTURER.toLowerCase().contains("genymobile")) {
            return true;
        }
        if (Build.MANUFACTURER.toLowerCase().contains("genymotion")) {
            return true;
        }
        if (Build.MANUFACTURER.toLowerCase().contains("google_sdk")) {
            return true;
        }
        if (Build.MANUFACTURER.toLowerCase().contains("droid4x")) {
            return true;
        }
        if (Build.MANUFACTURER.toLowerCase().contains("emulator")) {
            return true;
        }
        if (Build.MANUFACTURER.toLowerCase().contains("android sdk built for x86")) {
            return true;
        }
        if (Build.MANUFACTURER.toLowerCase().contains("generic")) {
            return true;
        }
        if (Build.MANUFACTURER.toLowerCase().contains("google_sdk")) {
            return true;
        }
        if (Build.FINGERPRINT.toLowerCase().contains("vbox86p")) {
            return true;
        }
        if (Build.DEVICE.toLowerCase().contains("vbox86p")) {
            return true;
        }
        if (Build.HARDWARE.toLowerCase().contains("vbox86")) {
            return true;
        }
        if (Build.BRAND.toLowerCase().startsWith("generic")) {
            return true;
        }
        if (Build.DEVICE.toLowerCase().startsWith("generic")) {
            return true;
        }
        return false;
    }
}
