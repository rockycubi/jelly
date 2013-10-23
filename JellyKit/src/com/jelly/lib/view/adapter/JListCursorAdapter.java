package com.jelly.lib.view.adapter;

import java.lang.reflect.Method;
import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.widget.ResourceCursorAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class JListCursorAdapter extends ResourceCursorAdapter {
	
	protected ArrayList<String> mFromFields;
	protected ArrayList<Integer> mToResIds;
	protected int[] mFromIndexes;
	
	public JListCursorAdapter(Context context, int layout, Cursor c, int flags) {
		super(context, layout, c, flags);

		mFromFields = new ArrayList<String>();
		mToResIds = new ArrayList<Integer>();
	}

	// when the view will be created for first time
	@Override
	public View newView(Context context, Cursor c, ViewGroup parent) {
		View v = super.newView(context, c, parent);
		
		if (mFromFields.size() == 0) {
			// fill the mapping between cursor fields and view resources
			if(v instanceof ViewGroup) {
				findFromAndTo(c, (ViewGroup)v);
				findColumns(c, mFromFields);
			}
		}
		return v;
	}
	
	@Override
    public void bindView(View view, Context context, Cursor cursor) {
        final int count = mToResIds.size();

        for (int i = 0; i < count; i++) {
            final View v = view.findViewById(mToResIds.get(i));
            if (v != null) {
                String text = cursor.getString(mFromIndexes[i]);
                if (text == null) {
                    text = "";
                }

                if (v instanceof TextView) {
                    setViewText((TextView) v, text);
                } else if (v instanceof ImageView) {
                    setViewImage((ImageView) v, text);
                } else {
                    throw new IllegalStateException(v.getClass().getName() + " is not a " +
                            " view that can be bounds by this SimpleCursorAdapter");
                }
            }
        }
    }
	
	protected void findFromAndTo(Cursor c, ViewGroup parent) {
		// scan all children views
		int count = parent.getChildCount();
		for (int i = 0; i < count; i++) {
            View child = parent.getChildAt(i);
            if(child instanceof ViewGroup) {
            	findFromAndTo(c, (ViewGroup)child);
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
	
	/**
     * Create a map from an array of strings to an array of column-id integers in cursor c.
     * If c is null, the array will be discarded.
     *
     * @param c the cursor to find the columns from
     * @param from the Strings naming the columns of interest
     */
    protected void findColumns(Cursor c, ArrayList<String> from) {
        if (c != null) {
            int i;
            int count = from.size();
            if (mFromIndexes == null || mFromIndexes.length != count) {
            	mFromIndexes = new int[count];
            }
            for (i = 0; i < count; i++) {
            	mFromIndexes[i] = c.getColumnIndexOrThrow(from.get(i));
            }
        } else {
        	mFromIndexes = null;
        }
    }
    
    /**
     * Called by bindView() to set the text for a TextView.
     * 
     * Intended to be overridden by Adapters that need to filter strings
     * retrieved from the database.
     * 
     * @param v TextView to receive text
     * @param text the text to be set for the TextView
     */    
    public void setViewText(TextView v, String text) {
        v.setText(text);
    }
    
    /**
     * Called by bindView() to set the image for an ImageView
     *
     * By default, the value will be treated as an image resource. If the
     * value cannot be used as an image resource, the value is used as an
     * image Uri.
     *
     * Intended to be overridden by Adapters that need to filter strings
     * retrieved from the database.
     *
     * @param v ImageView to receive an image
     * @param value the value retrieved from the cursor
     */
    public void setViewImage(ImageView v, String value) {
        try {
            v.setImageResource(Integer.parseInt(value));
        } catch (NumberFormatException nfe) {
            v.setImageURI(Uri.parse(value));
        }
    }

}
