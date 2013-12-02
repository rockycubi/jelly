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
	
	public void setImageResource(int score) {
		if (score >= 50) {
			super.setImageResource(R.drawable.fresh_tomatoe_mob);
		}
		else {
			super.setImageResource(R.drawable.rotten_tomatoe_mob);
		}
	}
}
