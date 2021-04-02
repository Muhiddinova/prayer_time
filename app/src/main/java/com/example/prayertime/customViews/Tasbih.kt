package com.example.prayertime.customViews


import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.example.prayertime.R


@RequiresApi(Build.VERSION_CODES.HONEYCOMB)
class Tasbih @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attrs, defStyle), ValueAnimator.AnimatorUpdateListener {

    private var mValueAnimator = ValueAnimator.ofInt(1, 100)
    private val sPaint = Paint()
    private val dp = resources.displayMetrics.density
    private var mBitmap: Bitmap? = null
    private var mAnimatedValue = 100
    private var mListYPos = arrayListOf<Float>()
    private var mListSizes = arrayListOf<Int>()
    private var mIsAnimationStart = false


    init {
        mBitmap = ContextCompat.getDrawable(
            context,
            R.drawable.tasbih
        )?.toBitmap()
        mListYPos = arrayListOf(
            0 * dp,
            8 * dp,
            40 * dp,
            48 * dp,
            92 * dp,
            100 * dp,
            156 * dp,
            164 * dp,
            232 * dp,
            240 * dp,
            320 * dp,
            328 * dp,
            396 * dp,
            404 * dp,
            460 * dp,
            468 * dp,
            512 * dp,
            520 * dp,
            552 * dp


//            -36 * dp, //4+4
//            -28 * dp, //4+28  +12
//            4 * dp, //4+4
//            12 * dp, //4+40
//            56 * dp,
//            64 * dp,
//            120 * dp,
//            128 * dp,
//            196 * dp,
//            204 * dp,
//            284 * dp,
//            292 * dp,
//            360 * dp,
//            368 * dp,
//            424 * dp,
//            432 * dp,
//            476 * dp,
//            484 * dp,
//            516 * dp
        )
        mListSizes = arrayListOf(
            (4 * dp).toInt(),
            (28 * dp).toInt(),
            (4 * dp).toInt(),
            (40 * dp).toInt(),
            (4 * dp).toInt(),
            (52 * dp).toInt(),
            (4 * dp).toInt(),
            (64 * dp).toInt(),
            (4 * dp).toInt(),
            (76 * dp).toInt(),
            (4 * dp).toInt(),
            (64 * dp).toInt(),
            (4 * dp).toInt(),
            (52 * dp).toInt(),
            (4 * dp).toInt(),
            (40 * dp).toInt(),
            (4 * dp).toInt(),
            (28 * dp).toInt(),
            (4 * dp).toInt(),
        )

    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (mIsAnimationStart) drawCircleWithAnimation(canvas)
        else drawCircle(canvas)

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


    private fun drawCircleWithAnimation(canvas: Canvas?) {
        (1..17).forEach { i ->
            val y = (mListYPos[i + 1] - mListYPos[i - 1]) * mAnimatedValue / 100 + mListYPos[i - 1]
            Log.d("Tasbih", "drawCircle: $y")
            if (i % 2 == 0) {
                val size =
                    (mListSizes[i + 1] - mListSizes[i - 1]) * mAnimatedValue / 100 + mListSizes[i - 1]
                when (i) {
                    16 -> {
                        sPaint.alpha = 255 - 255 * mAnimatedValue / 100
                    }
                    2 -> {
                        sPaint.alpha = 255 * mAnimatedValue / 100
                    }
                    else -> sPaint.alpha = 255
                }

                getBitmap(size)?.let {
                    canvas?.drawBitmap(
                        it,
                        measuredWidth / 2 - it.width / 2f,
                        y,
                        sPaint
                    )
                }

            } else {
                if (i == 17)
                    sPaint.alpha = 255 - 255 * mAnimatedValue / 100
                else
                    sPaint.alpha = 255
                getBitmap(mListSizes[i - 1])?.let {
                    canvas?.drawBitmap(
                        it,
                        measuredWidth / 2 - it.width / 2f,
                        y,
                        sPaint
                    )
                }
            }
        }
        if (mAnimatedValue == 100) {
            mIsAnimationStart = false
        }
    }


    private fun drawCircle(canvas: Canvas?) {
        (1..17).forEach { i ->
            getBitmap(mListSizes[i - 1])?.let {
                canvas?.drawBitmap(
                    it,
                    measuredWidth / 2 - it.width / 2f,
                    mListYPos[i - 1],
                    sPaint
                )
            }
        }

    }


    private fun getBitmap(size: Int): Bitmap? {
        return if (mBitmap != null) {
            Bitmap.createScaledBitmap(mBitmap!!, size, size, true)
        } else {
            null
        }
    }


//    -36 * dp,
//    -28 * dp,
//    4 * dp,
//    12 * dp,
//    56 * dp,
//    64 * dp,
//    120 * dp,
//    128 * dp,
//    196 * dp,
//    204 * dp,
//    284 * dp,
//    292 * dp,
//    360 * dp,
//    368 * dp,
//    424 * dp,
//    432 * dp,
//    476 * dp,
//    484 * dp,
//    516 * dp


}