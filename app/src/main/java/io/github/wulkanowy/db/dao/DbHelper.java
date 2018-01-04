package io.github.wulkanowy.db.dao;

import android.content.Context;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.github.wulkanowy.db.dao.entities.DaoMaster;
import io.github.wulkanowy.di.annotations.ApplicationContext;

@Singleton
public class DbHelper extends DaoMaster.DevOpenHelper {

    @Inject
    public DbHelper(@ApplicationContext Context context, String dbName) {
        super(context, dbName);
    }
}
