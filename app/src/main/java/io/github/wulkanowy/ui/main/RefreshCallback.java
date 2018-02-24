package io.github.wulkanowy.ui.main;

public interface RefreshCallback {

    void onDoInBackground() throws Exception;

    void onCanceledAsync();

    void onEndAsync(boolean result, Exception exception);
}
