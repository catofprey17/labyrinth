package ru.c17.labyrinth.engine.entities

class PointCell(cell: Cell) : Cell(cell.posX, cell.posY) {

    var direction: Direction =
        Direction.UP
    var oldDirection: Direction =
        Direction.UP
    var oldPosX: Int = 0
    var oldPosY: Int = 0

    var animX = 0f
    var animY = 0f
    var animRotation = 0f
    var animScale = 1f;



    fun refreshOldPos() {
        oldPosX = posX
        oldPosY = posY
    }

    fun setNewPos(cell: Cell) {
        this.posX = cell.posX
        this.posY = cell.posY
    }

    fun needToRotate(): Boolean {
        return direction != oldDirection
    }

    fun needToMove(): Boolean {
        return (posX != oldPosX || posY != oldPosY)
    }

    fun getRotationParameters(): RotationParameters {
        if (oldDirection == Direction.UP && direction == Direction.LEFT) {
            return RotationParameters(360f, Direction.LEFT.float)
        }
        if (oldDirection == Direction.LEFT && direction == Direction.UP) {
            return RotationParameters(-90f, Direction.UP.float)
        }

        return RotationParameters(oldDirection.float, direction.float)
    }

    fun isPoint(other: Cell): Boolean = posX == other.posX && posY == other.posY

    enum class Direction(var float: Float) {
        UP(0f),
        DOWN(180f),
        LEFT(270f),
        RIGHT(90f)
    }

    inner class RotationParameters(val oldF: Float, val newF: Float) {

    }

}