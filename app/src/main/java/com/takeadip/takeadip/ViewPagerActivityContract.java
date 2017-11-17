package com.takeadip.takeadip;

import android.support.annotation.NonNull;

import com.takeadip.takeadip.data.model.DipData;
import com.takeadip.takeadip.ui.DipListContract;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vik on 20/10/2017.
 */

public interface ViewPagerActivityContract {

    interface Presenter extends com.takeadip.takeadip.ui.Presenter<View> {
        void setNavigator(@NonNull DipListContract.Navigator navigator);
       // void initDataSet();

    }

    interface View extends com.takeadip.takeadip.ui.Presenter.View
    {

        void setDips (ArrayList<DipData> dipdata);



        void showErrorMessage (String message);

        void refresh ();
    }
}
