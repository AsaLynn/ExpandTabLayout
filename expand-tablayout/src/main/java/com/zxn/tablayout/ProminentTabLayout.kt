package com.zxn.tablayout

import android.animation.TypeEvaluator
import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.zxn.tablayout.listener.CustomTabEntity
import com.zxn.tablayout.listener.OnTabSelectListener

/**
 *  Created by zxn on 2021/5/18.
 **/
class ProminentTabLayout : FrameLayout, AnimatorUpdateListener {

    private val mCurrentP: IndicatorPoint = IndicatorPoint()
    private val mLastP: IndicatorPoint = IndicatorPoint()

    constructor(context: Context) : this(context, null, 0)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        //重写onDraw方法,需要调用这个方法来清除flag
        setWillNotDraw(false)
        clipChildren = false
        clipToPadding = false
        attrs?.let {
            obtainAttributes(context, it)
            //get layout_height
            val height =
                it.getAttributeValue("http://schemas.android.com/apk/res/android", "layout_height")
            if (height != ViewGroup.LayoutParams.MATCH_PARENT.toString() + "") {
                val systemAttrs = intArrayOf(android.R.attr.layout_height)
                val a = context.obtainStyledAttributes(attrs, systemAttrs)
                mHeight = a.getDimensionPixelSize(0, ViewGroup.LayoutParams.WRAP_CONTENT)
                a.recycle()
            }
        }

