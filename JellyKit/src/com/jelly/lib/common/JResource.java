package com.jelly.lib.common;

import java.io.InputStream;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import android.content.Context;
import android.util.SparseArray;

import com.jelly.lib.model.JDataModel;

public class JResource {
	private static JResource self = null;

	private static SparseArray<Document> mXmlDocuments = null;
	private static SparseArray<JDataModel> mDataModels = null;
	/*
	 * application context
	 */
	private Context mContext = null;

	public static JResource getInstance(Context context) {
		if (self == null) {
			self = new JResource(context);
		}
		return self;
	}

	private JResource(Context context) {
		mXmlDocuments = new SparseArray<Document>();
		mDataModels = new SparseArray<JDataModel>();
		mContext = context.getApplicationContext();
	}

	/**
	 * get DataModel instance defined in a given resource and a name
	 * 
	 * @param datamodelId
	 *            The JDataModal resource id defined in res/raw/
	 * @return the existing JDataModal object, otherwise creating a new one.
	 */
	public JDataModel getDataModel(int datamodelId) {

		/*
		 * check if the modal already exists. If yes, then return it directly.
		 */
		if (mDataModels.get(datamodelId) != null) {
			return mDataModels.get(datamodelId);
		}

		/*
		 * The modal does not exists. Create a new one.
		 */
		int resourceId = datamodelId;
		// load the xml file and get the root element
		Document dom = loadXmlResource(resourceId);

		// find the element by id
		Element elem = dom.getDocumentElement(); // dom.getElementById(datamodelId);

		JDataModel dm = new JDataModel(mContext, elem);

		// TODO: 貌似不需要如下的代码

		// String classname = elem.getAttribute("class");
		// if (classname == "") {
		// dm = new JDataModel(elem);
		// }
		// else {
		// // new an object by passing its own xml element
		// try {
		// dm = (JDataModel) Class.forName(classname).newInstance();
		// } catch (Exception e) {
		// JLogger.e(e.toString());
		// return null;
		// }
		// }

		// 必须要做构造函数中给出，因为构造函数中要load东西
		// dm.setContext(mContext);
		// dm.loadXmlElement(elem);

		mDataModels.put(datamodelId, dm);

		return dm;
	}

	public Document loadXmlResource(String resourceName) {
		int resourceId = 0;
		if (resourceName.startsWith("raw")) {
			resourceId = mContext.getResources().getIdentifier(resourceName,null,mContext.getPackageName());
		}
		else {	// TODO: support more syntax like @asset
			return null;
		}
		
		return loadXmlResource(resourceId);
	}
	
	// load xml resource and return the root element
	protected Document loadXmlResource(int resourceId) {
		// if resourceId is in cache, return the element
		if (mXmlDocuments.get(resourceId) != null) {
			return mXmlDocuments.get(resourceId);
		}

		// get the xml file inputstream
		InputStream is = mContext.getResources().openRawResource(resourceId);

		// get the root element
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document dom = builder.parse(is);
			mXmlDocuments.put(resourceId, dom);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return mXmlDocuments.get(resourceId);
	}
}
