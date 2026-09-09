package com.example.kompa_app

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.LinearInterpolator

class AirplaneAnimationView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var viewWidth: Float = 0f
    private var viewHeight: Float = 0f

    private var airplane: Airplane? = null
    private var trajectory: Trajectory? = null

    companion object {
        private const val FLIGHT_DURATION_MS = 4000L
        private const val PAUSE_DURATION_MS = 300L
        private const val BACKGROUND_COLOR = "#F2F2F2"
    }

    var onFlightCompleted: (() -> Unit)? = null

    private var currentAngle: Float = -Math.PI.toFloat() / 2f
    private var currentRotationDegrees: Float = 0f

    private var flightAnimator: ValueAnimator? = null
    private var pauseAnimator: ValueAnimator? = null

    private var airplaneAlpha: Int = 255

    private val trailPath = Path()
    private var trailStarted = false
    private val trailPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
        color = Color.parseColor("#9D4EDD")
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        viewWidth = w.toFloat()
        viewHeight = h.toFloat()

        val airplaneSize = minOf(viewWidth, viewHeight) * 0.25f
        airplane = Airplane(airplaneSize)

        val trajectoryRadius = minOf(viewWidth, viewHeight) * 0.3f
        trajectory = Trajectory(
            centerX = viewWidth / 2f,
            centerY = viewHeight / 2f,
            radius = trajectoryRadius
        )

        trailPaint.strokeWidth = 4f * resources.displayMetrics.density

        if (flightAnimator == null) {
            startFlightAnimation()
        }
    }

    private fun startFlightAnimation() {
        val startAngle = -Math.PI.toFloat() / 2f
        val endAngle = startAngle + (2f * Math.PI.toFloat())

        trailPath.reset()
        trailStarted = false

        flightAnimator = ValueAnimator.ofFloat(startAngle, endAngle).apply {
            duration = FLIGHT_DURATION_MS
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener { animator ->
                val angle = animator.animatedValue as Float
                updateAirplanePosition(angle)
                updateTrail(angle)
                invalidate()
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    startPause()
                }
            })
            start()
        }
    }

    private fun startPause() {
        pauseAnimator = ValueAnimator.ofInt(255, 140, 255).apply {
            duration = PAUSE_DURATION_MS
            interpolator = LinearInterpolator()
            addUpdateListener { animator ->
                airplaneAlpha = animator.animatedValue as Int
                invalidate()
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    airplaneAlpha = 255
                    onFlightCompleted?.invoke()
                }
            })
            start()
        }
    }

    private fun updateAirplanePosition(angleRadians: Float) {
        currentAngle = angleRadians

        val traj = trajectory ?: return
        val tangentRadians = traj.tangentAngleAt(angleRadians)
        currentRotationDegrees = Math.toDegrees(tangentRadians.toDouble()).toFloat() + 90f
    }

    private fun updateTrail(angleRadians: Float) {
        val traj = trajectory ?: return
        val point = traj.pointAt(angleRadians)

        if (!trailStarted) {
            trailPath.moveTo(point.x, point.y)
            trailStarted = true
        } else {
            trailPath.lineTo(point.x, point.y)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(Color.parseColor(BACKGROUND_COLOR))

        val plane = airplane ?: return
        val traj = trajectory ?: return

        canvas.drawPath(trailPath, trailPaint)

        plane.bodyPaint.alpha = airplaneAlpha
        plane.wingsPaint.alpha = airplaneAlpha
        plane.tailPaint.alpha = airplaneAlpha

        val position = traj.pointAt(currentAngle)
        canvas.save()
        canvas.translate(position.x, position.y)
        canvas.rotate(currentRotationDegrees)
        canvas.drawPath(plane.wingsPath, plane.wingsPaint)
        canvas.drawPath(plane.tailPath, plane.tailPaint)
        canvas.drawPath(plane.bodyPath, plane.bodyPaint)
        canvas.restore()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        flightAnimator?.cancel()
        pauseAnimator?.cancel()
    }
}