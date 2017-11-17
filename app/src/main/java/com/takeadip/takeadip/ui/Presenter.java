package com.takeadip.takeadip.ui;

/**
 * Created by vik on 17/10/2017.
 */

public interface Presenter <T extends Presenter.View>{



    /**
     * Called to attach view to the Presenter
     * @param view view to attach
     */
    void onAttachView(T view);

    /**
     * Called to detach view from the Presenter
     */
    void onDetachView();

    /**
     * Interface represents View in MVP approach.
     */
    interface View {

    }
}
