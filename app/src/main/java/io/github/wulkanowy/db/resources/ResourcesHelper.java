package io.github.wulkanowy.db.resources;

import android.content.Context;
import android.content.res.Resources;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.github.wulkanowy.R;
import io.github.wulkanowy.di.annotations.ApplicationContext;

@Singleton
public class ResourcesHelper implements AppResources {

    private Resources resources;

    @Inject
    public ResourcesHelper(@ApplicationContext Context context) {
        resources = context.getResources();
    }

    @Override
    public String[] getSymbolsKeysArray() {
        return resources.getStringArray(R.array.symbols);
    }

    @Override
    public String[] getSymbolsValuesArray() {
        return resources.getStringArray(R.array.symbols_values);
    }
}
