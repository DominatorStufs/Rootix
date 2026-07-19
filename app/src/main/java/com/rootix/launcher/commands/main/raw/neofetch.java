package com.rootix.launcher.commands.main.raw;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.os.SystemClock;
import android.util.DisplayMetrics;

import java.util.Locale;

import com.rootix.launcher.BuildConfig;
import com.rootix.launcher.R;
import com.rootix.launcher.commands.CommandAbstraction;
import com.rootix.launcher.commands.ExecutePack;
import com.rootix.launcher.commands.main.MainPack;
import com.rootix.launcher.tuils.Tuils;

/**
 * Prints a neofetch-style summary of the device, similar to the Linux "neofetch" tool.
 * Usage: just type "neofetch" and press enter.
 */
public class neofetch implements CommandAbstraction {

    @Override
    public String exec(ExecutePack pack) {
        MainPack info = (MainPack) pack;
        Context context = info.context;

        String userAtDevice = "rootix@" + Build.MODEL;

        StringBuilder b = new StringBuilder();

        b.append("   ██████╗").append(Tuils.NEWLINE)
                .append("   ██╔══██╗").append(Tuils.NEWLINE)
                .append("   ██████╔╝").append(Tuils.NEWLINE)
                .append("   ██╔══██╗").append(Tuils.NEWLINE)
                .append("   ██║  ██║").append(Tuils.NEWLINE)
                .append("   ╚═╝  ╚═╝").append(Tuils.NEWLINE)
                .append(Tuils.NEWLINE);

        b.append(userAtDevice).append(Tuils.NEWLINE);
        b.append(dashes(userAtDevice.length())).append(Tuils.NEWLINE);

        b.append("OS: Android ").append(Build.VERSION.RELEASE)
                .append(" (API ").append(Build.VERSION.SDK_INT).append(")").append(Tuils.NEWLINE);
        b.append("Host: ").append(Build.MANUFACTURER).append(Tuils.SPACE).append(Build.MODEL).append(Tuils.NEWLINE);
        b.append("Kernel: ").append(System.getProperty("os.version")).append(Tuils.NEWLINE);
        b.append("Uptime: ").append(formatUptime(SystemClock.elapsedRealtime())).append(Tuils.NEWLINE);
        b.append("Launcher: Rootix ").append(BuildConfig.VERSION_NAME).append(Tuils.NEWLINE);

        try {
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
            am.getMemoryInfo(mi);
            long totalMemMB = mi.totalMem / (1024 * 1024);
            long usedMemMB = totalMemMB - (mi.availMem / (1024 * 1024));
            b.append("Memory: ").append(usedMemMB).append("MiB / ").append(totalMemMB).append("MiB").append(Tuils.NEWLINE);
        } catch (Exception e) {
            b.append("Memory: N/A").append(Tuils.NEWLINE);
        }

        try {
            StatFs stat = new StatFs(Environment.getDataDirectory().getPath());
            long totalBytes = stat.getTotalBytes();
            long usedBytes = totalBytes - stat.getAvailableBytes();
            b.append("Storage: ").append(formatGiB(usedBytes)).append(" / ").append(formatGiB(totalBytes)).append(Tuils.NEWLINE);
        } catch (Exception e) {
            b.append("Storage: N/A").append(Tuils.NEWLINE);
        }

        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        b.append("Resolution: ").append(dm.widthPixels).append("x").append(dm.heightPixels).append(Tuils.NEWLINE);

        try {
            Intent batteryIntent = context.getApplicationContext().registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
            int rawLevel = batteryIntent != null ? batteryIntent.getIntExtra("level", -1) : -1;
            int scale = batteryIntent != null ? batteryIntent.getIntExtra("scale", -1) : -1;
            int pct = (rawLevel >= 0 && scale > 0) ? Math.round(rawLevel * 100f / scale) : -1;
            b.append("Battery: ").append(pct >= 0 ? pct + "%" : "N/A").append(Tuils.NEWLINE);
        } catch (Exception e) {
            b.append("Battery: N/A").append(Tuils.NEWLINE);
        }

        String cpuAbi = Build.SUPPORTED_ABIS.length > 0 ? Build.SUPPORTED_ABIS[0] : "unknown";
        b.append("CPU: ").append(Build.HARDWARE).append(" (").append(cpuAbi).append(")");

        return b.toString();
    }

    private static String dashes(int n) {
        StringBuilder d = new StringBuilder();
        for (int i = 0; i < n; i++) d.append("-");
        return d.toString();
    }

    private static String formatUptime(long millis) {
        long totalSeconds = millis / 1000;
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;

        if (hours > 0) return hours + "h " + minutes + "m";
        if (minutes > 0) return minutes + "m " + seconds + "s";
        return seconds + "s";
    }

    private static String formatGiB(long bytes) {
        double gib = bytes / (1024.0 * 1024.0 * 1024.0);
        return String.format(Locale.US, "%.1fGiB", gib);
    }

    @Override
    public int[] argType() {
        return new int[0];
    }

    @Override
    public int priority() {
        return 1;
    }

    @Override
    public int helpRes() {
        return R.string.help_neofetch;
    }

    @Override
    public String onArgNotFound(ExecutePack info, int index) {
        return null;
    }

    @Override
    public String onNotArgEnough(ExecutePack info, int nArgs) {
        return null;
    }
}
