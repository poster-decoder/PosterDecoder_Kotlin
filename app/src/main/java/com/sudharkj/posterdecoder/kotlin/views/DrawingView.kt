package com.sudharkj.posterdecoder.kotlin.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.ImageView
import kotlin.math.max
import kotlin.math.min

class DrawingView : ImageView {
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    companion object {
        val paint = Paint().also {
            it.setARGB(255, 0, 0, 0)
            it.strokeWidth = 5f
            it.style = Paint.Style.STROKE
        }
        var start = Pair(0, 0)
        var end = Pair(0, 0)
        var rect = Rect(0, 0, 0, 0)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawRect(rect, paint)
    }

    fun getCropRect(): Rect {
        return rect
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val newLocation = event?.let { Pair(event.x.toInt(), event.y.toInt()) }!!

        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> { drawRect(newLocation, newLocation) }
            MotionEvent.ACTION_MOVE -> { drawRect(start, newLocation) }
            MotionEvent.ACTION_UP -> { drawRect(start, newLocation) }
        }

        return true
    }

    private fun drawRect(s: Pair<Int, Int>, e: Pair<Int, Int>) {
        start = s
        end = e
        if (s != e) {
            rect = Rect(
                min(start.first, end.first), min(start.second, end.second),
                max(start.first, end.first), max(start.second, end.second)
            )
            invalidate()
        }
    }
}