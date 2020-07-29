package ru.c17.labyrinth.engine

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.content.SharedPreferences
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import ru.c17.labyrinth.engine.entities.PointCell
import ru.c17.labyrinth.engine.layout.GameLayout
import ru.c17.labyrinth.engine.layout.PointView

// TODO Fix arrow moves sync
class GameAdapter(context: Context) : Level.LevelListener {

    private val LEVEL_STEP = 3
    private val ROTATION_DURATION = 100L

    lateinit var field: ConstraintLayout
    lateinit var gameLayout: GameLayout
    private lateinit var level: Level
    lateinit var levelName: TextView
    lateinit var currentPointView: PointView
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("main", Context.MODE_PRIVATE)
    var currentLevel: Int = sharedPreferences.getInt("level", 1)



    fun launchGame() {
        level =
            Level(
                this,
                generateLevelSize(),
                sharedPreferences.getInt("lastEndX", -1),
                sharedPreferences.getInt("lastEndY", -1)
            )
        field.post {
            gameLayout.drawLevel(level, currentLevel, true)
        }
    }

    override fun stopMoving() {
        gameLayout.attachListener()
    }

    private fun levelDone() {
        currentLevel++
        sharedPreferences.edit()
            .putInt("level", currentLevel)
            .putInt("lastEndX", level.endPoint.posX)
            .putInt("lastEndY", level.endPoint.posY)
            .apply()
        level = Level(
            this,
            generateLevelSize(),
            level.endPoint.posX,
            level.endPoint.posY,
            level.currentPoint.direction
        )

        gameLayout.changeLevel(level, currentLevel)

    }

    fun move(direction: PointCell.Direction) {
        level.move(direction)
    }

    override fun movePoint(
        oldPosX: Int,
        newPosX: Int,
        oldPosY: Int,
        newPosY: Int,
        direction: PointCell.Direction
    ) {
        gameLayout.movePoint(
            oldPosX,
            newPosX,
            oldPosY,
            newPosY,
            direction.float,
            object: AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    if (level.currentPoint.isPoint(level.endPoint))
                        levelDone()
                    else
                        stopMoving()

                }
            }
        )
    }

//    private fun generateLevelSize(): Int = 30
    private fun generateLevelSize(): Int = currentLevel.div(LEVEL_STEP) + 5
}