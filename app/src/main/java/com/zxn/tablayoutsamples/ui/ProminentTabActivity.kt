package com.zxn.tablayoutsamples.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.zxn.tablayoutsamples.R
import com.zxn.tablayoutsamples.entity.TabEntity
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

    }
}