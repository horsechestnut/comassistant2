package com.example.administrator.comassistant2.simulation.tool;

import android.util.Log;


/**
 * 对Log的封装
 *
 * @author Phoenix
 * @date 2016-8-3 15:08
 */
public final class LogUtil {
    private static final String TAG = "GXT";
    private static boolean Is_Log_Show = true;
    private static String GXT_LOG = "GXT";
    private static String GXT_LOG_ERROR = "GERROR";

    private LogUtil() {
    }

    /**
     * 默认tag 为LOG
     *
     * @param text
     */
    public static void ii(String text) {
        try {
            if (!Is_Log_Show) {
                return;
            }

            StackTraceElement ste = new Throwable().getStackTrace()[1];
            String className = ste.getClassName();
            className = className.substring(className.lastIndexOf(".") + 1);
            Log.i(GXT_LOG, "[" + className + ":" + ste.getLineNumber() + "] --> " + text);
        } catch (Exception e) {

        }

    }

    public static void i(String logtype, String text) {
        try {
            if (!Is_Log_Show) {
                return;
            }
            StackTraceElement ste = new Throwable().getStackTrace()[1];
            String className = ste.getClassName();
            className = className.substring(className.lastIndexOf(".") + 1);
            Log.i(logtype, "[" + className + ":" + ste.getLineNumber() + "] --> " + text);
        } catch (Exception e) {

        }
    }


    /**
     * 默认tag 为LOG
     *
     * @param text
     */
    public static void e(String text) {
        try {
            if (!Is_Log_Show) {
                return;
            }
            Log.e(TAG, text);
        } catch (Exception e) {

        }

    }

    /**
     * 默认tag 为LOG
     *
     * @param text
     */
    public static void d(String text) {
        try {
            if (!Is_Log_Show) {
                return;
            }
            Log.d(TAG, text);
        } catch (Exception e) {

        }
    }

    public static void ee(Exception e) {
        try {
            if (!Is_Log_Show) {
                return;
            }

            StackTraceElement ste = new Throwable().getStackTrace()[1];
            String className = ste.getClassName();
            className = className.substring(className.lastIndexOf(".") + 1);
            if (e != null && e.getMessage() != null) {
                Log.e(GXT_LOG_ERROR, "[" + className + ":" + ste.getLineNumber() + "] --> " + e.getMessage());
            } else {
                Log.e(GXT_LOG_ERROR, "[" + className + ":" + ste.getLineNumber() + "] --> " + e.toString());
            }
        } catch (Exception e2) {

        }
    }

    /**
     * 默认tag 为LOG
     *
     * @param text
     */
    public static void v(String text) {
        try {
            if (!Is_Log_Show) {
                return;
            }
            Log.v(TAG, text);
        } catch (Exception e) {

        }
    }


    /**
     * 自定义tag
     *
     * @param tag
     * @param text
     */
    public static void e(String tag, String text) {
        try {
            if (!Is_Log_Show) {
                return;
            }
            Log.e(tag, text);
        } catch (Exception e) {

        }
    }

    /**
     * 自定义tag
     *
     * @param tag
     * @param text
     */
    public static void d(String tag, String text) {
        try {
            if (!Is_Log_Show) {
                return;
            }
            Log.d(tag, text);
        } catch (Exception e) {

        }
    }

    /**
     * 自定义tag
     *
     * @param tag
     * @param text
     */
    public static void v(String tag, String text) {
        try {
            if (!Is_Log_Show) {
                return;
            }
            Log.v(tag, text);
        } catch (Exception e) {

        }
    }

}
