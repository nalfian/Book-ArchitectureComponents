package com.toshiba.book.view.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.toshiba.book.R
import com.toshiba.book.adapter.BookPager
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        view_pager.adapter = BookPager(supportFragmentManager)
        tabs.setupWithViewPager(view_pager)
    }
}
