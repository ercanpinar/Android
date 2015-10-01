/*
 * Copyright (c) StreetHawk, All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.
 */
package com.streethawk.library.core;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.StatFs;
import android.os.SystemClock;
import android.view.Surface;
import android.view.WindowManager;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.net.ssl.HttpsURLConnection;

public class StreetHawkCoreService extends Service implements Thread.UncaughtExceptionHandler{

    private Context mContext;
    private final String SHTASKTIME  = "shTaskTime";


    private void registerScheduledTask(final Context context) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean taskRegistered = (PendingIntent.getBroadcast(context, 0,
                        new Intent(Constants.BROADCAST_APP_STATUS_CHK),
                        PendingIntent.FLAG_NO_CREATE) != null);
                if (taskRegistered) {
                    return;
                }
                SharedPreferences pref = context.getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
                SharedPreferences.Editor e = pref.edit();
                e.putLong(SHTASKTIME, System.currentTimeMillis());
                e.commit();

                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(context, StreethawkBroadcastReceiver.class);
                intent.setAction(Constants.BROADCAST_APP_STATUS_CHK);
                intent.putExtra(Constants.SHPACKAGENAME,context.getPackageName());
                PendingIntent appStatusIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), AlarmManager.INTERVAL_HOUR/*60000*/, appStatusIntent);
            }
        }).start();
    }

    public StreetHawkCoreService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        this.mContext = getApplicationContext();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            getApplication().registerActivityLifecycleCallbacks(StreetHawkActivityLifecycleCallback.getInstance());
        }
        defaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        forceFlushCrashReportToServer(getApplicationContext());
        registerScheduledTask(mContext);
        Logging manager = Logging.getLoggingInstance(mContext);
        manager.ForceFlushLogsToServer();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /***********CRASH REPORTING ***************/
    private final String SUBTAG = "CrashReportingService ";
    private static final String NEXT_ROW = "\n";
    private static final String TAB = "\t";
    private static final String COLON = ":";
    private Thread.UncaughtExceptionHandler defaultUncaughtExceptionHandler;
    private String getCpuInfo() {
        return System.getProperty("os.arch") + " | " + Build.CPU_ABI + " | " + Build.CPU_ABI2 + " | " + Build.BOARD;
    }
    public static final String CRASH_CACHE_DIR_NAME = "sh_crash_cache";
    private static File getCacheDir(Context context) {
        File cacheDir;
        cacheDir = new File(context.getCacheDir().toString() + File.separator + CRASH_CACHE_DIR_NAME);
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }
        return cacheDir;
    }
    private String getProcessName(Context context) {
        int id = android.os.Process.myPid();
        String myProcessName = context.getPackageName();

        ActivityManager actvityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> procInfos = actvityManager.getRunningAppProcesses();

        for (ActivityManager.RunningAppProcessInfo procInfo : procInfos) {
            if (id == procInfo.pid) {
                myProcessName = procInfo.processName;
            }
        }
        return Util.getAppName(context) + " [" + id + " : " + myProcessName + "]";
    }
    private String formRow(String title, String value, int tabs, boolean valueOnNextRow) {

        final String NEXT_ROW = "\n";
        final String TAB = "\t";
        final String COLON = ":";

        StringBuilder row = new StringBuilder();
        row.append(title);
        row.append(COLON);
        for (int i = 0; i < tabs; i++) {
            row.append(TAB);
        }
        if (valueOnNextRow) {
            row.append(NEXT_ROW);
        }
        row.append(value);

        row.append(NEXT_ROW);

        return row.toString();
    }

    /**
     * API returns current battery level on device
     *
     * @param context
     * @return
     */
    private String getBatteryLevel(Context context) {
        Intent batteryIntent = context.getApplicationContext().registerReceiver(null,
                new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int rawlevel = batteryIntent.getIntExtra("level", -1);
        double scale = batteryIntent.getIntExtra("scale", -1);
        double level = -1;
        if (rawlevel >= 0 && scale > 0) {
            level = rawlevel / scale;
        }
        String strlevel = Double.toString(level * 100) + "%";
        return strlevel;
    }
    private static String getApplicationFilePath(Context context) {
        final File filesDir = context.getFilesDir();
        if (filesDir != null) {
            return filesDir.getAbsolutePath();
        }
        return "Couldn't retrieve ApplicationFilePath";
    }

    private String formRow(String title, String value, int tabs) {
        return formRow(title, value, tabs, false);
    }

    /**
     * return stacktrace for the crash
     *
     * @param t
     * @return
     */
    private String getStackTrace(Throwable t) {
        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);

        // If the exception was thrown in a background thread inside
        // AsyncTask, then the actual exception can be found with getCause
        Throwable cause = t;
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        final String stacktraceAsString = result.toString();
        printWriter.close();

        return stacktraceAsString;
    }

    /**
     * Retruns info of crashed thread.
     *
     * @param t
     * @return
     */
    private static String getThreadInfo(Thread t) {
        if (t != null) {
            return Long.toString(t.getId()) + " -- " + t.getName() + " -- " + t.getThreadGroup();
        } else {
            return "";
        }
    }

    /**
     * Return version of Android OS
     *
     * @return
     */
    private static String getOSVersion() {
        String os;
        try {
            // This field has been added in Android 1.6
            final Field SDK_INT = Build.VERSION.class.getField("SDK_INT");
            os = "Android " + Build.VERSION.RELEASE + " (" + Integer.toString(SDK_INT.getInt(null)) + ")";
        } catch (SecurityException e) {
            os = "Android " + Build.VERSION.RELEASE + " (" + Build.VERSION.SDK + ")";
        } catch (NoSuchFieldException e) {
            os = "Android " + Build.VERSION.RELEASE + " (" + Build.VERSION.SDK + ")";
        } catch (IllegalArgumentException e) {
            os = "Android " + Build.VERSION.RELEASE + " (" + Build.VERSION.SDK + ")";
        } catch (IllegalAccessException e) {
            os = "Android " + Build.VERSION.RELEASE + " (" + Build.VERSION.SDK + ")";
        }

        return os;
    }

    /**
     * returns stack trace of all the threads running at the time of crash.
     *
     * @return
     */
    private String getAllStackTrace() {
        String str = "";
        Map<Thread, StackTraceElement[]> allStackTraceOutput = Thread.getAllStackTraces();
        Set<Thread> threadSet = allStackTraceOutput.keySet();
        Iterator<Thread> iterator = threadSet.iterator();
        while (iterator.hasNext()) {
            Thread key = iterator.next();
            str += key;
            str += "\n";
            StackTraceElement[] trace = allStackTraceOutput.get(key);
            for (int i = 0; i < trace.length; i++) {
                str += trace[i];
                str += "\n";
            }
            str += "\n";
            str += "\n";
        }
        return str;
    }
    private static String floatForm(double d) {
        return new DecimalFormat("#.##").format(d);
    }
    private static String bytesToHuman(long size) {
        long Kb = 1 * 1024;
        long Mb = Kb * 1024;
        long Gb = Mb * 1024;
        if (size < Kb) return floatForm(size) + " byte";
        if (size >= Kb && size < Mb) return floatForm((double) size / Kb) + " Kb";
        if (size >= Mb && size < Gb) return floatForm((double) size / Mb) + " Mb";
        if (size >= Gb) return floatForm((double) size / Gb) + " Gb";
        return "Unknown";
    }

    /**
     * Function returns memory information
     * @return
     */
    private String getMemInfo() {
        String newLine = "\n";
        String tab = "\t";
        try {
            StatFs statFs = new StatFs(Environment.getRootDirectory().getAbsolutePath());

            long Total = ((long) statFs.getBlockCount() * (long) statFs.getBlockSize());
            long Free = (statFs.getAvailableBlocks() * (long) statFs.getBlockSize());
            long Used = Total - Free;

            String memInfo = tab + "Total: " + bytesToHuman(Total) + newLine
                    + tab + "Free: " + bytesToHuman(Free) + newLine
                    + tab + "Used: " + bytesToHuman(Used);
            return memInfo;
        } catch (Exception e) {
            e.printStackTrace();
            return "Unknown";
        }
    }

    /**
     * Returns true if device supports Telephony
     * @param context
     * @return
     */
    private boolean isTelephonySupported(Context context){
        PackageManager packageManager = context.getPackageManager();
        if(packageManager.hasSystemFeature(PackageManager.FEATURE_TELEPHONY)){
            return true;
        }
        return false;
    }



    /**
     * Form crash report to be presented at server.
     *
     * @param context
     * @param thread
     * @param exception
     * @param time
     * @return
     */
    private String formReport(Context context, Thread thread, Throwable exception, long time) {
        StringBuilder report = new StringBuilder();
        report.append(formRow("Incident Identifier", UUID.randomUUID().toString(), 1));
        report.append(formRow("CrashReporter Key", "", 2));
        report.append(formRow("Hardware Model", "(" + Build.MANUFACTURER + ") " + Build.BRAND + " " + Build.MODEL + " " + Build.PRODUCT, 3));
        report.append(formRow("Core Type", getCpuInfo(), 2));
        report.append(formRow("Process", getProcessName(context), 2));
        report.append(NEXT_ROW);
        report.append(formRow("Path", getApplicationFilePath(context), 3));
        report.append(formRow("Identifier", context.getPackageName(), 2));
        report.append(formRow("Application Version Name", Util.getAppVersionName(context), 2));
        report.append(formRow("Application version code", Integer.toString(Util.getAppVersion(context)), 2));
        report.append(formRow("Streethawk Library Version", Util.getLibraryVersion(), 2));
        report.append(formRow("Streethawk DistributionType", Util.getDistributionType(), 2));
        report.append(formRow("Development Platform", Util.getPlatformName(), 2));
        report.append(formRow("Installer", Util.getMarketName(context), 2));
        report.append(formRow("Supports calling", Boolean.toString(isTelephonySupported(context)), 2));
        report.append(NEXT_ROW);
        report.append(formRow("BatteryLevel", getBatteryLevel(context), 2));
        report.append(formRow("MemInfo",getMemInfo(), 2));
        report.append(NEXT_ROW);
        report.append(formRow("Date/Time", Util.getFormattedDateTime(System.currentTimeMillis(), true), 2));
        report.append(formRow("OS Version", getOSVersion(), 2));
        report.append(NEXT_ROW);
        report.append(formRow("Session Number", Integer.toString(getCurrentSessionId(context)), 2));
        report.append(formRow("Device Orientation", getDeviceCurrentOrientation(), 2));
        report.append(formRow("Exception Type", exception.toString(), 2));
        report.append(formRow("Crashed Thread", getThreadInfo(thread), 2));
        report.append(NEXT_ROW);
        report.append(formRow("StackTrace", getStackTrace(exception), 1, true));
        report.append(NEXT_ROW);
        report.append(formRow("All Thread information", getAllStackTrace(), 1, true));
        return report.toString();
    }

    /**
     * Returns current session id
     *
     * @param context
     * @return
     */
    private int getCurrentSessionId(Context context) {
        String SESSIONIDCNT = "session_id_cnt";
        SharedPreferences sharedPreferences = context.getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(SESSIONIDCNT, 1);
    }


    private boolean prepareCrashReport(Thread thread, Throwable exception) {
        long time = System.currentTimeMillis();
        String formattedTime = Util.getFormattedDateTime(time, true);
        File crashReportFile = new File(getCacheDir(getApplicationContext()), "crash" + formattedTime + ".txt");
        try {
            FileWriter writer = new FileWriter(crashReportFile);
            writer.append(formReport(getApplicationContext(), thread, exception, time));
            writer.flush();
            writer.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private String getTimeFromFileName(String fileName) {
        try {
            return fileName.substring((fileName.indexOf("h") + 1), (fileName.indexOf(".") - 1));
        } catch (NumberFormatException e) {
            return Util.getFormattedDateTime(System.currentTimeMillis(), true);
        }
    }

    private byte[] getBytesFromFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);

        // Get the size of the file
        long length = file.length();

        // You cannot create an array using a long type.
        // It needs to be an int type.
        // Before converting to an int type, check
        // to ensure that file is not larger than Integer.MAX_VALUE.
        if (length > Integer.MAX_VALUE) {
            // File is too large
        }

        // Create the byte array to hold the data
        byte[] bytes = new byte[(int)length];

        // Read in the bytes
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
                && (numRead = is.read(bytes, offset, Math.min(bytes.length - offset, 512*1024))) >= 0) {
            offset += numRead;
        }

        // Ensure all the bytes have been read in
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file "+file.getName());
        }

        // Close the input stream and return bytes
        is.close();
        return bytes;
    }


    private void flushIndividualCrashReportToServer(final File file) {
        final Context context = getApplicationContext();
        final String EXCEPTION_FILE = "exception_file";
        if (Util.isNetworkConnected(context)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String lineEnd = "\r\n";
                        String twoHyphens = "--";
                        String boundary = "*****";
                        int bytesRead, bytesAvailable, bufferSize;
                        final int maxBufferSize = 1 * 1024 * 1024;
                        byte[] buffer;
                        FileInputStream fileInputStream = new FileInputStream(file);
                        URL url = Util.getCrashReportingUrl(context);
                        HttpsURLConnection conn = null;
                        final String installId =Util.getInstallId(context);
                        final String app_key  = Util.getAppKey(context);
                        String time = getTimeFromFileName(file.getName());
                        conn = (HttpsURLConnection) url.openConnection();
                        conn.setRequestMethod("POST");
                        conn.setDoInput(true);
                        conn.setDoOutput(true);
                        conn.setRequestProperty("X-Installid", installId);
                        conn.setRequestProperty("X-App-Key", app_key);
                        conn.setRequestProperty("User-Agent", app_key + "(" + Util.getLibraryVersion() + ")");
                        conn.setRequestProperty("Connection", "Keep-Alive");
                        conn.setRequestProperty("Cache-Control", "no-cache");
                        conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                        conn.setRequestProperty("charset", "utf-8");
                        DataOutputStream request = new DataOutputStream(conn.getOutputStream());
                        request.writeBytes(twoHyphens + boundary + lineEnd);
                        request.writeBytes("Content-Disposition: form-data; name=\"installid \""+ lineEnd);
                        request.writeBytes(lineEnd);
                        request.writeBytes(installId);
                        request.writeBytes(lineEnd);
                        request.writeBytes(twoHyphens + boundary + lineEnd);
                        request.writeBytes("Content-Disposition: form-data; name=\"created \"" + lineEnd);
                        request.writeBytes(lineEnd);
                        request.writeBytes(time);
                        request.writeBytes(twoHyphens + boundary + lineEnd);
                        request.writeBytes("Content-Disposition: form-data; name=\"" + EXCEPTION_FILE + "\"; filename=\"" + file.getName() + "\"" + lineEnd);
                        request.writeBytes(lineEnd);
                        // create a buffer of  maximum size
                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        buffer = new byte[bufferSize];
                        // read file and write it into form...
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                        while (bytesRead > 0) {
                            request.write(buffer, 0, bufferSize);
                            bytesAvailable = fileInputStream.available();
                            bufferSize = Math.min(bytesAvailable, maxBufferSize);
                            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                        }
                        request.writeBytes(lineEnd);
                        request.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                        request.flush();
                        request.close();
                        InputStream error = conn.getErrorStream();
                        if(null==error){
                            BufferedReader reader = null;
                            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                            String answer = reader.readLine();
                            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                                file.delete();
                                if (null == answer)
                                    return;
                                if (answer.isEmpty())
                                    return;
                                //manager.processAppStatusCall(answer);
                                // TODO: add routine to process appstatus call here
                            } else {
                                //manager.processErrorAckFromServer(answer);
                                // TODO: add routine to process error appstatus call here
                            }
                        }
                        conn.disconnect();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (ProtocolException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }


    /**
     * return device's current orientation
     *
     * @return
     */
    private String getDeviceCurrentOrientation() {
        String orientation = "Unknown";
        WindowManager windowService = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        int currentAngle = windowService.getDefaultDisplay().getRotation();
        if (Surface.ROTATION_0 == currentAngle) {
            orientation = "Potrait";
        } else if (Surface.ROTATION_180 == currentAngle) {
            orientation = "Potrait";
        } else if (Surface.ROTATION_90 == currentAngle) {
            orientation = "Landscape";
        } else if (Surface.ROTATION_270 == currentAngle) {
            orientation = "Landscape";
        }
        return orientation;
    }

    /**
     * Flush pending crash reports to server
     *
     * @param dir
     */
    private void flushCrashReportToServer(File dir) {
        for (final File file : dir.listFiles()) {
            flushIndividualCrashReportToServer(file);
        }

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Thread.setDefaultUncaughtExceptionHandler(this);
        flushCrashReportToServer(getCacheDir(getApplicationContext()));
        if ((intent != null) && (intent.getBooleanExtra("ALARM_RESTART_SERVICE_DIED", false)))
        {
            if (Util.isServiceRunning(getApplicationContext(),StreetHawkCoreService.class)) {
                ensureServiceStaysRunning();
                return START_STICKY;
            }
        }
        return Service.START_STICKY;
    }


    private void ensureServiceStaysRunning() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
            // A restart intent - this never changes...
            final int restartAlarmInterval = 20*60*1000;
            final int resetAlarmTimer = 2*60*1000;
            final Intent restartIntent = new Intent(this, StreetHawkCoreService.class);
            restartIntent.putExtra("ALARM_RESTART_SERVICE_DIED", true);
            final AlarmManager alarmMgr = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
            Handler restartServiceHandler = new Handler()
            {
                @Override
                public void handleMessage(Message msg) {
                    // Create a pending intent
                    PendingIntent pintent = PendingIntent.getService(getApplicationContext(), 0, restartIntent, 0);
                    alarmMgr.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + restartAlarmInterval, pintent);
                    sendEmptyMessageDelayed(0, resetAlarmTimer);
                }
            };
            restartServiceHandler.sendEmptyMessageDelayed(0, 0);
        }
    }


    /**
     * Force flush individual crash reports to server. Use this API to flush crash reports when device gets active internet connection.
     *
     * @return
     */
    public boolean forceFlushCrashReportToServer(Context context) {
        try {
            if (null == context)
                return false;
            flushCrashReportToServer(getCacheDir(context));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Force terminate application when it crashes. Application when crashed is not terminated automatically die to StreetHawk's crash reporting service.
     */
    private void endApplication() {
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(10);
    }

    /**
     * Function is called when application crashes. The function handles crash and throws crash to default crash handler so that crash can be handled by other services
     * such as new relic or crashlytics.
     *
     * @param thread
     * @param exception
     */
    @Override
    public void uncaughtException(final Thread thread, final Throwable exception) {
        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                    endApplication();
                }
                endApplication();
            }
        }.start();
        prepareCrashReport(thread, exception);
        flushCrashReportToServer(getCacheDir(getApplicationContext()));
        defaultUncaughtExceptionHandler.uncaughtException(thread, exception);
    }




}
