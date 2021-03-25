package com.example.prayertime.customViews


import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.example.prayertime.R


class Tasbih @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) :
    View(context, attrs, defStyle), ValueAnimator.AnimatorUpdateListener {


//    val bouncer = AnimatorSet()
//    bouncer.play(bounceAnim).before(squashAnim1)
//    bouncer.play(squashAnim1).before(squashAnim2)
//    val fadeAnim = ObjectAnimator.ofFloat(newBall, “alpha”, 1f, 0f)
//    fadeAnim.duration = 250
//    val animatorSet = AnimatorSet()
//    animatorSet.play(bouncer).before(fadeAnim)
//    animatorSet.start()


    private var valueAnimator = ValueAnimator.ofInt(1, 100)
    private val paint = Paint()
    private val dp = resources.displayMetrics.density
    private var bitmap: Bitmap? = null
    private var animatedValue = 100
    private var listYPos = arrayListOf<Float>()
    private var listSizes = arrayListOf<Int>()
    private var isAnimationStart = false

    init {
        bitmap = ContextCompat.getDrawable(
            context,
            R.drawable.tasbih
        )?.toBitmap()

        listYPos = arrayListOf(
            -36 * dp,
            -28 * dp,
            4 * dp,
            12 * dp,
            56 * dp,
            64 * dp,
            120 * dp,
            128 * dp,
            196 * dp,
            204 * dp,
            284 * dp,
            292 * dp,
            360 * dp,
            368 * dp,
            424 * dp,
            432 * dp,
            476 * dp,
            484 * dp,
            516 * dp
        )

        listSizes = arrayListOf(
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

    fun startAnimation() {
        valueAnimator.duration = 500
        valueAnimator.interpolator = AccelerateDecelerateInterpolator()
        valueAnimator.addUpdateListener(this)
        isAnimationStart = true
        valueAnimator.start()
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        if (isAnimationStart)
            drawCircleWithAnimation(canvas)
        else
            drawCircle(canvas)

    }

    private fun drawCircleWithAnimation(canvas: Canvas?) {

        (1..17).forEach { i ->
            val y = (listYPos[i + 1] - listYPos[i - 1]) * animatedValue / 100 + listYPos[i - 1]
            Log.d("Tasbih", "drawCircle: $y")
            if (i % 2 == 0) {
                val size =
                    (listSizes[i + 1] - listSizes[i - 1]) * animatedValue / 100 + listSizes[i - 1]
                when (i) {
                    16 -> {
                        paint.alpha = 255 - 255 * animatedValue / 100
                    }
                    2 -> {
                        paint.alpha = 255 * animatedValue / 100
                    }
                    else -> paint.alpha = 255
                }

                getBitmap(size)?.let {
                    canvas?.drawBitmap(
                        it,
                        measuredWidth / 2 - it.width / 2f,
                        y,
                        paint
                    )
                }

            } else {
                if (i == 17)
                    paint.alpha = 255 - 255 * animatedValue / 100
                else
                    paint.alpha = 255
                getBitmap(listSizes[i - 1])?.let {
                    canvas?.drawBitmap(
                        it,
                        measuredWidth / 2 - it.width / 2f,
                        y,
                        paint
                    )
                }
            }
        }
        if (animatedValue == 100) {
            isAnimationStart = false
        }
    }

    private fun drawCircle(canvas: Canvas?) {
        (1..17).forEach { i ->
            getBitmap(listSizes[i - 1])?.let {
                canvas?.drawBitmap(
                    it,
                    measuredWidth / 2 - it.width / 2f,
                    listYPos[i - 1],
                    paint
                )
            }
        }

    }

    private fun getBitmap(size: Int): Bitmap? {
        return if (bitmap != null) {
            Bitmap.createScaledBitmap(bitmap!!, size, size, true)
        } else {
            null
        }
    }

    override fun onAnimationUpdate(animator: ValueAnimator?) {

        animatedValue = animator?.animatedValue as Int
        invalidate()
    }

//    override fun onTouchEvent(event: MotionEvent?): Boolean {
//        val pointerIndex = event?.actionIndex
//        when (event?.actionMasked) {
//            MotionEvent.ACTION_DOWN,
//                MotionEvent.ACTION_POINTER_DOWN-> return true
//            MotionEvent.ACTION_UP,
//                MotionEvent.ACTION_POINTER_UP-> return true
//        }
//    }


//    fun a(z: Boolean, z2: Boolean) {
//        var h :Float
//        val f2 = if (z2) 0.0f else if (z) i else -i
//        val abs = Math.abs((f2 - h) * 250.0f / i) as Int
//        if (valueAnimator != null && valueAnimator.isRunning()) {
//            valueAnimator.end()
//        }
//        valueAnimator = ValueAnimator.ofFloat(bitmap1, bitmap2)
//        valueAnimator.setInterpolator(DecelerateInterpolator())
//        valueAnimator.setDuration(abs)
//        valueAnimator.addUpdateListener { valueAnimator ->
//            h = valueAnimator.getAnimatedValue() as Float
//            invalidate()
//        }
//        valueAnimator.addListener(object : AnimatorListener {
//            override fun onAnimationRepeat(animator: Animator) {}
//            override fun onAnimationStart(animator: Animator) {
//                if (!z2) {
//                    listener.onBead(z)
//                }
//            }
//
//            override fun onAnimationEnd(animator: Animator) {
//                h = 0.0f
//                invalidate()
//            }
//
//            override fun onAnimationCancel(animator: Animator) {
//                h = 0.0f
//                invalidate()
//            }
//        })
//        this.valueAnimator.start()
//    }

}