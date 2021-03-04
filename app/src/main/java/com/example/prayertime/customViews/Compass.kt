package com.example.prayertime.customViews

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.example.prayertime.R

class Compass @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
): View(context, attrs, defStyle), ValueAnimator.AnimatorUpdateListener {

    private var valueAnimator = ValueAnimator.ofInt(1, 100)
    private val paint = Paint()
    private val dp = resources.displayMetrics.density
    private var bitmap: Bitmap? = null
    private var animatedValue = 100
    private var isAnimationStart = false

    init {
        bitmap = ContextCompat.getDrawable(
            context,
            R.drawable.ic_compass
        )?.toBitmap()
    }

    fun startAnimation() {
        valueAnimator.duration = 500
        valueAnimator.interpolator = AccelerateDecelerateInterpolator()
        valueAnimator.addUpdateListener(this)
        isAnimationStart = true
        valueAnimator.start()
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        drawCircle(canvas)
//        if (isAnimationStart)
//            drawCircleWithAnimation(canvas)
//        else
//            drawCircle(canvas)

    }

    private fun getBitmap(size: Int): Bitmap? {
        return if (bitmap != null) {
            Bitmap.createScaledBitmap(bitmap!!, size, size, true)
        } else {
            null
        }
    }

    private fun drawCircle(canvas: Canvas?) {
        getBitmap(100)?.let {
            canvas?.drawBitmap(
                it,
                (measuredWidth / 2 - 50f),
                (measuredHeight/2 - 370f),
                paint
            )
        }
    }

    override fun onAnimationUpdate(animator: ValueAnimator?) {
        animatedValue = animator?.animatedValue as Int
        invalidate()
    }

}