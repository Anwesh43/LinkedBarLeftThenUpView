package com.example.barleftthenupview

import android.view.View
import android.view.MotionEvent
import android.app.Activity
import android.content.Context
import android.graphics.Paint
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.RectF

val parts : Int = 4
val scGap : Float = 0.02f / parts
val barWFactor : Float = 9.8f
val barHFactor : Float = 11.2f
val delay : Long = 20
val colors : Array<Int> = arrayOf(
    "#f44336",
    "#4CAF50",
    "#1A237E",
    "#006064",
    "#2962FF"
).map {
    Color.parseColor(it)
}.toTypedArray()
val backColor : Int = Color.parseColor("#BDBDBD")
