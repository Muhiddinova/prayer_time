package com.example.prayertime.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.TypeConverters
import com.example.prayertime.model.DateConverter
import com.example.prayertime.model.Times

@Database(entities = [Times::class], version = 2, exportSchema = false)
@TypeConverters(DateConverter::class)
abstract class RoomDatabase: androidx.room.RoomDatabase() {
    abstract val timesByYearDao: TimesByYearDao

    companion object{
        private var INSTANCE: RoomDatabase? = null

        fun getDatabase(context: Context): RoomDatabase{
            if(INSTANCE != null){
                return  INSTANCE!!
            }
            synchronized(this){
                val database = Room
                    .databaseBuilder(context, RoomDatabase::class.java, "Time")
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = database
                return INSTANCE!!
            }
        }
    }
}