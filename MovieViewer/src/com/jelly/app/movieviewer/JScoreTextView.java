package com.jelly.app.movieviewer;

import com.jelly.lib.view.JTextView;
import android.content.Context;
import android.util.AttributeSet;

/**
 * 
 * @author rockyswen@gmail.com
 * 
 */
public class JScoreTextView extends JTextView {

	public JScoreTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public JScoreTextView(Context context) {
		super(context);
	}

	public JScoreTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	public void setFieldValue(String fieldValue) {
		// if the value is -1
		if (fieldValue.equals("-1")) {
			String text = getContext().getString(R.string.no_review);
			setText(text);
		}
		else {
			setText(fieldValue + "%");
		}
	}
}
