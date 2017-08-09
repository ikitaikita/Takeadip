package com.takeadip.takeadip.internal;

import android.util.Log;

import com.takeadip.takeadip.model.Dip;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by vik on 24/05/2017.
 */

public class AccessInterface {
    public static final String TAG = "AccessInterface";
    private static final String API_KEY = "&key=AIzaSyAFDR6eb6y8mwiSF-bWKOnxClGROq_xXko";
    public static String URL_GETALLDIPS = "http://esunescandalo.com/chapuzones/datos.class.php?tipo=mostrar_chapuzones";
    public static String URL_GETDIPSFILTER = "http://esunescandalo.com/chapuzones/datos.class.php?tipo=mostrar_tipo_chapuzones&filtro=PF";
    public static String URL_GETGOOGLE_PLACEID = "https://maps.googleapis.com/maps/api/geocode/json?latlng=42.537896,-6.518599&key=AIzaSyAFDR6eb6y8mwiSF-bWKOnxClGROq_xXko";
    public static String URL_GETPHOTOREFERENCE = "https://maps.googleapis.com/maps/api/place/details/json?placeid=ChIJU_VJZsGxMA0R6Pm4bxn68h0&key=AIzaSyAFDR6eb6y8mwiSF-bWKOnxClGROq_xXko";
    public static String URL_GETGOOGLEPHOTO ="https://maps.googleapis.com/maps/api/place/photo?maxwidth=300&photoreference=CmRbAAAAk_8FDQWnTUJ2sOCzGXkJm-vY1cLi6ZjDSpcMYanXLKl1IYyIjrfqho_B8Qov-v9bKwCyfQehsGEtP28KqayhtAREewqFJByuFAEHR1CSemZsjECxsiDwpdWDvfRhCDmVEhBXMx_ScFhOmqSRtsE8l8QEGhRrnlbZdpDNBsmVYDmCZZtU4Vm-DQ&key=AIzaSyAFDR6eb6y8mwiSF-bWKOnxClGROq_xXko";
    public static String URL_GETPHOTO="http://www.esunescandalo.com/apparboles/imagenes/avalchon.jpg";

