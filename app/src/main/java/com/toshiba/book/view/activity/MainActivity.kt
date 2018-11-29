package com.toshiba.book.view.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.widget.LinearLayout
import com.toshiba.book.R
import com.toshiba.book.adapter.BookAdapter
import com.toshiba.book.model.Book
import com.toshiba.book.other.Utility
import com.toshiba.book.room.AppDataBase
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private var book: MutableList<Book>? = ArrayList()
    private var adapter: BookAdapter? = null
    private var desc = false
    private var db: AppDataBase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = AppDataBase.getAppDatabase(this)

        initView()
        initEvent()
    }

    private fun initView() {
        rvMain.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        adapter = book?.let { BookAdapter(it, this) }
        rvMain.adapter = adapter

    }

    private fun initEvent() {
        btnAdd.setOnClickListener {
            startActivity(Intent(this, CreateBook::class.java))
        }

        btNDelete.setOnClickListener {
            delete()
        }

        svMain.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { search(it) }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        tvSort.setOnClickListener {
            if (desc) {
                desc = false
                fun selector(p: Book): String? = p.title
                book?.sortBy { selector(it) }
            } else {
                fun selector(p: Book): String? = p.title
                book?.sortByDescending { selector(it) }
                desc = true
            }
            adapter?.notifyDataSetChanged()
        }
    }

    private fun search(query: String) {
        book?.clear()
        val bookList = Arrays.asList(*db?.bookDao()!!.searchBook("%$query%"))
        book?.addAll(bookList)
        adapter?.notifyDataSetChanged()
        if (book?.size == 0){
            getData()
        }
    }

    private fun getData() {
        book?.clear()
        val bookList = Arrays.asList(*db?.bookDao()!!.selectAll())
        book?.addAll(bookList)
        adapter?.notifyDataSetChanged()
    }

    fun deletePos(book: Book){
        db?.bookDao()?.delete(book)
        Utility.toastCustom(this, "Berhasil menghapus")
        getData()
    }

    fun update(bookList: MutableList<Book>){
        book = bookList
    }

    private fun delete() {
        var count = 0
        for (i in 0 until (book?.size ?: 0)) {
            count++
            if (book?.get(i)?.check!!) {
                db?.bookDao()?.delete(book?.get(i)!!)
                if (count == book?.size) {
                    getData()
                    Utility.toastCustom(this, "Berhasil menghapus")
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        getData()
    }
}
