package io.github.wulkanowy.data.db.dao.migrations;

import org.greenrobot.greendao.database.Database;

import io.github.wulkanowy.api.Vulcan;
import io.github.wulkanowy.data.db.dao.DbHelper;
import io.github.wulkanowy.data.db.shared.SharedPrefContract;

public class Migration25 implements DbHelper.Migration {

    @Override
    public Integer getVersion() {
        return 25;
    }

    @Override
    public void runMigration(Database db, SharedPrefContract sharedPref, Vulcan vulcan) throws Exception {
        throw new Exception("No migrations");
    }
}
