package com.jelly.lib.view.helper;

import java.util.HashMap;

import com.jelly.lib.R;
import com.jelly.lib.view.adapter.JListCursorAdapter;
import com.jelly.lib.model.JDataModel;

import android.app.Activity;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

/**
 * 
 */
public class JListViewHelper extends JViewHelper implements 
	JDataModel.LoadListener {

	protected ListView mLv;
	protected JListCursorAdapter mCursorAdapter;
	protected int mRowLayout;
	protected int mHeaderRowLayout;
	protected int mFooterRowLayout;
	protected String mIntentQueryString;
	protected int mDataAutoLoad;
	protected ProgressDialog pd;
	protected String mSelection = null;
	protected String mSortOrder = null;
	
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
		
		// read UI attributes
		TypedArray a = mContext.obtainStyledAttributes(attrs,
				R.styleable.Jelly_UI);
		
		// read header_row_layout and footer_row_layout
		mHeaderRowLayout = a.getResourceId(R.styleable.Jelly_UI_header_row_layout, 0);
		mFooterRowLayout = a.getResourceId(R.styleable.Jelly_UI_footer_row_layout, 0);
		// read data loading parameters
		mIntentQueryString = a.getString(R.styleable.Jelly_UI_intent_querystring);
		mDataAutoLoad = a.getInt(R.styleable.Jelly_UI_data_autoload, 1);
		
		// read row_layout attribute
		mRowLayout = a.getResourceId(R.styleable.Jelly_UI_row_layout, 0);
		
		a.recycle();
		
		loadHeaderFooterView();

		mCursorAdapter = new JListCursorAdapter(mContext, mRowLayout, null, 0);
		mLv.setAdapter(mCursorAdapter);

		if (mDataAutoLoad > 0) {
			loadData();
		}
		initEventListeners();
		mLv.setItemsCanFocus(true);
	}
	
	protected void loadHeaderFooterView() {
		// load header and footer
		Activity act =  (Activity)mContext;
		if (mHeaderRowLayout > 1) {
			View header = act.getLayoutInflater().inflate(mHeaderRowLayout, null);
			mLv.addHeaderView(header);
		}
		if (mFooterRowLayout > 1) {
			View footer = act.getLayoutInflater().inflate(mFooterRowLayout, null);
			mLv.addFooterView(footer);
		}
	}
	
	public void loadData() {
		// show loading progress bar
		String loading_title = mContext.getResources().getString(R.string.loading_title);
		String loading_message = mContext.getResources().getString(R.string.loading_message);
		pd = ProgressDialog.show(mContext, loading_title, loading_message, true, true);
		// start data loading
		mDataModel.load(mSelection, mSortOrder, (Activity)mContext, this, false);
	}
	
	public void setSelection(String selection) {
		mSelection = selection;
	}
	
	public void setSortOrder(String sortOrder) {
		mSortOrder = sortOrder;
	}
	
	protected void initEventListeners() {
		/*
		 * add listener for OnItemClick event
		 */
		if (mOnItemClick != null) {
			mLv.setOnItemClickListener(new OnItemClickListener() {
				
				@Override
				public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
					mOutExtraData.clear();
					mOutExtraData.put("id", Long.toString(id));
					// get data from adapter and add in extra data
					//Object itemData = mLv.getAdapter().getItem(position);
		            //Cursor cursor = (Cursor) parent.getItemAtPosition(position);
		            // get data based on the model
					invokeMethod(mOnItemClick);
				}
			});
		}
	}
	
	public void reload() {
		// show loading progress bar
		String loading_title = mContext.getResources().getString(R.string.loading_title);
		String loading_message = mContext.getResources().getString(R.string.loading_message);
		pd = ProgressDialog.show(mContext, loading_title, loading_message, true, true);
		
		mDataModel.load(mSelection, mSortOrder, (Activity)mContext, this, true);
		
		// scroll to top
		mLv.setSelection(0);
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
		// hide the progress dialog
		if (pd != null) {
			pd.dismiss();
		}
	}

	@Override
	public void onLoaderReset() {
		// data is not available anymore, delete reference
		mCursorAdapter.swapCursor(null);
	}
}
