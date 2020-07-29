package ru.c17.labyrinth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

//TODO Add saveInstanceState to GameLayout

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.activity_main)

    }
}