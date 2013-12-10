package com.jelly.lib.content;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.jelly.lib.common.JResource;
import com.jelly.lib.content.sqlite.JDatabase;
import com.jelly.lib.content.sqlite.JTable;
import com.jelly.lib.content.sqlite.JSQLiteHelper;

import android.R;
import android.annotation.SuppressLint;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

/** 
 * The data source provider for SQLite.
 */
public class JSQLiteContentProvider extends ContentProvider {
	
	//protected String PROVIDER_XML = "raw/data_provider";
	protected String DATABASE_XML = "raw/todo_database";
	protected String AUTHORITY = "com.jelly.app.contentprovider";
	
	protected JDatabase mDatabase;
	//protected ArrayList<String> mBasePaths;
	protected UriMatcher URI_MATCHER;
	protected HashMap<Integer, String> mUriTypes;
	protected HashMap<Integer, String> mUriTables;
	protected HashMap<Integer, Boolean> mUriItemFlags;
	
	protected JSQLiteHelper mDBHelper;
	
	/*
	 * add database variable, JDatabase mDatabase
	 * add uri_mapping (base_path -> [table,type,isItem]), HashMap <String, Object>.
	 * add method getUriMappingFromDatabase(), it set UriMatcher, UriMapping
	 * add method checkColumns()
	 */
	
	@Override
	public boolean onCreate() {
		// init the database
		init();
		return false;
	}
	
	@SuppressLint("UseSparseArrays")
	protected void init() {
		// init variables
		mUriTypes = new HashMap<Integer, String>();
		mUriTables = new HashMap<Integer, String>();
		mUriItemFlags = new HashMap<Integer, Boolean>();
		
		// initiate database
		Document dom = JResource.getInstance(getContext()).loadXmlResource(getDatabaseXmlRes());
		Element elem = dom.getDocumentElement();
		mDatabase = new JDatabase(getContext(), elem);
		
		// populate content provider variables from database
		initProviderVarsFromDB();
		
		// initiate helper object
		initDBHelper();
	}
	
	protected void initDBHelper() {
		mDBHelper = new JSQLiteHelper(getContext(),mDatabase.getDatabaseName(),mDatabase.getDatabaseVersion());
		mDBHelper.setSchemaSQL(mDatabase.getSchemaSQL());
	}
	
	/*
	 * use database tables info to populate provider necessary variables
	 */
	@SuppressLint("UseValueOf")
	protected void initProviderVarsFromDB() {
		ArrayList<String> tableNames = new ArrayList<String>();
		HashMap<String, JTable> tables = mDatabase.getTables();
		for (String key: tables.keySet()) {
			tableNames.add(tables.get(key).mName);
		}
		// populate URI_MATCHER, uri types, uri tables, uri item flag
		URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
		int uriIndex = 101;
		for (int i=0; i<tableNames.size(); i++) {
			Integer ikey = new Integer(uriIndex);
			String table = tableNames.get(i);
			URI_MATCHER.addURI(AUTHORITY,table,uriIndex);
			Log.i("JSQLiteContentProvider", "addURI("+AUTHORITY+","+table+","+uriIndex+")");
			String contentType = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + table;
			mUriTypes.put(ikey, contentType);
			mUriTables.put(ikey, table);
			mUriItemFlags.put(ikey, Boolean.FALSE);
			uriIndex++;
			
			Integer ikey2 = new Integer(uriIndex);
			URI_MATCHER.addURI(AUTHORITY,table+"/#",uriIndex);
			Log.i("JSQLiteContentProvider", "addURI("+AUTHORITY+","+table+"/#,"+uriIndex+")");
			String contentItemType = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + table;
			mUriTypes.put(ikey2, contentItemType);
			mUriTables.put(ikey2, table);
			mUriItemFlags.put(ikey2, Boolean.TRUE);
			uriIndex++;
		}
	}
	
	protected String getDatabaseXmlRes() {
		return DATABASE_XML;
	}
	
