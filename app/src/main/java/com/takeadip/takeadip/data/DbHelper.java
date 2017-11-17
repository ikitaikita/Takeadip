package com.takeadip.takeadip.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.takeadip.takeadip.di.ApplicationContext;
import com.takeadip.takeadip.di.DatabaseInfo;
import com.takeadip.takeadip.data.model.DipData;

import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by vik on 16/10/2017.
 */

@Singleton
public class DbHelper extends SQLiteOpenHelper {

    private static final String DBNAME = "DBTAKEADIP";
    private static int CURRENT_BBDD_VERSION = 1;

    // FAVOURITES TABLE
    public static final String FAVDIP_TABLE_NAME = "TABLE_FAV";

    public static final String FAVDIP_COLUMN_ID ="dip_id";
    public static final String FAVDIP_COLUMN_NAME ="name";
    public static final String FAVDIP_COLUMN_DESC ="desc";
    public static final String FAVDIP_COLUMN_PIC ="pic";
    public static final String FAVDIP_COLUMN_LATITUDE ="latitude";
    public static final String FAVDIP_COLUMN_LONGITUDE="longitude";
    public static final String FAVDIP_COLUMN_TYPE ="type";
    public static final String FAVDIP_COLUMN_PROVINCE ="province";
    public static final String FAVDIP_COLUMN_ADDRESS ="address";
    public static final String FAVDIP_COLUMN_DISTANCE ="distance";


    @Inject
    public DbHelper(@ApplicationContext Context context,
                    @DatabaseInfo String dbName,
                    @DatabaseInfo Integer version) {
        super(context, dbName, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        tableCreateStatements(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + FAVDIP_TABLE_NAME);
        onCreate(db);
    }

    private void tableCreateStatements(SQLiteDatabase db) {
        try {
            db.execSQL(
                    "CREATE TABLE IF NOT EXISTS "
                            + FAVDIP_TABLE_NAME + "("
                            + FAVDIP_COLUMN_ID + " TEXT PRIMARY KEY, "
                            + FAVDIP_COLUMN_NAME + " TEXT, "
                            + FAVDIP_COLUMN_DESC + " TEXT, "
                            + FAVDIP_COLUMN_PIC + " TEXT, "
                            + FAVDIP_COLUMN_LATITUDE + " TEXT, "
                            + FAVDIP_COLUMN_LONGITUDE + " TEXT, "
                            + FAVDIP_COLUMN_TYPE + " TEXT, "
                            + FAVDIP_COLUMN_PROVINCE + " TEXT, "
                            + FAVDIP_COLUMN_ADDRESS + " TEXT, "
                            + FAVDIP_COLUMN_DISTANCE + " TEXT ) "
            );

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Insert nre favourite dip into TABLE_FAVOURITE
     * @param d, dip to insert
     */
    public long insertDip (DipData d) throws Exception {

        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(FAVDIP_COLUMN_ID, d.getId());
            contentValues.put(FAVDIP_COLUMN_NAME, d.getNombre());
            contentValues.put(FAVDIP_COLUMN_DESC, d.getDescripcion());
            contentValues.put(FAVDIP_COLUMN_PIC, d.getUrlfoto());
            contentValues.put(FAVDIP_COLUMN_LATITUDE, d.getLatitud());
            contentValues.put(FAVDIP_COLUMN_LONGITUDE, d.getLongitud());
            contentValues.put(FAVDIP_COLUMN_TYPE, d.getTipo());
            contentValues.put(FAVDIP_COLUMN_PROVINCE, d.getProvincia());
            contentValues.put(FAVDIP_COLUMN_ADDRESS, d.getDireccion());
            contentValues.put(FAVDIP_COLUMN_DISTANCE, d.getDistancia());
            return db.insertWithOnConflict(FAVDIP_TABLE_NAME, null, contentValues,SQLiteDatabase.CONFLICT_REPLACE);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public long deleteDip(String id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(FAVDIP_TABLE_NAME, FAVDIP_COLUMN_ID + "=" + id, null);
        //db.execSQL("delete from TABLE_FAV where dip_id='"+id+"'");
        //db.close();
    }

    /*
     * get Dip list from TABLE_FAV which are the favourite dips
     * @param context
     * @return list
     */
    public ArrayList<DipData> getFavourites() {

        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<DipData> l_favourites = new ArrayList<DipData>();

        // Selcccion de todas las Query
        Cursor cursor = db.rawQuery("select * from TABLE_FAV ", null);

        if (cursor.moveToFirst()) {
            do {
                String dip_id = cursor.getString(0);
                String name = cursor.getString(1);
                String desc = cursor.getString(2);
                String pic = cursor.getString(3);
                String latitude = cursor.getString(4);
                String longitude = cursor.getString(5);
                String type = cursor.getString(6);
                String province = cursor.getString(7);
                String address = cursor.getString(8);
                String distance = cursor.getString(9);
                DipData d = new DipData();
                d.setId(dip_id);
                d.setNombre(name);
                d.setDescripcion(desc);
                d.setUrlfoto(pic);
                d.setLatitud(latitude);
                d.setLongitud(longitude);
                d.setTipo(type);
                d.setProvincia(province);
                d.setDireccion(address);
                d.setDistancia(distance);
                l_favourites.add(d);
            } while (cursor.moveToNext());
        }
        db.close();

        return l_favourites;

    }

    public boolean isFavourite(String id) {


        SQLiteDatabase db = this.getReadableDatabase();


        Cursor cursor = db.rawQuery(
                "SELECT * FROM "
                        + FAVDIP_TABLE_NAME
                        + " WHERE "
                        + FAVDIP_COLUMN_ID
                        + " = ",
                new String[]{id + ""});

        if (cursor != null) {
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                db.close();
                return true;
            } else {
                db.close();
                return false;
            }
        } else{
            db.close();
            return false;}

    }



}
