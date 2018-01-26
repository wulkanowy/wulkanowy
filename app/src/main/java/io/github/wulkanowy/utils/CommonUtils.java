package io.github.wulkanowy.utils;

import android.content.Context;
import android.net.Uri;
import android.support.customtabs.CustomTabsIntent;

import io.github.wulkanowy.R;

public final class CommonUtils {

    private CommonUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static void openInternalBrowserViewer(Context context, String url) {
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setToolbarColor(context.getResources().getColor(R.color.colorPrimary));
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(context, Uri.parse(url));
    }
}
