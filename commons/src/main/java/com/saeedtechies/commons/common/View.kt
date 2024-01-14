package com.saeedtechies.commons.common

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.EditText
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.google.android.material.chip.Chip

fun View.visible(){
    visibility = VISIBLE
}

fun View.gone(){
    visibility = GONE
}

fun View.invisible(){
    visibility = INVISIBLE
}

fun ImageView.setVectorTint(context: Context, colorId: Int) {
    setColorFilter(context.getColorById(colorId), android.graphics.PorterDuff.Mode.SRC_IN)
}

fun ImageView.setImageTint(context: Context, colorId: Int) {
    setColorFilter(context.getColorById(colorId), android.graphics.PorterDuff.Mode.MULTIPLY)
}

fun Chip.setIconTint(context: Context, colorId: Int) {
    val chipIcon = this.chipIcon
    chipIcon?.setTint(context.getColorById(colorId)) // Set the desired color
    this.chipIcon = chipIcon
}

fun ImageView.setImageDrawable(context: Context, drawable: Int) {
    setImageDrawable(ContextCompat.getDrawable(context, drawable))
}

fun ImageView.setImageColor(colorId: Int) {
    setColorFilter(ContextCompat.getColor(context, colorId), android.graphics.PorterDuff.Mode.SRC_IN);
}

fun EditText.onTextChanged(callback: (charSequence: CharSequence?, start: Int, before: Int, count: Int) -> Unit){
    addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            callback(s, start, before, count)
        }
        override fun afterTextChanged(s: Editable?) {}
    })
}