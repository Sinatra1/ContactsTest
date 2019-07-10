package ru.shumilov.vladislav.contactstest.modules.core.preferences

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.widget.Toast
import ru.shumilov.vladislav.contactstest.R
import ru.shumilov.vladislav.contactstest.modules.core.injection.ApplicationScope
import java.lang.ref.WeakReference


@ApplicationScope
class PhoneHelper {

    protected var fragView: WeakReference<Fragment>? = null
    protected var phone: String? = null

    companion object {
        const val ONLY_NUMBERS = "\\D+"
        const val PHONE_FORMAT = "(\\d{1})(\\d{3})(\\d{3})(\\d+)"
        const val CALL_PHONE_REQUEST_CODE = 100
    }

    fun formattedPhoneToOnlyNumbers(phone: String?): String? {
        if (phone == null) {
            return null
        }

        return phone.replace(Regex(ONLY_NUMBERS), "")
    }

    fun onlyNumberToFormattedPhone(phone: String?): String? {
        if (phone == null) {
            return null
        }

        return phone.replace(Regex(PHONE_FORMAT), "+$1 ($2) $3-$4")
    }

    private fun isViewAttached(): Boolean {
        return fragView != null
    }

    fun dialPhoneNumber(phone: String?, view: Fragment) {
        if (phone == null || view == null) {
            return
        }

        this.fragView = WeakReference(view)
        this.phone = phone

        if (ContextCompat.checkSelfPermission(fragView?.get()?.context!!, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionWriteExternalStorage()
            return
        }

        doDialPhoneNumber()
    }

    private fun requestPermissionWriteExternalStorage() {
        if (!isViewAttached()) {
            return
        }

        if (ActivityCompat.shouldShowRequestPermissionRationale(fragView?.get()?.activity as Activity, Manifest.permission.CALL_PHONE)) {
            AlertDialog.Builder(fragView?.get()?.context!!)
                    .setMessage(R.string.call_phone_rationale_description)
                    .setPositiveButton(R.string.agree, DialogInterface.OnClickListener { dialog, which ->
                        doRequestWriteExternalStoragePermission()
                    }).show()
        } else {
            doRequestWriteExternalStoragePermission()
        }
    }

    private fun doRequestWriteExternalStoragePermission() {
        if (!isViewAttached()) {
            return
        }

        fragView?.get()?.requestPermissions(arrayOf(Manifest.permission.CALL_PHONE), CALL_PHONE_REQUEST_CODE)
    }

    @SuppressLint("MissingPermission")
    private fun doDialPhoneNumber() {
        if (phone == null || !isViewAttached()) {
            return
        }

        val number = Uri.parse("tel:$phone")
        val dial = Intent(Intent.ACTION_CALL, number)
        fragView?.get()?.startActivity(dial)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (!isViewAttached() || requestCode != CALL_PHONE_REQUEST_CODE || grantResults.size != 1) {
            return
        }

        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            doDialPhoneNumber()
        } else {
            Toast.makeText(fragView?.get()?.context!!, R.string.call_phone_permission_not_granted_message, Toast.LENGTH_LONG).show()
        }
    }

    fun onDestroy() {
        fragView = null
    }
}