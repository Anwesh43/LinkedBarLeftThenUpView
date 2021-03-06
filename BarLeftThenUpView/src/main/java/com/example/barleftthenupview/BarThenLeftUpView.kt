package com.example.barleftthenupview

import android.view.View
import android.view.MotionEvent
import android.app.Activity
import android.content.Context
import android.graphics.Paint
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.RectF

val parts : Int = 2
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
    val sf2 : Float = sf.divideScale(0, parts)
    val sf4 : Float = sf.divideScale(1, parts)
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
                barW * sf2.divideScale(j * 2, 3),
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
            barW + (w - 2 * barW) * sf2.divideScale(1, 3),
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

    private val renderer : Renderer = Renderer(this)

    override fun onDraw(canvas : Canvas) {
        renderer.render(canvas)
    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap()
            }
        }
        return true
    }

    data class State(var scale : Float = 0f, var dir : Float = 0f, var prevScale : Float = 0f) {

        fun update(cb : (Float) -> Unit) {
            scale += scGap * dir
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                cb(prevScale)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            if (dir == 0f) {
                dir = 1f - 2 * prevScale
                cb()
            }
        }
    }

    data class Animator(var view : View, var animated : Boolean = false) {

        fun animate(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(delay)
                    view.invalidate()
                } catch(ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    data class BTLUNode(var i : Int, val state : State = State()) {

        private var next : BTLUNode? = null
        private var prev : BTLUNode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < colors.size - 1) {
                next = BTLUNode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawBTLUNode(i, state.scale, paint)
        }

        fun update(cb : (Float) -> Unit) {
            state.update(cb)
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : BTLUNode {
            var curr : BTLUNode? = prev
            if (dir == 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }
    }

    data class BarThenLeftUp(var i : Int) {

        private var curr : BTLUNode = BTLUNode(0)
        private var dir : Int = 1

        fun draw(canvas : Canvas, paint : Paint) {
            curr.draw(canvas, paint)
        }

        fun update(cb : (Float) -> Unit) {
            curr.update {
                curr = curr.getNext(dir) {
                    dir *= -1
                }
                cb(it)
            }
        }

        fun startUdpating(cb : () -> Unit) {
            curr.startUpdating(cb)
        }
    }

    data class Renderer(var view : BarThenLeftUpView) {

        private val animator : Animator = Animator(view)
        private val bltu : BarThenLeftUp = BarThenLeftUp(0)
        private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

        fun render(canvas : Canvas) {
            canvas.drawColor(backColor)
            bltu.draw(canvas, paint)
            animator.animate {
                bltu.update {
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            bltu.startUdpating {
                animator.start()
            }
        }
    }

    companion object {

        fun create(activity : Activity) : BarThenLeftUpView {
            val view : BarThenLeftUpView = BarThenLeftUpView(activity)
            activity.setContentView(view)
            return view
        }
    }
}