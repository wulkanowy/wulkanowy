package io.github.wulkanowy.utils;

import android.util.Log;

public final class LogUtils {

    private LogUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static void debug(String tag, String message) {
        Log.d(tag, message);
    }
}
