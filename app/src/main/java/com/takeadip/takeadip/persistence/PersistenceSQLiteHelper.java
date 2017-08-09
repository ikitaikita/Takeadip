package com.takeadip.takeadip.persistence;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Clase para la gestion de la base de datos. Extiende de SQLiteOpenHelper
 * Crea las tablas de datos correspondientes
 * @see SQLiteOpenHelper
 * @version 1.0
 * @author Victoria Marcos
 */
public class PersistenceSQLiteHelper extends SQLiteOpenHelper {

    //sentencias para crear las tablas de Poi y TABLE_VOTO
    String sqlCreate_MYFAVORUITES = "CREATE TABLE TABLE_FAV (dip_id TEXT PRIMARY KEY, name TEXT, desc TEXT, pic TEXT, latitude TEXT, longitude TEXT, type TEXT, province TEXT, address TEXT); ";

	//String sqlCreateIndex_pois_date = "CREATE INDEX index_pois_date ON Poi ( date ASC );";
	

 
    public PersistenceSQLiteHelper(Context context, String name,
                               CursorFactory factory, int version) {
        super(context, name, factory, version);
    }
 
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(sqlCreate_MYFAVORUITES);
        //db.execSQL(sqlCreateIndex_pois_date);

    }
 
    @Override
    public void onUpgrade(SQLiteDatabase db, int versionAnterior, int versionNueva) {

     
        db.execSQL( "DROP TABLE IF EXISTS TABLE_FAV ; ");

 

        onCreate(db);
    }
}
