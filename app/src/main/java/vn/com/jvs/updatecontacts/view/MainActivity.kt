package vn.com.jvs.updatecontacts.view

import android.Manifest
import android.content.ContentProviderOperation
import android.content.OperationApplicationException
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.RemoteException
import android.provider.ContactsContract
import android.view.View
import android.view.Window
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import vn.com.jvs.updatecontacts.model.ContractsModel
import vn.com.jvs.updatecontacts.R
import vn.com.jvs.updatecontacts.viewModel.MainViewModel
import vn.com.jvs.updatecontacts.databinding.ActivityMainBinding
import java.util.*
import org.koin.android.ext.android.get

class MainActivity : AppCompatActivity() {

    private var _contractsList = ArrayList<ContractsModel>()
    private lateinit var _dataBinding: ActivityMainBinding
    private lateinit var _viewModel : MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _viewModel = get()
        _dataBinding = DataBindingUtil.setContentView(this@MainActivity, R.layout.activity_main)
        _dataBinding.viewModel = _viewModel
        _dataBinding.setLifecycleOwner { this@MainActivity.lifecycle }

        _dataBinding.listItem.layoutManager = LinearLayoutManager(this@MainActivity, RecyclerView.VERTICAL, false)

        getPermissionGranted()

        setUpButtonUpdateClick()

        // ic_event reload list
        _dataBinding.listSwipe.setColorSchemeColors(
                ContextCompat.getColor(this@MainActivity, android.R.color.holo_blue_bright),
                ContextCompat.getColor(this@MainActivity, android.R.color.holo_green_light),
                ContextCompat.getColor(this@MainActivity, android.R.color.holo_orange_light),
                ContextCompat.getColor(this@MainActivity, android.R.color.holo_red_light)
        )

        _dataBinding.listSwipe.setOnRefreshListener {
            _viewModel.getContactList(contentResolver)
        }

