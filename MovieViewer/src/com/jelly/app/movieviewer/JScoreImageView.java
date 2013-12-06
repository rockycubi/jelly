package com.jelly.app.movieviewer;

import com.jelly.lib.view.JImageView;
import com.jelly.lib.view.helper.JViewHelper;
import com.squareup.picasso.Picasso;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 
 * @author rockyswen@gmail.com
 * 
 */
public class JScoreImageView extends JImageView {

	public JScoreImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public JScoreImageView(Context context) {
		super(context);
	}

	public JScoreImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	public void setFieldValue(String fieldValue) {
		// if the value is -1
		if (fieldValue.equals("-1")) {
			setImageResource(R.drawable.ic_action_about);
		}
		else {
			int score = Integer.parseInt(fieldValue);
			if (score >= 50) {
				setImageResource(R.drawable.fresh_tomatoe_mob);
			}
			else {
				setImageResource(R.drawable.rotten_tomatoe_mob);
			}
		}
	}
}
