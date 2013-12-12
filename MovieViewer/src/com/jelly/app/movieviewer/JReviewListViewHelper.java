package com.jelly.app.movieviewer;

import com.jelly.lib.view.helper.JListViewHelper;
import android.app.Activity;
import android.util.AttributeSet;
import android.view.View;

/**
 * 
 */
public class JReviewListViewHelper extends JListViewHelper {

	public JReviewListViewHelper(View v, AttributeSet attrs) {
		super(v, attrs);
	}
	
	/*protected void loadHeaderFooterView() {
		Activity act =  (Activity)mContext;
		View header = act.getLayoutInflater().inflate(R.layout.movie_detail_header, null);
		mLv.addHeaderView(header);
	}*/

	public void loadData() {
		if (mLv.isInEditMode()) {
			return;
		}
		// get the movie id from extra
		String id = (String)getInExtraData("id");
		
		// start data loading
		mDataModel.load(id, (Activity)mContext, this);
	}
}