        mValueAnimator = ValueAnimator.ofObject(PointEvaluator(), mLastP, mCurrentP).also {
            it.addUpdateListener(this)
        }
        mTabsContainer.also {
            addView(it)
            //it.background = tabContainerBackground
        }
    }

    private val mTabsContainer: LinearLayout by lazy {
        LinearLayout(context)
    }

    /**
     * indicator
     */
    private var mIndicatorColor = 0
    private var mIndicatorHeight = 0f
    private var mIndicatorWidth = 0f
    private var mIndicatorCornerRadius = 0f
    private var mIndicatorAnimEnable = false
    private var mIndicatorAnimDuration: Long = 0

    /**
     * Text
     */
    private var mTextSize = 0f
    private var mTextSelectColor = 0
    private var mTextUnselectColor = 0

    //    private var mTextBold = 0
    private var mTextAllCaps = false
    private var mTabWidth = 0f
    private var mHeight = 0

    /**
     * anim
     */
    private var mValueAnimator: ValueAnimator? = null

    /**
     * 控制是否重新计算绘制尺寸.
     */
    private var mIsFirstDraw = true

    var tabEntityList = mutableListOf<CustomTabEntity>()
        set(value) {
            if (value.isNullOrEmpty()) {
                throw IllegalStateException("tabEntityList can not be NULL or EMPTY !")
            }
            field.clear()
            field.addAll(value)
            mIsFirstDraw = true
            notifyDataSetChanged()
        }
    private var mTabCount = 0
    private val mUnselectIndicatorDrawables = mutableListOf<GradientDrawable>()
    private var mLastTab = 0
    var mCurrentTab = 0
        set(value) {
            mLastTab = field
            field = value
            updateTabSelection(value)
            /*mFragmentChangeManager?.setFragments(value)*/
            if (mIndicatorAnimEnable) {
                calcOffset()
            } else {
                invalidate()
            }
        }

    private val mListener: OnTabSelectListener? = null

    /**
     * 用于绘制显示器
     */
    private val mIndicatorRect = Rect()
    private val mUnselectIndicatorRect = Rect()
    private val mIndicatorDrawable = GradientDrawable()
    private var mTabSpaceEqual = false
    private var mIndicatorMarginBottom = 0f
    private var tabContainerBackground: Drawable? = null

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (isInEditMode || mTabCount <= 0) {
            return
        }

        tabContainerBackground?.draw(canvas)

        //draw indicator line
        if (mIndicatorAnimEnable) {
            if (mIsFirstDraw) {
                mIsFirstDraw = false
                calcIndicatorRect()
            }
        } else {
            calcIndicatorRect()
        }

        if (mIndicatorHeight > 0) {
            if (mIndicatorCornerRadius < 0 || mIndicatorCornerRadius > mIndicatorHeight / 2) {
                mIndicatorCornerRadius = mIndicatorHeight / 2
            }

            for (i in mUnselectIndicatorDrawables.indices) {
                val drawable = mUnselectIndicatorDrawables[i]
                //drawable.setColor(mUnselectIndicatorColor)
                val width = mIndicatorRect.right - mIndicatorRect.left
                val view = mTabsContainer.getChildAt(i)
                val left = view.left + (view.right - view.left) / 2 - width / 2
                val right = view.left + (view.right - view.left) / 2 + width / 2
                val top = 0
                val bottom = (mIndicatorHeight).toInt()
                drawable.setBounds(
                    left,
                    top,
                    right,
                    bottom
                )

                drawable.cornerRadius =
                    mIndicatorCornerRadius
                drawable.draw(canvas)
            }

            mIndicatorDrawable.run {
                setColor(mIndicatorColor)
                setBounds(
                    paddingLeft + mIndicatorRect.left,
                    height - mIndicatorHeight.toInt(),
                    (paddingLeft + mIndicatorRect.right),
                    (height - paddingBottom - mIndicatorMarginBottom).toInt()
                )
                cornerRadius = mIndicatorCornerRadius
                draw(canvas)
            }
        }


    }

    override fun onAnimationUpdate(animation: ValueAnimator) {
        val currentTabView = mTabsContainer.getChildAt(mCurrentTab)
        val p = animation.animatedValue as IndicatorPoint
        mIndicatorRect.left = p.left.toInt()
        mIndicatorRect.right = p.right.toInt()

        if (mIndicatorWidth > 0) {
            //indicatorWidth大于0时,圆角矩形以及三角形
            val indicatorLeft = p.left + (currentTabView.width - mIndicatorWidth) / 2
            mIndicatorRect.left = indicatorLeft.toInt()
            mIndicatorRect.right = (mIndicatorRect.left + mIndicatorWidth).toInt()
        }
        invalidate()
    }

    override fun onSaveInstanceState(): Parcelable = Bundle().apply {
        putParcelable("instanceState", super.onSaveInstanceState())
        putInt("mCurrentTab", mCurrentTab)
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        var newState = state
        if (state is Bundle) {
            mCurrentTab = state.getInt("mCurrentTab")
            newState = state.getParcelable("instanceState")!!
            if (mCurrentTab != 0 && mTabsContainer.childCount > 0) {
                updateTabSelection(mCurrentTab)
            }
        }
        super.onRestoreInstanceState(newState)
    }

    private fun calcIndicatorRect() {
        for (i in 0 until mTabsContainer.childCount) {
            mUnselectIndicatorRect.left = 0
            mUnselectIndicatorRect.top = 0
            mUnselectIndicatorRect.right = 0
            mUnselectIndicatorRect.bottom = 0
        }
        val currentTabView = mTabsContainer.getChildAt(mCurrentTab)
        val left = currentTabView.left.toFloat()
        val right = currentTabView.right.toFloat()
        mIndicatorRect.left = left.toInt()
        mIndicatorRect.right = right.toInt()
        if (mIndicatorWidth < 0) {   //indicatorWidth小于0时,原jpardogo's PagerSlidingTabStrip
        } else { //indicatorWidth大于0时,圆角矩形以及三角形
            val indicatorLeft = currentTabView.left + (currentTabView.width - mIndicatorWidth) / 2
            mIndicatorRect.left = indicatorLeft.toInt()
            mIndicatorRect.right = (mIndicatorRect.left + mIndicatorWidth).toInt()
        }
    }

    fun notifyDataSetChanged() {
        mTabsContainer.removeAllViews()
        this.mTabCount = tabEntityList.size
        for (i in 0 until mTabCount) {
            val tabView: View = View.inflate(context, R.layout.layout_tab_top, null)
            mUnselectIndicatorDrawables.add(GradientDrawable())
            tabView.tag = i
            addTab(i, tabView)
        }
        updateTabStyles()
    }

    private fun updateTabStyles() {
        for (i in 0 until mTabCount) {
            val tabView = mTabsContainer.getChildAt(i)
            val tv_tab_title = tabView.findViewById<View>(R.id.tv_tab_title) as TextView
            tv_tab_title.setTextColor(if (i == mCurrentTab) mTextSelectColor else mTextUnselectColor)
            tv_tab_title.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize)
            if (mTextAllCaps) {
                tv_tab_title.text = tv_tab_title.text.toString().toUpperCase()
            }
            /*if (mTextBold == CommonTabLayout.TEXT_BOLD_BOTH) {
                tv_tab_title.paint.isFakeBoldText = true
            } else if (mTextBold == CommonTabLayout.TEXT_BOLD_NONE) {
                tv_tab_title.paint.isFakeBoldText = false
            }*/
            val iv_tab_icon = tabView.findViewById<View>(R.id.iv_tab_icon) as ImageView
            iv_tab_icon.visibility = GONE
        }
    }

    private fun calcOffset() {
        val currentTabView = mTabsContainer.getChildAt(mCurrentTab)
        mCurrentP.left = currentTabView.left.toFloat()
        mCurrentP.right = currentTabView.right.toFloat()
        val lastTabView = mTabsContainer.getChildAt(mLastTab)
        mLastP.left = lastTabView.left.toFloat()
        mLastP.right = lastTabView.right.toFloat()
//        Log.d("AAA", "mLastP--->" + mLastP.left + "&" + mLastP.right);
//        Log.d("AAA", "mCurrentP--->" + mCurrentP.left + "&" + mCurrentP.right);
        if (mLastP.left == mCurrentP.left && mLastP.right == mCurrentP.right) {
            invalidate()
        } else {
            mValueAnimator?.setObjectValues(mLastP, mCurrentP)
            /*if (mIndicatorBounceEnable) {
                mValueAnimator!!.interpolator = mInterpolator
            }*/

            //java.lang.IllegalArgumentException: Animators cannot have negative duration: -1
            if (mIndicatorAnimDuration < 0) {
                //mIndicatorAnimDuration = if (mIndicatorBounceEnable) 500 else 250.toLong()
                mIndicatorAnimDuration = 250L
            }
            mValueAnimator?.duration = mIndicatorAnimDuration
            mValueAnimator!!.start()
        }
    }

    private fun addTab(position: Int, tabView: View) {
        val tv_tab_title = tabView.findViewById<View>(R.id.tv_tab_title) as TextView
        tv_tab_title.setText(tabEntityList[position].tabTitle)
        val iv_tab_icon = tabView.findViewById<View>(R.id.iv_tab_icon) as ImageView
        iv_tab_icon.setImageResource(tabEntityList[position].tabUnselectedIcon)
        tabView.setOnClickListener { v ->
            val position = v.tag as Int
            if (mCurrentTab != position) {
                mCurrentTab = position
                mListener?.onTabSelect(position)
            } else {
                mListener?.onTabReselect(position)
            }
        }
        /** 每一个Tab的布局参数  */
        var lp_tab = if (mTabSpaceEqual) LinearLayout.LayoutParams(
            0,
            LayoutParams.MATCH_PARENT,
            1.0f
        ) else LinearLayout.LayoutParams(
            LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT
        )
        if (mTabWidth > 0) {
            lp_tab = LinearLayout.LayoutParams(mTabWidth.toInt(), LayoutParams.MATCH_PARENT)
        }
        mTabsContainer.addView(tabView, position, lp_tab)
    }

    private fun updateTabSelection(position: Int) {
        for (i in 0 until mTabCount) {
            val tabView = mTabsContainer.getChildAt(i)
            val isSelect = i == position
            val tab_title = tabView.findViewById<View>(R.id.tv_tab_title) as TextView
            tab_title.setTextColor(if (isSelect) mTextSelectColor else mTextUnselectColor)

            val iv_tab_icon = tabView.findViewById<View>(R.id.iv_tab_icon) as ImageView
            val tabEntity: CustomTabEntity = tabEntityList[i]
            iv_tab_icon.setImageResource(if (isSelect) tabEntity.tabSelectedIcon else tabEntity.tabUnselectedIcon)
            /*if (mTextBold == CommonTabLayout.TEXT_BOLD_WHEN_SELECT) {
                tab_title.paint.isFakeBoldText = isSelect
            }*/
        }
    }

    private fun obtainAttributes(context: Context, attrs: AttributeSet) {
        val ta: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.ProminentTabLayout)
        mIndicatorColor = ta.getColor(
            R.styleable.ProminentTabLayout_tl_indicator_color,
            Color.parseColor("#ffffff")
        )
        mIndicatorHeight = ta.getDimension(
            R.styleable.ProminentTabLayout_tl_indicator_height,
            dp2px(4.0F).toFloat()
        )
        mIndicatorWidth = ta.getDimension(
            R.styleable.ProminentTabLayout_tl_indicator_width,
            dp2px(-1F).toFloat()
        )
        mIndicatorCornerRadius = ta.getDimension(
            R.styleable.ProminentTabLayout_tl_indicator_corner_radius,
            dp2px(0F).toFloat()
        )
        mIndicatorAnimEnable =
            ta.getBoolean(R.styleable.ProminentTabLayout_tl_indicator_anim_enable, true)
        mIndicatorAnimDuration =
            ta.getInt(R.styleable.ProminentTabLayout_tl_indicator_anim_duration, -1).toLong()
        mTextSize =
            ta.getDimension(R.styleable.ProminentTabLayout_tl_textSize, sp2px(13f).toFloat())
        mTextSelectColor =
            ta.getColor(
                R.styleable.ProminentTabLayout_tl_textSelectColor,
                Color.parseColor("#ffffff")
            )
        mTextUnselectColor = ta.getColor(
            R.styleable.ProminentTabLayout_tl_textUnselectColor,
            Color.parseColor("#AAffffff")
        )
        /*mTextBold =
            ta.getInt(R.styleable.ProminentTabLayout_tl_textBold, ProminentTabLayout.TEXT_BOLD_NONE)*/
        mTextAllCaps = ta.getBoolean(R.styleable.ProminentTabLayout_tl_textAllCaps, false)
        mTabWidth =
            ta.getDimension(R.styleable.ProminentTabLayout_tl_tab_width, dp2px(-1f).toFloat())
        mTabSpaceEqual = ta.getBoolean(R.styleable.ProminentTabLayout_tl_tab_space_equal, true)
        mIndicatorMarginBottom = ta.getDimension(
            R.styleable.ProminentTabLayout_tl_indicator_margin_bottom,
            dp2px(0F).toFloat()
        )
        tabContainerBackground =
            ta.getDrawable(R.styleable.ProminentTabLayout_tabContainerBackground)
        ta.recycle()
    }


    private fun dp2px(dp: Float): Int {
        val scale: Float = resources.displayMetrics.density
        return (dp * scale + 0.5f).toInt()
    }

    private fun sp2px(sp: Float): Int {
        val scale: Float = resources.displayMetrics.scaledDensity
        return (sp * scale + 0.5f).toInt()
    }

}

internal class PointEvaluator : TypeEvaluator<IndicatorPoint> {
    override fun evaluate(
        fraction: Float,
        startValue: IndicatorPoint,
        endValue: IndicatorPoint
    ): IndicatorPoint {
        val left = startValue.left + fraction * (endValue.left - startValue.left)
        val right = startValue.right + fraction * (endValue.right - startValue.right)
        val point = IndicatorPoint()
        point.left = left
        point.right = right
        return point
    }
}

internal class IndicatorPoint {
    var left = 0f
    var right = 0f
}