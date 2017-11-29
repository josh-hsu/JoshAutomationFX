package com.mumu.libjoshgame;

import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Log {
    public static final int DEBUG = 3;
    public static final int ERROR = 6;
    public static final int INFO = 4;
    public static final int VERBOSE = 2;
    public static final int WARN = 5;

    public static final String logFilePath = System.getProperty("user.dir") + "\\JAFXLog_" + getCurrentTimeSimple() + ".txt";

    public static void v(String tag, String msg) {
        System.out.print(logFormatted(tag, msg, VERBOSE));
        saveLogToFile(tag, msg, VERBOSE);
    }

    public static void d(String tag, String msg) {
        System.out.print(logFormatted(tag, msg, DEBUG));
        saveLogToFile(tag, msg, DEBUG);
    }

    public static void e(String tag, String msg) {
        System.out.print(logFormatted(tag, msg, ERROR));
        saveLogToFile(tag, msg, ERROR);
    }

    public static void i(String tag, String msg) {
        System.out.print(logFormatted(tag, msg, INFO));
        saveLogToFile(tag, msg, INFO);
    }

    public static void w(String tag, String msg) {
        System.out.print(logFormatted(tag, msg, WARN));
        saveLogToFile(tag, msg, WARN);
    }

    private static void saveLogToFile(String tag, String msg, int level) {
        String log = logFormatted(tag, msg, level);
        try {
            FileOutputStream fos = new FileOutputStream(logFilePath, true);
            fos.write(log.getBytes());
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getCurrentTimeSimple() {
        DateFormat df = new SimpleDateFormat("MM-dd_hh-mm", Locale.getDefault());
        return df.format(Calendar.getInstance().getTime());
    }

    private static String logFormatted(String tag, String msg, int level) {
        DateFormat df = new SimpleDateFormat("yyyy/MM/dd a hh:mm:ss", Locale.getDefault());
        String thisTime = df.format(Calendar.getInstance().getTime());
        return String.format("%18s: [%s] %s: %s\n", thisTime, getLevelString(level), tag, msg);
    }

    private static String getLevelString(int level) {
        String ret = "";

        switch (level) {
            case DEBUG:
                ret = "DEBG";
                break;
            case WARN:
                ret = "WARN";
                break;
            case INFO:
                ret = "INFO";
                break;
            case ERROR:
                ret = "ERRO";
                break;
            case VERBOSE:
                ret = "VRBS";
                break;
        }
        return ret;
    }
}
