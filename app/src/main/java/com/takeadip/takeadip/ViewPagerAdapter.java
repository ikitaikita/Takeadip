package com.takeadip.takeadip;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by vik on 22/05/2017.
 */

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

   // CharSequence Titles[]; // This will Store the Titles of the Tabs which are Going to be passed when ViewPagerAdapter is created
    int NumbOfTabs; // Store the number of tabs, this will also be passed when the ViewPagerAdapter is created
    // Build a Constructor and assign the passed Values to appropriate values in the class



    public ViewPagerAdapter(FragmentManager fm, int mNumbOfTabsumb) {
        super(fm);

        //this.Titles = mTitles;
        this.NumbOfTabs = mNumbOfTabsumb;

    }

    //This method return the fragment for the every position in the View Pager
    @Override
    public Fragment getItem(int position) {





            switch (position) {
                case 0:
                    TabNearFragmentNew tab1 = new TabNearFragmentNew();
                    return tab1;
                case 1:
                    TabMapFragment tab2 = new TabMapFragment();
                    return tab2;

                case 2:
                    //return TabDipsFragment.newInstance();
                    TabDipsFragment tab3 = new TabDipsFragment();
                    return tab3;

                case 3:
                    TabFavouriteFragment tab4 = new TabFavouriteFragment();
                    return tab4;
                default:
                    return null;
            }


    }
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Nearby";
            case 1:
                return "Map";
            case 2:
                return "Dips";

        }
        return null;
    }

    @Override
    public int getCount()
        {
        return NumbOfTabs;
    }
}
