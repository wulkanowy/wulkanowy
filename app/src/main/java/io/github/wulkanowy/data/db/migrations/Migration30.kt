package io.github.wulkanowy.data.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration30 : Migration(29, 30) {

    private val materialColors = listOf(
        0xffe57373,
        0xfff06292,
        0xffba68c8,
        0xff9575cd,
        0xff7986cb,
        0xff64b5f6,
        0xff4fc3f7,
        0xff4dd0e1,
        0xff4db6ac,
        0xff81c784,
        0xffaed581,
        0xffff8a65,
        0xffd4e157,
        0xffffd54f,
        0xffffb74d,
        0xffa1887f,
        0xff90a4ae
    )

    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE Students ADD COLUMN avatar_color TEXT NOT NULL DEFAULT ${materialColors.random()}")
        database.execSQL("ALTER TABLE Students ADD COLUMN nick TEXT")
    }
}
