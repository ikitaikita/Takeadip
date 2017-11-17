package com.takeadip.takeadip;

import com.takeadip.takeadip.data.model.DipData;
import com.takeadip.takeadip.data.model.DipsList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

import java.util.List;


/**
 * @author Victoria Marcos
 */

public interface DipsService {


    @GET("datos.class.php")
    Call<DipsList> getDips(@Query("tipo") String tipo);

   /* @GET("datos.class.php?tipo=mostrar_chapuzones")
    public void getDips(Callback<List<DipData>> callback)*/;

}

