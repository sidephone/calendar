package com.sidephone.calendar.db

import android.content.Context
import androidx.room3.ColumnTypeConverters
import androidx.room3.Database
import androidx.room3.Room
import androidx.room3.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver


@Database(entities = [Event::class, EventOverride::class], version = 1, exportSchema = true)
@ColumnTypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
	companion object {
		private const val DB_NAME = "calendar.db"

		@Volatile
		private var instance: AppDatabase? = null

		fun get(context: Context): AppDatabase =
			instance ?: synchronized(this) {
				instance ?: Room.databaseBuilder<AppDatabase>(context.applicationContext, DB_NAME)
					.setDriver(BundledSQLiteDriver())
					.build()
					.also { instance = it }
			}
	}

	abstract fun eventDao(): EventDao
}