	@Override
	public String getType(Uri uri) {
		int uriIndex = URI_MATCHER.match(uri);
		String type = mUriTypes.get(uriIndex);
		if (type == null) {
			throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
		return type;
	}
	
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		// get database instance
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
		
		// find table per the uri match
		int uriIndex = URI_MATCHER.match(uri);
		Log.i("JSQLiteContentProvider", "URI match "+uri.toString()+" as "+uriIndex);
		String table = mUriTables.get(uriIndex);
		if (table == null) {
			throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
		// Check if the caller has requested a column which does not exists
		checkColumns(projection, table);
		
		// set the table
		builder.setTables(table);
		
		// check if the uri is for item or list
		Boolean isItemUri = mUriItemFlags.get(uriIndex);
		if (isItemUri) {
			// get id column of the table
			ArrayList<String> columns = mDatabase.getTableColumns(table);
			String idColumn = columns.get(0);
			// limit query to one row at most:
			builder.appendWhere(idColumn + "=" + uri.getLastPathSegment());
		}
		
		// prepare the limit and offset
		String limit = uri.getQueryParameter("limit");
		String offset = uri.getQueryParameter("offset");
		String limitStr = null;
		if (limit != null) {
			if (limit.equals("-1")) {
				limitStr = null;
			}
			else {
				limitStr = " LIMIT " + limit;
				if (offset != null) {
					limitStr += "," + offset;
				}
			}
		}
		
		// make the query
		Cursor cursor = builder.query(db, projection, selection,
		        selectionArgs, null, null, sortOrder, limitStr);
		int count = cursor.getCount();
	    // Make sure that potential listeners are getting notified
	    cursor.setNotificationUri(getContext().getContentResolver(), uri);
	    
		return cursor;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// get database instance
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
		
		// find table per the uri match
		int uriIndex = URI_MATCHER.match(uri);
		String table = mUriTables.get(uriIndex);
		if (table == null) {
			throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
		
		long id = db.insert(table, null, values);
		
		getContext().getContentResolver().notifyChange(uri, null);
	    return Uri.parse(table + "/" + id);
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// get database instance
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		int rowsUpdated = 0;
		// find table per the uri match
		int uriIndex = URI_MATCHER.match(uri);
		String table = mUriTables.get(uriIndex);
		if (table == null) {
			throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
		
		Boolean isItemUri = mUriItemFlags.get(uriIndex);
		if (!isItemUri) {
			rowsUpdated = db.update(table, 
						values, 
						selection, 
						selectionArgs);
		}
		else {
			// get id column of the table
			ArrayList<String> columns = mDatabase.getTableColumns(table);
			String idColumn = columns.get(0);
			String idStr = uri.getLastPathSegment();
			String where = idColumn + " = " + idStr;
			if (!TextUtils.isEmpty(selection)) {
				where += " AND " + selection;
			}
			rowsUpdated = db.update(table,
						values,
						where,
						selectionArgs);
		}
		
		getContext().getContentResolver().notifyChange(uri, null);
	    return rowsUpdated;
	}
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// get database instance
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		int rowsDeleted = 0;
		// find table per the uri match
		int uriIndex = URI_MATCHER.match(uri);
		String table = mUriTables.get(uriIndex);
		if (table == null) {
			throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
		
		Boolean isItemUri = mUriItemFlags.get(uriIndex);
		if (!isItemUri) {
			rowsDeleted = db.delete(table, 
						selection, 
						selectionArgs);
		}
		else {
			// get id column of the table
			ArrayList<String> columns = mDatabase.getTableColumns(table);
			String idColumn = columns.get(0);
			String idStr = uri.getLastPathSegment();
			String where = idColumn + " = " + idStr;
			if (!TextUtils.isEmpty(selection)) {
				where += " AND " + selection;
			}
			rowsDeleted = db.delete(table,
						where,
						selectionArgs);
		}
		
		getContext().getContentResolver().notifyChange(uri, null);
	    return rowsDeleted;
	}

	/*
	 * validate that a query only requests valid columns. 
	 */
	private void checkColumns(String[] projection, String table) {
		ArrayList<String> available = mDatabase.getTableColumns(table);
		if (projection != null) {
			HashSet<String> requestedColumns = new HashSet<String>(Arrays.asList(projection));
			HashSet<String> availableColumns = new HashSet<String>(available);
			// Check if all columns which are requested are available
			if (!availableColumns.containsAll(requestedColumns)) {
				throw new IllegalArgumentException("Unknown columns in projection");
			}
		}
	}

}