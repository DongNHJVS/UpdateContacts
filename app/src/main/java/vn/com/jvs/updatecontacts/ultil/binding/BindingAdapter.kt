package vn.com.jvs.updatecontacts.ultil.binding

import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.BindingAdapter
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import vn.com.jvs.updatecontacts.ultil.exts.getParentActivity

@BindingAdapter("adapterRecyclerView")
fun setAdapterRecyclerView(view: RecyclerView, adapter: RecyclerView.Adapter<*>) {
    view.adapter = adapter
}

@BindingAdapter("bindingProgressBar")
fun setBindingProgressBar(view: ProgressBar, progress: MutableLiveData<Int>?) {
    val parentActivity: AppCompatActivity? = view.getParentActivity()
    if (parentActivity != null && progress != null) {
        progress.observe(parentActivity, Observer {
            value ->
            if (value != null) {
                view.progress = value
            }
        })
    }
}

@BindingAdapter("disableClickView")
fun disableClickView(view: View, enable: MutableLiveData<Boolean>?) {
    val parentActivity: AppCompatActivity? = view.getParentActivity()
    if (parentActivity != null && enable != null) {
        enable.observe(parentActivity, Observer {
            value ->
            if (value != null) {
                view.isEnabled = value
            }
        })
    }
}

@BindingAdapter("bindingViewVisibility")
fun bindingViewVisibility(view: View, enable: MutableLiveData<Int>?) {
    val parentActivity: AppCompatActivity? = view.getParentActivity()
    if (parentActivity != null && enable != null) {
        enable.observe(parentActivity, Observer {
            value ->
            if (value != null) {
                view.visibility = value
            }
        })
    }
}