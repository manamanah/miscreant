/*
 * Copyright (c) 2019. Birgit Schoenauer
 *
 * Licensed under the Creative Commons Attribution-NonCommercial-NonDerivatives 4.0 International License.
 * See file 'LICENSE.md' or https://creativecommons.org/licenses/by-nc-nd/4.0/ for full license details.
 */

package com.example.android.miscreant

import android.graphics.Color
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter

@BindingAdapter("backgroundColor")
fun setBGColor(view: CardView, setBG: Boolean){
    if (setBG) view.setBackgroundColor(view.resources.getIdentifier("cardHighlight", "color", view.context.packageName))
    else view.setBackgroundColor(Color.BLACK)
}

@BindingAdapter("android:src")
fun setImageViewDrawable(view: ImageView, imageString: String?) {
    if (imageString != null){
        val id : Int? = view.resources.getIdentifier(imageString, "drawable", view.context.packageName)
        view.setImageResource(id ?: android.R.color.transparent)
    }
    else view.setImageResource(android.R.color.transparent)
}

@BindingAdapter("toggleValue")
fun setValView(view: TextView, isVisible: Boolean) {
    view.visibility = if (isVisible) View.VISIBLE else View.INVISIBLE
}

@BindingAdapter("togglePotVal")
fun setPotValView(view: TextView, isVisible: Boolean) {
    view.visibility = if (isVisible) View.VISIBLE else View.INVISIBLE
}

@BindingAdapter("toggleAttackView")
fun setAttackView(view: TextView, isVisible: Boolean) {
    view.visibility = if (isVisible) View.VISIBLE else View.INVISIBLE
}

@BindingAdapter("toggleRIP")
fun setRIPView(view: TextView, isVisible: Boolean) {
    view.visibility = if (isVisible) View.VISIBLE else View.INVISIBLE
}

@BindingAdapter("toggleCurrentHealth")
fun setCurrentHealthView(view: TextView, isVisible: Boolean) {
    view.visibility = if (isVisible) View.VISIBLE else View.INVISIBLE
}

@BindingAdapter("togglePotCurrentHealth")
fun setPotCurrentHealthView(view: TextView, isVisible: Boolean) {
    view.visibility = if (isVisible) View.VISIBLE else View.INVISIBLE
}

@BindingAdapter("toggleMaxHealth")
fun setMaxHealthView(view: TextView, isVisible: Boolean) {
    view.visibility = if (isVisible) View.VISIBLE else View.INVISIBLE
}

@BindingAdapter("togglePotMaxHealth")
fun setPotMaxView(view: TextView, isVisible: Boolean) {
    view.visibility = if (isVisible) View.VISIBLE else View.INVISIBLE
}
