package net.htwater.njdistrictfx.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

import net.htwater.njdistrictfx.R;
import net.htwater.njdistrictfx.core.MyApplication;
import net.htwater.njdistrictfx.util.SharedPreferencesUtil;

import java.util.ArrayList;

public class FunctionFragment extends Fragment {
    private android.support.v4.view.ViewPager viewPager;
    private TextView switchText1;
    private TextView switchText2;
    private TextView switchText3;
    private TextView switchText4;
    private TextView switchText5;
    private TextView switchText6;
    private TextView switchText7;
    private HorizontalScrollView scrollView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.fragment_function, null);
        viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        switchText1 = (TextView) view.findViewById(R.id.switchText1);
        switchText2 = (TextView) view.findViewById(R.id.switchText2);
        switchText3 = (TextView) view.findViewById(R.id.switchText3);
        switchText4 = (TextView) view.findViewById(R.id.switchText4);
        switchText5 = (TextView) view.findViewById(R.id.switchText5);
        switchText6 = (TextView) view.findViewById(R.id.switchText6);
        switchText7 = (TextView) view.findViewById(R.id.switchText7);
        scrollView = (HorizontalScrollView) view.findViewById(R.id.scrollView);

        init();

        return view;
    }

    private void init() {
        String[] ids = SharedPreferencesUtil.getFunctionIdList().split(",");
        String[] names = SharedPreferencesUtil.getFunctionNameList().split(",");

        int count = 7;
        if (names.length < count) {
            count = names.length;
        }

        TextView[] switchTextViews = new TextView[]{switchText1, switchText2, switchText3, switchText4, switchText5, switchText6, switchText7};
        for (int i = 0; i < count; i++) {
            switchTextViews[i].setVisibility(View.VISIBLE);
            switchTextViews[i].setText(names[i]);
        }

        if (MyApplication.width > 70 * count) {
            int width = MyApplication.widthPixels / count;
            switchText1.getLayoutParams().width = width + 1;
            switchText2.getLayoutParams().width = width + 1;
            switchText3.getLayoutParams().width = width + 1;
            switchText4.getLayoutParams().width = width + 1;
            switchText5.getLayoutParams().width = width + 1;
            switchText6.getLayoutParams().width = width + 1;
            switchText7.getLayoutParams().width = width + 1;
        }

        ArrayList<Fragment> fragmentList = new ArrayList<>();
        for (String id : ids) {
            fragmentList.add(ChildFunctionFragment.getInstance(id));
        }

        viewPager.setOffscreenPageLimit(count - 1);
        viewPager.setAdapter(new MyFragmentPagerAdapter(getActivity().getSupportFragmentManager(), fragmentList));
        viewPager.setOnPageChangeListener(new OnPageChangeListener() {
            int themeColor = getResources().getColor(R.color.theme_blue);
            int textBlack = getResources().getColor(R.color.text_gray);

            @Override
            public void onPageSelected(int index) {
                switchText1.setTextColor(textBlack);
                switchText2.setTextColor(textBlack);
                switchText3.setTextColor(textBlack);
                switchText4.setTextColor(textBlack);
                switchText5.setTextColor(textBlack);
                switchText6.setTextColor(textBlack);
                switchText7.setTextColor(textBlack);
                switch (index) {
                    case 0:
                        switchText1.setTextColor(themeColor);
                        scrollView.smoothScrollTo(0, 0);
                        break;
                    case 1:
                        switchText2.setTextColor(themeColor);
                        scrollView.smoothScrollTo(0, 0);
                        break;
                    case 2:
                        switchText3.setTextColor(themeColor);
                        scrollView.smoothScrollTo(0, 0);
                        break;
                    case 3:
                        switchText4.setTextColor(themeColor);
                        scrollView.smoothScrollTo((int) (MyApplication.density * 90), 0);
                        break;
                    case 4:
                        switchText5.setTextColor(themeColor);
                        scrollView.smoothScrollTo((int) (MyApplication.density * 180), 0);
                        break;
                    case 5:
                        switchText6.setTextColor(themeColor);
                        scrollView.smoothScrollTo((int) (MyApplication.density * 270), 0);
                        break;
                    case 6:
                        switchText7.setTextColor(themeColor);
                        scrollView.smoothScrollTo((int) (MyApplication.density * 360), 0);
                        break;
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });
        switchText1.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(0);
            }
        });
        switchText2.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(1);
            }
        });
        switchText3.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(2);
            }
        });
        switchText4.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(3);
            }
        });
        switchText5.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(4);
            }
        });
        switchText6.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(5);
            }
        });
        switchText7.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(6);
            }
        });
        // viewPager.setCurrentItem(0);
    }

    private class MyFragmentPagerAdapter extends FragmentPagerAdapter {
        private final ArrayList<Fragment> list;

        private MyFragmentPagerAdapter(FragmentManager fm, ArrayList<Fragment> list) {
            super(fm);
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Fragment getItem(int arg0) {
            return list.get(arg0);
        }

    }
}
