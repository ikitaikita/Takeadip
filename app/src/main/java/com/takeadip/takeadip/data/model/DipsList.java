package com.takeadip.takeadip.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vik on 06/10/2017.
 */

public class DipsList {
    @SerializedName("datos")
    @Expose
    private ArrayList<DipData> datos = new ArrayList();

    public ArrayList<DipData> getDatos() {
        return datos;
    }

    public void setDatos(ArrayList<DipData> datos) {
        this.datos = datos;
    }
}
