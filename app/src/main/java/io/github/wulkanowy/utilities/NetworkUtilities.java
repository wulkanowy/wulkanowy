package io.github.wulkanowy.utilities;

import android.content.Context;
import android.net.ConnectivityManager;

public final class NetworkUtilities {

    private NetworkUtilities() {
        throw new IllegalStateException("Utility class");
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return connectivityManager != null && connectivityManager.getActiveNetworkInfo() != null
                && connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting();

    }
}
