package ru.c17.labyrinth.engine.layout

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.animation.doOnEnd
import ru.c17.labyrinth.R

class PyramidLayout: ConstraintLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private val rectList: ArrayList<View> = arrayListOf()

    private var playerPoint = ImageView(context)

    init {
        post {
            createPyramid()
            rectList[5].post {
                runAnimation()
            }
        }
    }

    fun setOnPlayerPointRunnable(runnable: Runnable) {
        playerPoint.setOnClickListener {
            runnable.run()
        }
    }


    private fun createPyramid() {
        val factor = resources.displayMetrics.density
        var rectWidth:Int = (width - 150 * 2 * factor).toInt()
        val rectStep = rectWidth / 10
        val bottomMargin = rectStep * 2


        val constraintSet = ConstraintSet()
        constraintSet.clone(this)

        // Rect 6
        var rect = View(context)
        rect.id = generateViewId()
        rect.setBackgroundResource(resources.getIdentifier("ru.c17.labyrinth:drawable/startscreen_rect_" + "6", null, null))

        addView(rect)
        rectList.add(rect)

        constraintSet.setVisibility(rect.id, ConstraintSet.INVISIBLE)
        constraintSet.constrainWidth(rect.id, rectWidth)
        constraintSet.constrainHeight(rect.id, rectWidth)

        constraintSet.connect(rect.id,ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
        constraintSet.connect(rect.id,ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
        constraintSet.connect(rect.id,ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
        constraintSet.connect(rect.id,ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)

        constraintSet.applyTo(this)

        // Rect 5 - 1
        for (i in 5 downTo 1) {
            rectWidth -= rectStep
            rect = View(context)
            rect.id = generateViewId()
            addView(rect)
            rect.setBackgroundResource(resources.getIdentifier("ru.c17.labyrinth:drawable/startscreen_rect_$i", null, null))
            rectList.add(rect)

            constraintSet.setVisibility(rect.id, ConstraintSet.INVISIBLE)
            constraintSet.constrainWidth(rect.id, rectWidth)
            constraintSet.constrainHeight(rect.id, rectWidth)

            constraintSet.connect(rect.id,ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
            constraintSet.connect(rect.id,ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
            constraintSet.connect(rect.id,ConstraintSet.BOTTOM, rectList[rectList.size - 2].id, ConstraintSet.BOTTOM, bottomMargin)

            constraintSet.applyTo(this)
        }

        playerPoint.id = generateViewId()
        playerPoint.setImageResource(R.drawable.ic_player)
        addView(playerPoint)

        constraintSet.setVisibility(playerPoint.id, ConstraintSet.INVISIBLE)
        constraintSet.constrainWidth(playerPoint.id, rectWidth)
        constraintSet.constrainHeight(playerPoint.id, rectWidth)

        constraintSet.connect(playerPoint.id,ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
        constraintSet.connect(playerPoint.id,ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
        constraintSet.connect(playerPoint.id,ConstraintSet.BOTTOM, rectList[rectList.size - 1].id, ConstraintSet.BOTTOM, bottomMargin + 20)

        constraintSet.applyTo(this)




    }

        private fun runAnimation() {
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(
            ObjectAnimator.ofFloat(rectList[0], "rotationX", 65f),
            ObjectAnimator.ofFloat(rectList[1], "rotationX", 65f),
            ObjectAnimator.ofFloat(rectList[2], "rotationX", 65f),
            ObjectAnimator.ofFloat(rectList[3], "rotationX", 65f),
            ObjectAnimator.ofFloat(rectList[4], "rotationX", 65f),
            ObjectAnimator.ofFloat(rectList[5], "rotationX", 65f),

            ObjectAnimator.ofFloat(rectList[0], "rotation", 40f),
            ObjectAnimator.ofFloat(rectList[1], "rotation", 40f),
            ObjectAnimator.ofFloat(rectList[2], "rotation", 40f),
            ObjectAnimator.ofFloat(rectList[3], "rotation", 40f),
            ObjectAnimator.ofFloat(rectList[4], "rotation", 40f),
            ObjectAnimator.ofFloat(rectList[5], "rotation", 40f)
        )
        animatorSet.duration = 0
        animatorSet.doOnEnd {
            showRects()
        }
        animatorSet.start()
    }

    private fun showRects() {
        playerPoint.visibility = View.VISIBLE
        rectList[0].visibility = View.VISIBLE
        rectList[1].visibility = View.VISIBLE
        rectList[2].visibility = View.VISIBLE
        rectList[3].visibility = View.VISIBLE
        rectList[4].visibility = View.VISIBLE
        rectList[5].visibility = View.VISIBLE

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(
            ObjectAnimator.ofFloat(playerPoint, "alpha", 0f, 1f),
            ObjectAnimator.ofFloat(rectList[0], "alpha", 0f, 1f),
            ObjectAnimator.ofFloat(rectList[1], "alpha", 0f, 1f),
            ObjectAnimator.ofFloat(rectList[2], "alpha", 0f, 1f),
            ObjectAnimator.ofFloat(rectList[3], "alpha", 0f, 1f),
            ObjectAnimator.ofFloat(rectList[4], "alpha", 0f, 1f),
            ObjectAnimator.ofFloat(rectList[5], "alpha", 0f, 1f)
        )
        animatorSet.duration = 2000
        animatorSet.doOnEnd {
            runRegularAnimation()
        }
        animatorSet.start()
    }

    private fun runRegularAnimation() {
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(
            ObjectAnimator.ofFloat(rectList[0], "rotation", 40f, 50f).apply {
                repeatMode = ObjectAnimator.REVERSE
                repeatCount = ObjectAnimator.INFINITE
            },
            ObjectAnimator.ofFloat(rectList[1], "rotation", 40f, 50f).apply {
                repeatMode = ObjectAnimator.REVERSE
                repeatCount = ObjectAnimator.INFINITE
            },
            ObjectAnimator.ofFloat(rectList[2], "rotation", 40f, 50f).apply {
                repeatMode = ObjectAnimator.REVERSE
                repeatCount = ObjectAnimator.INFINITE
            },
            ObjectAnimator.ofFloat(rectList[3], "rotation", 40f, 50f).apply {
                repeatMode = ObjectAnimator.REVERSE
                repeatCount = ObjectAnimator.INFINITE
            },
            ObjectAnimator.ofFloat(rectList[4], "rotation", 40f, 50f).apply {
                repeatMode = ObjectAnimator.REVERSE
                repeatCount = ObjectAnimator.INFINITE
            },
            ObjectAnimator.ofFloat(rectList[5], "rotation", 40f, 50f).apply {
                repeatMode = ObjectAnimator.REVERSE
                repeatCount = ObjectAnimator.INFINITE
            }
        )
        animatorSet.duration = 5000
        animatorSet.start()
    }
}