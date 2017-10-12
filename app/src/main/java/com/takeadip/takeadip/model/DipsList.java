package com.takeadip.takeadip.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by vik on 06/10/2017.
 */

public class DipsList {
    @SerializedName("datos")
    @Expose
    private List<DipData> datos = null;

    public List<DipData> getDatos() {
        return datos;
    }

    public void setDatos(List<DipData> datos) {
        this.datos = datos;
    }
}
