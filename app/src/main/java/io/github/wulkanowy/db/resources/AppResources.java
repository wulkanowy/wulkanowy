package io.github.wulkanowy.db.resources;

public interface AppResources {

    String[] getSymbolsKeysArray();

    String[] getSymbolsValuesArray();

    String getErrorLoginMessage(Exception e);

    interface ResourcesManager {

        AppResources getAppResources();
    }
}
