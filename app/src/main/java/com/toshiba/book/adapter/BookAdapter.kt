package com.toshiba.book.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import com.toshiba.book.R
import com.toshiba.book.model.Book
import com.toshiba.book.other.Constant
import com.toshiba.book.view.activity.DetailActivity
import com.toshiba.book.view.activity.MainActivity

class BookAdapter(
    private val bookList: MutableList<Book>, var context: MainActivity
) : RecyclerView.Adapter<BookAdapter.ViewHolder>() {

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.cbMain.isChecked = bookList[position].check!!
        holder.tvName.text = bookList[position].title
        holder.cbMain.setOnCheckedChangeListener { _, b ->
            bookList[position].check = !bookList[position].check!!
            context.update(bookList)
        }

        holder.itemView.setOnClickListener {
            context.startActivity(Intent(context, DetailActivity::class.java))
            Constant.book = bookList[position]
        }

        holder.delete.setOnClickListener {
            context.deletePos(bookList[position])
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_book, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return bookList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvName: TextView = itemView.findViewById(R.id.tvName)
        var cbMain: CheckBox = itemView.findViewById(R.id.cbItem)
        var delete: ImageView = itemView.findViewById(R.id.ivDelete)
    }

}