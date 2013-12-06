package com.jelly.lib.view.helper;

import java.lang.reflect.Method;
import java.util.ArrayList;

import com.jelly.lib.model.JDataModel;
import com.jelly.lib.view.JFormView;

import android.app.Activity;
import android.app.LoaderManager;
import android.net.Uri;
import android.os.Bundle;
import android.database.Cursor;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Spinner;

/**
 * TODO implements LoaderManager.LoaderCallbacks<Cursor>
 */
public class JFormViewHelper extends JViewHelper implements 
	JDataModel.LoadListener {
	
	protected JFormView mFv;
	protected ArrayList<String> mFromFields;
	protected ArrayList<Integer> mToResIds;
	protected int[] mFromIndexes;
	protected String _id;

	public JFormViewHelper(View v, AttributeSet attrs) {
		super(v, attrs);
		
		mFromFields = new ArrayList<String>();
		mToResIds = new ArrayList<Integer>();
		
		mFv = (JFormView)v;
		
		_id = (String)getInExtraData("id");
		
		// start data loading
		mDataModel.load(_id, (Activity)mContext, this);
	}
	
	public ContentValues getContentValues() {
		ContentValues cv = new ContentValues();
		for (int i=0; i<mFromFields.size(); i++) {
			String field = mFromFields.get(i);
			View v = mFv.findViewById(mToResIds.get(i));
			if (v instanceof TextView) {
				String value = getViewText((TextView)v);
				cv.put(field, value);
			}
			else if (v instanceof Spinner) {
				String value = getViewText((Spinner)v);
				cv.put(field, value);
			}
		}
		if (_id != "" && _id != null) {
			cv.put("_id", _id);
		}
		return cv;
	}
	
	public void initChildViewFields(ViewGroup parent) {
		// find all item views with data_field attribute
		if (mFromFields.size() == 0) {
			findFromAndTo((ViewGroup)mFv);
		}
	}
	
	protected void findFromAndTo(ViewGroup parent) {
		// scan all children views
		int count = parent.getChildCount();
		for (int i = 0; i < count; i++) {
            View child = parent.getChildAt(i);
            if(child instanceof ViewGroup && !(child instanceof Spinner)) {
            	findFromAndTo((ViewGroup)child);
            }
            else if (child != null) {
            	// check if this child has getFieldName method
            	Class<?>[] paramTypes = new Class[0];
            	try {
            		Method m = child.getClass().getMethod("getFieldName", paramTypes);
            		if (m != null) {
            			String field = (String) m.invoke(child);
            			// set the from and to list
            			mFromFields.add(field);
            			mToResIds.add(new Integer(child.getId()));
            		}
            	} catch (Exception e) {
            		// do nothing
            	}
            }
		}
	}
	
	protected void populateForm(Cursor c) {
		c.moveToFirst();
		
		Class<?>[] paramTypes = new Class[1];
    	paramTypes[0] = String.class;
    	
		for (int i=0; i<mFromFields.size(); i++) {
			String field = mFromFields.get(i);
			int columnIndex = c.getColumnIndexOrThrow(field);
			String text = c.getString(columnIndex);
			View v = mFv.findViewById(mToResIds.get(i));
            // check if the view has setFieldValue method
        	try {
        		Method m = v.getClass().getMethod("setFieldValue", paramTypes);
        		if (m != null) {
        			m.invoke(v, text);
        			continue;
        		}
        	} catch (Exception e) {
        		// do nothing
        	}
			if (v instanceof TextView) {
                setViewText((TextView) v, text);
            } else if (v instanceof ImageView) {
                setViewImage((ImageView) v, text);
            } else if (v instanceof Spinner) {
            	setViewText((Spinner) v, text);
            } else {
                throw new IllegalStateException(v.getClass().getName() + " is not a " +
                        " view that can be bounds by this SimpleCursorAdapter");
            }
		}
	}
	
	public String getViewText(TextView v) {
        return v.getText().toString();
    }
	
	public void setViewText(TextView v, String text) {
        v.setText(text);
    }
	
	public String getViewText(Spinner v) {
        return v.getSelectedItem().toString();
    }
	
	public void setViewText(Spinner v, String text) {
        for (int i = 0; i < v.getCount(); i++) {
            String s = (String) v.getItemAtPosition(i);
            if (s.equalsIgnoreCase(text)) {
            	v.setSelection(i);
            }
        }
    }
	
    public void setViewImage(ImageView v, String value) {
        try {
            v.setImageResource(Integer.parseInt(value));
        } catch (NumberFormatException nfe) {
            v.setImageURI(Uri.parse(value));
        }
    }
    
	////////////////////////////////////////////////////////
	// public methods that implement LoadListener methods
	////////////////////////////////////////////////////////
	
	@Override
	public void onLoadFinished(Cursor c) {
		populateForm(c);
	}
	
	@Override
	public void onLoaderReset() {
		// do nothing
	}
}
