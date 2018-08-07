package com.example.android.newsapp;

import android.text.TextUtils;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;


public final class QueryUtils {

    private static final String LOG_TAG = QueryUtils.class.getSimpleName();
    private static final String ERROR_CLOSING_INPUT_STREAM = "Error closing input stream";
    private static final String ERROR_WITH_CREATING_URL_ = "Error with creating URL ";
    private static final String ERROR_RESPONSE_CODE = "Error response code: ";
    private static final String PROBLEM_RETRIEVING_THE_NEWS_JSON_RESULTS = "Problem retrieving the news JSON results.";
    private static final String PROBLEM_PARSING_THE_NEWS_JSON_RESULTS = "Problem parsing the News JSON results";
    private static final int READ_TIMEOUT = 10000;
    private static final int CONNECT_TIMEOUT = 15000;
    private static final int RIGHT_RESPONSECODE = 200;
    private static final String JSONOBJ_RESPONSE = "response";
    private static final String JSONAR_RESULTS = "results";
    private static final String WEB_TITLE = "webTitle";
    private static final String WEB_AUTHOR = "webTitle";
    private static final String WEB_URL = "webUrl";
    private static final String SECTION_NAME = "sectionName";
    private static final String WEB_PUBLICATION_DATE = "webPublicationDate";
    private static final String TAGS = "tags";
    private static final String CONNECTION_REQUEST_METHOD = "GET";
    private static final String STREAM_UTF_EIGHT = "UTF-8";

    public static List<News> fetchNewsData(String requestUrl) {

        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        URL url = createUrl(requestUrl);

        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, ERROR_CLOSING_INPUT_STREAM, e);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return extractFeatureFromJson(jsonResponse);
    }

    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, ERROR_WITH_CREATING_URL_, e);
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(READ_TIMEOUT /* milliseconds */);
            urlConnection.setConnectTimeout(CONNECT_TIMEOUT /* milliseconds */);
            urlConnection.setRequestMethod(CONNECTION_REQUEST_METHOD);
            urlConnection.connect();

             if (urlConnection.getResponseCode() == RIGHT_RESPONSECODE) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, ERROR_RESPONSE_CODE + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, PROBLEM_RETRIEVING_THE_NEWS_JSON_RESULTS, e);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName(STREAM_UTF_EIGHT));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    private static List<News> extractFeatureFromJson(String newsJSON) {

        if (TextUtils.isEmpty(newsJSON)) {
            return null;
        }

        List<News> NewsList = new ArrayList<>();

        try {
            JSONObject baseJsonResponse = new JSONObject(newsJSON);
            JSONObject Main = baseJsonResponse.getJSONObject(JSONOBJ_RESPONSE);
            JSONArray featureArray = Main.getJSONArray(JSONAR_RESULTS);

            if (featureArray.length() > 0) {
                for (int i = 0; i < featureArray.length(); i++) {
                    JSONObject Feature = featureArray.getJSONObject(i);
                    String title = Feature.getString(WEB_TITLE);
                    String url = Feature.getString(WEB_URL);
                    String category = Feature.getString(SECTION_NAME);
                    String pubTime = Feature.getString(WEB_PUBLICATION_DATE);

                    if(Feature.getJSONArray(TAGS).length() > 0) {
                        JSONArray tags = Feature.getJSONArray(TAGS);
                        JSONObject FirstTag = tags.getJSONObject(0);
                        String author = FirstTag.getString(WEB_AUTHOR);
                        News news = new News(title, category, author, pubTime, url);
                        NewsList.add(news);
                    } else {
                        News news = new News(title, category, null, pubTime, url);
                        NewsList.add(news);
                    }

                }
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, PROBLEM_PARSING_THE_NEWS_JSON_RESULTS, e);
        } catch (Exception e){
            e.printStackTrace();
        }
        return NewsList;
    }

    private QueryUtils() {
    }
}