    public static JSONObject sendJSON(String url, JSONObject jObjentrada, String method)
    {
        HttpURLConnection conn = null;
        OutputStreamWriter wr;

        StringBuilder result = new StringBuilder();
        URL urlObj;
        //JSONArray jArray;
        JSONObject jObj = null;
        //String paramsString;
        //StringBuilder  sbParams = new StringBuilder();


        if (method.equals("POST")) {
            // request method is POST
            try {
                urlObj = new URL(url);
                Log.i("ACCESO POST: ", urlObj.toString());

                conn = (HttpURLConnection) urlObj.openConnection();

                conn.setDoOutput(true);
                conn.setDoInput(true);

                conn.setRequestMethod("POST");

                //conn.setRequestProperty("Accept-Charset", "UTF-8");
                //conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setRequestProperty("Content-Type", "application/json");

                //conn.setRequestProperty("Accept", "application/json");

                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);

                conn.connect();

//                paramsString = sbParams.toString();
//                Log.i("paramsString: ", paramsString);
                if(jObjentrada != null)
                {
                    wr = new OutputStreamWriter(conn.getOutputStream());
                    wr.write(jObjentrada.toString());
                    wr.flush();
                }




            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (method.equals("UPDATE")) {
            // request method is POST
            Log.i("method: ", method);
            try {
                urlObj = new URL(url);
                Log.i("ACCESO POST: ", urlObj.toString());

                conn = (HttpURLConnection) urlObj.openConnection();

                conn.setDoOutput(true);
                conn.setDoInput(true);

                conn.setRequestMethod("PUT");

                //conn.setRequestProperty("Accept-Charset", "UTF-8");
                //conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setRequestProperty("Content-Type", "application/json");


                //conn.setRequestProperty("Accept", "application/json");

                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);

                conn.connect();

//                 paramsString = sbParams.toString();
//                 Log.i("paramsString: ", paramsString);

                wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(jObjentrada.toString());
                wr.flush();



            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(method.equals("DELETE")){
            // request method is GET



            try {
                Log.i("url: ", url);
                urlObj = new URL(url);


                conn = (HttpURLConnection) urlObj.openConnection();

                conn.setDoOutput(false);
                conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
                conn.setRequestMethod("DELETE");

                //conn.setRequestProperty("Accept-Charset", "UTF-8");
                // conn.setRequestProperty("Content-Type", "application/json");
                //conn.setRequestProperty("Accept", "application/json");

                conn.setConnectTimeout(15000);

                conn.connect();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        else if(method.equals("GET")){
            // request method is GET



            try {

                urlObj = new URL(url);
                Log.i("ACCESO GET: ", urlObj.toString());

                conn = (HttpURLConnection) urlObj.openConnection();

                conn.setDoOutput(false);

                conn.setRequestMethod("GET");

                //conn.setRequestProperty("Accept-Charset", "UTF-8");
                conn.setRequestProperty("Content-Type", "application/json");
                //conn.setRequestProperty("Accept", "application/json");

                conn.setConnectTimeout(15000);

                conn.connect();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        try {
            //Receive the response from the server
            //StringBuilder sb = new StringBuilder();
            int HttpResult = conn.getResponseCode();
            Log.i("HttpResult", String.valueOf(HttpResult));
            if (HttpResult == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(conn.getInputStream(), "utf-8"));
                String line = null;
                while ((line = br.readLine()) != null) {
                    Log.i("line: ", line);
                    result.append(line + "\n");
                }
                br.close();
                Log.i("", result.toString());
                System.out.println("" + result.toString());
            } else {


                Log.e("error:", conn.getResponseMessage());
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(conn.getErrorStream(), "utf-8"));
                String line = null;
                while ((line = br.readLine()) != null) {
                    Log.i("line: ", line);
                    result.append(line + "\n");
                }
                br.close();
                Log.i("", result.toString());
                System.out.println(conn.getResponseMessage());
            }

//            InputStream in = new BufferedInputStream(conn.getInputStream());
//            if(in != null)
//            {
//            	BufferedReader reader = new BufferedReader(new InputStreamReader(in));
//                //result = new StringBuilder();
//                String line;
//                while ((line = reader.readLine()) != null) {
//                	Log.i("line", line);
//                    result.append(line);
//                }
//            }





            //Log.d("JSON Parser", "result: " + result.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }

        conn.disconnect();

        // try parse the string to a JSON object
        try {

            jObj = new JSONObject(result.toString());

        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }

        // return JSON Object
        return jObj;
    }

    public static ArrayList<Dip> getListDips(JSONObject jsonObject)
    {
        ArrayList<Dip> dipList = new ArrayList<Dip>();


        try {
            //JSONObject jsonObject = new JSONObject(JSONResponse);

            JSONArray array_data = jsonObject.getJSONArray("datos");


            for(int i = 0; i < array_data.length(); i++) {
                JSONObject elem = array_data.getJSONObject(i);

                    Dip dip = new Dip();

                    String id = elem.getString("id");
                    String lat = elem.getString("latitud");
                    String lng = elem.getString("longitud");
                    String name = elem.getString("nombre");
                    String province = elem.getString("provincia");
                    String type = elem.getString("tipo");
                    String description = elem.getString("descripcion");
                    String address = elem.getString("direccion");

                    dip.setDip_id(id);
                    dip.setName(name);
                    dip.setAddress(address);
                    dip.setDescription(description);
                    dip.setLatitude(lat);
                    dip.setLongitude(lng);
                    dip.setProvince(province);
                    dip.setType(type);

                    dipList.add(dip);

                    Log.i(TAG, "dip added: " + dip.getName());
            }


        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return dipList;

    }

}
