package io.github.wulkanowy.data.db.dao.migrations

import org.greenrobot.greendao.database.Database

import io.github.wulkanowy.api.Vulcan
import io.github.wulkanowy.data.db.dao.DbHelper
import io.github.wulkanowy.data.db.shared.SharedPrefContract

class Migration30 : DbHelper.Migration {

    override fun getVersion(): Int? {
        return 30
    }

    @Throws(Exception::class)
    override fun runMigration(db: Database, sharedPref: SharedPrefContract, vulcan: Vulcan) {
        throw Exception("No migrations")
    }
}
