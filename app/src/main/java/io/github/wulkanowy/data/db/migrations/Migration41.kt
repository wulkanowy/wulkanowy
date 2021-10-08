package io.github.wulkanowy.data.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration41 : Migration(40, 41) {

    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE Homework ADD COLUMN is_added_by_user INTEGER NOT NULL DEFAULT 0")
        database.execSQL(
            """CREATE TABLE IF NOT EXISTS `AdminMessages` (
            `id` INTEGER NOT NULL, 
            `title` TEXT NOT NULL, 
            `content` TEXT NOT NULL, 
            `version_name` INTEGER, 
            `version_max` INTEGER, 
            `target_register_host` TEXT, 
            `target_flavor` TEXT,
            `destination_url` TEXT,
            `priority` TEXT NOT NULL,
            `type` TEXT NOT NULL, 
            PRIMARY KEY(`id`))"""
        )
    }
}