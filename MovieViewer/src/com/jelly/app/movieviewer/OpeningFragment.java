package com.jelly.app.movieviewer;

import com.jelly.lib.view.JListView;

import android.app.Fragment;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class OpeningFragment extends MovieFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		 View myFragmentView = inflater.inflate(R.layout.movie_opening_frag, container, false);
		 return myFragmentView;
	}
	 
	public JListView getListView() {
		return (JListView) getView().findViewById(R.id.lvOpening);
	}
}
