package com.jelly.lib.view;

import com.jelly.lib.view.helper.JViewHelper;
import com.squareup.picasso.Picasso;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * <pre>
 *   <com.jelly.lib.view.JTextView
 *    		android:id="@+id/textview"
 *     		android:layout_width="fill_parent"
 *      	android:layout_height="wrap_content"
 *       	jelly:data_model="raw/datamodel"
 *       	jelly:data_field="lastname">
 *    </com.jelly.lib.view.JTextView>
 * 
 * </pre>
 * 
 * @author rockyswen@gmail.com
 * 
 */
public class JImageView extends ImageView implements JDataView {

	protected JViewHelper mHelper;

	public JImageView(Context context, AttributeSet attrs) {
		super(context, attrs);

		mHelper = new JViewHelper(this, attrs);
	}

	public JImageView(Context context) {
		super(context);
	}

	public JImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	public void setImageResource(int resId) {
		super.setImageResource(resId);
	}
	
	public void setImageURI(Uri uri) {
		// use picasso to load image
    	Picasso.with(getContext()).load(uri.toString()).into(this);
	}
	
	/**
	 * It is called by JDataModal and container view.
	 */
	public String getFieldName() {
		return mHelper.mFieldName;
	}

	public void release() {
	}
}
