package com.irinamihaila.quizzapp.utils

import android.content.Context
import android.content.res.Resources
import android.graphics.Outline
import android.graphics.Paint
import android.text.Editable
import android.util.TypedValue
import android.view.View
import android.view.ViewOutlineProvider
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.viewbinding.ViewBinding

/**
 * ViewUtils - provides a facade to all the boilerplate leveraging KT Extension Functions
 */
fun View.setTopCorners(roundedCorners: Int) {
    clipToOutline = true
    outlineProvider = object : ViewOutlineProvider() {
        override fun getOutline(view: View?, outline: Outline?) {
            view?.apply {
                outline?.setRoundRect(
                    0,
                    0,
                    width,
                    height + roundedCorners,
                    roundedCorners.toFloat()
                )
            }
        }
    }
}

fun pxToDp(px: Int, context: Context): Int {
    return (px / context.resources.displayMetrics.density).toInt()
}

fun ViewBinding.getString(res: Int, args: Double): String {
    return root.context.getString(res, args)
}

fun View.hide() {
    visibility = View.GONE
}

fun View.show() {
    visibility = View.VISIBLE
}

fun AppCompatTextView.applyStrikeThrough() {
    paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
}

fun AppCompatTextView.resetPaintFlags() {
    paintFlags = Paint.ANTI_ALIAS_FLAG
}

fun AppCompatTextView.setFontSizeTo(fontSize: Float) {
    setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize)
}

fun AppCompatTextView.setFontFamily(resFont: Int, textStyle: Int) {
    setTypeface(
        ResourcesCompat.getFont(
            this.context,
            resFont
        ), textStyle
    )
}

fun getBitmapFromDrawable(
    resources: Resources,
    resInt: Int,
    theme: Resources.Theme
) =
    ResourcesCompat.getDrawable(resources, resInt, theme)?.toBitmap()

fun AppCompatEditText.value() = this.text.toString()

fun String.toEditable() = Editable.Factory.getInstance().newEditable(this)