package ohi.andre.consolelauncher.commands.main.raw;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.os.SystemClock;
import android.util.DisplayMetrics;

import java.util.Locale;

import ohi.andre.consolelauncher.BuildConfig;
import ohi.andre.consolelauncher.R;
import ohi.andre.consolelauncher.commands.CommandAbstraction;
import ohi.andre.consolelauncher.commands.ExecutePack;
import ohi.andre.consolelauncher.commands.main.MainPack;
import ohi.andre.consolelauncher.tuils.Tuils;

/**
 * Built-in "neofetch"-style system info printer. Unlike the real neofetch, this needs
 * no separate binary/install step (no BusyBox, no root) - it's implemented natively
 * and works the moment you type "neofetch".
 */
public class neofetch implements CommandAbstraction {

    private static String bytesToReadable(long bytes) {
        if (bytes <= 0) return "0 B";
        String[] units = {"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(bytes) / Math.log10(1024));
        if (digitGroups >= units.length) digitGroups = units.length - 1;
        return String.format(Locale.US, "%.1f %s", bytes / Math.pow(1024, digitGroups), units[digitGroups]);
    }

    private static String uptime() {
        long millis = SystemClock.elapsedRealtime();
        long hours = millis / (1000 * 60 * 60);
        long minutes = (millis / (1000 * 60)) % 60;
        return hours + "h " + minutes + "m";
    }

    @Override
    public String exec(ExecutePack pack) {
        MainPack info = (MainPack) pack;
        Context context = info.context;

        String deviceLabel = Build.MANUFACTURER + Tuils.SPACE + Build.MODEL;

        String osLabel = "Android " + Build.VERSION.RELEASE + " (API " + Build.VERSION.SDK_INT + ")";

        //  memory
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
        String memoryLabel = "unknown";
        if (am != null) {
            am.getMemoryInfo(memInfo);
            long usedMem = memInfo.totalMem - memInfo.availMem;
            memoryLabel = bytesToReadable(usedMem) + " / " + bytesToReadable(memInfo.totalMem);
        }

        //  storage
        StatFs stat = new StatFs(Environment.getDataDirectory().getPath());
        long totalStorage = stat.getTotalBytes();
        long freeStorage = stat.getAvailableBytes();
        long usedStorage = totalStorage - freeStorage;
        String storageLabel = bytesToReadable(usedStorage) + " / " + bytesToReadable(totalStorage);

        //  battery
        Intent batteryIntent = context.getApplicationContext().registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        String batteryLabel = "unknown";
        if (batteryIntent != null) {
            int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            int status = batteryIntent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            boolean charging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL;
            if (level >= 0 && scale > 0) {
                batteryLabel = (level * 100 / scale) + "%" + (charging ? " (charging)" : Tuils.EMPTYSTRING);
            }
        }

        //  screen
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        String resolutionLabel = metrics.widthPixels + "x" + metrics.heightPixels + " @ " + Math.round(metrics.densityDpi) + "dpi";

        //  cpu
        String cpuLabel = Build.SUPPORTED_ABIS.length > 0 ? Build.SUPPORTED_ABIS[0] : Build.CPU_ABI;
        int cores = Runtime.getRuntime().availableProcessors();

        String logo =
                "      .o.       \n" +
                "     .oxo.      \n" +
                "   .oxxxxxo.    \n" +
                "  oxxxxxxxxxo   \n" +
                " oxxx ROOTIX xo \n" +
                "  oxxxxxxxxxo   \n" +
                "   .oxxxxxo.    \n" +
                "     .oxo.      \n" +
                "      'o'       ";

        String[] logoLines = logo.split("\n");
        String[] infoLines = new String[] {
                Build.USER != null ? Build.USER + "@" + Build.DEVICE : Build.DEVICE,
                "------------------",
                "Launcher: Rootix " + BuildConfig.VERSION_NAME,
                "Device: " + deviceLabel,
                "OS: " + osLabel,
                "Kernel: " + System.getProperty("os.version", "unknown"),
                "Uptime: " + uptime(),
                "CPU: " + cpuLabel + " (" + cores + " cores)",
                "Memory: " + memoryLabel,
                "Storage: " + storageLabel,
                "Battery: " + batteryLabel,
                "Resolution: " + resolutionLabel
        };

        StringBuilder builder = new StringBuilder();
        int lineCount = Math.max(logoLines.length, infoLines.length);
        for (int i = 0; i < lineCount; i++) {
            String logoLine = i < logoLines.length ? logoLines[i] : "                ";
            String infoLine = i < infoLines.length ? infoLines[i] : Tuils.EMPTYSTRING;

            builder.append(logoLine).append("  ").append(infoLine);
            if (i < lineCount - 1) builder.append(Tuils.NEWLINE);
        }

        return builder.toString();
    }

    @Override
    public int[] argType() {
        return new int[0];
    }

    @Override
    public int priority() {
        return 2;
    }

    @Override
    public int helpRes() {
        return R.string.help_neofetch;
    }

    @Override
    public String onArgNotFound(ExecutePack pack, int index) {
        return null;
    }

    @Override
    public String onNotArgEnough(ExecutePack pack, int nArgs) {
        return ((MainPack) pack).context.getString(helpRes());
    }
}
