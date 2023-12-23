package com.rb.crafty.utils

import android.graphics.Path
import android.graphics.RectF

class Shapes() {
    //Available shapes.
    val RECT_SHAPE = "Rectangle"
    val CIRCLE_SHAPE = "Circle"
    val TRIANGLE_SHAPE = "Triangle"

    //Shape paths.
    fun circleShape(width: Float, height: Float, strokeWidth: Int): Path {
        val path = Path()
        path.addCircle(width/2, height/2, (width / 2).coerceAtMost(height / 2) - strokeWidth, Path.Direction.CW)
        return path
    }

    fun rectShape(width: Float, height: Float, strokeWidth: Int): Path {
        val path = Path()
        val rectF = RectF(0f, 0f, width, height)
        path.addRoundRect(rectF, floatArrayOf(100f, 100f, 100f, 100f, 100f, 100f, 100f, 100f), Path.Direction.CW)
        return path
    }
}