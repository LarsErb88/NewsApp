package com.example.android.newsapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class NewsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>> {

    private static final String SHOW_TAGS = "show-tags";
    private static final String CONTRIBUTOR = "contributor";
    private static final String PAGE_SIZE = "page-size";
    private static final String PAGE = "15";
    private static final String API_KEY = "api-key";
    private static final String KEY = "ead42e6c-ceaa-4530-83fc-1aecbe48e337";
    private static final String DEFAULT_ARTICLES = "All articles";
    private static final String QUERRY = "q";
    private static final String DEFAULT_SECTIONS = "All sections";
    private static final String SECTION = "section";
    private static final String DEFAULT_AUTHORS = "All authors";
    private static final String DEFAULT_PUBTIME = "Today";
    private static final String FROM_DATE = "from-date";
    private static final String ORDER_BY = "order-by";
    private static final String OLDEST = "oldest";
    private NewsAdapter newsAdapter;
    private static final int News_LOADER_ID = 1;
    private static final String REQUEST_URL = "https://content.guardianapis.com/search";
    private TextView mEmptyStateTextView;
    private static final String DATE_FORMAT_NOW = "yyyy-MM-dd";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        ListView NewsList = findViewById(R.id.list);

        mEmptyStateTextView = findViewById(R.id.empty_view);
        NewsList.setEmptyView(mEmptyStateTextView);

        newsAdapter = new NewsAdapter(this, new ArrayList<News>());
        NewsList.setAdapter(newsAdapter);

        //Go to Homepage
        NewsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                try {
                    News currentNews = newsAdapter.getItem(position);
                    Uri NewsUri = Uri.parse(currentNews.getUrl());
                    Intent websiteIntent = new Intent(Intent.ACTION_VIEW, NewsUri);
                    startActivity(websiteIntent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        if (isConnected()) {
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(News_LOADER_ID, null, this);
        } else {
            View progressBar = findViewById(R.id.loading_spinner);
            progressBar.setVisibility(View.GONE);
            mEmptyStateTextView.setText(R.string.NoInternet);
        }
    }

    private boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    @Override
    public Loader<List<News>> onCreateLoader(int i, Bundle bundle) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        String articleTitle = sharedPrefs.getString(
                getString(R.string.settings_articleTitle_key),
                getString(R.string.settings_articleTitle_default));

        String section = sharedPrefs.getString(
                getString(R.string.settings_sectionName_key),
                getString(R.string.settings_sectionName_default));

        String author = sharedPrefs.getString(
                getString(R.string.settings_author_key),
                getString(R.string.settings_author_default));

        String pubTime = sharedPrefs.getString(
                getString(R.string.settings_pubTime_key),
                getString(R.string.settings_pubTime_default));

        Uri baseUri = Uri.parse(REQUEST_URL);
        final Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter(SHOW_TAGS, CONTRIBUTOR);
        uriBuilder.appendQueryParameter(PAGE_SIZE, PAGE);
        uriBuilder.appendQueryParameter(API_KEY, KEY);
        if (articleTitle.equals(DEFAULT_ARTICLES) || articleTitle.equals("")) {
        } else {
            uriBuilder.appendQueryParameter(QUERRY, articleTitle);
        }

        if (section.equals(DEFAULT_SECTIONS) || section.equals("")) {
        } else {
            uriBuilder.appendQueryParameter(SECTION, section.toLowerCase());
        }

        if (author.equals(DEFAULT_AUTHORS) || author.equals("")) {
        } else {
            uriBuilder.appendQueryParameter(QUERRY, author);
        }

        if (pubTime.equals(DEFAULT_PUBTIME) || pubTime.equals("") || !pubTime.matches("([0-9]{4})-([0-9]{2})-([0-9]{2})")) {
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
            uriBuilder.appendQueryParameter(FROM_DATE, sdf.format(cal.getTime()));
        } else {
            uriBuilder.appendQueryParameter(ORDER_BY, OLDEST);
            uriBuilder.appendQueryParameter(FROM_DATE, pubTime);
        }

        return new NewsLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> news) {
        View progressBar = findViewById(R.id.loading_spinner);
        progressBar.setVisibility(View.GONE);
        mEmptyStateTextView.setText(R.string.NoNewsFound);
        newsAdapter.clear();

        if (news != null && !news.isEmpty()) {
            updateUi(news);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        newsAdapter.clear();
    }

    private void updateUi(final List<News> news) {
        ListView NewsListView = findViewById(R.id.list);
        newsAdapter = new NewsAdapter(this, news);
        NewsListView.setAdapter(newsAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

