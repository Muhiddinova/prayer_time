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
) : View(context, attrs, defStyle), ValueAnimator.AnimatorUpdateListener {

    private var mValueAnimator = ValueAnimator.ofInt(1, 100)
    private val sPaint = Paint()
    private val dp = resources.displayMetrics.density
    private var mBitmap: Bitmap? = null
    private var mAnimatedValue = 100
    private var mIsAnimationStart = false


    init {
        mBitmap = ContextCompat.getDrawable(
            context,
            R.drawable.ic_compass
        )?.toBitmap()
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        drawCircle(canvas)
    }


    override fun onAnimationUpdate(animator: ValueAnimator?) {
        mAnimatedValue = animator?.animatedValue as Int
        invalidate()
    }


    fun startAnimation() {
        mValueAnimator.duration = 500
        mValueAnimator.interpolator = AccelerateDecelerateInterpolator()
        mValueAnimator.addUpdateListener(this)
        mIsAnimationStart = true
        mValueAnimator.start()
    }


    private fun getBitmap(size: Int): Bitmap? {
        return if (mBitmap != null) {
            Bitmap.createScaledBitmap(mBitmap!!, size, size, true)
        } else null
    }


    private fun drawCircle(canvas: Canvas?) {
        getBitmap(100)?.let {
            canvas?.drawBitmap(
                it,
                (measuredWidth / 2 - 50f),
                (measuredHeight / 2 - 370f),
                sPaint
            )
        }
    }


}