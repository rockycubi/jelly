package com.jelly.lib.view;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.jelly.lib.view.helper.JViewHelper;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * <pre>
 *   <com.jelly.lib.view.JButton
 *    		android:id="@+id/etTest"
 *     		android:layout_width="fill_parent"
 *      	android:layout_height="wrap_content"
 *       	jelly:onClick="saveAndStart(activity)"
 *       	jelly:onLongClick="startActicity(activity)">
 *    </com.jelly.lib.view.JButton>
 * 
 * </pre>
 * 
 * @author rockyswen@gmail.com
 * 
 */
public class JButton extends Button {
	
	protected JViewHelper mHelper;

	public JButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		// create view event helper to initiate event listeners
		mHelper = new JViewHelper(this, attrs);
	}

	public JButton(Context context) {
		super(context);
	}

	public JButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void release() {
		
	}
	
}