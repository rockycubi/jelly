package com.jelly.lib.content.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class JSQLiteHelper extends SQLiteOpenHelper {
	
	protected String mDatabaseName = "";
	protected int mDatabaseVersion = 1;
	protected String mSchemaSQL;

	public JSQLiteHelper(Context context, String dbName, int dbVersion) {
		super(context, dbName, null, dbVersion);
		mDatabaseName = dbName;
		mDatabaseVersion = dbVersion;
	}
	
	public void setSchemaSQL(String schemaSQL) {
		mSchemaSQL = schemaSQL;
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		// execute schema sql
		try {
		     String[] queries = mSchemaSQL.split(";");
		     for(String query : queries){
		    	 database.execSQL(query.trim());
		     }
		} catch (Exception e) {
			 Log.e("JSQLiteHelper", e.getMessage());
		}
		//database.execSQL(mSchemaSQL);
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		// TODO: execute upgrade sql
	}

}
