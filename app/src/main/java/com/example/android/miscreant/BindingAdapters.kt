/*
 * Copyright (c) 2019. Birgit Schoenauer
 *
 * Licensed under the Creative Commons Attribution-NonCommercial-NonDerivatives 4.0 International License.
 * See file 'LICENSE.md' or https://creativecommons.org/licenses/by-nc-nd/4.0/ for full license details.
 */

package com.example.android.miscreant

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.databinding.BindingAdapter
import com.example.android.miscreant.views.GameFragment

// workaround to be able to set listeners on included layouts in fragment
@BindingAdapter("setListeners")
fun setListeners(view: View, dummyParameter: String?) {
    GameFragment.setListeners(view)
}

@BindingAdapter("android:src")
fun setImageViewDrawable(view: ImageView, imageString: String?) {
    if (imageString != null && imageString.isNotEmpty()){
        val id : Int? = view.resources.getIdentifier(imageString, "drawable", view.context.packageName)
        view.setImageResource(id ?: android.R.color.transparent)
    }
    else view.setImageResource(android.R.color.transparent)
}

@BindingAdapter("toggleMaxHealth")
fun setMaxHealthView(view: TextView, isVisible: Boolean) {
    view.visibility = if (isVisible) View.VISIBLE else View.INVISIBLE
}

@BindingAdapter("togglePotMaxHealth")
fun setPotMaxView(view: TextView, isVisible: Boolean) {
    view.visibility = if (isVisible) View.VISIBLE else View.INVISIBLE
}
