package com.example.kompa_app

import android.graphics.PointF
import kotlin.math.cos
import kotlin.math.sin

class Trajectory(
    val centerX: Float,
    val centerY: Float,
    val radius: Float
) {
    fun pointAt(angleRadians: Float): PointF {
        val x = centerX + radius * cos(angleRadians)
        val y = centerY + radius * sin(angleRadians)
        return PointF(x, y)
    }

    fun tangentAngleAt(angleRadians: Float): Float {
        return angleRadians + Math.PI.toFloat() / 2f
    }
}