package com.example.speechify.ui.player.lyrics.lyricsParser

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.os.Looper
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.ViewConfiguration
import android.view.animation.DecelerateInterpolator
import android.widget.OverScroller
import androidx.annotation.Nullable
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import com.example.speechify.R
import java.util.*


class LyricsView @JvmOverloads constructor(
    context: Context,
    @Nullable attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var mLrcData: MutableList<Lyrics>? = null
    private var mTextPaint: TextPaint? = null
    private var mDefaultContent: String? = null
    private var mCurrentLine = 0
    private var mOffset = 0f
    private var mLastMotionX = 0f
    private var mLastMotionY = 0f
    private var mScaledTouchSlop = 0
    private var mOverScroller: OverScroller? = null
    private var mVelocityTracker: VelocityTracker? = null
    private var mMaximumFlingVelocity = 0
    private var mMinimumFlingVelocity = 0
    private var mLrcTextSize = 0f
    private var mLrcLineSpaceHeight = 0f
    private var mTouchDelay = 0
    private var mNormalColor = 0
    private var mCurrentPlayLineColor = 0
    private var mNoLrcTextSize = 0f
    private var mNoLrcTextColor = 0

    private var isDragging = false

    private var isUserScroll = false
    private var isAutoAdjustPosition = true
    private var mPlayDrawable: Drawable? = null
    private var isShowTimeIndicator = false
    private lateinit var mPlayRect: Rect
    private lateinit var mIndicatorPaint: Paint
    private var mIndicatorLineWidth = 0f
    private var mIndicatorTextSize = 0f
    private var mCurrentIndicateLineTextColor = 0
    private var mIndicatorLineColor = 0
    private var mIndicatorMargin = 0f
    private var mIconLineGap = 0f
    private var mIconWidth = 0f
    private var mIconHeight = 0f
    private var isEnableShowIndicator = true
    private var mIndicatorTextColor = 0
    private var mIndicatorTouchDelay = 0
    private var isCurrentTextBold = false
    private var isLrcIndicatorTextBold = false
    private fun init(context: Context, attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.LrcView)
        mLrcTextSize =
            typedArray.getDimension(R.styleable.LrcView_lrcTextSize, sp2px(context, 15f).toFloat())
        mLrcLineSpaceHeight = typedArray.getDimension(
            R.styleable.LrcView_lrcLineSpaceSize,
            dp2px(context, 20f).toFloat()
        )
        mTouchDelay = typedArray.getInt(R.styleable.LrcView_lrcTouchDelay, 3500)
        mIndicatorTouchDelay = typedArray.getInt(R.styleable.LrcView_indicatorTouchDelay, 2500)
        mNormalColor = typedArray.getColor(R.styleable.LrcView_lrcNormalTextColor, Color.GRAY)
        mCurrentPlayLineColor =
            typedArray.getColor(R.styleable.LrcView_lrcCurrentTextColor, Color.WHITE)
        mNoLrcTextSize = typedArray.getDimension(
            R.styleable.LrcView_noLrcTextSize,
            dp2px(context, 20f).toFloat()
        )
        mNoLrcTextColor = typedArray.getColor(R.styleable.LrcView_noLrcTextColor, Color.BLACK)
        mIndicatorLineWidth = typedArray.getDimension(
            R.styleable.LrcView_indicatorLineHeight,
            dp2px(context, 0.5f).toFloat()
        )
        mIndicatorTextSize = typedArray.getDimension(
            R.styleable.LrcView_indicatorTextSize,
            sp2px(context, 13f).toFloat()
        )
        mIndicatorTextColor =
            typedArray.getColor(R.styleable.LrcView_indicatorTextColor, Color.GRAY)
        mCurrentIndicateLineTextColor =
            typedArray.getColor(R.styleable.LrcView_currentIndicateLrcColor, Color.GRAY)
        mIndicatorLineColor =
            typedArray.getColor(R.styleable.LrcView_indicatorLineColor, Color.GRAY)
        mIndicatorMargin = typedArray.getDimension(
            R.styleable.LrcView_indicatorStartEndMargin,
            dp2px(context, 5f).toFloat()
        )
        mIconLineGap =
            typedArray.getDimension(R.styleable.LrcView_iconLineGap, dp2px(context, 3f).toFloat())
        mIconWidth = typedArray.getDimension(
            R.styleable.LrcView_playIconWidth,
            dp2px(context, 20f).toFloat()
        )
        mIconHeight = typedArray.getDimension(
            R.styleable.LrcView_playIconHeight,
            dp2px(context, 20f).toFloat()
        )
        mPlayDrawable = typedArray.getDrawable(R.styleable.LrcView_playIcon)
        mPlayDrawable = if (mPlayDrawable == null) ContextCompat.getDrawable(
            context,
            R.drawable.play_icon
        ) else mPlayDrawable
        isCurrentTextBold = typedArray.getBoolean(R.styleable.LrcView_isLrcCurrentTextBold, false)
        isLrcIndicatorTextBold =
            typedArray.getBoolean(R.styleable.LrcView_isLrcIndicatorTextBold, false)
        typedArray.recycle()
        setupConfigs(context)
    }

    private fun setupConfigs(context: Context) {
        mScaledTouchSlop = ViewConfiguration.get(context).scaledTouchSlop
        mMaximumFlingVelocity = ViewConfiguration.get(context).scaledMaximumFlingVelocity
        mMinimumFlingVelocity = ViewConfiguration.get(context).scaledMinimumFlingVelocity
        mOverScroller = OverScroller(context, DecelerateInterpolator())
        mOverScroller!!.setFriction(0.1f)

        mTextPaint = TextPaint()
        mTextPaint!!.isAntiAlias = true
        mTextPaint!!.textAlign = Paint.Align.CENTER
        mTextPaint!!.textSize = mLrcTextSize
        mDefaultContent = DEFAULT_CONTENT
        mIndicatorPaint = Paint()
        mIndicatorPaint.isAntiAlias = true
        mIndicatorPaint.strokeWidth = mIndicatorLineWidth
        mIndicatorPaint.color = mIndicatorLineColor
        mPlayRect = Rect()
        mIndicatorPaint.textSize = mIndicatorTextSize
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (changed) {
            mPlayRect.left = mIndicatorMargin.toInt()
            mPlayRect.top = (height / 2 - mIconHeight / 2).toInt()
            mPlayRect.right = (mPlayRect.left + mIconWidth).toInt()
            mPlayRect.bottom = (mPlayRect.top + mIconHeight).toInt()
            mPlayDrawable!!.bounds = mPlayRect
        }
    }

    private val lrcWidth: Int
        get() = width - paddingLeft - paddingRight
    private val lrcHeight: Int
        get() = height
    private val isLrcEmpty: Boolean
         get() = mLrcData == null || lrcCount == 0
    private val lrcCount: Int
         get() = mLrcData!!.size

    fun setLrcData(lrcData: MutableList<Lyrics>?) {
        resetView(DEFAULT_CONTENT)
        mLrcData = lrcData
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (isLrcEmpty) {
            drawEmptyText(canvas)
            return
        }
        val indicatePosition = indicatePosition
        mTextPaint!!.textSize = mLrcTextSize
        mTextPaint!!.textAlign = Paint.Align.CENTER
        var y = lrcHeight / 2f
        val x = lrcWidth / 2f + paddingLeft
        for (i in 0 until lrcCount) {
            if (i > 0) {
                y += (getTextHeight(i - 1) + getTextHeight(i)) / 2f + mLrcLineSpaceHeight
            }
            if (mCurrentLine == i) {
                mTextPaint!!.color = mCurrentPlayLineColor
                mTextPaint!!.isFakeBoldText = isCurrentTextBold
            } else if (indicatePosition == i && isShowTimeIndicator) {
                mTextPaint!!.isFakeBoldText = isLrcIndicatorTextBold
                mTextPaint!!.color = mCurrentIndicateLineTextColor
            } else {
                mTextPaint!!.isFakeBoldText = false
                mTextPaint!!.color = mNormalColor
            }
            drawLrc(canvas, x, y, i)
        }
        if (isShowTimeIndicator) {
            mPlayDrawable!!.draw(canvas)
            val time = mLrcData!![indicatePosition].time
            val timeWidth = mIndicatorPaint.measureText(LyricsHelper.formatTime(time))
            mIndicatorPaint.color = mIndicatorLineColor
            canvas.drawLine(
                mPlayRect.right + mIconLineGap, height / 2f,
                width - timeWidth * 1.3f, height / 2f, mIndicatorPaint
            )
            val baseX = (width - timeWidth * 1.1f).toInt()
            val baseline =
                height / 2f - (mIndicatorPaint.descent() - mIndicatorPaint.ascent()) / 2 - mIndicatorPaint.ascent()
            mIndicatorPaint.color = mIndicatorTextColor
            canvas.drawText(LyricsHelper.formatTime(time), baseX.toFloat(), baseline, mIndicatorPaint)
        }
    }

    private val mLrcMap = HashMap<String?, StaticLayout>()
    private fun drawLrc(canvas: Canvas, x: Float, y: Float, i: Int) {
        val text = mLrcData!![i].text
        var staticLayout = mLrcMap[text]
        if (staticLayout == null) {
            mTextPaint!!.textSize = mLrcTextSize
            staticLayout = StaticLayout(
                text, mTextPaint, lrcWidth,
                Layout.Alignment.ALIGN_NORMAL, 1f, 0f, false
            )
            mLrcMap[text] = staticLayout
        }
        canvas.save()
        canvas.translate(x, y - staticLayout.height / 2f - mOffset)
        staticLayout.draw(canvas)
        canvas.restore()
    }

    private fun drawEmptyText(canvas: Canvas) {
        mTextPaint!!.textAlign = Paint.Align.CENTER
        mTextPaint!!.color = mNoLrcTextColor
        mTextPaint!!.textSize = mNoLrcTextSize
        canvas.save()
        val staticLayout = StaticLayout(
            mDefaultContent, mTextPaint,
            lrcWidth, Layout.Alignment.ALIGN_NORMAL, 1f, 0f, false
        )
        canvas.translate(lrcWidth / 2f + paddingLeft, lrcHeight / 2f)
        staticLayout.draw(canvas)
        canvas.restore()
    }

    fun updateTime(time: Long) {
        if (isLrcEmpty) {
            return
        }
        val linePosition = getUpdateTimeLinePosition(time)
        if (mCurrentLine != linePosition) {
            mCurrentLine = linePosition
            if (isUserScroll) {
                invalidateView()
                return
            }
            ViewCompat.postOnAnimation(this@LyricsView, mScrollRunnable)
        }
    }

    private fun getUpdateTimeLinePosition(time: Long): Int {
        var linePos = 0
        for (i in 0 until lrcCount) {
            val lrc = mLrcData!![i]
            if (time >= lrc.time) {
                if (i == lrcCount - 1) {
                    linePos = lrcCount - 1
                } else if (time < mLrcData!![i + 1].time) {
                    linePos = i
                    break
                }
            }
        }
        return linePos
    }

    private val mScrollRunnable = Runnable {
        isUserScroll = false
        scrollToPosition(mCurrentLine)
    }
    private val mHideIndicatorRunnable = Runnable {
        isShowTimeIndicator = false
        invalidateView()
    }

    private fun scrollToPosition(linePosition: Int) {
        val scrollY = getItemOffsetY(linePosition)
        val animator = ValueAnimator.ofFloat(mOffset, scrollY)
        animator.addUpdateListener { animation ->
            mOffset = animation.animatedValue as Float
            invalidateView()
        }
        animator.duration = 300
        animator.start()
    }


    private val indicatePosition: Int
        get() {
            var pos = 0
            var min = Float.MAX_VALUE
            for (i in mLrcData!!.indices) {
                val offsetY = getItemOffsetY(i)
                val abs = Math.abs(offsetY - mOffset)
                if (abs < min) {
                    min = abs
                    pos = i
                }
            }
            return pos
        }

    private fun getItemOffsetY(linePosition: Int): Float {
        var tempY = 0f
        for (i in 1..linePosition) {
            tempY += (getTextHeight(i - 1) + getTextHeight(i)) / 2 + mLrcLineSpaceHeight
        }
        return tempY
    }

    private val mStaticLayoutHashMap = HashMap<String?, StaticLayout>()
    private fun getTextHeight(linePosition: Int): Float {
        val text = mLrcData!![linePosition].text
        var staticLayout = mStaticLayoutHashMap[text]
        if (staticLayout == null) {
            mTextPaint!!.textSize = mLrcTextSize
            staticLayout = StaticLayout(
                text, mTextPaint,
                lrcWidth, Layout.Alignment.ALIGN_NORMAL, 1f, 0f, false
            )
            mStaticLayoutHashMap[text] = staticLayout
        }
        return staticLayout.height.toFloat()
    }

    private fun overScrolled(): Boolean {
        return mOffset > getItemOffsetY(lrcCount - 1) || mOffset < 0
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (isLrcEmpty) {
            return super.onTouchEvent(event)
        }
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain()
        }
        mVelocityTracker!!.addMovement(event)
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                removeCallbacks(mScrollRunnable)
                removeCallbacks(mHideIndicatorRunnable)
                if (!mOverScroller!!.isFinished) {
                    mOverScroller!!.abortAnimation()
                }
                mLastMotionX = event.x
                mLastMotionY = event.y
                isUserScroll = true
                isDragging = false
            }
            MotionEvent.ACTION_MOVE -> {
                var moveY = event.y - mLastMotionY
                if (Math.abs(moveY) > mScaledTouchSlop) {
                    isDragging = true
                    isShowTimeIndicator = isEnableShowIndicator
                }
                if (isDragging) {
                    val maxHeight = getItemOffsetY(lrcCount - 1)
                    if (mOffset < 0 || mOffset > maxHeight) {
                        moveY /= 3.5f
                    }
                    mOffset -= moveY
                    mLastMotionY = event.y
                    invalidateView()
                }
            }
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                if (!isDragging && (!isShowTimeIndicator || !onClickPlayButton(event))) {
                    isShowTimeIndicator = false
                    invalidateView()
                    performClick()
                }
                handleActionUp(event)
            }
        }
        return true
    }

    private fun handleActionUp(event: MotionEvent) {
        if (isEnableShowIndicator) {
            ViewCompat.postOnAnimationDelayed(
                this@LyricsView,
                mHideIndicatorRunnable,
                mIndicatorTouchDelay.toLong()
            )
        }
        if (isShowTimeIndicator && onClickPlayButton(event)) {
            isShowTimeIndicator = false
            invalidateView()
            if (mOnPlayIndicatorLineListener != null) {
                mOnPlayIndicatorLineListener!!.onPlay(
                    mLrcData!![indicatePosition].time,
                    mLrcData!![indicatePosition].text
                )
            }
        }
        if (overScrolled() && mOffset < 0) {
            scrollToPosition(0)
            if (isAutoAdjustPosition) {
                ViewCompat.postOnAnimationDelayed(this@LyricsView, mScrollRunnable,
                    mTouchDelay.toLong()
                )
            }
            return
        }
        if (overScrolled() && mOffset > getItemOffsetY(lrcCount - 1)) {
            scrollToPosition(lrcCount - 1)
            if (isAutoAdjustPosition) {
                ViewCompat.postOnAnimationDelayed(this@LyricsView, mScrollRunnable,
                    mTouchDelay.toLong()
                )
            }
            return
        }
        mVelocityTracker!!.computeCurrentVelocity(1000, mMaximumFlingVelocity.toFloat())
        val yVelocity = mVelocityTracker!!.yVelocity
        val absYVelocity = Math.abs(yVelocity)
        if (absYVelocity > mMinimumFlingVelocity) {
            mOverScroller!!.fling(
                0, mOffset.toInt(), 0, (-yVelocity).toInt(), 0,
                0, 0, getItemOffsetY(lrcCount - 1).toInt(),
                0, getTextHeight(0).toInt()
            )
            invalidateView()
        }
        releaseVelocityTracker()
        if (isAutoAdjustPosition) {
            ViewCompat.postOnAnimationDelayed(this@LyricsView, mScrollRunnable, mTouchDelay.toLong())
        }
    }

    private fun onClickPlayButton(event: MotionEvent): Boolean {
        val left = mPlayRect.left.toFloat()
        val right = mPlayRect.right.toFloat()
        val top = mPlayRect.top.toFloat()
        val bottom = mPlayRect.bottom.toFloat()
        val x = event.x
        val y = event.y
        return mLastMotionX > left && mLastMotionX < right && mLastMotionY > top && mLastMotionY < bottom && x > left && x < right && y > top && y < bottom
    }

    override fun computeScroll() {
        super.computeScroll()
        if (mOverScroller!!.computeScrollOffset()) {
            mOffset = mOverScroller!!.currY.toFloat()
            invalidateView()
        }
    }

    private fun releaseVelocityTracker() {
        if (null != mVelocityTracker) {
            mVelocityTracker!!.clear()
            mVelocityTracker!!.recycle()
            mVelocityTracker = null
        }
    }

    fun resetView(defaultContent: String?) {
        if (mLrcData != null) {
            mLrcData!!.clear()
        }
        mLrcMap.clear()
        mStaticLayoutHashMap.clear()
        mCurrentLine = 0
        mOffset = 0f
        isUserScroll = false
        isDragging = false
        mDefaultContent = defaultContent
        removeCallbacks(mScrollRunnable)
        invalidate()
    }

    override fun performClick(): Boolean {
        return super.performClick()
    }

    fun dp2px(context: Context, dpVal: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dpVal, context.resources.displayMetrics
        ).toInt()
    }

    fun sp2px(context: Context, spVal: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            spVal, context.resources.displayMetrics
        ).toInt()
    }

    private fun invalidateView() {
        if (Looper.getMainLooper().thread === Thread.currentThread()) {
            invalidate()
        } else {
            postInvalidate()
        }
    }

    private var mOnPlayIndicatorLineListener: OnPlayIndicatorLineListener? = null
    fun setOnPlayIndicatorLineListener(onPlayIndicatorLineListener: OnPlayIndicatorLineListener?) {
        mOnPlayIndicatorLineListener = onPlayIndicatorLineListener
    }

    interface OnPlayIndicatorLineListener {
        fun onPlay(time: Long, content: String?)
    }

    companion object {
        private const val DEFAULT_CONTENT = "Empty"
    }

    init {
        init(context, attrs)
    }
}