        _viewModel.disableSwipe.observe(this@MainActivity, androidx.lifecycle.Observer {
            if (it != null && it) {
                _dataBinding.listSwipe.isRefreshing = false
            }
        })
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_PERMISSIONS -> {
                // Do not care about the results returned
                // Switch to the next screen
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) { // Request
                    initDialogError()
                } else {
                    _viewModel.getContactList(contentResolver)
                }
            }
        }
    }

    /**
     * Request permission for app
     */
    private fun getPermissionGranted() {
        // Check permission
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this@MainActivity,
                            Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(this@MainActivity,
                            Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED) { // Request
                requestPermissions(arrayOf(Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS),
                        REQUEST_PERMISSIONS)
            } else {
                _viewModel.getContactList(contentResolver)
            }
        } else {
            _viewModel.getContactList(contentResolver)
        }
    }

    /**
     * Init dialog show need grant permission
     */
    private fun initDialogError() {
        val builder = AlertDialog.Builder(this@MainActivity)
        builder.setTitle(getString(R.string.app_dialog_title))
        builder.setMessage(getString(R.string.app_dialog_missing_permission))
        builder.setPositiveButton(getString(R.string.app_dialog_btn_ok)) { dialog, which ->
            dialog.dismiss()
        }
    }

    /**
     * Button update click
     */
    private fun setUpButtonUpdateClick() {
        this@MainActivity._dataBinding.progressbar.visibility = View.GONE
        this@MainActivity._dataBinding.btnUpdate.setOnClickListener {
            this@MainActivity._dataBinding.progressbar.setVisibility(View.VISIBLE)
            val builder = AlertDialog.Builder(this@MainActivity)
            builder.setTitle(getString(R.string.app_dialog_title))
            builder.setMessage(getString(R.string.app_dialog_content))
            builder.setPositiveButton(getString(R.string.app_dialog_btn_ok)) { dialog, which ->
                _viewModel.clickEnable.value = false
                for (i in _contractsList.indices) {
                    this@MainActivity._dataBinding.progressbar.max = _contractsList.size
                    try {
                        val newNumber = buildNewNulber(_contractsList[i]._phone)
                        updateContactNew(_contractsList[i]._id, newNumber, _contractsList[i]._type)
                        _viewModel.process.value = i
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                _dataBinding.progressbar?.visibility = View.GONE

                // Get new contact
                _viewModel.clickEnable.value = true
                _viewModel.getContactList(contentResolver)
                dialog.dismiss()
            }
            builder.setNegativeButton(getString(R.string.app_dialog_btn_cancel)) { dialog, which ->
                // Do nothing
                dialog.dismiss()
            }
            val alert = builder.create()
            alert.requestWindowFeature(Window.FEATURE_NO_TITLE)
            if (!alert.isShowing) alert.show()
        }
    }

    /**
     * Create New number for update contact
     */
    private fun buildNewNulber(inputPhone: String): String {
        var input = inputPhone
        var newNumber = ""
        if (input.startsWith("+84")) {
            input = input.replace("+84", "0")
        }
        val startInput = input.substring(0, 4)
        val endInput = input.substring(4, input.length)
        val check84 = _dataBinding.check84.isChecked

        // Viettell
        if (startInput.startsWith("0169")) {
            newNumber = if (check84) {
                "+8439$endInput"
            } else {
                "039$endInput"
            }
        }
        if (startInput.startsWith("0168")) {
            newNumber = if (check84) {
                "+8438$endInput"
            } else {
                "038$endInput"
            }
        }
        if (startInput.startsWith("0167")) {
            newNumber = if (check84) {
                "+8437$endInput"
            } else {
                "037$endInput"
            }
        }
        if (startInput.startsWith(" 0166")) {
            newNumber = if (check84) {
                "+8436$endInput"
            } else {
                "036$endInput"
            }
        }
        if (startInput.startsWith("0165")) {
            newNumber = if (check84) {
                "+8435$endInput"
            } else {
                "035$endInput"
            }
        }
        if (startInput.startsWith("0164")) {
            newNumber = if (check84) {
                "+8434$endInput"
            } else {
                "034$endInput"
            }
        }
        if (startInput.startsWith("0163")) {
            newNumber = if (check84) {
                "+8433$endInput"
            } else {
                "033$endInput"
            }
        }
        if (startInput.startsWith("0162")) {
            newNumber = if (check84) {
                "+8432$endInput"
            } else {
                "032$endInput"
            }
        }
        // Mobi
        if (startInput.startsWith(" 0120")) {
            newNumber = if (check84) {
                "+8470$endInput"
            } else {
                "070$endInput"
            }
        }
        if (startInput.startsWith("0121")) {
            newNumber = if (check84) {
                "+8479$endInput"
            } else {
                "079$endInput"
            }
        }
        if (startInput.startsWith("0122")) {
            newNumber = if (check84) {
                "+8477$endInput"
            } else {
                "077$endInput"
            }
        }
        if (startInput.startsWith("0126")) {
            newNumber = if (check84) {
                "+8476$endInput"
            } else {
                "076$endInput"
            }
        }
        if (startInput.startsWith("0128")) {
            newNumber = if (check84) {
                "+8478$endInput"
            } else {
                "078$endInput"
            }
        }
        // Vina
        if (startInput.startsWith("0124")) {
            newNumber = if (check84) {
                "+8484$endInput"
            } else {
                "084$endInput"
            }
        }
        if (startInput.startsWith("0127")) {
            newNumber = if (check84) {
                "+8481$endInput"
            } else {
                "081$endInput"
            }
        }
        if (startInput.startsWith("0129")) {
            newNumber = if (check84) {
                "+8482$endInput"
            } else {
                "082$endInput"
            }
        }
        if (startInput.startsWith("0123")) {
            newNumber = if (check84) {
                "+8483$endInput"
            } else {
                "083$endInput"
            }
        }
        if (startInput.startsWith("0125")) {
            newNumber = if (check84) {
                "+8485$endInput"
            } else {
                "085$endInput"
            }
        }
        // Vietnam
        if (startInput.startsWith("0186")) {
            newNumber = if (check84) {
                "+8456$endInput"
            } else {
                "056$endInput"
            }
        }
        if (startInput.startsWith("0188")) {
            newNumber = if (check84) {
                "+8458$endInput"
            } else {
                "058$endInput"
            }
        }
        // Gtel
        if (startInput.startsWith("0199")) {
            newNumber = if (check84) {
                "+8459$endInput"
            } else {
                "059$endInput"
            }
        }
        return newNumber
    }

    /**
     * Update new contact for db
     */
    @Throws(RemoteException::class, OperationApplicationException::class)
    fun updateContactNew(contactId: String, newNumber: String?, phoneType: String) {
        //ASSERT: @contactId alreay has a work phone number
        val ops = ArrayList<ContentProviderOperation>()
        val selectPhone = ContactsContract.Data.CONTACT_ID + "=? AND " + ContactsContract.Data.MIMETYPE + "='" +
                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE + "'" + " AND " + ContactsContract.CommonDataKinds.Phone.TYPE + "=?"
        val phoneArgs = arrayOf(contactId, phoneType)
        ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                .withSelection(selectPhone, phoneArgs)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, newNumber)
                .build())
        this.contentResolver.applyBatch(ContactsContract.AUTHORITY, ops)
    }

    companion object {
        const val REQUEST_PERMISSIONS = 100
    }
}