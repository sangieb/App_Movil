package com.example.kompa_app.ui.widget.animacion

import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path

class Airplane(private val size: Float) {

    val bodyPath = Path()
    val wingsPath = Path()
    val tailPath = Path()

    val bodyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.parseColor("#7B2CBF")
    }
    val wingsPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.parseColor("#9D4EDD")
    }
    val tailPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.parseColor("#7B2CBF")
    }

    init {
        buildBody()
        buildWings()
        buildTail()
    }

    private fun buildBody() {
        val halfWidth = size * 0.08f
        val noseY = -size * 0.5f
        val tailY = size * 0.35f

        bodyPath.apply {
            reset()
            moveTo(0f, noseY)
            quadTo(halfWidth * 1.3f, noseY * 0.5f, halfWidth, tailY * 0.3f)
            lineTo(halfWidth * 0.6f, tailY)
            lineTo(-halfWidth * 0.6f, tailY)
            lineTo(-halfWidth, tailY * 0.3f)
            quadTo(-halfWidth * 1.3f, noseY * 0.5f, 0f, noseY)
            close()
        }
    }

    private fun buildWings() {
        val wingSpan = size * 0.45f
        val wingY = size * 0.02f
        val wingTipY = size * 0.22f

        wingsPath.apply {
            reset()
            moveTo(size * 0.06f, wingY)
            lineTo(wingSpan, wingTipY)
            quadTo(wingSpan * 0.85f, wingTipY + size * 0.05f, size * 0.08f, wingY + size * 0.12f)
            close()

            moveTo(-size * 0.06f, wingY)
            lineTo(-wingSpan, wingTipY)
            quadTo(-wingSpan * 0.85f, wingTipY + size * 0.05f, -size * 0.08f, wingY + size * 0.12f)
            close()
        }
    }

    private fun buildTail() {
        val tailSpan = size * 0.2f
        val tailY = size * 0.25f
        val tailTipY = size * 0.4f

        tailPath.apply {
            reset()
            moveTo(size * 0.05f, tailY)
            lineTo(tailSpan, tailTipY)
            lineTo(size * 0.06f, tailY + size * 0.08f)
            close()

            moveTo(-size * 0.05f, tailY)
            lineTo(-tailSpan, tailTipY)
            lineTo(-size * 0.06f, tailY + size * 0.08f)
            close()
        }
    }
}