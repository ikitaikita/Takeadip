package com.takeadip.takeadip.ui;

import android.support.annotation.NonNull;

import com.takeadip.takeadip.data.model.DipData;


import java.util.List;

/**
 * Created by vik on 17/10/2017.
 */

public interface DipListContract {

    interface Presenter extends com.takeadip.takeadip.ui.Presenter<View> {
        void setNavigator(@NonNull Navigator navigator);
       // void initDataSet();



    }
    interface View extends com.takeadip.takeadip.ui.Presenter.View {
        //void showDips(List<DipData> dipDataList,  OnDipClickListener onDipClickListener);

        List<DipData> getDips();
        void displayDips(@NonNull List<DipData> dips, @NonNull OnDipClickListener onDipClickListener);

        void showMessage (String message);


    }
    interface Navigator {
        void openDip(DipData dip);

        //void openFavoriteArticles();
    }
    interface NavigatorProvider {

        @NonNull
        Navigator getNavigator(DipListContract.Presenter presenter);
    }
}
