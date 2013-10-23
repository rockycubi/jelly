package com.jelly.app.todos;

import android.app.Activity;
import android.os.Bundle;

public class TodoDetailActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.todo_edit);
	}

}
