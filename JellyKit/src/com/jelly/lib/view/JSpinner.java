package com.jelly.lib.view;

import com.jelly.lib.view.helper.JViewHelper;

import android.content.ContentValues;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.Spinner;

/**
 * <pre>
 *   <com.jelly.liv.view.JSpinner
 *    		android:id="@+id/spinnerTest"
 *     		android:layout_width="fill_parent"
 *      	android:layout_height="wrap_content"
 *      	android:entries="@array/priorities"
 *       	jelly:data_field="category">
 *    </com.jelly.lib.view.JSpinner>
 * 
 * </pre>
 * 
 * @author rockyswen@gmail.com
 * 
 */
public class JSpinner extends Spinner implements JDataView {

	protected JViewHelper mHelper;

	public JSpinner(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		mHelper = new JViewHelper(this, attrs);
	}

	public JSpinner(Context context) {
		super(context);
	}

	public JSpinner(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public String getFieldName() {
		return mHelper.mFieldName;
	}
}
