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
import androidx.cardview.widget.CardView
import androidx.core.animation.doOnEnd
import com.example.android.miscreant.views.GameFragment
import kotlinx.android.synthetic.main.card.view.*


object ViewAnimator {

    fun triggerCounterAttack(view: CardView, trigger: Boolean) {
        if (trigger) {
            // render into off-screen buffer to avoid laggy animation
            view.setLayerType(View.LAYER_TYPE_HARDWARE, null)
            view.counter_attack_value.setTextColor(view.context.getColor(R.color.aggressiveRed))
            Log.i(this.javaClass.simpleName, "Counter fade_in_out animation TRIGGERED")

            val startMoveAnimator =
                AnimatorInflater.loadAnimator(view.context, R.animator.start_move_counterattack)
            val endMoveAnimator =
                AnimatorInflater.loadAnimator(view.context, R.animator.end_move_counterattack)
            val hitAnimator = AnimatorInflater.loadAnimator(view.context, R.animator.counterattack)

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

    fun triggerCounterAttackHit(view: CardView, trigger: Boolean) {
        if (trigger) {
            // set image resource for animation
            view.animation_image.setImageResource(R.drawable.claw)
            Log.i(this.javaClass.simpleName, "Claw animation image set")

            triggerAnimation(
                view,
                isMonsterClaw = true,
                animatorID = R.animator.delayed_fade_in_out_counterattack_hit
            )
        }
    }

    fun triggerClaw(view: CardView, trigger: Boolean) {
        if (trigger) {
            // set image resource for animation
            view.animation_image.setImageResource(R.drawable.claw)
            Log.i(this.javaClass.simpleName, "Claw animation image set")

            triggerAnimation(view, isMonsterClaw = true, animatorID = R.animator.fade_in_out)
        }
    }

    fun triggerHitAnimation(view: CardView, trigger: Boolean) {
        if (trigger) {
            // set image resource for animation
            view.animation_image.setImageResource(R.drawable.hit)
            Log.i(this.javaClass.simpleName, "Hit animation image set")

            triggerAnimation(view, animatorID = R.animator.fade_in_out)
        }
    }

    fun triggerHealing(view: ImageView, trigger: Boolean) {
        if (trigger) {
            view.setLayerType(View.LAYER_TYPE_HARDWARE, null)
            Log.i(this.javaClass.simpleName, "Healing animation TRIGGERED")

            val healingAnimator =
                AnimatorInflater.loadAnimator(view.context, R.animator.scale_twice)
            healingAnimator.setTarget(view)
            healingAnimator.doOnEnd {
                // reset to default layer
                view.setLayerType(View.LAYER_TYPE_NONE, null)
            }
            healingAnimator.start()
        }
    }

    private fun triggerAnimation(view: CardView, isMonsterClaw: Boolean = false, animatorID: Int) {
        view.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        Log.i(this.javaClass.simpleName, "Attack animation TRIGGERED")

        val attackAnimator = AnimatorInflater.loadAnimator(view.context, animatorID)

        attackAnimator.setTarget(view.animation_image)
        attackAnimator.doOnEnd {
            if (isMonsterClaw) {
                GameFragment.onClawEnd(view)
            } else GameFragment.onHitEnd(view)

            // reset to default layer
            view.setLayerType(View.LAYER_TYPE_NONE, null)
        }
        attackAnimator.start()
    }
}