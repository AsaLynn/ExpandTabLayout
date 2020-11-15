package com.zxn.tablayoutsamples.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.zxn.tablayout.SlidingScaleTabLayout;
import com.zxn.tablayoutsamples.R;

public class SlidingScaleTabLayoutActivity extends AppCompatActivity {

    private final int[] colors = {
            Color.BLACK, Color.BLUE, Color.CYAN, Color.RED
    };

    private SlidingScaleTabLayout tabLayout;

    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabLayout = findViewById(R.id.tablayout);
        viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(new MyViewPagerAdapter());
        viewPager.setOffscreenPageLimit(4);
        tabLayout.setViewPager(viewPager);
        viewPager.setCurrentItem(3);
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
            TextView textView = new TextView(SlidingScaleTabLayoutActivity.this);
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
