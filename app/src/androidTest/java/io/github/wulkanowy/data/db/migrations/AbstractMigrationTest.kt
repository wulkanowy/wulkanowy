package io.github.wulkanowy.data.db.migrations

import androidx.preference.PreferenceManager
import androidx.room.Room
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import io.github.wulkanowy.data.db.AppDatabase
import io.github.wulkanowy.data.db.SharedPrefProvider
import io.github.wulkanowy.utils.AppInfo
import io.mockk.every
import io.mockk.mockk
import org.junit.Rule

abstract class AbstractMigrationTest {

    val dbName = "migration-test"

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        AppDatabase::class.java.canonicalName,
        FrameworkSQLiteOpenHelperFactory()
    )

    fun getMigratedRoomDatabase(): AppDatabase {
        val appInfo = mockk<AppInfo>()
        every { appInfo.defaultColorsForAvatar } returns listOf(1)

        val database = Room.databaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java, dbName
        )
            .addMigrations(
                *AppDatabase.getMigrations(
                    SharedPrefProvider(
                        PreferenceManager
                            .getDefaultSharedPreferences(ApplicationProvider.getApplicationContext())
                    ), appInfo
                )
            )
            .build()
        // close the database and release any stream resources when the test finishes
        helper.closeWhenFinished(database)
        return database
    }
}
