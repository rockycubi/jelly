package com.jelly.app.movieviewer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.util.Log;


public class AppContentProvider extends ContentProvider {
	// override 
	protected String AUTHORITY = "com.jelly.movieviewer.contentprovider";
	protected String LIST_SERVICE_URL="http://api.rottentomatoes.com/api/public/v1.0/lists/movies";
	protected String MOVIE_SERVICE_URL="http://api.rottentomatoes.com/api/public/v1.0/movies";
	protected String apikey = "kgmbm9q32gty7nquy33p856a";
	
	protected UriMatcher URI_MATCHER;
	protected String[] dataTypes = new String[]{
			"opening",
			"in_theaters",
			"upcoming",
			"info",
			"reviews",
			"search"
	};
	protected HashMap<Integer, String> mUriDataTypes;
	protected HashMap<Integer, Boolean> mUriItemFlags;
	
	@Override
	public boolean onCreate() {
		init();
		return false;
	}
	
	protected void init() {
		mUriDataTypes = new HashMap<Integer, String>();
		mUriItemFlags = new HashMap<Integer, Boolean>();
		
		// populate URI_MATCHER, uri types, uri tables, uri item flag
		URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
		int uriIndex = 101;
		for (int i=0; i<dataTypes.length; i++) {
			Integer ikey = new Integer(uriIndex);
			mUriDataTypes.put(ikey, dataTypes[i]);
			URI_MATCHER.addURI(AUTHORITY,dataTypes[i],uriIndex);
			mUriItemFlags.put(ikey, Boolean.FALSE);
			uriIndex++;
			
			Integer ikey2 = new Integer(uriIndex);
			mUriDataTypes.put(ikey2, dataTypes[i]);
			URI_MATCHER.addURI(AUTHORITY,dataTypes[i]+"/#",uriIndex);
			mUriItemFlags.put(ikey2, Boolean.TRUE);
			uriIndex++;
		}
	}
	
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		// find table per the uri match
		int uriIndex = URI_MATCHER.match(uri);
		Log.i("JAppContentProvider", "URI match "+uri.toString()+" as "+uriIndex);
		String dataType = mUriDataTypes.get(uriIndex);
		if (dataType == null) {
			throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
		
		// construct request url
		String url = LIST_SERVICE_URL + "/" + dataType + ".json";
		if (dataType.equals("info")) {
			return queryMovieInfo(uri);
		} else if (dataType.equals("reviews")) {
			return queryMovieReviews(uri);
		} else if (dataType.equals("search")) {
			return searchMovies(uri, selection);
		}
		url += "?apikey="+apikey;
		String limit = uri.getQueryParameter("limit");
		String country = uri.getQueryParameter("country");
		if (limit != null && !limit.equals("-1")) {
			url += "&limit="+limit;
		}
		if (country != null && !country.equals("")) {
			url += "&country="+country;
		}
		// send the request url
		String content = fetchContent(url);
		
		return parserMovieListContent(content);
	}
	
