package com.jelly.app.movieviewer;

import com.jelly.lib.view.JListView;
import com.jelly.lib.view.helper.JListViewHelper;

import android.content.Context;
import android.util.AttributeSet;

public class JReviewListView extends JListView {

	public JReviewListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	protected void initViewHelper(Context context, AttributeSet attrs) {
		JReviewListViewHelper helper = new JReviewListViewHelper(this, attrs);
		mHelper = (JListViewHelper)helper;
	}
}
