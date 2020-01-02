package vn.com.jvs.updatecontacts.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import vn.com.jvs.updatecontacts.model.ContractsModel

class ViewContactViewModel : ViewModel() {

    var contact: MutableLiveData<ContractsModel> = MutableLiveData()

    fun binding(contact: ContractsModel) {
        this@ViewContactViewModel.contact.value = contact
    }
}