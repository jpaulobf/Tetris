package util;

public class Logger {

    public static final int INFO        = 0;
    public static final int WARNING     = 1;
    public static final int DEBUG       = 2;
    public static final int ERROR       = 3;
    public static int LOGGER_OUT_LEVEL  = INFO;

    public void setLoggerOutLevel(int level) {
        LOGGER_OUT_LEVEL = level;
    }

    public static void INFO(String message, Object object) {
        if (LOGGER_OUT_LEVEL <= INFO) {
            System.out.println("INFO - " + d() + " - " + message + " - " + object.getClass().getName() + " - line: " +  Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
    }

    public static void WARNING(String message, Object object) {
        if (LOGGER_OUT_LEVEL <= WARNING) {
            System.out.println("WARNING - " + d() + " - " + message + " - " + object.getClass().getName() + " - line: " +  Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
    }

    public static void DEBUG(String message, Object object) {
        if (LOGGER_OUT_LEVEL <= DEBUG) {
            System.out.println("DEBUG - " + d() + " - " + message + " - " + object.getClass().getName() + " - line: " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
    }

    public static void ERROR(String message, Object object) {
        if (LOGGER_OUT_LEVEL <= ERROR) {
            System.out.println("ERROR - " + d() + " - " + message + " - " + object.getClass().getName() + " - line: " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
    }

    public static String d() {
        return (new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()));
    }
}
