package io.github.wulkanowy.utilities;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;

public final class KeyboardUtilities {

    private KeyboardUtilities() {
        throw new IllegalStateException("Utility class");
    }

    public static void toggleSoftInput(Context context) {
        InputMethodManager inputMethodManager = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }
    }
}