package com.takeadip.takeadip.ui.dip;



import com.takeadip.takeadip.data.model.DipData;
import com.takeadip.takeadip.ui.Presenter;

/**
 * Created by vik on 17/10/2017.
 */

public interface DipContract {

    interface Presenter extends com.takeadip.takeadip.ui.Presenter<View> {}

    interface View extends com.takeadip.takeadip.ui.Presenter.View {
        void displayDip(DipData dip);
        void displayMap ();
        void drawPointOnMap (DipData dip);
        void showErrorMessage();
    }
}
