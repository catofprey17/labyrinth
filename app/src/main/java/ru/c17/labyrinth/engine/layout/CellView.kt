package ru.c17.labyrinth.engine.layout

import android.content.Context
import android.util.AttributeSet
import android.view.View

class CellView: View {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    var posX: Int = 0
    var posY: Int = 0
}