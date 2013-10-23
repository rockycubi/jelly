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
		
		mHelper = new JListViewHelper(this, attrs);
	}

	public JListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void release() {
		// do necessary clean up
	}	

	public JDataModel getDataModel() {
		return mHelper.getDataModel();
	}
	
	public void reload() {
		mHelper.reload();
	}

}
