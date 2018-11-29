package com.toshiba.book.view.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.theartofdev.edmodo.cropper.CropImage
import com.toshiba.book.R
import com.toshiba.book.model.Book
import com.toshiba.book.other.Constant
import com.toshiba.book.other.PickerHelp
import com.toshiba.book.other.Utility
import com.toshiba.book.room.AppDataBase
import kotlinx.android.synthetic.main.activity_create_book.*
import java.util.*

class CreateBook : AppCompatActivity() {

    private var db: AppDataBase? = null
    private var cover = false
    private var dateP: Long = 0
    private var filePath: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_book)
        title = "Create Book"
        db = AppDataBase.getAppDatabase(this)

        initEvent()
    }

    private fun getPermission() {
        Dexter.withActivity(this)
            .withPermissions(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE

            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {
                        CropImage.activity()
                            .start(this@CreateBook)
                    }

                    // check for permanent denial of any permission
                    if (report.isAnyPermissionPermanentlyDenied) {

                    }
                }

                override fun onPermissionRationaleShouldBeShown(permissions: List<PermissionRequest>, token: PermissionToken) {
                    token.continuePermissionRequest()
                }
            }).check()
    }

    private val date: Long? = System.currentTimeMillis()

    private fun initEvent() {
        tvPrice?.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                tvPrice.removeTextChangedListener(this)

                try {
                    if (s.length == 2) {
                        tvPrice.setText("")
                    }
                    val rupiah = s.toString()
                        .replace("Rp", "", ignoreCase = true)
                        .replace(".", "")
                    tvPrice.setText(Utility.getRupiah(rupiah.toLong()))
                    tvPrice.setSelection(tvPrice.text.toString().length)
                } catch (nfe: NumberFormatException) {
                    nfe.printStackTrace()
                }

                tvPrice.addTextChangedListener(this)
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })

        ivCover.setOnClickListener {
            getPermission()
        }

        val c: Calendar = Calendar.getInstance()
        tvDate.setOnClickListener {
            val yearSet = c.get(Calendar.YEAR)
            val monthSet = c.get(Calendar.MONTH)
            val daySet = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                c.set(Calendar.YEAR, year)
                c.set(Calendar.MONTH, monthOfYear)
                c.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                dateP = c.timeInMillis
                tvDate.setText(Utility.getDate(c.timeInMillis))
                tvDate.error = null
            }, yearSet, monthSet, daySet)
            dpd.show()
        }

        cbHardVover.setOnCheckedChangeListener { _, b ->
            if (b) {
                cover = true
                ivCover.visibility = View.VISIBLE
            } else {
                cover = false
                ivCover.visibility = View.GONE
            }
        }

        btnSave.setOnClickListener {
            if (tvTitle.text.isEmpty()) {
                tvTitle.error = "Title Can't Null"
                return@setOnClickListener
            }

            if (tvDesc.text.isEmpty()) {
                tvDate.error = "Description Can't Null"
                return@setOnClickListener
            }

            if (tvPrice.text.isEmpty()) {
                tvPrice.error = "Price Can't Null"
                return@setOnClickListener
            }

            if (tvDate.text.isEmpty()) {
                tvDate.error = "Date Can't Null"
                return@setOnClickListener
            }

            if (tvIsbn.text.isEmpty()) {
                tvIsbn.error = "ISBN Can't Null"
                return@setOnClickListener
            }

            if (cover) {
                if (filePath == null) {
                    cbHardVover.error = "Cover Can't Null"
                    return@setOnClickListener
                }
            }

            val book = Book()
            book.title = tvTitle.text.toString()
            book.descrip = tvDesc.text.toString()
            book.price = tvPrice.text.toString()
                .replace("Rp","")
                .replace(".","")
                .toLong()
            book.datePublish = date
            book.isbn = tvIsbn.text.toString()
            book.category = spCategory.selectedItem.toString()
            book.image = filePath.toString()

            var digit = ""
            if (Constant.count.length == 1) {
                digit = "00" + Constant.count
            } else if (Constant.count.length == 2) {
                digit = "0" + Constant.count
            } else {
                digit = Constant.count
            }
            book.code = "BOOKIN$digit"

            send(book)
        }
    }

    @SuppressLint("StaticFieldLeak")
    private fun send(book: Book) {
        db?.bookDao()?.insert(book)
        Utility.toastCustom(this, "Berhasil menambah")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                filePath = result.uri
                val bitmap = PickerHelp.getImageResize(this, filePath!!)
                ivCover.setImageBitmap(bitmap)
                cbHardVover.error = null
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
            }
        }
    }

}
