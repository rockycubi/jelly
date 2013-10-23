package com.jelly.lib.view;

import com.jelly.lib.view.helper.JViewHelper;

import android.content.ContentValues;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * <pre>
 *   <com.jelly.liv.view.JEditText
 *    		android:id="@+id/etTest"
 *     		android:layout_width="fill_parent"
 *      	android:layout_height="wrap_content"
 *       	jelly:data_model="raw/datamodal"
 *       	jelly:data_field="lastname">
 *    </com.jelly.lib.view.JEditText>
 * 
 * </pre>
 * 
 * @author rockyswen@gmail.com
 * 
 */
public class JEditText extends EditText implements JDataView {

	protected JViewHelper mHelper;

	public JEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		mHelper = new JViewHelper(this, attrs);
	}

	public JEditText(Context context) {
		super(context);
	}

	public JEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public String getFieldName() {
		return mHelper.mFieldName;
	}
}
