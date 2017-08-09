package com.takeadip.takeadip.persistence;

import java.util.ArrayList;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.takeadip.takeadip.model.Dip;

/**
 * Clase para la gestion de la base de datos 
 * @version 1.0
 * @author Victoria Marcos
 */
public class PersistenceSQL {

	private static final String DBNAME = "DBTAKEADIP";
	
	
	private static int CURRENT_BBDD_VERSION = 1;




	/**
	 * Insert nre favourite dip into TABLE_FAVOURITE
	 * @param context
	 * @param d, dip to insert
	 */
	public static void insertDip(Context context, Dip d) {

		
		PersistenceSQLiteHelper usdbh = new PersistenceSQLiteHelper(context,
				DBNAME, null, CURRENT_BBDD_VERSION);

		SQLiteDatabase db = usdbh.getWritableDatabase();

		// Si hemos abierto correctamente la base de datos
		if (db != null) {
			db.beginTransaction();
			try {
					ContentValues cv = new ContentValues();
					
				

					cv.put("dip_id", d.getDip_id());
					cv.put("name", d.getName());
					cv.put("desc", d.getDescription());
					cv.put("pic", d.getPic());
					cv.put("latitude", d.getLatitude());
					cv.put("longitude", d.getLongitude());
					cv.put("type", d.getType());
					cv.put("province", d.getProvince());
					cv.put("address", d.getAddress());
					
					db.insertWithOnConflict("TABLE_FAV", null, cv, SQLiteDatabase.CONFLICT_REPLACE);

					db.setTransactionSuccessful();
			} finally {
				db.endTransaction();
			}

			// Cerramos la base de datos
			db.close();
		}
	}

	public static void deleteDip(String id, Context context)
	{
		PersistenceSQLiteHelper usdbh = new PersistenceSQLiteHelper(context,
				DBNAME, null, CURRENT_BBDD_VERSION);

		SQLiteDatabase db = usdbh.getWritableDatabase();
		db.execSQL("delete from TABLE_FAV where dip_id='"+id+"'");
		db.close();
	}


	/*
	 * get Dip list from TABLE_FAV which are the favourite dips
	 * @param context
	 * @return list
	 */
	public static ArrayList<Dip> getFavourites(Context ctx) {
		// Creamos una lista de enteros
		
		PersistenceSQLiteHelper usdbh = new PersistenceSQLiteHelper(
				ctx, DBNAME, null, CURRENT_BBDD_VERSION);

		SQLiteDatabase db = usdbh.getReadableDatabase();
		ArrayList<Dip> l_favourites = new ArrayList<Dip>();

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
				Dip d = new Dip();
				d.setDip_id(dip_id);
				d.setName(name);
				d.setDescription(desc);
				d.setPic(pic);
				d.setLatitude(latitude);
				d.setLongitude(longitude);
				d.setType(type);
				d.setProvince(province);
				d.setAddress(address);
				l_favourites.add(d);
			} while (cursor.moveToNext());
		}
		db.close();

		return l_favourites;

	}

	public static boolean isFavourite(String id, Context context) {
		PersistenceSQLiteHelper usdbh = new PersistenceSQLiteHelper(
				context, DBNAME, null, CURRENT_BBDD_VERSION);

		SQLiteDatabase db = usdbh.getReadableDatabase();


		Cursor cursor = db.rawQuery("select * from TABLE_FAV where dip_id="+ id, null);

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
