package com.takeadip.takeadip;

import com.takeadip.takeadip.model.DipData;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

import java.util.List;

/**
 * Created by vik on 06/10/2017.
 */

public interface DipsService {

    @GET("datos.class.php")
    Call<List<DipData>> getDips(@Query("tipo") String tipo);

}

