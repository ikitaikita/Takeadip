package com.takeadip.takeadip.ui.dip_list;

import android.app.Activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.takeadip.takeadip.Constants;
import com.takeadip.takeadip.R;
import com.takeadip.takeadip.TabDipsFragment;
import com.takeadip.takeadip.data.model.DipData;
import com.takeadip.takeadip.di.Injector;
import com.takeadip.takeadip.model.Dip;
import com.takeadip.takeadip.ui.DipListContract;
import com.takeadip.takeadip.ui.DipListPresenter;
import com.takeadip.takeadip.ui.DipsAdapter;
import com.takeadip.takeadip.ui.OnDipClickListener;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by vik on 17/10/2017.
 */

public class DipListFragment extends Fragment implements DipListContract.View {

    private DipListContract.Presenter mPresenter;

    //private DipsAdapter dipssAdapter;
    private OnDipClickListener onDipClickListener;

    private RecyclerView mRecyclerView;

    private ArrayList<DipData> l_dips = new ArrayList<DipData>();

    public DipListFragment() {
        // Required empty public constructor
    }

    public static DipListFragment newInstance(ArrayList<DipData> diplist)
    {
        DipListFragment myFragment = new DipListFragment();
        //myFragment.l_dips = diplist;
        Bundle args = new Bundle();
        args.putSerializable("diplist", diplist);

        myFragment.setArguments(args);

        return myFragment;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //DaggerManager.component().inject(this);
        if (savedInstanceState != null) {
            l_dips = (ArrayList<DipData>)savedInstanceState.get("diplist");
            //mTitle = state.getString("mTitle");
        }
        
        //dipssAdapter = new DipsAdapter(l_dips, onDipClickListener,Constants.DIPS_VIEW );
    }
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        mPresenter = createPresenter();

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.tab_dips, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(container.getContext()
                , LinearLayoutManager.VERTICAL,
                false));



        return view;
    }

    @NonNull
    protected DipListContract.Presenter createPresenter() {
        DipListContract.Presenter presenter = new DipListPresenter();
        presenter.setNavigator(getNavigator(presenter));
        return presenter;
    }

    @NonNull
    protected DipListContract.Navigator getNavigator(final DipListContract.Presenter presenter) {
        Fragment parentFragment = getParentFragment();
        if (parentFragment != null && parentFragment instanceof DipListContract.NavigatorProvider) {
            return ((DipListContract.NavigatorProvider) parentFragment).getNavigator(presenter);
        } else {
            Activity activity = getActivity();
            if (activity instanceof DipListContract.NavigatorProvider) {
                return ((DipListContract.NavigatorProvider) activity).getNavigator(presenter);
            }
        }

        throw new IllegalStateException("Activity or parent Fragment must implement "
                + "DipListContract.NavigatorProvider");
    }






    @Override
    public void onStart() {
        super.onStart();
        mPresenter.onAttachView(this);
    }




    @Override
    public void onStop() {
        super.onStop();
        mPresenter.onDetachView();
    }

    /*@Override
    public void showDips(@NonNull List<DipData> dipDataList) {


        dipssAdapter.updateDips( dipDataList );

    }*/

    @Override
    public void displayDips(@NonNull final List<DipData> dips,
                                @NonNull final OnDipClickListener onDipClickListener) {
        mRecyclerView.setAdapter(new DipsAdapter(l_dips, onDipClickListener, Constants.DIPS_VIEW));
    }

    @Override
    public List<DipData> getDips() {

        return l_dips;
    }


    @Override
    public void showMessage(String message) {

    }



}
