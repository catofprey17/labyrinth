package ru.c17.labyrinth.engine

import ru.c17.labyrinth.engine.entities.FieldCell
import ru.c17.labyrinth.engine.entities.PointCell
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.abs
import kotlin.random.Random


class Level
    (
    private val listener: LevelListener,
    size: Int,
    var startPosX: Int = -1,
    var startPosY: Int = -1,
    initDirection: PointCell.Direction = PointCell.Direction.UP
    ) {


    var surfaceArray: Array<Array<FieldCell>>
    var currentPoint: PointCell
    var endPoint: FieldCell
    var startPoint: FieldCell

    var random = Random(700)

    init {

        if (startPosX == -1)
            startPosX = random.nextInt(0, size - 1) * 2 + 1
        if (startPosY == -1) {
            startPosY = random.nextInt(0, size - 1) * 2 + 1
        }


        val len = size * 2 + 1
        surfaceArray = Array(len) {Array(len) {
            FieldCell(
                0,
                0
            )
        } }

        for (i in 0 until len) {
            for (j in 0 until len) {
                surfaceArray[i][j] =
                    FieldCell(i, j)
            }
        }

        var point = surfaceArray[startPosX][startPosY]
        point.isChecked = true

        startPoint = point
        currentPoint = PointCell(point)
        currentPoint.direction = initDirection

        val stack = Stack<FieldCell>()
        stack.push(point)

        while (stack.size != 0) {
            val neighbor =
                getRandomNeighbor(
                    surfaceArray,
                    point
                )
            if (neighbor == null) {
                point = stack.pop()
            } else {
                stack.push(neighbor)
                neighbor.isChecked = true
                surfaceArray[abs((point.posX + neighbor.posX) / 2)][abs((point.posY + neighbor.posY) / 2)].isWall = false
                point = neighbor
            }
        }

        endPoint = getEndPoint(surfaceArray)
    }

    private fun getEndPoint(array: Array<Array<FieldCell>>): FieldCell {

        val whitePoints = arrayListOf<FieldCell>()

        for(i in 1..array.size - 2 step 2) {
            for (j in 1..array[i].size - 2 step 2) {
                val cell = array[i][j]
                if (!cell.isWall &&
                        cell != startPoint &&
                        abs(cell.posX - startPoint.posX) > 3 &&
                        abs(cell.posY - startPoint.posY) > 3) {
                    whitePoints.add(cell)
                }
            }
        }

        return whitePoints[random.nextInt(0, whitePoints.size)]


    }

    private fun getRandomNeighbor(array: Array<Array<FieldCell>>, point: FieldCell): FieldCell? {
        val availableNeighbors = ArrayList<FieldCell>()

        // check neighbor above
        if (point.posY != 1 && !array[point.posX][point.posY-2].isChecked) {
            availableNeighbors.add(array[point.posX][point.posY-2])
        }

        //check right neighbor
        if (point.posX != array.size - 2 && !array[point.posX + 2][point.posY].isChecked) {
            availableNeighbors.add(array[point.posX + 2][point.posY])
        }

        //check neighbor below
        if (point.posY != array.size - 2 && !array[point.posX][point.posY+2].isChecked) {
            availableNeighbors.add(array[point.posX][point.posY+2])
        }

        //check left neighbor
        if (point.posX != 1 && !array[point.posX - 2][point.posY].isChecked) {
            availableNeighbors.add(array[point.posX - 2][point.posY])
        }

        return if (availableNeighbors.size == 0)
            null
        else
            availableNeighbors[random.nextInt(0,availableNeighbors.size)]
    }



    // TODO Optimize
    fun move(direction: PointCell.Direction) {

        var needToMove: Boolean
        currentPoint.refreshOldPos()

        currentPoint.oldDirection = currentPoint.direction
        currentPoint.direction = direction

        do {
            needToMove = false

            when(direction) {
                PointCell.Direction.UP -> {
                    if (!surfaceArray[currentPoint.posX][currentPoint.posY - 1].isWall) {
                        currentPoint.setNewPos(surfaceArray[currentPoint.posX][currentPoint.posY - 1])
                        needToMove = true
                    }
                }

                PointCell.Direction.DOWN -> {
                    if (!surfaceArray[currentPoint.posX][currentPoint.posY + 1].isWall) {
                        currentPoint.setNewPos(surfaceArray[currentPoint.posX][currentPoint.posY + 1])
                        needToMove = true
                    }
                }

                PointCell.Direction.RIGHT -> {
                    if (!surfaceArray[currentPoint.posX + 1][currentPoint.posY].isWall) {
                        currentPoint.setNewPos(surfaceArray[currentPoint.posX + 1][currentPoint.posY])
                        needToMove = true
                    }
                }

                PointCell.Direction.LEFT -> {
                    if (!surfaceArray[currentPoint.posX - 1][currentPoint.posY].isWall) {
                        currentPoint.setNewPos(surfaceArray[currentPoint.posX - 1][currentPoint.posY])
                        needToMove = true
                    }
                }
            }

        } while (needToMove && checkAvailableMove(direction) && !currentPoint.isPoint(endPoint))


        if (currentPoint.needToMove() || currentPoint.needToRotate()) {
            movePoint(currentPoint)
        } else {
            listener.stopMoving()
        }
    }

    interface LevelListener {
//        fun moveCurrentPoint(cell: PointCell)
        fun stopMoving()
        fun movePoint(oldPosX: Int, newPosX: Int, oldPosY: Int, newPosY: Int, direction: PointCell.Direction)
    }

    //TODO Optimize
    private fun checkAvailableMove(direction: PointCell.Direction): Boolean {
            if (!surfaceArray[currentPoint.posX][currentPoint.posY - 1].isWall && direction != PointCell.Direction.DOWN && direction != PointCell.Direction.UP) {
                return false
            }
            if (!surfaceArray[currentPoint.posX][currentPoint.posY + 1].isWall && direction != PointCell.Direction.DOWN && direction != PointCell.Direction.UP) {
                return false
            }
            if (!surfaceArray[currentPoint.posX + 1][currentPoint.posY].isWall && direction != PointCell.Direction.LEFT && direction != PointCell.Direction.RIGHT) {
                return false
            }
            if (!surfaceArray[currentPoint.posX - 1][currentPoint.posY].isWall && direction != PointCell.Direction.LEFT && direction != PointCell.Direction.RIGHT) {
                return false
            }

            return true
    }

    private fun movePoint(cell: PointCell) {
        listener.movePoint(cell.oldPosX, cell.posX, cell.oldPosY, cell.posY, cell.direction)
    }


}