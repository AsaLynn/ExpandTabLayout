package com.zxn.tablayoutsamples.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.zxn.tablayoutsamples.adapter.SimpleHomeAdapter;

/**
 * 标签列表页面.
 */
public class SimpleHomeActivity extends AppCompatActivity {

    private final String[] mItems = {
            "ProminentTabLayout",
            "SlidingTabLayout",
            "CommonTabLayout",
            "SegmentTabLayout",
            "ShadowTabLayout",
            "SingleShadowTabActivity",
            "SlidingScaleTabLayout(new)",
            "SlidingScaleTabLayoutFragmentActivity(new)",
            "SlidingScaleTabLayoutFragmentActivity(2)"

    };
    private final Class<?>[] mClasses = {
            ProminentTabActivity.class,
            SlidingTabActivity.class,
            CommonTabActivity.class,
            SegmentTabActivity.class,
            ShadowTabActivity.class,
            SingleShadowTabActivity.class,
            SlidingScaleTabLayoutActivity.class,
            SlidingScaleTabLayoutFragmentActivity.class,
            SlidingScaleTabLayoutActivity2.class
    };
    private final Context mContext = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ListView lv = new ListView(mContext);
        lv.setCacheColorHint(Color.TRANSPARENT);
        lv.setFadingEdgeLength(0);
        lv.setAdapter(new SimpleHomeAdapter(mContext, mItems));

        lv.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(mContext, mClasses[position]);
            startActivity(intent);
        });

        setContentView(lv);
    }
}
