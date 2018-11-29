package com.toshiba.book.room


import android.arch.persistence.room.*
import com.toshiba.book.model.Book

@Dao
interface BookDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(book: Book): Long

    @Update
    fun update(book: Book): Int

    @Delete
    fun delete(book: Book): Int

    @Query("SELECT * FROM Book")
    fun selectAll(): Array<Book>

    @Query("SELECT * FROM Book WHERE title LIKE  :title")
    fun searchBook(title: String): Array<Book>
}