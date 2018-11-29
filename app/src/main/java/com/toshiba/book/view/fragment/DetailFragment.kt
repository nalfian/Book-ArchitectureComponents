package com.toshiba.book.view.fragment


import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.toshiba.book.R
import com.toshiba.book.model.Book
import com.toshiba.book.other.Constant
import com.toshiba.book.other.Utility
import com.toshiba.book.room.AppDataBase
import kotlinx.android.synthetic.main.fragment_detail.view.*
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class DetailFragment : Fragment() {

    private var db: AppDataBase? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_detail, container, false)

        initset(view)

        db = context?.let { AppDataBase.getAppDatabase(it) }

        initEvent(view)
        return view
    }

    private var date: Long = Constant.book?.datePublish!!

    private fun initEvent(view: View) {
        val c: Calendar = Calendar.getInstance()
        view.tvDate.setOnClickListener {
            val yearSet = c.get(Calendar.YEAR)
            val monthSet = c.get(Calendar.MONTH)
            val daySet = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(context, DatePickerDialog.OnDateSetListener { v, year, monthOfYear, dayOfMonth ->
                c.set(Calendar.YEAR, year)
                c.set(Calendar.MONTH, monthOfYear)
                c.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                date = c.timeInMillis
                view.tvDate.setText(Utility.getDate(c.timeInMillis))
            }, yearSet, monthSet, daySet)
            dpd.show()
        }

        view.btnSave.setOnClickListener {
            Constant.book?.isbn = view.tvIsbn.text.toString()
            Constant.book?.category = view.spCategory?.selectedItem.toString()
            Constant.book?.datePublish = date
            Constant.book?.price = view.tvPrice.text.toString()
                .replace("Rp","")
                .replace(".","")
                .toLong()
            update(Constant.book!!)
        }

        view.tvPrice?.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                view.tvPrice.removeTextChangedListener(this)

                try {
                    if (s.length == 2) {
                        view.tvPrice.setText("")
                    }
                    val rupiah = s.toString()
                        .replace("Rp", "", ignoreCase = true)
                        .replace(".", "")
                    view.tvPrice.setText(Utility.getRupiah(rupiah.toLong()))
                    view.tvPrice.setSelection(view.tvPrice.text.toString().length)
                } catch (nfe: NumberFormatException) {
                    nfe.printStackTrace()
                }

                view.tvPrice.addTextChangedListener(this)
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })
    }

    @SuppressLint("SetTextI18n")
    private fun initset(view: View?) {
        view?.tvCodeBook?.setText(Constant.book?.code)
        view?.tvPrice?.setText(Constant.book?.price?.let { Utility.getRupiah(it) })
        view?.tvDate?.setText(Constant.book?.datePublish?.let { Utility.getDate(it) })
        view?.tvIsbn?.setText(Constant.book?.isbn)
        if (Constant.book?.category?.contains("1", true)!!) {
            view?.spCategory?.setSelection(0)
        } else {
            view?.spCategory?.setSelection(1)
        }
    }

    private fun update(book: Book) {
        db?.bookDao()?.update(book)
        context?.let { Utility.toastCustom(it, "Berhasil memperbarui") }
    }

}
