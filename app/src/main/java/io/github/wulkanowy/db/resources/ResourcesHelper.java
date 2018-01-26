package io.github.wulkanowy.db.resources;

import android.content.Context;
import android.content.res.Resources;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.github.wulkanowy.R;
import io.github.wulkanowy.di.annotations.ApplicationContext;

@Singleton
public class ResourcesHelper {

    private Resources resources;

    @Inject
    public ResourcesHelper(@ApplicationContext Context context) {
        resources = context.getResources();
    }

    public String[] getSymbolsKeysArray() {
        return resources.getStringArray(R.array.symbols);
    }

    public String[] getSymbolsValuesArray() {
        return resources.getStringArray(R.array.symbols_values);
    }

    public String getErrorFieldRequired() {
        return resources.getString(R.string.error_field_required);
    }

    public String getErrorPassInvalid() {
        return resources.getString(R.string.error_invalid_password);
    }

    public String getErrorEmailInvalid() {
        return resources.getString(R.string.error_invalid_email);
    }
}
