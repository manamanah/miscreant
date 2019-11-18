/*
 * Copyright (c) 2019. Birgit Schoenauer
 *
 * Licensed under the Creative Commons Attribution-NonCommercial-NonDerivatives 4.0 International License.
 * See file 'LICENSE.md' or https://creativecommons.org/licenses/by-nc-nd/4.0/ for full license details.
 */

package com.example.android.miscreant

import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.databinding.BindingAdapter


@BindingAdapter("src")
fun setImageViewDrawable(view: ImageView, imageString: String?) {
    if (imageString != null && imageString.isNotEmpty()) {
        val id: Int? =
            view.resources.getIdentifier(imageString, "drawable", view.context.packageName)
        view.setImageResource(id ?: android.R.color.transparent)
    } else view.setImageResource(android.R.color.transparent)
}

@BindingAdapter("triggerCounterAttack")
fun triggerCounterAttack(view: CardView, startAnimation: Boolean) {
    ViewAnimator.triggerCounterAttack(view, startAnimation)
}

@BindingAdapter("triggerCounterHit")
fun triggerCounterHitAnimation(view: CardView, start: Boolean) {
    ViewAnimator.triggerCounterAttackHit(view, start)
}

@BindingAdapter("triggerClaw")
fun triggerClawAnimation(view: CardView, start: Boolean) {
    ViewAnimator.triggerClaw(view, start)
}

@BindingAdapter("triggerHit")
fun triggerHitAnimation(view: CardView, start: Boolean) {
    ViewAnimator.triggerHitAnimation(view, start)
}