	protected Cursor searchMovies(Uri uri, String selection) {
		String url = MOVIE_SERVICE_URL + "/movies.json";
		url += "?apikey="+apikey;
		String queryStr = selection.replace("q=", "");
		try {
			url += "&q=" + URLEncoder.encode(queryStr, "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			Log.e("AppContentProvider", "Failed to URL encode "+queryStr);
		}
		String limit = uri.getQueryParameter("limit");
		String offset = uri.getQueryParameter("offset");
		int iOffset = Integer.parseInt(offset);
		String country = uri.getQueryParameter("country");
		if (limit != null && !limit.equals("-1")) {
			url += "&page_limit="+limit;
			int iLimit = Integer.parseInt(limit);
			int page = iOffset % iLimit + 1;
			url += "&page="+page;
		}
		if (country != null && !country.equals("")) {
			url += "&country="+country;
		}
		
		// send the request url
		String content = fetchContent(url);
		
		return parserMovieListContent(content);
	}
	
	protected Cursor queryMovieInfo(Uri uri) {
		String movieId = uri.getLastPathSegment();
		String url = MOVIE_SERVICE_URL + "/" + movieId + ".json";
		url += "?apikey="+apikey;
		String country = uri.getQueryParameter("country");
		if (country != null && !country.equals("")) {
			url += "&country="+country;
		}
		// send the request url
		String content = fetchContent(url);
		
		return parseMovieInfoContent(content);
	}
	
	protected Cursor queryMovieReviews(Uri uri) {
		String movieId = uri.getLastPathSegment();
		String url = MOVIE_SERVICE_URL + "/" + movieId + "/reviews.json";
		url += "?apikey="+apikey;
		String country = uri.getQueryParameter("country");
		if (country != null && !country.equals("")) {
			url += "&country="+country;
		}
		// send the request url
		String content = fetchContent(url);
				
		return parseMovieReviewContent(content);
	}
	
	protected String fetchContent(String url) {
		StringBuilder builder = new StringBuilder();
		HttpClient client = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(url);
		try {
			HttpResponse response = client.execute(httpGet);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			if (statusCode == 200) {
				HttpEntity entity = response.getEntity();
				InputStream content = entity.getContent();
				BufferedReader reader = new BufferedReader(new InputStreamReader(content));
				String line;
				while ((line = reader.readLine()) != null) {
					builder.append(line);
				}
			} else {
				Log.e("AppContentProvider", "Failed to download file");
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return builder.toString();
	}
	

	protected Cursor parserMovieListContent(String content) {
		MatrixCursor cursor = new 
				MatrixCursor(new String[]{"_id","title","release_date","rating",
						"critics_score","audience_score","thumbnail","cast1","cast2"});
		
		// parse the content as JSON object
		try {
			JSONObject jsonObject = new JSONObject(content);
			JSONArray movieArray = jsonObject.getJSONArray("movies");
			Log.i("AppContentProvider", "Number of movies " + movieArray.length());
			for (int i = 0; i < movieArray.length(); i++) {
				JSONObject movieObject = movieArray.getJSONObject(i);
				String id =  movieObject.getString("id");
				String title =  movieObject.getString("title");
				String release_date = "";
				if (movieObject.getJSONObject("release_dates").has("theater")) {
					release_date =  movieObject.getJSONObject("release_dates").getString("theater");
				}
				String rating =  movieObject.getString("mpaa_rating");
				String critics_score = movieObject.getJSONObject("ratings").getString("critics_score"); 
				String audience_score = movieObject.getJSONObject("ratings").getString("audience_score");
				JSONObject posters =  movieObject.getJSONObject("posters");
				String thumbnail = posters.getString("thumbnail");
				JSONArray castArray = movieObject.getJSONArray("abridged_cast");
				String cast1 = castArray.getJSONObject(0).getString("name");
				String cast2 = (castArray.length() > 1) ?  castArray.getJSONObject(1).getString("name") : "";
				//String thumbnail = posters.getString("profile");
				cursor.addRow(new Object[]{id,title,release_date,rating,critics_score,audience_score,thumbnail,cast1,cast2});
				Log.i("AppContentProvider", movieObject.getString("title"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return cursor;
	}
	
	protected Cursor parseMovieInfoContent(String content) {
		MatrixCursor cursor = new 
				MatrixCursor(new String[]{"_id","title","release_date","rating",
						"critics_score","audience_score","thumbnail","cast1","cast2","director","synopsis"});
	
		// parse the content as JSON object
		try {
			JSONObject movieObject = new JSONObject(content);
			String id =  movieObject.getString("id");
			String title =  movieObject.getString("title");
			String synopsis = movieObject.getString("synopsis");
			String release_date = "";
			if (movieObject.getJSONObject("release_dates").has("theater")) {
				release_date =  movieObject.getJSONObject("release_dates").getString("theater");
			}
			String rating =  movieObject.getString("mpaa_rating");
			String critics_score = movieObject.getJSONObject("ratings").getString("critics_score"); 
			String audience_score = movieObject.getJSONObject("ratings").getString("audience_score");
			JSONObject posters =  movieObject.getJSONObject("posters");
			String thumbnail = posters.getString("profile");
			JSONArray castArray = movieObject.getJSONArray("abridged_cast");
			String cast1 = "";
			if (castArray.length() > 0) {
				cast1 = castArray.getJSONObject(0).getString("name");
				if (castArray.getJSONObject(0).has("characters")) {
					JSONArray char1Array = castArray.getJSONObject(0).getJSONArray("characters");
					String cast1Chars = char1Array.getString(0);
					cast1 += " as " + cast1Chars;
				}
			}
			String cast2 = "";
			if (castArray.length() > 1) {
				cast2 = castArray.getJSONObject(1).getString("name");
				if (castArray.getJSONObject(1).has("characters")) {
					JSONArray char2Array = castArray.getJSONObject(1).getJSONArray("characters");
					String cast2Chars = char2Array.getString(0);
					cast2 += " as " + cast2Chars;
				}
			}
			String director1 = "";
			if (movieObject.has("abridged_directors")) {
				JSONArray directorArray = movieObject.getJSONArray("abridged_directors");
				director1 = directorArray.getJSONObject(0).getString("name");
			}
			cursor.addRow(new Object[]{id,title,release_date,rating,critics_score,audience_score,thumbnail,cast1,cast2,director1,synopsis});
			Log.i("AppContentProvider", movieObject.getString("title"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cursor;
	}
	
	protected Cursor parseMovieReviewContent(String content) {
		MatrixCursor cursor = new 
				MatrixCursor(new String[]{"_id","critic","date","freshness",
						"publication","quote","links"});
		try {
			JSONObject jsonObject = new JSONObject(content);
			JSONArray reviewArray = jsonObject.getJSONArray("reviews");
			Log.i("AppContentProvider", "Number of reviews " + reviewArray.length());
			for (int i = 0; i < reviewArray.length(); i++) {
				JSONObject reviewObject = reviewArray.getJSONObject(i);
				String quote = "";
				if (reviewObject.has("quote")) {
					quote =  reviewObject.getString("quote");
					if (quote.equals("")) continue;
				}
				else {
					continue;
				}
				String id = (i+1)+"";
				String critic =  reviewObject.getString("critic");
				String date =  reviewObject.getString("date");
				String freshness_str =  reviewObject.getString("freshness");
				String freshness = "40";
				if (freshness_str.equalsIgnoreCase("fresh")) {
					freshness = "60";
				}
				String publication =  reviewObject.getString("publication");
				
				String link = "";
				if (reviewObject.getJSONObject("links").has("review")) {
					link =  reviewObject.getJSONObject("links").getString("review");
				}
				cursor.addRow(new Object[]{id,critic,date,freshness,publication,quote,link});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}		
		return cursor;
	}

	@Override
	public int delete(Uri arg0, String arg1, String[] arg2) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}
}