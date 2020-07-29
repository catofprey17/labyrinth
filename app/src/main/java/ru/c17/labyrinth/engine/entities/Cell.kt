package ru.c17.labyrinth.engine.entities

import android.view.View

abstract class Cell(var posX: Int,
                    var posY: Int) {
    var viewId: Int = 0
}