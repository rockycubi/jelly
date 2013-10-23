package com.jelly.lib.view;

import com.jelly.lib.model.JDataModel;
import com.jelly.lib.view.helper.JFormViewHelper;

import android.content.ContentValues;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class JFormView extends LinearLayout {

	protected JFormViewHelper mHelper;
	
	public JFormView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		mHelper = new JFormViewHelper(this, attrs);
	}
	
	public ContentValues getContentValue() {
		return mHelper.getContentValues();
	}

	public void release() {
		// TODO Auto-generated method stub
		
	}

	public JDataModel getDataModel() {
		return mHelper.getDataModel();
	}
	
	/**
     * Position all children within this layout.
     */
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
    	super.onLayout(changed, left, top, right, bottom);
    	
    	mHelper.initChildViewFields(this);
    }
}
