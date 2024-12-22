package com.stein.nnhknews.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.stein.nnhknews.nhk.NhkNews
import kotlinx.coroutines.flow.Flow

@Dao
interface NewsDao {
    @Query("SELECT * from news ORDER BY title ASC") fun getAllNews(): Flow<List<NhkNews>>
    @Query("DELETE FROM news") fun clearAllData(): Int
    @Insert(onConflict = OnConflictStrategy.IGNORE) fun insertNews(vararg news: NhkNews)
}
