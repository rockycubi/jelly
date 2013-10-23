package com.jelly.lib.view.helper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.Class;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jelly.lib.R;
import com.jelly.lib.common.JResource;
import com.jelly.lib.model.JDataModel;
import com.jelly.lib.view.JFormView;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class JViewHelper {
	/*
	 * reference to view object
	 */
	protected View mView;
	/*
	 * context object, same as activity
	 */
	protected Context mContext;

	/**
	 * DataModel name and DataModel object.
	 */
	public int mDataModelId;
	protected JDataModel mDataModel;
	
	/*
	 * Data field name
	 */
	public String mFieldName;
	
	/*
	 * onClick action
	 */
	public String mOnClick;
	
	/*
	 * Out extra data to pass in action invocation
	 */
	protected HashMap<String, String> mOutExtraData;
	
	/*
	 * Container FormView id
	 */
	protected int mFormViewId;

	public JViewHelper(View v, AttributeSet attrs) {
		mView = v;
		mContext = v.getContext();
		mOutExtraData = new HashMap<String,String>();

		/*
		 * read event handler from xml definition
		 */
		TypedArray aEvents = mContext.obtainStyledAttributes(attrs, 
				R.styleable.Jelly_Event);
		try {
			mOnClick = aEvents.getString(R.styleable.Jelly_Event_onClick);
		} finally {
			aEvents.recycle();
		}

		/*
		 * read data binding from xml definition
		 */
		TypedArray aDataBind = mContext.obtainStyledAttributes(attrs,
				R.styleable.Jelly_DataBind);
		try {
			mDataModelId = aDataBind.getResourceId(R.styleable.Jelly_DataBind_data_model, 0);
			Log.i("JViewHelper", "data module id = " + mDataModelId);
			if (mDataModelId != 0) {
				mDataModel = JResource.getInstance(mContext).getDataModel(
						mDataModelId); // raw/xml_filename
			}
			mFieldName = aDataBind
					.getString(R.styleable.Jelly_DataBind_data_field);
			Log.i("JViewHelper", "fieldName = " + mFieldName);


		} finally {
			aDataBind.recycle();
		}
		
		initEventListeners();

		/*
		 * read FromView from xml definition
		 */
		TypedArray aFormView = mContext.obtainStyledAttributes(attrs,
				R.styleable.Jelly_UI);
		try {
			mFormViewId = aFormView.getResourceId(
					R.styleable.Jelly_UI_formview, 0);
		} finally {
			aFormView.recycle();
		}
	}
	
	protected void initEventListeners() {
		/*
		 * add listener for click event
		 */
		if (mOnClick != null) {
			mView.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					invokeMethod(mOnClick);
					
				}
			});
		}
	}
	
	/*
	 * method to get additional data in method invocation
	 */
	protected HashMap<String, String> getOutExtraData() {
		return mOutExtraData;
	}
	
	/*
	 * method to get additional data set outside the host activity
	 */
	protected Object getInExtraData(String key) {
		// ask activity for extra data
		Activity act = (Activity)mContext;
		Bundle extras = act.getIntent().getExtras();
		if (extras != null && extras.containsKey(key)) {
			return extras.get(key);
		}
		return null;
	}

	// //////////////////////////////////////////////////////
	// public get method
	// //////////////////////////////////////////////////////

	public JDataModel getDataModel() {
		return mDataModel;
	}

	public String getOnClick() {
		return mOnClick;
	}

	public String getFieldName() {
		return mFieldName;
	}

	public int getmFormViewId() {
		return mFormViewId;
	}

	public View getView() {
		return mView;
	}
	
	////////////////////////////////////////////////////////
	// public method that maps to view event attribute
	// TODO move common methods into another JAction class
	////////////////////////////////////////////////////////
	
	/*
	* Display toast message
	*/
	public void makeToast(String message) {
		Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
	}
	
	/*
	 * start activity
	 */
	public void startActivity(String actClassName) {
		try {
			Intent i = new Intent(mContext, Class.forName(actClassName));
			// check if there is additional data to pass
			HashMap<String,String> data = getOutExtraData();
			for(String key : data.keySet()) {
				i.putExtra(key, data.get(key));
			}
			mContext.startActivity(i);
		} catch (ClassNotFoundException e) {
			Log.e("JViewHelper", e.getMessage());
		}
	}
	
	/*
	 * save form view
	 */
	public void save() {
		// make sure use activity
		if (!(mContext instanceof Activity)) {
			return;
		}
		
		// get the form view by formview id (or data model)
		Activity act = (Activity)mContext;
		JFormView formView = (JFormView)act.findViewById(getmFormViewId());

		// call form view's getContentValues()
		ContentValues cv = formView.getContentValue();
		
		// get form view's data model and save the content
		JDataModel model = formView.getDataModel();
		if (model != null) {
			// when to insert, when to update?
			if (cv.get("_id") == null) {
				model.insert(cv);
			}
			else {
				model.update(cv);
			}
			//makeToast("Data saved");
			// finish current activity
			act.finish();
		}
		else {
			makeToast("Data is not saved");
		}
	}
	
	/*
	* TODO add more common methods
	* navigation method: 
	* 		toast, dialog, activity, close, back, 
	* data method: (need data_model attribute in view xml)
	* 		save, delete, query,
	*/
	
	////////////////////////////////////////////////////////
	// methods that handles methods invocation
	////////////////////////////////////////////////////////
	
	/*
	* parse method(p1,p2,...) into String methodName, String[] params
	*/
	protected void invokeMethod(String methodString) {
		String method = getMethod(methodString);
		String[] params = getMethodParameters(methodString);
		invoke(method, params);
	}
	
	protected void invoke(String method, String[] params) {
		// call internal method
		if (method.equals("makeToast")) {
			this.makeToast(params[0]);
		}
		else if (method.equals("startActivity")) {
			this.startActivity(params[0]);
		}
		else if (method.equals("save")) {
			this.save();
		}
		else {
			// delegate a method defined in host activity
			Activity host = (Activity)mContext;
			Class<?>[] paramTypes = new Class[params.length];
			for (int i=0; i<params.length; i++) {
				paramTypes[i]=String.class;
			}
			try {
				Method m = host.getClass().getMethod(method, paramTypes);
				switch(params.length) {
				case 0: m.invoke(host);
				case 1: m.invoke(host, params[0]); break;
				case 2: m.invoke(host, params[0], params[1]); break;
				case 3: m.invoke(host, params[0], params[1], params[2]); break;
				case 4: m.invoke(host, params[0], params[1], params[2], params[3]); break;
				case 5: m.invoke(host, params[0], params[1], params[2], params[3], params[4]); break;
				case 6: m.invoke(host, params[0], params[1], params[2], params[3], params[4], params[5]); break;
				default: m.invoke(host, params);
			}
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	protected String getMethod(String action) {
		String method = action;
		Pattern pattern = Pattern.compile("([a-zA-Z0-9_]+)\\((.*)\\)");
		Matcher matcher = pattern.matcher(action);
		while (matcher.find()) {
			method = matcher.group(1);
			break;
		}
		return method;
	}
	
	protected String[] getMethodParameters(String action) {
		Pattern pattern = Pattern.compile("([a-zA-Z0-9_]+)\\((.*)\\)");
		Matcher matcher = pattern.matcher(action);
		while (matcher.find()) {
			//String method = matcher.group(1);
			String paramString = matcher.group(2);
			String[] params = paramString.split(",");
			return params;
		}
		return null;
	}
}
