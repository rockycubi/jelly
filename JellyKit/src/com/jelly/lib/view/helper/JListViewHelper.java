package com.jelly.lib.view.helper;

import java.util.HashMap;

import com.jelly.lib.R;
import com.jelly.lib.view.adapter.JListCursorAdapter;
import com.jelly.lib.model.JDataModel;

import android.app.Activity;
import android.database.Cursor;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

/**
 * TODO implements LoaderManager.LoaderCallbacks<Cursor>
 */
public class JListViewHelper extends JViewHelper implements 
	JDataModel.LoadListener {

	protected ListView mLv;
	protected JListCursorAdapter mCursorAdapter;
	protected int mRowLayout;
	
	/*
	 * onListItemClick action
	 */
	public String mOnItemClick;

	public JListViewHelper(View v, AttributeSet attrs) {
		super(v, attrs);
		mLv = (ListView) v;
		
		/*
		 * read event handler from xml definition
		 */
		TypedArray aEvents = mContext.obtainStyledAttributes(attrs, 
				R.styleable.Jelly_Event);
		try {
			mOnItemClick = aEvents.getString(R.styleable.Jelly_Event_onItemClick);
		} finally {
			aEvents.recycle();
		}
		
		// read row_layout attribute
		TypedArray a = mContext.obtainStyledAttributes(attrs,
				R.styleable.Jelly_UI);
		mRowLayout = a.getResourceId(R.styleable.Jelly_UI_row_layout, 0);

		mCursorAdapter = new JListCursorAdapter(mContext, mRowLayout, null, 0);
		mLv.setAdapter(mCursorAdapter);
		
		// start data loading
		mDataModel.load(null, null, (Activity)mContext, this);
		
		a.recycle();
		
		initEventListeners();
	}
	
	protected void initEventListeners() {
		/*
		 * add listener for OnItemClick event
		 */
		if (mOnItemClick != null) {
			mLv.setOnItemClickListener(new OnItemClickListener() {
				
				@Override
				public void onItemClick(AdapterView<?> l, View v, int position, long id) {
					mOutExtraData.clear();
					mOutExtraData.put("id", Long.toString(id));
					invokeMethod(mOnItemClick);
				}
			});
		}
	}
	
	public void reload() {
		mDataModel.load(null, null, (Activity)mContext, this);
	}
	
	/*
	 * method to get additional data in method invocation
	 */
	protected HashMap<String, String> getExtraData() {
		// for a form, it provides data inputs from child views
		// for a list, it provides id of a selected list item
		HashMap<String, String> data = new HashMap<String, String>();
		
		return data;
	}

	////////////////////////////////////////////////////////
	// public methods that implement LoadListener methods
	////////////////////////////////////////////////////////

	@Override
	public void onLoadFinished(Cursor c) {
		mCursorAdapter.swapCursor(c);
	}

	@Override
	public void onLoaderReset() {
		// data is not available anymore, delete reference
		mCursorAdapter.swapCursor(null);
	}
}
