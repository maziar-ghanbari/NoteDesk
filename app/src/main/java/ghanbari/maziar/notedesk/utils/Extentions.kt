package ghanbari.maziar.notedesk.utils

import android.app.Activity
import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.google.android.material.snackbar.Snackbar
import ghanbari.maziar.notedesk.R

fun RecyclerView.init(adapter: RecyclerView.Adapter<*>, layoutManager: LayoutManager) {
    this.adapter = adapter
    this.layoutManager = layoutManager
}

fun View.isShown(visible: Boolean, isInVisible: Boolean = false) {
    if (visible) {
        this.visibility = View.VISIBLE
    } else {
        if (isInVisible) {
            this.visibility = View.INVISIBLE
        } else {
            this.visibility = View.GONE
        }
    }
}

//open keyboard
fun View.showKeyboard() {
    this.requestFocus()
    val inputMethodManager =
        context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}

//close keyboard
fun View.hideKeyboard() {
    val inputMethodManager =
        context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
}

//show view but gone another
fun View.showBut(paitiAr1: View? = null, paitiAr2: View? = null) {
    this.visibility = View.VISIBLE
    paitiAr1?.visibility = View.GONE
    paitiAr2?.visibility = View.GONE
}


//snackBar
fun Activity.snackBar(
    colorBackground: Int,
    message: String,
    view: View? = null,
    /*btn open for open somewhere*/
    btnOpen: (() -> Unit)? = null
) {

    //this.findViewById(android.R.id.content)
    //show default view but in bottomSheet or dialog use their view
    val mySnack = Snackbar.make(
        view ?: this.findViewById(android.R.id.content),
        message,
        Snackbar.LENGTH_SHORT
    )

    //set background
    mySnack.setBackgroundTint(ContextCompat.getColor(this, colorBackground))
    //setShape
    mySnack.view.background = ContextCompat.getDrawable(this, R.drawable.snack_bar_shape_bg)

    //margin
    val params = mySnack.view.layoutParams as (FrameLayout.LayoutParams)
    params.setMargins(16, 32, 16, 32)
    mySnack.view.layoutParams = params

    //wrap content size
    //gravity center
    params.width = FrameLayout.LayoutParams.WRAP_CONTENT
    params.gravity = if (view == null) Gravity.CENTER_HORIZONTAL else Gravity.CENTER
    mySnack.view.layoutParams = params

    //icon
    val textView =
        mySnack.view.findViewById(com.google.android.material.R.id.snackbar_text) as TextView
    textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_info_24, 0, 0, 0)
    textView.compoundDrawablePadding =
        resources.getDimensionPixelOffset(`in`.nouri.dynamicsizeslib.R.dimen._3mdp)

    //check for btn
    btnOpen?.let { btn ->
        //set color
        val color = ContextCompat.getColor(this, R.color.teal_200)
        mySnack.setActionTextColor(color)
        //set action
        mySnack.setAction("باز کردن"){
            btn()
        }
    }

    mySnack.show()
}

//alert dialog
fun Activity.alertDialog(title: String, message: String, yes: (() -> Unit)) {
    AlertDialog.Builder(this)
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton("بله") { _, _ ->
            yes()
        }.setNegativeButton("خیر") { _, _ ->
        }.create().show()
}

//english number to iranian number
fun String.englishToIranianNumber(): String {
    var result = ""
    var ir = '۰'
    for (ch in this) {
        ir = ch
        when (ch) {
            '0' -> ir = '۰'
            '1' -> ir = '۱'
            '2' -> ir = '۲'
            '3' -> ir = '۳'
            '4' -> ir = '۴'
            '5' -> ir = '۵'
            '6' -> ir = '۶'
            '7' -> ir = '۷'
            '8' -> ir = '۸'
            '9' -> ir = '۹'
        }
        result = "${result}$ir"
    }
    return result
    //copy right of this extention function: Alireza_Barakati in stackOverFlow
}

//file naming rule
fun String.convertByPolicyNoteDeskFileNaming(): String {
    val c = this.toCharArray().toMutableList()
    for (i in 0 until c.size) {
        c[i] =
            if (c[i] in ('a'..'z') || c[i] in ('A'..'A') || c[i] in ('ا'..'ی') || c[i] in ('0'..'9')) {
                c[i]
            } else {
                '\u200C'
            }
    }
    var result = ""
    c.forEach {
        result += it
    }
    return result
}