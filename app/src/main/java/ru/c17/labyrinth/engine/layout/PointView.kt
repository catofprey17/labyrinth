package ru.c17.labyrinth.engine.layout

import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.View

const val ROTATION_DURATION = 50L
const val MOVEMENT_DURATION = 50L

class PointView : androidx.appcompat.widget.AppCompatImageView {



    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

//    var fieldLayoutX = 0f
//    var fieldLayoutY = 0f
//
//    var fieldPosX = 0
//    var fieldPosY = 0
//
//    var posX = 0
//    var posY = 0
//
//    var animPosX = 0f
//    var animPosY = 0f


    var animRotation = 0f


    fun moveTo(newX: Float, newY: Float, steps: Int, angle: Float, listener: AnimatorListenerAdapter = object: AnimatorListenerAdapter(){}) {
        val animatorSet = AnimatorSet()


        val animDuration = steps * MOVEMENT_DURATION

        val xAnim = ObjectAnimator.ofFloat(this, View.TRANSLATION_X, x, newX).apply {
            duration = animDuration
        }

        val yAnim = ObjectAnimator.ofFloat(this, View.TRANSLATION_Y, y, newY).apply {
            duration = animDuration
        }

        val fromAngle: Float
        val toAngle: Float

        if (animRotation == 270f && angle == 0f) {
            fromAngle = -90f
            toAngle = 0f
        } else if (animRotation == 0f && angle == 270f) {
            fromAngle = 360f
            toAngle = 270f
        } else {
            fromAngle = animRotation
            toAngle = angle
        }

        val rotationAnim = ObjectAnimator.ofFloat(this, "rotation", fromAngle, toAngle).apply {
            duration = if (angle == animRotation) 0 else ROTATION_DURATION
        }
        animRotation = toAngle

        animatorSet.addListener(listener)
        animatorSet.playTogether(xAnim, yAnim, rotationAnim)
        animatorSet.start()
    }


}