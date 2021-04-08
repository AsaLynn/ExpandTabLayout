package com.zxn.tablayoutsamples.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.zxn.tablayout.CommonTabLayout
import com.zxn.tablayout.listener.CustomTabEntity
import com.zxn.tablayoutsamples.R
import com.zxn.tablayoutsamples.entity.TabEntity
import com.zxn.tablayoutsamples.utils.ViewFindUtils
import kotlinx.android.synthetic.main.activity_shadow_tab_single.*
import java.util.*

open class SingleShadowTabActivity : AppCompatActivity() {
    private val mContext: Context = this
    private val mTitles10 = arrayOf("外卖", "自取", "预订")
    private val mTabEntities = ArrayList<CustomTabEntity>()
    private val mTabEntities10 = ArrayList<CustomTabEntity>()
    private var mDecorView: View? = null

    private var mTabLayout_10: CommonTabLayout? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shadow_tab_single)
        for (i in mTitles10.indices) {
            mTabEntities10.add(TabEntity(mTitles10[i]))
        }
        mDecorView = window.decorView
        /** indicator圆角色块2  */
        mTabLayout_10 = ViewFindUtils.find(mDecorView, R.id.tabA)
        mTabLayout_10?.setTabData(mTabEntities10)
        tabB.setTabData(mTabEntities10)

        tabC.setTabData(mutableListOf<CustomTabEntity>(TabEntity("常见饮品"),
                TabEntity("蛋白饮品"),
                TabEntity("果蔬饮品"),
                TabEntity("清凉饮品"),
                TabEntity("更多")))
    }

    protected fun dp2px(dp: Float): Int {
        val scale = mContext.resources.displayMetrics.density
        return (dp * scale + 0.5f).toInt()
    }
}