package com.rb.crafty.utils

import android.view.animation.*

class AnimUtils {
    companion object{

        fun pressAnim(listener: Animation.AnimationListener?): Animation? {
            val press: Animation = ScaleAnimation(1f, 0.8f, 1f, 0.8f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
            press.duration = 150
            press.repeatCount = 1
            press.repeatMode = Animation.REVERSE
            press.interpolator = AccelerateInterpolator()
            if (listener != null) {
                press.setAnimationListener(listener)
            }
            return press
        }


        fun blinkAnim(listener: Animation.AnimationListener?): Animation? {
            val blink: Animation = AlphaAnimation(1f, 0f)
            blink.duration = 150
            blink.repeatCount = 1
            blink.repeatMode = Animation.REVERSE
            if (listener != null) {
                blink.setAnimationListener(listener)
            }
            return blink
        }

        fun rotateAnim(from: Float, to: Float): Animation {
            val anim = RotateAnimation(from, to, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
            anim.duration = 150

            return anim
        }

    }

}