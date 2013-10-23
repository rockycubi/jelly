package com.jelly.lib.common;

import org.w3c.dom.Element;

import android.content.Context;

/**
 * The JMetaObject is the base class for loading xml into an object
 * 
 * @author rockyswen@gmail.com
 * 
 */
public class JMetaObject {
	
	public String mId;
	public String mClass;
	public String mName;
	
	protected Context mContext;
	
	public JMetaObject() {
		
	}
	
	public JMetaObject(Element elem) {
		loadXmlElement(elem);
	}
	
	// load xml definition element
	public void loadXmlElement(Element elem) {
		// init attributes
		mId = elem.getAttribute("id");
		mClass = elem.getAttribute("class");
		mName = elem.getAttribute("name");
	}
	
	public void setContext(Context context) {
		mContext = context;
	}

}
