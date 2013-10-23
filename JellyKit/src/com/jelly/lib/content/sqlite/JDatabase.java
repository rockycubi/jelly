package com.jelly.lib.content.sqlite;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.jelly.lib.common.JMetaObject;

import android.content.Context;
import android.util.Log;

public class JDatabase extends JMetaObject {
	
	protected HashMap<String, JTable> mTables;
	//protected String mHelperClass;
	protected String mSchema;
	protected String mSchemaContent;
	protected int mDatabaseVersion = 1;
	
	public JDatabase(Context context, Element elem) {
		mContext = context;
		mTables = new HashMap<String, JTable>();
		loadXmlElement(elem);
	}
	
	/**
	 * load xml definition element.
	 */
	public void loadXmlElement(Element elem) {
		// init attributes
		super.loadXmlElement(elem);
		//mHelperClass = elem.getAttribute("helper_class");
		mSchema = elem.getAttribute("schema");
		
		// load schema content from schema sql file
		mSchemaContent = loadSchemaContent(mSchema); 

		// init tables
		NodeList listOfTables = elem.getElementsByTagName("Table");
		for (int i = 0; i < listOfTables.getLength(); i++) {
			Element tableElem = (Element) listOfTables.item(i);
			String tableName = tableElem.getAttribute("name");
			JTable tableObj = new JTable(tableElem);
			mTables.put(tableName, tableObj);
		}
	}
	
	public HashMap<String, JTable> getTables() {
		return mTables;
	}
	
	protected String loadSchemaContent(String schemaName) {
		int resourceId = 0;
		if (schemaName.startsWith("raw")) {
			resourceId = mContext.getResources().getIdentifier(schemaName,null,mContext.getPackageName());
			if (resourceId == 0) {
				return null;
			}
		}
		else {
			return null;
		}
		String text = null;
		// read the content in the resource
		InputStream is = mContext.getResources().openRawResource(resourceId);
		try {
			byte[] b = new byte[is.available()];
	        is.read(b);
	        text = new String(b);
		} catch (IOException e) {
			Log.e("JDatabase", e.getMessage());
			return null;
		}
		return text;
	}
	
	public String getDatabaseName() {
		return mName;
	}
	
	public int getDatabaseVersion() {
		return mDatabaseVersion;
	}
	
	public String getSchemaSQL() {
		return mSchemaContent;
	}
	
	public ArrayList<String> getTableColumns(String table) {
		return mTables.get(table).getColumns();
	}
}