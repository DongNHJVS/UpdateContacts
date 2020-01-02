package vn.com.jvs.updatecontacts.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import vn.com.jvs.updatecontacts.model.ContractsModel
import vn.com.jvs.updatecontacts.R
import vn.com.jvs.updatecontacts.viewModel.ViewContactViewModel
import vn.com.jvs.updatecontacts.databinding.ViewContractBinding
import java.util.*

/**
 * Created by JVS017
 * on 2018/09/17.
 */
class AdapterViewContact : RecyclerView.Adapter<AdapterViewContact.ViewHolder>() {

    private var dataList: ArrayList<ContractsModel> = arrayListOf()

    // Create view of Adapter
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ViewContractBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.view_contract, parent, false)
        return ViewHolder(binding)
    }

    // Bind data
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataList[position])
    }

    // Get item count
    override fun getItemCount(): Int {
        return dataList.size
    }

    // add view when data change
    fun addDataList(transactionDatas: ArrayList<ContractsModel>){
        this.dataList.addAll(transactionDatas)
        notifyDataSetChanged()
    }

    // update view when data change
    fun updateDataList(transactionDatas: ArrayList<ContractsModel>){
        this.dataList = arrayListOf()
        this.dataList.addAll(transactionDatas)
        notifyDataSetChanged()
    }

    // Class binding data
    class ViewHolder(private val binding: ViewContractBinding): RecyclerView.ViewHolder(binding.root){
        private val viewModel = ViewContactViewModel()

        fun bind(contact: ContractsModel){
            viewModel.binding(contact)
        }
    }
}