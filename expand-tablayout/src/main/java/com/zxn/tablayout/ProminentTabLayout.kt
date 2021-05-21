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
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.zxn.tablayout.listener.CustomTabEntity
import com.zxn.tablayout.utils.FragmentChangeManager

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
    private var mIndicatorCornerRadius = 0F
    private var indicatorTopLeftR = 0F
    private var indicatorTopRightR = 0F
    private var indicatorBottomLeftR = 0F
    private var indicatorBottomRightR = 0F
    private var mIndicatorAnimEnable = false
    private var mIndicatorAnimDuration: Long = 0

    /**
     * Text
     */
    private var mUnSelectSize = 0f
    private var mTextSelectSize = 0f
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
    var currentTab = 0
        set(value) {
            mLastTab = field
            field = value
            updateTabSelection(value)
            mFragmentChangeManager?.setFragments(value)
            if (mIndicatorAnimEnable) {
                calcOffset()
            } else {
                invalidate()
            }
        }

    private var mListener: ((Int) -> Unit)? = null

    /**
     * 用于绘制显示器
     */
    private val mIndicatorRect = Rect()
    private val mIndicatorDrawable = GradientDrawable()
    private var mTabSpaceEqual = false
    private var mIndicatorMarginBottom = 0f
    private var tabContainerBackground: Drawable? = null

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (isInEditMode || mTabCount <= 0) {
            return
        }

        tabContainerBackground?.let {
            it.setBounds(
                mTabsContainer.left,
                mTabsContainer.top,
                mTabsContainer.right,
                mTabsContainer.bottom
            )
            it.draw(canvas)
        }

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
            mIndicatorDrawable.run {
                setColor(mIndicatorColor)
                setBounds(
                    paddingLeft + mIndicatorRect.left,
                    height - mIndicatorHeight.toInt(),
                    (paddingLeft + mIndicatorRect.right),
                    (height - mIndicatorMarginBottom).toInt()
                )
                if (indicatorTopLeftR > 0
                    || indicatorTopRightR > 0
                    || indicatorBottomLeftR > 0
                    || indicatorBottomRightR > 0
                ) {
                    //设置左,上,右,下,四个部位的圆角.
                    cornerRadii = floatArrayOf(
                        indicatorTopLeftR,
                        indicatorTopLeftR,
                        indicatorTopRightR,
                        indicatorTopRightR,
                        indicatorBottomLeftR,
                        indicatorBottomLeftR,
                        indicatorBottomRightR,
                        indicatorBottomRightR,
                    )
                } else {
                    cornerRadius = mIndicatorCornerRadius
                }
                draw(canvas)
            }
        }
    }

    override fun onAnimationUpdate(animation: ValueAnimator) {
        val currentTabView = mTabsContainer.getChildAt(currentTab)
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
        putInt("mCurrentTab", currentTab)
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        var newState = state
        if (state is Bundle) {
            currentTab = state.getInt("mCurrentTab")
            newState = state.getParcelable("instanceState")!!
            if (currentTab != 0 && mTabsContainer.childCount > 0) {
                updateTabSelection(currentTab)
            }
        }
        super.onRestoreInstanceState(newState)
    }

    private fun calcIndicatorRect() {
        val currentTabView = mTabsContainer.getChildAt(currentTab)
        val left = currentTabView.left.toFloat()
        val right = currentTabView.right.toFloat()
        mIndicatorRect.left = left.toInt()
        mIndicatorRect.right = right.toInt()

        if (mIndicatorWidth >= 0) {
            //indicatorWidth大于0时,圆角矩形以及三角形
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
            tabView.findViewById<TextView>(R.id.tv_tab_title).run {
                this.setTextColor(if (i == currentTab) mTextSelectColor else mTextUnselectColor)
                this.setTextSize(
                    TypedValue.COMPLEX_UNIT_PX,
                    if (i == currentTab) mTextSelectSize else mUnSelectSize
                )
                if (mTextAllCaps) {
                    this.text = this.text.toString().toUpperCase()
                }
            }
            tabView.findViewById<ImageView>(R.id.iv_tab_icon).visibility = GONE
        }
    }

    private fun calcOffset() {
        val currentTabView = mTabsContainer.getChildAt(currentTab)
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

            if (mIndicatorAnimDuration < 0) {
                //mIndicatorAnimDuration = if (mIndicatorBounceEnable) 500 else 250.toLong()
                mIndicatorAnimDuration = 250L
            }
            mValueAnimator?.duration = mIndicatorAnimDuration
            mValueAnimator!!.start()
        }
    }

    private fun addTab(position: Int, tabView: View) {
        tabView.findViewById<TextView>(R.id.tv_tab_title).also {
            it.text = tabEntityList[position].tabTitle
        }

        tabView.findViewById<ImageView>(R.id.iv_tab_icon).also {
            it.setImageResource(tabEntityList[position].tabUnselectedIcon)
        }

        tabView.setOnClickListener { v ->
            (v.tag as Int).also {
                if (currentTab != it) {
                    currentTab = it
                    mListener?.invoke(it)
                }
            }
        }
        /** 每一个Tab的布局参数  */
        var lpTab = if (mTabSpaceEqual) LinearLayout.LayoutParams(
            0,
            LayoutParams.MATCH_PARENT,
            1.0f
        ) else LinearLayout.LayoutParams(
            LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT
        )
        if (mTabWidth > 0) {
            lpTab = LinearLayout.LayoutParams(mTabWidth.toInt(), LayoutParams.MATCH_PARENT)
        }
        mTabsContainer.addView(tabView, position, lpTab)
    }

    private fun updateTabSelection(position: Int) {
        for (i in 0 until mTabCount) {
            val tabView = mTabsContainer.getChildAt(i)
            val isSelect = i == position
            tabView.findViewById<TextView>(R.id.tv_tab_title).also {
                it.setTextColor(if (isSelect) mTextSelectColor else mTextUnselectColor)
                it.setTextSize(
                    TypedValue.COMPLEX_UNIT_PX,
                    if (isSelect) mTextSelectSize else mUnSelectSize
                )
            }
            val tabEntity: CustomTabEntity = tabEntityList[i]
            tabView.findViewById<ImageView>(R.id.iv_tab_icon).also {
                it.setImageResource(if (isSelect) tabEntity.tabSelectedIcon else tabEntity.tabUnselectedIcon)
            }

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
        if (mIndicatorCornerRadius < 0) {
            mIndicatorCornerRadius = 0F
        }

        if (mIndicatorCornerRadius > mIndicatorHeight / 2) {
            mIndicatorCornerRadius = mIndicatorHeight / 2
        }

        indicatorTopLeftR = ta.getDimension(
            R.styleable.ProminentTabLayout_indicatorTopLeftRadius,
            dp2px(0F).toFloat()
        )
        if (indicatorTopLeftR < 0) {
            indicatorTopLeftR = 0F
        }

        if (indicatorTopLeftR > mIndicatorHeight / 2) {
            indicatorTopLeftR = mIndicatorHeight / 2
        }


        indicatorTopRightR = ta.getDimension(
            R.styleable.ProminentTabLayout_indicatorTopRightRadius,
            dp2px(0F).toFloat()
        )
        if (indicatorTopRightR < 0) {
            indicatorTopRightR = 0F
        }

        if (indicatorTopRightR > mIndicatorHeight / 2) {
            indicatorTopRightR = mIndicatorHeight / 2
        }

        indicatorBottomLeftR = ta.getDimension(
            R.styleable.ProminentTabLayout_indicatorBottomLeftRadius,
            dp2px(0F).toFloat()
        )
        if (indicatorBottomLeftR < 0) {
            indicatorBottomLeftR = 0F
        }
        if (indicatorBottomLeftR > mIndicatorHeight / 2) {
            indicatorBottomLeftR = mIndicatorHeight / 2
        }

        indicatorBottomRightR = ta.getDimension(
            R.styleable.ProminentTabLayout_indicatorBottomRightRadius,
            dp2px(0F).toFloat()
        )
        if (indicatorBottomRightR < 0) {
            indicatorBottomRightR = 0F
        }

        if (indicatorBottomRightR > mIndicatorHeight / 2) {
            indicatorBottomRightR = mIndicatorHeight / 2
        }

        mIndicatorAnimEnable =
            ta.getBoolean(R.styleable.ProminentTabLayout_tl_indicator_anim_enable, true)
        mIndicatorAnimDuration =
            ta.getInt(R.styleable.ProminentTabLayout_tl_indicator_anim_duration, -1).toLong()
        mUnSelectSize =
            ta.getDimension(
                R.styleable.ProminentTabLayout_tl_textUnSelectSize,
                sp2px(12f).toFloat()
            )
        mTextSelectSize =
            ta.getDimension(R.styleable.ProminentTabLayout_tl_textSelectSize, mUnSelectSize)
        mTextSelectColor =
            ta.getColor(
                R.styleable.ProminentTabLayout_tl_textSelectColor,
                Color.parseColor("#ffffff")
            )
        mTextUnselectColor = ta.getColor(
            R.styleable.ProminentTabLayout_tl_textUnselectColor,
            Color.parseColor("#AAffffff")
        )
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

    fun setOnTabSelectListener(block: (value: Int) -> Unit) {
        mListener = block
    }

    private var mFragmentChangeManager: FragmentChangeManager? = null

    /**
     * 关联数据支持同时切换fragments
     */
    fun setTabData(
        tabEntityList: MutableList<CustomTabEntity>,
        fa: FragmentActivity,
        containerViewId: Int,
        fragments: MutableList<Fragment>
    ) {
        mFragmentChangeManager =
            FragmentChangeManager(fa.supportFragmentManager, containerViewId, fragments)
        this.tabEntityList = tabEntityList
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