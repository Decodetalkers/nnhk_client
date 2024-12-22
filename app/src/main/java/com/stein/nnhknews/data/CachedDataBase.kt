package com.stein.nnhknews.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.stein.nnhknews.nhk.NhkNews

/** Database class with a singleton Instance object. */
@Database(entities = [NhkNews::class], version = 1, exportSchema = false)
abstract class NewsDatabase : RoomDatabase() {

    abstract fun newsDao(): NewsDao

    companion object {
        @Volatile private var Instance: NewsDatabase? = null

        fun getDatabase(context: Context): NewsDatabase {
            // if the Instance is not null, return it, otherwise create a new database instance.
            return Instance
                    ?: synchronized(this) {
                        Room.databaseBuilder(
                                        context,
                                        NewsDatabase::class.java,
                                        "news_database"
                                )
                                /**
                                 * Setting this option in your app's database builder means that
                                 * Room permanently deletes all data from the tables in your
                                 * database when it attempts to perform a migration with no defined
                                 * migration path.
                                 */
                                .fallbackToDestructiveMigration()
                                .build()
                                .also { Instance = it }
                    }
        }
    }
}
