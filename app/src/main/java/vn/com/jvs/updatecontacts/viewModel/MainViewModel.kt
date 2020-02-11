package vn.com.jvs.updatecontacts.viewModel

import android.content.ContentResolver
import android.provider.ContactsContract
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import vn.com.jvs.updatecontacts.adapter.AdapterViewContact
import vn.com.jvs.updatecontacts.model.ContractsModel
import java.util.*

@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class MainViewModel : ViewModel() {

    // adapter
    val adapter: AdapterViewContact = AdapterViewContact()

    // Process of progressbar
    val process: MutableLiveData<Int> = MutableLiveData()

    // Disable view when this progress
    val clickEnable: MutableLiveData<Boolean> = MutableLiveData()

    // Flag view or hide
    val viewList: MutableLiveData<Int> = MutableLiveData()
    val viewNull: MutableLiveData<Int> = MutableLiveData()
    val disableSwipe: MutableLiveData<Boolean> = MutableLiveData()

    init {
        clickEnable.value = true
        process.value = 0
        viewList.value = View.VISIBLE
        viewNull.value = View.GONE
        disableSwipe.value = false
    }

    // List contact length >= 11
    private var _contractsList = ArrayList<ContractsModel>()

    /**
     * Get contact with length >= 11
     */
    fun getContactList(contentResolver: ContentResolver) {
        _contractsList = ArrayList()
        val cr = contentResolver
        val cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null)
        if (cur.count ?: 0 > 0) {
            while (cur!!.moveToNext()) {
                val id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID))
                val name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME))
                if (cur.getInt(cur.getColumnIndex(
                                ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    val pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", arrayOf(id), null)
                    if (pCur != null) {
                        while (pCur.moveToNext()) {
                            var phoneNo = pCur.getString(pCur.getColumnIndex(
                                    ContactsContract.CommonDataKinds.Phone.NUMBER))
                            val type = pCur.getString(pCur.getColumnIndex(
                                    ContactsContract.CommonDataKinds.Phone.TYPE))
                            phoneNo = phoneNo.replace("-", "")
                            phoneNo = phoneNo.replace(" ", "")
                            phoneNo = phoneNo.replace("(", "")
                            phoneNo = phoneNo.replace(")", "")
                            val phoneTemp = phoneNo.replace("+84", "0")
                            // Chi lay 11 so va so may ban
                            if (phoneTemp.length >= 11 && !phoneTemp.startsWith("02")) {
                                val contracts = ContractsModel(name, phoneNo, id, type)
                                _contractsList.add(contracts)
                            }
                        }
                        pCur.close()
                    }
                }
            }
        }
        cur.close()

        if (_contractsList.size == 0) {
            viewList.value = View.GONE
            viewNull.value = View.VISIBLE
        } else {
            viewList.value = View.VISIBLE
            viewNull.value = View.GONE
        }

        disableSwipe.value = true
        adapter.addDataList(_contractsList)
    }
}