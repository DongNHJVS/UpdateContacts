package vn.com.jvs.updatecontacts.Ultil.Exts

import android.content.ContextWrapper
import android.view.View
import androidx.appcompat.app.AppCompatActivity

// Get context of activity
fun View.getParentActivity(): AppCompatActivity?{
    var context = this.context
    while (context is ContextWrapper) {
        if (context is AppCompatActivity) {
            return context
        }
        context = context.baseContext
    }
    return null
}