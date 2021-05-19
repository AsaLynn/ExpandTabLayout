package com.zxn.tablayoutsamples.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.zxn.tablayoutsamples.R
import com.zxn.tablayoutsamples.entity.TabEntity
import com.zxn.tablayoutsamples.ui.fragment.TestFragment
import kotlinx.android.synthetic.main.activity_prominent_tab.*

/**
 *  Created by zxn on 2021/5/18.
 */
class ProminentTabActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_prominent_tab)

        prominentTabA.tabEntityList = (mutableListOf(
            TabEntity("常见"),
            TabEntity("蛋白"),
            TabEntity("果蔬"),
            TabEntity("清凉")
        ))

        prominentTabB.tabEntityList = (mutableListOf(
            TabEntity("鱼宠"),
            TabEntity("道具"),
            TabEntity("背景"),
            TabEntity("贝币")
        ))
        prominentTabB.setOnTabSelectListener {
            Toast.makeText(this, "$it-->", Toast.LENGTH_SHORT).show()
        }

        /*prominentTabC.tabEntityList = (
                mutableListOf(
                    TabEntity("鱼宠"),
                    TabEntity("道具"),
                    TabEntity("背景"),
                    TabEntity("贝币")))*/
        
        prominentTabC.setTabData(
            mutableListOf(
                TabEntity("鱼宠"),
                TabEntity("道具"),
                TabEntity("背景"),
                TabEntity("贝币")
            ), this, R.id.flContainer, mutableListOf(
                TestFragment.newInstance("鱼宠"),
                TestFragment.newInstance("道具"),
                TestFragment.newInstance("背景"),
                TestFragment.newInstance("贝币")
            )
        )
    }
}