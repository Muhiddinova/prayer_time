package com.example.prayertime.customViews


import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.animation.addListener
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
    private var bitmap1: Bitmap? = null
    private var bitmap2: Bitmap? = null
    private var animatedValue = 100
    private var listYPos = arrayListOf<Float>()

    init {
        bitmap1 = ContextCompat.getDrawable(
            context,
            R.drawable.tasbih
        )?.toBitmap((4 * dp).toInt(), (4 * dp).toInt(), Bitmap.Config.ARGB_8888)
        bitmap2 = ContextCompat.getDrawable(
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
            476 * dp
        )

    }

    fun startAnimation() {
        valueAnimator.duration = 2000
        valueAnimator.interpolator = AccelerateDecelerateInterpolator()
        valueAnimator.addUpdateListener(this)
        valueAnimator.addListener(object :Animator.AnimatorListener{
            var i= listYPos.size
            override fun onAnimationRepeat(animator: Animator) {}
            override fun onAnimationStart(animator: Animator) {
            }

            override fun onAnimationEnd(animator: Animator) {

                invalidate()
            }

            override fun onAnimationCancel(p0: Animator?) {
                TODO("Not yet implemented")
            }


        })
        valueAnimator.start()
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        drawCircle(canvas)

    }

    private fun drawCircle(canvas: Canvas?) {
        var isBigCircle = false
        var startHeight = -36 * dp
        var size = 28 * dp
        (1..17).forEach { i ->
            if (isBigCircle) {
                getBitmap(size.toInt())?.let {
                    canvas?.drawBitmap(
                        it,
                        measuredWidth / 2 - it.width / 2f,
                        listYPos[i - 1],
                        paint
                    )
                    if (i >= 10)
                        size -= 12 * dp
                    else
                        size += 12 * dp
//                    startHeight += (it.height + 4 * dp) * animatedValue / 100
                    isBigCircle = false
                }
            } else {
                bitmap1?.let {
                    canvas?.drawBitmap(it, measuredWidth / 2 - 2 * dp, listYPos[i - 1], paint)
//                    startHeight += 8 * dp * animatedValue / 100
                    isBigCircle = true
                }
            }
        }
    }

    private fun getBitmap(size: Int): Bitmap? {
        return ContextCompat.getDrawable(context, R.drawable.tasbih)?.toBitmap(
            size, size, Bitmap.Config.ARGB_8888
        ) 
    }

    override fun onAnimationUpdate(animator: ValueAnimator?) {

        animatedValue = animator?.getAnimatedValue() as Int
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