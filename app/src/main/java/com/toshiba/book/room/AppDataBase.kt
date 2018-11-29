package com.toshiba.book.room

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import com.toshiba.book.model.Book

@Database(entities = [Book::class], version = 1, exportSchema = false)
abstract class AppDataBase : RoomDatabase() {

    abstract fun bookDao(): BookDao

    companion object {

        private var INSTANCE: AppDataBase? = null

        fun getAppDatabase(context: Context): AppDataBase? {
            if (INSTANCE == null) {
                synchronized(AppDataBase::class) {
                    INSTANCE = Room.databaseBuilder(
                            context.applicationContext,
                            AppDataBase::class.java,
                            "book.db"
                    )
                            .allowMainThreadQueries()
                            .fallbackToDestructiveMigration()
                            .build()
                }
            }
            return INSTANCE
        }
    }
}