package com.jelly.app.movieviewer;

import com.jelly.lib.view.JListView;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;

public class MovieSearchActivity extends Activity {
	
	int listview_id = R.id.lvSearch;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.movie_search);
		
		handleIntent(getIntent());
	}

	public void onNewIntent(Intent intent) { 
		setIntent(intent); 
		handleIntent(intent); 
	}
	
	private void handleIntent(Intent intent) { 
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) { 
			String query = 
               intent.getStringExtra(SearchManager.QUERY); 
			doSearch(query); 
		} 
	}    

	private void doSearch(String queryStr) { 
		// get the listview and load the data
		JListView lv = (JListView)findViewById(listview_id);
		String selection = "q="+queryStr;
		// load data of listview
		lv.loadData(selection, null);
	} 
}
