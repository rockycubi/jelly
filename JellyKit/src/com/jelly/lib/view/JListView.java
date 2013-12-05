package com.jelly.lib.view;

import com.jelly.lib.model.JDataModel;
import com.jelly.lib.view.helper.JListViewHelper;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class JListView extends ListView {

	protected JListViewHelper mHelper;
	
	public JListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		initViewHelper(context, attrs);
	}

	public JListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	protected void initViewHelper(Context context, AttributeSet attrs) {
		mHelper = new JListViewHelper(this, attrs);
	}

	public void release() {
		// do necessary clean up
	}	

	public JDataModel getDataModel() {
		return mHelper.getDataModel();
	}
	
	public void loadData(String selection, String sortOrder) 
	{
		mHelper.setSelection(selection);
		mHelper.setSortOrder(sortOrder);
		mHelper.loadData();
	}

	public void reload() {
		mHelper.reload();
	}
}
