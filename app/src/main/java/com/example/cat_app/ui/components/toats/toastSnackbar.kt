package com.example.cat_app.ui.components.toats

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar


fun toastSnackbar(
    context: Context,
    message: String,
    duration: Int = Snackbar.LENGTH_LONG,
    backgroundColor: Int = Color.parseColor("#323232"), //stard color
    textColor: Int = Color.WHITE
) {
    val view = (context as? Activity)?.findViewById<View>(android.R.id.content)
    view?.let {
        val snackbar = Snackbar.make(it, message, duration)
        val snackbarView = snackbar.view
        snackbarView.setBackgroundColor(backgroundColor)

        //updates the text color
        val textView = snackbarView.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        textView.setTextColor(textColor)

        snackbar.show()
    }
}
