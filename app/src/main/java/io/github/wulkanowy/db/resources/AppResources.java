package io.github.wulkanowy.db.resources;

public interface AppResources {

    String[] getSymbolsKeysArray();

    String[] getSymbolsValuesArray();

    interface ResourcesManager {

        AppResources getAppResources();
    }
}
