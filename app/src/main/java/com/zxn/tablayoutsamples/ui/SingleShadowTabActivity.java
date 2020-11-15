package com.zxn.tablayoutsamples.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.zxn.tablayout.CommonTabLayout;
import com.zxn.tablayout.listener.CustomTabEntity;
import com.zxn.tablayoutsamples.R;
import com.zxn.tablayoutsamples.entity.TabEntity;
import com.zxn.tablayoutsamples.utils.ViewFindUtils;

import java.util.ArrayList;

public class SingleShadowTabActivity extends AppCompatActivity {
    private final Context mContext = this;

    private final String[] mTitles10 = {"外卖", "自取", "预订"};

    private final ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();
    private final ArrayList<CustomTabEntity> mTabEntities10 = new ArrayList<>();
    private View mDecorView;
    private CommonTabLayout mTabLayout_10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shadow_tab_single);

        for (int i = 0; i < mTitles10.length; i++) {
            mTabEntities10.add(new TabEntity(mTitles10[i]));
        }

        mDecorView = getWindow().getDecorView();

        /** indicator圆角色块2 */

        mTabLayout_10 = ViewFindUtils.find(mDecorView, R.id.tl_10);
        mTabLayout_10.setTabData(mTabEntities10);
    }

    protected int dp2px(float dp) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}
