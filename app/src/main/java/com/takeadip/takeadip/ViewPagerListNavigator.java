package com.takeadip.takeadip;

import android.content.Context;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.takeadip.takeadip.data.model.DipData;
import com.takeadip.takeadip.ui.DipListContract;
import com.takeadip.takeadip.ui.dip.DipFragment;

/**
 * Created by vik on 19/10/2017.
 */

public class ViewPagerListNavigator implements DipListContract.Navigator {

    //private final Context mActivityContext;
    private final ViewPager mViewPager;
    private final AppCompatActivity mActivity;

    public ViewPagerListNavigator(final AppCompatActivity activity, final ViewPager mViewPager) {
        //this.mActivityContext = mActivityContext;
        this.mViewPager = mViewPager;
        this.mActivity = activity;
    }

    @Override
    public void openDip(final DipData dip) {
        mActivity.getSupportFragmentManager()

                .beginTransaction()
               //.replace(R.id.secondary_content, DipFragment.newInstance(id), null)
                .add(android.R.id.content,DipFragment.newInstance(dip), "tagFragment")
                .addToBackStack("tagFragment")
                .commit();


    }


}
