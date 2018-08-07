package com.example.android.newsapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;

public class NewsAdapter extends ArrayAdapter<News> {

    private static final String AUTHOR = "Author: ";
    private static final String CAT1 = "Sport";
    private static final String CAT2 = "Football";
    private static final String CAT3 = "Technology";
    private static final String CAT4 = "Film";
    private static final String AFTER_DATE_REPLACE = "T";
    private static final String AFTER_TIME_REPLACE = "Z";

    public NewsAdapter(Context context, List<News> news) {
        super(context, 0, news);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.activity_news_list_item, parent, false);
        }
        News currentNews = getItem(position);

        TextView titleView = convertView.findViewById(R.id.title);
        String title = currentNews.getTitle();
        titleView.setText(title);

        ImageView categoryView = convertView.findViewById(R.id.category);
        String cat = currentNews.getCategory();
        switch (cat) {
            case CAT1:
                categoryView.setImageResource(R.drawable.sport);
                break;
            case CAT2:
                categoryView.setImageResource(R.drawable.football);
                break;
            case CAT3:
                categoryView.setImageResource(R.drawable.tech);
                break;
            case CAT4:
                categoryView.setImageResource(R.drawable.film);
                break;
            default:
                categoryView.setImageResource(R.drawable.news);
                break;
        }

        TextView authorView = convertView.findViewById(R.id.author);
        if (currentNews.getAuthor() == null) {
            authorView.setVisibility(View.GONE);
        } else {
            authorView.setVisibility(View.VISIBLE);
            String author = AUTHOR + currentNews.getAuthor();
            authorView.setText(author);
        }

        TextView pubTimeView = convertView.findViewById(R.id.pubTime);
        if (currentNews.getPubTime() == null) {
            pubTimeView.setVisibility(View.GONE);
        } else {
            pubTimeView.setVisibility(View.VISIBLE);
            String pubTime = currentNews.getPubTime();
            pubTimeView.setText(pubTime.replace(AFTER_DATE_REPLACE, " ").replace(AFTER_TIME_REPLACE, ""));
        }

        return convertView;
    }
}

