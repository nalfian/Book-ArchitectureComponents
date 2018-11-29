package com.toshiba.book.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity
data class Book(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    var title: String? = null,
    var descrip: String? = null,
    var code: String? = null,
    var price: Long? = null,
    var category: String? = null,
    var datePublish: Long? = null,
    var check: Boolean? = false,
    var isbn: String? = null,
    var image: String? = null)