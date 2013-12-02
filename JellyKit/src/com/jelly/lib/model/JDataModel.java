package com.jelly.lib.model;

import java.util.HashMap;
import java.util.Vector;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.jelly.lib.common.JMetaObject;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

/**
 * The JDataModal class is to provide data for all UI components. The JDataModal
 * is the bridge to connect UIViews and ContentProviders. You may add your data
 * source by implement ContentProvider interface. JDataModal could connect your
 * data source as well.
 * 
 * @author rockyswen@gmail.com
 * 
 */
public class JDataModel extends JMetaObject implements
	LoaderManager.LoaderCallbacks<Cursor>{

	public interface LoadListener {
		public void onLoadFinished(Cursor c);
		public void onLoaderReset();
	}
	
	private class LoadParameters {
		public String id;
		public String selection;
		public String sortOrder;
		public int limit;
		public int offset;
		public void reset() {
			id = null;
			selection = null;
			sortOrder = null;
			limit = -1;
			offset = 0;
		}
	}
	
	//TODO:¡¡We should be able to read the value from Provider later.
	//
	public static final String KEY_FIELD_NAME = "_id";
	
	protected int modelId;

	/**
	 * The cursor object from ContentProvider.
	 */
	private Cursor mCursor;

	/**
	 * The content provider uri.
	 */
	private String mProviderUri;

	/**
	 * The list of data fields
	 */
	private HashMap<String, JDataField> mDataFields = null;
	
	protected ContentValues mCV;
	
	protected LoadListener mLoadListener;
	
	protected LoadParameters mLoadParams;

	//////////////////////////////////
	// Public methods
	//////////////////////////////////

	public JDataModel(Context context, Element elem, int modelId) {
		mContext = context;
		mDataFields = new HashMap<String, JDataField>();
		mLoadParams = new LoadParameters();
		loadXmlElement(elem);
		this.modelId = modelId;
	}

	/**
	 * load xml definition element.
	 */
	public void loadXmlElement(Element elem) {
		// init attributes
		super.loadXmlElement(elem);
		mProviderUri = elem.getAttribute("contentprovider_uri");

		// init fields
		NodeList listOfFields = elem.getElementsByTagName("Field");
		for (int i = 0; i < listOfFields.getLength(); i++) {
			Element fldElem = (Element) listOfFields.item(i);
			String fldId = fldElem.getAttribute("id");
			JDataField fldObj = new JDataField(fldElem);
			mDataFields.put(fldId, fldObj);
		}
	}

	public String getProviderUri() {
		return mProviderUri;
	}

	/**
	 * Release the object and reference.
	 */
	public void release() {
		if (mCursor != null) {
			try {
				mCursor.close();
			} catch (Exception e) {
			}
			mCursor = null;
		}
	}
	
	/*
	 * async query using activity loader manager
	 * callback loadlistener instance when data loading is completed
	 */
	public void load(String selection, String sortOrder, 
				Activity act, LoadListener loadListener, boolean reload) {
		mLoadListener = loadListener;
		mLoadParams.reset();
		mLoadParams.selection = selection;
		mLoadParams.sortOrder = sortOrder;
		if (reload) {
			act.getLoaderManager().restartLoader(modelId, null, this);
		} else {
			act.getLoaderManager().initLoader(modelId, null, this);
		}
	}
	
	/*
	 * async query using activity loader manager
	 * callback loadlistener instance when data loading is completed
	 */
	public void load(String selection, String sortOrder, int limit, int offset, 
				Activity act, LoadListener loadListener, boolean reload) {
		mLoadListener = loadListener;
		mLoadParams.reset();
		mLoadParams.selection = selection;
		mLoadParams.sortOrder = sortOrder;
		if (reload) {
			act.getLoaderManager().restartLoader(modelId, null, this);
		} else {
			act.getLoaderManager().initLoader(modelId, null, this);
		}
	}
	
	/*
	 * async query using activity loader manager
	 * callback loadlistener instance when data loading is completed
	 * TODO: set id as member vars such that they can be use in onCreateLoader
	 */
	public void load(String id, 
				Activity act, LoadListener loadListener) {
		mLoadListener = loadListener;
		mLoadParams.reset();
		mLoadParams.id = id;
		act.getLoaderManager().initLoader(modelId, null, this);
	}
	
	public Cursor query(String selection, String sortOrder) {
		return query(selection, sortOrder, -1, 0);
	}

	public Cursor query(String selection, String sortOrder, int limit, int offset) {
		String[] projection = new String[mDataFields.size()];
		int i = 0;
		for (String key : mDataFields.keySet()) {
			projection[i] = key;
			i++;
		}
		try {
			// prepare uri like content://authority/table?limit=.&offset=.
			Uri providerUri = Uri.parse(mProviderUri).buildUpon()
					.appendQueryParameter("limit", String.valueOf(limit))
					.appendQueryParameter("offset", String.valueOf(offset))
					.build();
			ContentResolver resolver = mContext.getContentResolver();
			mCursor = resolver.query(providerUri, projection, selection, null,
					sortOrder);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mCursor;
	}
	
	public Cursor query(long id) {
		String[] projection = new String[mDataFields.size()];
		int i = 0;
		for (String key : mDataFields.keySet()) {
			projection[i] = key;
			i++;
		}
		try {
			// prepare uri like content://authority/table/id
			Uri providerUri = Uri.parse(mProviderUri+"/"+id);
			ContentResolver resolver = mContext.getContentResolver();
			mCursor = resolver.query(providerUri, projection, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mCursor;
	}

	/**
	 * Save the edited data into data source. FormView should call this method
	 * to submit data.
	 */
	public void save(ContentValues value) {
		Log.i("JDataModel", "save(ContentValues value) is called");
		update(value);
	}
	

	public void update(ContentValues values) {
		if (values == null) {
			return;
		}
		
		try {
			String itemUri = mProviderUri + "/" + values.getAsString("_id");
			Uri providerUri = Uri.parse(itemUri);
			ContentResolver resolver = mContext.getContentResolver();
			resolver.update(providerUri, values, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void insert(ContentValues values) {
		if (values == null) {
			return;
		}
		
		try {
			Uri providerUri = Uri.parse(mProviderUri);
			ContentResolver resolver = mContext.getContentResolver();
			resolver.insert(providerUri, values);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void delete(long id) {
		try {
			Uri providerUri = Uri.parse(mProviderUri + "/" + id);
			ContentResolver resolver = mContext.getContentResolver();
			resolver.delete(providerUri, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	////////////////////////////////////////////////////////
	// public methods that implement loadermanager methods
	////////////////////////////////////////////////////////

	// Creates a new loader after the initLoader () call
	// TODO: use mLoadParams in the load creation
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		String[] projection = null;
		String selection = mLoadParams.selection;
		String sort = mLoadParams.sortOrder;
		String providerUriStr = getProviderUri();
		Uri providerUri = Uri.parse(providerUriStr);
		if (mLoadParams.id != null) {
			providerUriStr += "/" + mLoadParams.id;
			providerUri = Uri.parse(providerUriStr);
		} else {
			providerUri = Uri.parse(mProviderUri).buildUpon()
					.appendQueryParameter("limit", String.valueOf(mLoadParams.limit))
					.appendQueryParameter("offset", String.valueOf(mLoadParams.offset))
					.build();
		}
		CursorLoader cursorLoader = new CursorLoader(mContext, providerUri,
				projection, selection, null, sort);
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor c) {
		//mCursorAdapter.swapCursor(c);
		mLoadListener.onLoadFinished(c);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// data is not available anymore, delete reference
		//mCursorAdapter.swapCursor(null);
		mLoadListener.onLoaderReset();
	}
}