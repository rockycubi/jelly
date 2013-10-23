package com.jelly.lib.model;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.jelly.lib.common.JMetaObject;

/**
 * The JField class is to provide data field of a model
 * 
 * @author Rocky Swen
 * 
 */
public class JDataField extends JMetaObject {

	protected String mType;
	protected String mValue;
	
	public JDataField() {
		
	}
	
	public JDataField(Element elem) {
		loadXmlElement(elem);
	}
	
	// load xml definition element
	public void loadXmlElement(Element elem) {
		// init attributes
		super.loadXmlElement(elem);
		mType = elem.getAttribute("type");
	}

	public void setValue(String value) {
		mValue = value;
	}
	
	public String getValue() {
		return mValue;
	}
}
