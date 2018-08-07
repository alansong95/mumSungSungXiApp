package com.wootae.mumsungsungxi;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Alan on 8/7/2018.
 */

public class ViewPagerAdapter extends FragmentPagerAdapter {
    private String[] classNames;
    private Context mContext;

    private Fragment[] classFragments;

    public ViewPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        mContext = context;
        classNames = mContext.getResources().getStringArray(R.array.classes);

        classFragments = new Fragment[] {
                new ClassOneFragment(),
                new ClassTwoFragment(),
                new ClassThreeFragment(),
                new ClassFourFragment(),
                new ClassFiveFragment()
        };
    }

    @Override
    public Fragment getItem(int position) {
        return classFragments[position];
    }

    @Override
    public int getCount() {
        return MainActivity.NUMBER_OF_CLASSES;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return classNames[position];
    }

//    public int getItemPosition(Object object) {
//        return POSITION_NONE;
//    }
}
