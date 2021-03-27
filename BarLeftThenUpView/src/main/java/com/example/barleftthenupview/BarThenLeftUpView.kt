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

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n
fun Float.sinify() : Float = Math.sin(this * Math.PI).toFloat()

fun Canvas.drawBarThenLeftUp(scale : Float, w : Float, h : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    val sf : Float = scale.sinify()
    val sf1 : Float = sf.divideScale(0, parts)
    val sf2 : Float = sf.divideScale(1, parts)
    val sf3 : Float = sf.divideScale(2, parts)
    val sf4 : Float = sf.divideScale(3, parts)
    val barH : Float = Math.min(w, h) / barHFactor
    val barW : Float = Math.min(w, h) / barWFactor
    save()
    translate(0f, h)
    for (j in 0..1) {
        save()
        translate((w - barW) * j, 0f)
        drawRect(
            RectF(
                0f,
                -(barH) - (h - barH) * sf4,
                barW * sf.divideScale(j * 2, parts),
                0f
            ),
            paint
        )
        restore()
    }
    drawRect(
        RectF(
            barW,
            -barH,
            barW + (w - 2 * barW) * sf3,
            0f
        ),
        paint
    )
    restore()
}

fun Canvas.drawBTLUNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    paint.color = colors[i]
    drawBarThenLeftUp(scale, w, h, paint)
}

class BarThenLeftUpView(ctx : Context) : View(ctx) {

    override fun onDraw(canvas : Canvas) {

    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

            }
        }
        return true
    }
}