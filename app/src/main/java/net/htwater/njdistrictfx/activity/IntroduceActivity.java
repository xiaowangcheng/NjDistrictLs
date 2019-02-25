package net.htwater.njdistrictfx.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.viewpagerindicator.CirclePageIndicator;
import com.viewpagerindicator.PageIndicator;

import net.htwater.njdistrictfx.R;
import net.htwater.njdistrictfx.activity.User.LoginActivity;
import net.htwater.njdistrictfx.core.MyApplication;

/**
 * 功能介绍activity
 */
public class IntroduceActivity extends Activity {
    // private ViewPager viewPager;
    private ImageView[] mImageViews;
    private boolean isBoot;
    // private PageIndicator mIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduce);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        PageIndicator mIndicator = (CirclePageIndicator)findViewById(R.id.indicator);

        isBoot = getIntent().getBooleanExtra("isBoot", false);

        int[] array = new int[]{R.mipmap.introduce1, R.mipmap.introduce2,
                R.mipmap.introduce3, R.mipmap.introduce4};
        mImageViews = new ImageView[array.length];
        for (int i = 0; i < mImageViews.length; i++) {
            ImageView imageView = new ImageView(this);
            mImageViews[i] = imageView;

            imageView.setTag(i);
            imageView.setBackgroundResource(array[i]);
            imageView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    int tag = (int) v.getTag();
                    if (tag == 3) {
                        if (isBoot) {
                            Intent intent = new Intent(IntroduceActivity.this,
                                    LoginActivity.class);
                            startActivity(intent);
                        }
                        finish();
                    } else {
                        viewPager.setCurrentItem(++tag);
                    }
                }
            });
        }
        viewPager.setAdapter(new MyAdapter());
        mIndicator.setViewPager(viewPager);

        MyApplication.addActivity(this);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.removeActivity(this);
    }

    @Override
    public void onBackPressed() {
        if (isBoot) {
            Intent intent = new Intent(IntroduceActivity.this,
                    LoginActivity.class);
            startActivity(intent);
        }
        super.onBackPressed();
    }

    private class MyAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mImageViews.length;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mImageViews[position]);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mImageViews[position]);
            return mImageViews[position];
        }
    }
}
