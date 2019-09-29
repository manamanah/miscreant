/*
 * Copyright (c) 2019. Birgit Schoenauer
 *
 * Licensed under the Creative Commons Attribution-NonCommercial-NonDerivatives 4.0 International License.
 * See file 'LICENSE.md' or https://creativecommons.org/licenses/by-nc-nd/4.0/ for full license details.
 */

package com.example.android.miscreant

import android.animation.AnimatorInflater
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.animation.doOnEnd
import androidx.databinding.BindingAdapter
import com.example.android.miscreant.views.GameFragment
import kotlinx.android.synthetic.main.card.view.*


// workaround to be able to set listeners on included layouts in fragment
@Suppress("UNUSED_PARAMETER")
@BindingAdapter("setListeners")
fun setListeners(view: View, dummyParameter: String?) {
    GameFragment.setListeners(view)
}

@BindingAdapter("src")
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

@BindingAdapter("startAnimation")
fun startCounterAttackAnimation(view: CardView, startAnimation: Boolean) {
    if (startAnimation){
        // render into off-screen buffer to avoid laggy animation
        view.setLayerType(View.LAYER_TYPE_HARDWARE, null)

        view.counter_attack_value.setTextColor(view.context.getColor(R.color.aggressiveRed))

        val startMoveAnimator = AnimatorInflater.loadAnimator(view.context, R.animator.start_move_counterattack)
        val endMoveAnimator = AnimatorInflater.loadAnimator(view.context, R.animator.end_move_counterattack)
        val hitAnimator = AnimatorInflater.loadAnimator(view.context, R.animator.hit_counterattack)

        hitAnimator.setTarget(view)
        hitAnimator.doOnEnd {
            endMoveAnimator.start()
        }

        startMoveAnimator.setTarget(view)
        startMoveAnimator.doOnEnd {
            hitAnimator.start()
        }

        endMoveAnimator.setTarget(view)
        endMoveAnimator.doOnEnd {
            view.counter_attack_value.setTextColor(view.context.getColor(R.color.cardHighlight))
            GameFragment.onCounterAttackAnimationEnd(view)

            // reset to default layer
            view.setLayerType(View.LAYER_TYPE_NONE, null)
        }

        startMoveAnimator.start()
    }
}

@BindingAdapter("startClawAnimation")
fun startClawAnimation(view: CardView, start: Boolean){
    if (start){
        view.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        Log.i("BindingAdapter", "CLAW ANIMATION")

        val attackAnimator = AnimatorInflater.loadAnimator(view.context, R.animator.attack)
        attackAnimator.setTarget(view.claw_image)
        attackAnimator.doOnEnd {
            GameFragment.onClawEnd(view)

            // reset to default layer
            view.setLayerType(View.LAYER_TYPE_NONE, null)
        }
        attackAnimator.start()
    }
}
