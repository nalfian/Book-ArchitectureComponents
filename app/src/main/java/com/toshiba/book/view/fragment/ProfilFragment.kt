package com.toshiba.book.view.fragment


import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import kotlinx.android.synthetic.main.fragment_profil.view.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class ProfilFragment : Fragment() {

    private var db: AppDataBase? = null
    private var view1 : View? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profil, container, false)
        initset(view)
        view1 = view

        db = context?.let { AppDataBase.getAppDatabase(it) }

        initEvent(view)
        return view
    }

    private fun initEvent(view: View) {
        view.btnSave.setOnClickListener {
            Constant.book?.title = view.tvTitle?.text.toString()
            Constant.book?.descrip = view.tvTitle?.text.toString()
            Constant.book?.image = filePath.toString()
            update(Constant.book!!)
        }

        view.ivCover.setOnClickListener {
            getPermission()
        }
    }

    private fun getPermission() {
        Dexter.withActivity(activity)
            .withPermissions(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE

            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {
                        context?.let {
                            CropImage.activity()
                                .start(it, this@ProfilFragment)
                        }
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

    private fun initset(view: View?) {
        view?.tvTitle?.setText(Constant.book?.title)
        view?.tvDesc?.setText(Constant.book?.descrip)
        if (Constant.book?.image != "null") {
            val bitmap = context?.let { PickerHelp.getImageResize(it, Uri.parse(Constant.book?.image)) }
            view?.ivCover?.setImageBitmap(bitmap)
        }
    }

    private fun update(book: Book) {
        db?.bookDao()?.update(book)
        context?.let { Utility.toastCustom(it, "Berhasil memperbarui") }
    }

    private var filePath: Uri? = Uri.parse(Constant.book?.image)

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                filePath = result.uri
                val bitmap = context?.let { PickerHelp.getImageResize(it, filePath!!) }
                view1?.ivCover?.setImageBitmap(bitmap)
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
            }
        }
    }
}
