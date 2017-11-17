package com.takeadip.takeadip.ui.dip;

import com.takeadip.takeadip.data.model.DipData;

/**
 * Created by vik on 17/10/2017.
 */

public class DipPresenter implements DipContract.Presenter {

    private final DipData mDip;
    //private final int mDipId;
    private DipContract.View mView;

    public DipPresenter(final DipData mDip) {
        this.mDip = mDip;
    }

    @Override
    public void onAttachView(DipContract.View view) {

        this.mView = view;
        if (mDip != null) {
            mView.displayDip(mDip);
            mView.displayMap();
            mView.drawPointOnMap(mDip);
        } else {
            // Show error screen, or snack bar.
            mView.showErrorMessage();
        }


    }

    @Override
    public void onDetachView() {mView = null;}
}
