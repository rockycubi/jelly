package com.jelly.app.todos;

import com.jelly.lib.model.JDataModel;
import com.jelly.lib.view.JListView;

import android.os.Bundle;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;

public class TodoListActivity extends Activity {

	private static final int DELETE_ID = Menu.FIRST + 1;
	int activity_layout = R.layout.todo_list;
	int listview_id = android.R.id.list;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(activity_layout);
		registerForContextMenu((ListView)findViewById(listview_id));
	}

	// create the menu based on the XML defintion
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.todo_list, menu);
		return true;
	}
	
	// Reaction to the menu selection
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.insert:
			createTodo();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void createTodo() {
		Intent i = new Intent(this, TodoDetailActivity.class);
		startActivity(i);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, DELETE_ID, 0, R.string.menu_delete);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case DELETE_ID:
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
			// get the model and delete the record 
			JListView lv = (JListView)findViewById(listview_id);
			JDataModel model = lv.getDataModel();
			model.delete(info.id);
			
			// refresh the listview
			lv.reload();
			return true;
		}
		return super.onContextItemSelected(item);
	}
}
