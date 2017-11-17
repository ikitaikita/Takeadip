
package com.takeadip.takeadip;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.takeadip.takeadip.data.DataManager;
import com.takeadip.takeadip.data.model.DipData;
import com.takeadip.takeadip.di.Injector;
import com.takeadip.takeadip.di.component.ActivityComponent;
import com.takeadip.takeadip.di.component.DaggerActivityComponent;
import com.takeadip.takeadip.di.module.ActivityModule;
import com.takeadip.takeadip.model.Dip;
import com.takeadip.takeadip.ui.DipListContract;
import com.takeadip.takeadip.ui.DipsAdapter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by vik on 19/10/2017.
 */

public class ViewPagerActivity extends AppCompatActivity implements DipListContract.NavigatorProvider, ViewPagerActivityContract.View   {


    @Inject
    DataManager mDataManager;

    private ViewPager mViewPager;

    private ActivityComponent activityComponent;

    private ViewPagerActivityPresenter presenter;
    private ViewPagerAdapter adapter;
    private TabLayout tabLayout;

    private ArrayList<DipData> l_dips = new ArrayList<DipData>( 0 );


    public ActivityComponent getActivityComponent() {
        if (activityComponent == null) {
            activityComponent = DaggerActivityComponent.builder()
                    .activityModule(new ActivityModule(this))
                    .applicationComponent(TakeadipApplication.get(this).getComponent())
                    .build();
        }
        return activityComponent;
    }


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getActivityComponent().inject(this);

        presenter = new ViewPagerActivityPresenter( this, Injector.provideDipService() );
        presenter.initDataSet();

        //presenter.initDataSet2(getResources().openRawResource(R.raw.sample_data));




        //Initializing the tablayout
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);

        tabLayout.addTab(tabLayout.newTab().setText("Nearby").setTag("Nearby"));

        tabLayout.addTab(tabLayout.newTab().setText("Map"));
        tabLayout.addTab(tabLayout.newTab().setText("Dips"));
        //tabLayout.addTab(tabLayout.newTab().setText("Favourites"));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.mipmap.ic_action_favorite_border));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        mViewPager = (ViewPager) findViewById(R.id.pager);









        getActivityComponent().inject(this);
    }

    @NonNull
    @Override
    public DipListContract.Navigator getNavigator(final DipListContract.Presenter presenter) {
        return new ViewPagerListNavigator(this, mViewPager);
    }


    @Override
    public void setDips(ArrayList<DipData> dipdata) {

        //mDataManager.saveDipsListOnPrefs(l_dips);
        l_dips = dipdata;
        //dipsAdapter.updateDips(l_dips);
        adapter = new ViewPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount(),l_dips);

        //Adding adapter to pager
        mViewPager.setAdapter(adapter);
    }

    @Override
    public void showErrorMessage(String message) {
        Toast.makeText( this, message, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void refresh() {

    }
}


