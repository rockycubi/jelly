package com.jelly.lib.view;

import com.jelly.lib.view.helper.JViewHelper;

import android.content.Context;
import android.util.AttributeSet;
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
public class JTextView extends TextView implements JDataView {

	protected JViewHelper mHelper;

	public JTextView(Context context, AttributeSet attrs) {
		super(context, attrs);

		mHelper = new JViewHelper(this, attrs);
	}

	public JTextView(Context context) {
		super(context);
	}

	public JTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
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
