package com.jelly.app.todos;

import com.jelly.lib.content.JSQLiteContentProvider;

public class AppContentProvider extends JSQLiteContentProvider {
	// override 
	protected String DATABASE_XML = "raw/todo_database";
	protected String AUTHORITY = "com.jelly.app.contentprovider";
}