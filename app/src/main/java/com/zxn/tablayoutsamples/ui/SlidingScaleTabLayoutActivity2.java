package com.zxn.tablayoutsamples.ui;

import android.graphics.Color;
import android.os.Bundle;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.zxn.tablayout.SlidingScaleTabLayout;
import com.zxn.tablayout.listener.OnTabSelectListener;
import com.zxn.tablayoutsamples.R;


public class SlidingScaleTabLayoutActivity2 extends AppCompatActivity {

    private int[] colors = {
            Color.BLACK, Color.BLUE, Color.CYAN, Color.RED
    };

    private SlidingScaleTabLayout tabLayout;

    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        tabLayout = findViewById(R.id.tablayout);
        viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(new MyViewPagerAdapter());
        viewPager.setOffscreenPageLimit(4);
        tabLayout.setViewPager(viewPager);
        viewPager.setCurrentItem(3);

        tabLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // 监听测量完成
                showTitleLeftDrawable();
                tabLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        tabLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                showTitleLeftDrawable();
            }

            @Override
            public void onTabReselect(int position) {

            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                showTitleLeftDrawable();
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

    }

    private void showTitleLeftDrawable() {
        for (int i = 0; i < viewPager.getAdapter().getCount(); i ++) {
            if (i != viewPager.getCurrentItem()) {
                tabLayout.getTitleView(i).setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(this, R.drawable.icon_wddj_dj), null, null, null);
            } else {
                tabLayout.getTitleView(i).setCompoundDrawables(null, null, null, null);
            }
        }
    }

    class MyViewPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            View view = (View) object;
            return (int) view.getTag();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return "标题位置" + position;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
            return view == o;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            TextView textView = new TextView(SlidingScaleTabLayoutActivity2.this);
            textView.setBackgroundColor(colors[position]);
            textView.setText(getPageTitle(position));
            textView.setTag(position);
            container.addView(textView);
            return textView;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }
    }
}
