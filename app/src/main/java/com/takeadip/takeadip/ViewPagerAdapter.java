package com.takeadip.takeadip;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.takeadip.takeadip.data.model.DipData;
import com.takeadip.takeadip.model.Dip;
import com.takeadip.takeadip.ui.dip_list.DipListFragment;
import com.takeadip.takeadip.ui.favourite_list.FavouriteListFragment;
import com.takeadip.takeadip.ui.map.MapFragment;
import com.takeadip.takeadip.ui.nearby_list.NearbyListFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vik on 22/05/2017.
 */

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

   // CharSequence Titles[]; // This will Store the Titles of the Tabs which are Going to be passed when ViewPagerAdapter is created
    int NumbOfTabs; // Store the number of tabs, this will also be passed when the ViewPagerAdapter is created
    // Build a Constructor and assign the passed Values to appropriate values in the class



    ArrayList<DipData> diplist = new ArrayList<DipData>();



    public ViewPagerAdapter(FragmentManager fm, int mNumbOfTabsumb, ArrayList<DipData>diplist ) {
        super(fm);

        //this.Titles = mTitles;
        this.NumbOfTabs = mNumbOfTabsumb;
        this.diplist = diplist;

    }

    //This method return the fragment for the every position in the View Pager
    @Override
    public Fragment getItem(int position) {





            switch (position) {
                case 0:
                    /*NearbyListFragment tab1 = new NearbyListFragment();
                    return tab1;*/
                    return NearbyListFragment.newInstance(diplist);
                case 1:
                    /*MapFragment tab2 = new MapFragment();
                    return tab2;*/
                    return MapFragment.newInstance(diplist);

                case 2:
                    //return TabDipsFragment.newInstance();
                    /*DipListFragment tab3 = new DipListFragment();
                    return tab3;*/
                    return DipListFragment.newInstance(diplist);

                case 3:
                    /*FavouriteListFragment tab4 = new FavouriteListFragment();
                    return tab4;*/
                    return FavouriteListFragment.newInstance(diplist);
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

    public void setDiplist(ArrayList<DipData> diplist) {
        this.diplist = diplist;
    }
}
