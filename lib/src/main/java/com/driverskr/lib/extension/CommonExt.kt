package com.driverskr.lib.extension

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.Gravity
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.driverskr.lib.databinding.CustomToastBinding
import com.google.gson.Gson

/**
 * @Author: driverSkr
 * @Time: 2023/11/17 16:43
 * @Description: $
 */
fun Context.toast(content: String) {
    showToast(this, content)
}

fun Fragment.toast(content: String) {
    showToast(requireContext(), content)
}

fun Fragment.toastCenter(content: String) {
    val context = requireContext()
    val toast = Toast(context)
    toast.duration = Toast.LENGTH_LONG
    val inflate = CustomToastBinding.inflate(layoutInflater)
    inflate.tvContent.text = content
    toast.view = inflate.root
    toast.setGravity(Gravity.CENTER, 0, 0)
    toast.show()
}

private fun showToast(context: Context, content: String) {
    Toast.makeText(context, content, Toast.LENGTH_LONG).show()
}

inline fun <reified T: Activity> Activity.startActivity(pair: Pair<String, Int>) {
    val intent = Intent(this, T::class.java)
    intent.putExtra(pair.first, pair.second)
    startActivity(intent)
}

inline fun CharSequence?.notEmpty(): Boolean {
    return this != null && this.isNotEmpty()
}

inline fun <reified T : Any> String.fromJson(): T {
    return Gson().fromJson(this, T::class.java)
}