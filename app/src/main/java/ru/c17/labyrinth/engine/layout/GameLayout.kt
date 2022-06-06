package ru.c17.labyrinth.engine.layout

import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.animation.addListener
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import ru.c17.labyrinth.R
import ru.c17.labyrinth.engine.GameAdapter
import ru.c17.labyrinth.engine.Level
import ru.c17.labyrinth.engine.entities.FieldCell
import ru.c17.labyrinth.engine.entities.PointCell
import kotlin.math.abs

class GameLayout: ConstraintLayout {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(
        context,
        attrs
    )
    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr)

    init {
        isSaveEnabled = true
    }

    private lateinit var fieldLayout: ConstraintLayout
    private lateinit var adapter: GameAdapter
    private var currentPointView: PointView? = null
    private lateinit var pointFieldLayout: ConstraintLayout
    private lateinit var levelName: TextView
    private lateinit var cellViews: Array<Array<CellView>>

    fun setAdapter(adapter: GameAdapter) {
        this.adapter = adapter
        adapter.gameLayout = this
    }

    fun attachListener() {
        setOnTouchListener(object: OnSwipeTouchListener(context) {
            override fun onSwipeRight() {
                super.onSwipeRight()
                detachListener()
                adapter.move(PointCell.Direction.RIGHT)
            }

            override fun onSwipeLeft() {
                super.onSwipeLeft()
                detachListener()
                adapter.move(PointCell.Direction.LEFT)
            }

            override fun onSwipeTop() {
                super.onSwipeTop()
                detachListener()
                adapter.move(PointCell.Direction.UP)

            }

            override fun onSwipeBottom() {
                super.onSwipeBottom()
                detachListener()
                adapter.move(PointCell.Direction.DOWN)

            }
        })
    }

    private fun detachListener() {
        setOnTouchListener(null)
    }

    fun runGame() {
        drawGameField()
        drawLevelTextView()

        attachListener()

        adapter.field = fieldLayout
        adapter.levelName = levelName
        adapter.launchGame()
    }

    fun drawLevel(level: Level, currentLevel: Int, isFirstLaunch: Boolean) {
        val cellSize = fieldLayout.width / level.surfaceArray.size
        drawLabyrinth(cellSize, level.surfaceArray)
        if (currentPointView == null) {
            drawCurrentPointView(cellSize, level)
        } else {
            updateCurrentPointView(cellSize, level)
        }
        levelName.text = currentLevel.toString()
        fieldLayout.findViewById<View>(level.endPoint.viewId).setBackgroundColor(Color.BLUE)

        if (isFirstLaunch)
            initAnimation()
    }

    private fun drawGameField() {
        val constraintSet = ConstraintSet()
        constraintSet.clone(this)
        fieldLayout = ConstraintLayout(context)
        fieldLayout.id = generateViewId()
        addView(fieldLayout)

        constraintSet.setVisibility(fieldLayout.id, ConstraintSet.INVISIBLE)

        constraintSet.constrainWidth(fieldLayout.id, ConstraintSet.MATCH_CONSTRAINT)
        constraintSet.constrainHeight(fieldLayout.id, ConstraintSet.MATCH_CONSTRAINT)
        constraintSet.connect(fieldLayout.id, ConstraintSet.TOP, id, ConstraintSet.TOP,0)
        constraintSet.connect(fieldLayout.id, ConstraintSet.BOTTOM, id, ConstraintSet.BOTTOM,0)
        constraintSet.connect(fieldLayout.id, ConstraintSet.START, id, ConstraintSet.START,
            (resources.displayMetrics.density * 16).toInt()
        )
        constraintSet.connect(fieldLayout.id, ConstraintSet.END, id, ConstraintSet.END,
            (resources.displayMetrics.density * 16).toInt()
        )
        constraintSet.setDimensionRatio(fieldLayout.id, "H,1:1")
        constraintSet.applyTo(this)
    }

    private fun drawLevelTextView() {
        val constraintSet = ConstraintSet()
        constraintSet.clone(this)
        levelName = TextView(context)
        levelName.id = generateViewId()
        levelName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 32f)
        addView(levelName)
        constraintSet.constrainWidth(levelName.id, ConstraintSet.WRAP_CONTENT)
        constraintSet.constrainHeight(levelName.id, ConstraintSet.WRAP_CONTENT)
        constraintSet.connect(levelName.id, ConstraintSet.TOP, id, ConstraintSet.TOP)
        constraintSet.connect(levelName.id, ConstraintSet.BOTTOM, fieldLayout.id, ConstraintSet.TOP)
        constraintSet.connect(levelName.id, ConstraintSet.START, id, ConstraintSet.START)
        constraintSet.connect(levelName.id, ConstraintSet.END, id, ConstraintSet.END)
        constraintSet.applyTo(this)
    }

    private fun drawLabyrinth(cellSize: Int, surfaceArray: Array<Array<FieldCell>>) {
        var cell: CellView
        cellViews = Array(surfaceArray.size) { Array(surfaceArray.size) { CellView(context) } }
        val constraintSet = ConstraintSet()
        constraintSet.clone(fieldLayout)

        for (i in surfaceArray.indices) {
            for (j in surfaceArray.indices) {
                cell = cellViews[i][j]
                cell.id = View.generateViewId()
                cell.posX = i
                cell.posY = j
                surfaceArray[i][j].viewId = cell.id
                cell.setBackgroundColor(if (surfaceArray[i][j].isWall) Color.BLACK else Color.TRANSPARENT)

                fieldLayout.addView(cell)

                constraintSet.constrainWidth(cell.id, cellSize)
                constraintSet.constrainHeight(cell.id, cellSize)

                if (i>0)
                    constraintSet.connect(cell.id, ConstraintSet.START, surfaceArray[i-1][0].viewId, ConstraintSet.END)
                else
                    constraintSet.connect(cell.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)

                if (j>0)
                    constraintSet.connect(cell.id, ConstraintSet.TOP, surfaceArray[0][j-1].viewId, ConstraintSet.BOTTOM)
                else
                    constraintSet.connect(cell.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)

                constraintSet.applyTo(fieldLayout)
            }
        }
    }

    // TODO Optimize
    private fun drawCurrentPointView(cellSize: Int, level: Level) {

        val constraintSet = ConstraintSet()
        constraintSet.clone(this)

        // Draw layout for point
        pointFieldLayout = ConstraintLayout(context)
        pointFieldLayout.id = generateViewId()
        addView(pointFieldLayout)
        constraintSet.constrainWidth(pointFieldLayout.id, ConstraintSet.MATCH_CONSTRAINT)
        constraintSet.constrainHeight(pointFieldLayout.id, ConstraintSet.MATCH_CONSTRAINT)
        constraintSet.connect(pointFieldLayout.id, ConstraintSet.TOP, fieldLayout.id, ConstraintSet.TOP)
        constraintSet.connect(pointFieldLayout.id, ConstraintSet.BOTTOM, fieldLayout.id, ConstraintSet.BOTTOM)
        constraintSet.connect(pointFieldLayout.id, ConstraintSet.START, fieldLayout.id, ConstraintSet.START)
        constraintSet.connect(pointFieldLayout.id, ConstraintSet.END, fieldLayout.id, ConstraintSet.END)
        constraintSet.applyTo(this)


        // Draw point
        constraintSet.clone(pointFieldLayout)
//        val oldX = currentPointView?.x
//        val oldY = currentPointView?.y
//        val oldWidth = currentPointView?.width
//        val oldHeight = currentPointView?.height

        if (currentPointView != null) {
            this.removeView(currentPointView)
        }

        currentPointView = PointView(fieldLayout.context)
        currentPointView!!.id = ImageView.generateViewId()
        level.currentPoint.viewId = currentPointView!!.id
        currentPointView!!.setImageDrawable(ContextCompat.getDrawable(
            fieldLayout.context,
            R.drawable.ic_player
        ))

        pointFieldLayout.addView(currentPointView)

        constraintSet.constrainWidth(currentPointView!!.id, cellSize)
        constraintSet.constrainHeight(currentPointView!!.id, cellSize)

        constraintSet.connect(currentPointView!!.id, ConstraintSet.START, pointFieldLayout.id, ConstraintSet.START)
        constraintSet.connect(currentPointView!!.id, ConstraintSet.TOP, pointFieldLayout.id, ConstraintSet.TOP)

        constraintSet.applyTo(pointFieldLayout)

        ObjectAnimator.ofFloat(currentPointView!!, View.TRANSLATION_X, (level.currentPoint.posX * cellSize).toFloat()).apply {
            duration = 0
            start()
        }

        ObjectAnimator.ofFloat(currentPointView!!, View.TRANSLATION_Y, (level.currentPoint.posY * cellSize).toFloat()).apply {
            duration = 0
            start()
        }

        ObjectAnimator.ofFloat(currentPointView!!, View.ROTATION, level.currentPoint.direction.float).apply {
            duration = 0
            start()
        }


        adapter.currentPointView = currentPointView as PointView
    }

    // TODO Fix scale pivot
    private fun updateCurrentPointView(cellSize: Int, level: Level) {
        val set = AnimatorSet()

        set.playTogether(
            ObjectAnimator.ofFloat(currentPointView!!, View.TRANSLATION_X, (level.currentPoint.posX * cellSize).toFloat()).apply {
                duration = 500
            },

            ObjectAnimator.ofFloat(currentPointView!!, View.TRANSLATION_Y, (level.currentPoint.posY * cellSize).toFloat()).apply {
                duration = 500
            },

            ObjectAnimator.ofFloat(currentPointView!!, View.SCALE_X, 1f,cellSize.toFloat() / currentPointView!!.width.toFloat()).apply {
                duration = 500
            },

            ObjectAnimator.ofFloat(currentPointView!!, View.SCALE_Y, 1f, cellSize.toFloat() / currentPointView!!.width.toFloat()).apply {
                duration = 500
            }
        )
        set.addListener(onEnd = {
            pointFieldLayout.removeAllViews()
            drawCurrentPointView(cellSize, level)
        })
        set.start()
    }

    fun movePoint(oldPosX: Int, newPosX: Int, oldPosY: Int, newPosY: Int, rotation: Float, listener: AnimatorListenerAdapter) {

        currentPointView!!.moveTo(
            cellViews[newPosX][newPosY].x,
            cellViews[newPosX][newPosY].y,
            abs(newPosX - oldPosX) + abs(newPosY - oldPosY),
            rotation,
            listener
        )
    }

    // TODO Fix name
    fun changeLevel(level: Level, currentLevel:Int) {
        val exitSet = AnimatorSet()
        fieldLayout.pivotX = 0.5f * fieldLayout.width
        fieldLayout.pivotY = 0.5f * fieldLayout.height

        levelName.pivotX = 0.5f * levelName.width
        levelName.pivotY = 2f * levelName.height
        exitSet.playTogether(
            ObjectAnimator.ofFloat(fieldLayout, View.ALPHA, 0f).apply {
                duration = 500
            },

            ObjectAnimator.ofFloat(fieldLayout, View.SCALE_X, 2f).apply {
                duration = 500
            },

            ObjectAnimator.ofFloat(fieldLayout, View.SCALE_Y, 2f).apply {
                duration = 500
            },

            ObjectAnimator.ofFloat(levelName, View.ALPHA, 0f).apply {
                duration = 500
            },

            ObjectAnimator.ofFloat(levelName, View.SCALE_X, 2f).apply {
                duration = 500
            },

            ObjectAnimator.ofFloat(levelName, View.SCALE_Y, 2f).apply {
                duration = 500
            }
        )

        exitSet.addListener(onEnd = {
            fieldLayout.visibility = View.INVISIBLE
            levelName.visibility = View.INVISIBLE
            fieldLayout.removeAllViews()
            fieldLayout.post {

                drawLevel(level, currentLevel, false)

                fieldLayout.visibility = View.VISIBLE
                levelName.visibility = View.VISIBLE
                val enterSet = AnimatorSet()
                enterSet.playTogether(
                    ObjectAnimator.ofFloat(fieldLayout, View.ALPHA, 0f, 1f).apply {
                        duration = 500
                    },

                    ObjectAnimator.ofFloat(fieldLayout, View.SCALE_X, 0.5f,1f).apply {
                        duration = 500
                    },

                    ObjectAnimator.ofFloat(fieldLayout, View.SCALE_Y, 0.5f,1f).apply {
                        duration = 500
                    },


                    ObjectAnimator.ofFloat(levelName, View.ALPHA, 0f, 1f).apply {
                        duration = 500
                    },

                    ObjectAnimator.ofFloat(levelName, View.SCALE_X, 0.5f,1f).apply {
                        duration = 500
                    },

                    ObjectAnimator.ofFloat(levelName, View.SCALE_Y, 0.5f,1f).apply {
                        duration = 500
                    }
                )
                enterSet.doOnEnd {
                    attachListener()
                }
                enterSet.start()
            }
        })
        exitSet.start()


    }

    private fun initAnimation() {
        val initSet = AnimatorSet()

        val constraintSet = ConstraintSet()
        constraintSet.clone(this)
        constraintSet.setVisibility(fieldLayout.id, ConstraintSet.VISIBLE)
        constraintSet.applyTo(this)

        fieldLayout.pivotX = 0.5f * fieldLayout.width
        fieldLayout.pivotY = 0.5f * fieldLayout.height

        initSet.playTogether(
            ObjectAnimator.ofFloat(fieldLayout, View.ALPHA, 0f, 1f),
            ObjectAnimator.ofFloat(fieldLayout, View.SCALE_X, 0.5f,1f),
            ObjectAnimator.ofFloat(fieldLayout, View.SCALE_Y, 0.5f,1f),

            ObjectAnimator.ofFloat(levelName, View.ALPHA, 0f, 1f),
            ObjectAnimator.ofFloat(levelName, View.SCALE_X, 0.5f,1f),
            ObjectAnimator.ofFloat(levelName, View.SCALE_Y, 0.5f,1f)
        )

        initSet.duration = 500

        initSet.doOnEnd {
            ObjectAnimator.ofFloat(currentPointView, View.ALPHA, 0f,1f).apply {
                duration = 200
                doOnEnd {
                    attachListener()
                }
            }
        }
        initSet.start()
    }



}