package com.jelly.lib.content.sqlite;

import java.util.ArrayList;
import org.w3c.dom.Element;

import com.jelly.lib.common.JMetaObject;
	
public class JTable extends JMetaObject {
	
	protected ArrayList<String> mColumns;
	
	public JTable(Element elem) {
		mColumns = new ArrayList<String>();
		loadXmlElement(elem);
	}
	
	/**
	 * load xml definition element.
	 */
	public void loadXmlElement(Element elem) {
		// init attributes
		super.loadXmlElement(elem);
		String columnsString = elem.getAttribute("columns");
		for (String retval: columnsString.split(",")) {
			mColumns.add(retval);
		}
	}
	
	public ArrayList<String> getColumns() {
		return mColumns;
	}
}