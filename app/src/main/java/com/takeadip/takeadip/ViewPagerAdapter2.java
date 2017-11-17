package com.takeadip.takeadip;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.takeadip.takeadip.model.Dip;

import java.util.ArrayList;

/**
 * Created by vik on 22/05/2017.
 */

public class ViewPagerAdapter2 extends FragmentStatePagerAdapter {

   // CharSequence Titles[]; // This will Store the Titles of the Tabs which are Going to be passed when ViewPagerAdapter is created
    int NumbOfTabs; // Store the number of tabs, this will also be passed when the ViewPagerAdapter is created
    // Build a Constructor and assign the passed Values to appropriate values in the class
    ArrayList<Dip> listdip;
    ArrayList<Dip> favourites;


    public ViewPagerAdapter2(FragmentManager fm, int mNumbOfTabsumb, ArrayList<Dip> listdips, ArrayList<Dip> favourites) {
        super(fm);

        //this.Titles = mTitles;
        this.NumbOfTabs = mNumbOfTabsumb;
        this.listdip = listdips;
        this.favourites = favourites;

    }

    //This method return the fragment for the every position in the View Pager
    @Override
    public Fragment getItem(int position) {





            switch (position) {
                case 0:
                    /*TabNearFragmentNew tab1 = new TabNearFragmentNew();
                    return tab1;*/
                    return TabNearFragmentNew.newInstance(listdip);


                case 1:
                    /*TabMapFragment tab2 = new TabMapFragment();
                    return tab2;*/
                    return TabMapFragment.newInstance(listdip);

                case 2:
                    //return TabDipsFragment.newInstance();
                    /*TabDipsFragment tab3 = new TabDipsFragment();
                    return tab3;*/
                    return TabDipsFragment.newInstance(listdip);

                case 3:
                   /* TabFavouriteFragment tab4 = new TabFavouriteFragment();
                    return tab4;*/
                    return TabFavouriteFragment.newInstance(favourites);
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
