package ru.c17.labyrinth.engine.entities

import ru.c17.labyrinth.engine.entities.Cell

class FieldCell(posX: Int, posY: Int) : Cell(posX, posY) {

    var isWall: Boolean = posX % 2 == 0 || posY % 2 == 0
    var isChecked = false
}