package com.example.android.newsapp;

public class News {

    private String title;
    private String category;
    private String author;
    private String pubTime;
    private String url;

    public News (String title, String category, String author, String pubTime, String url){
        this.title = title;
        this.category = category;
        this.author = author;
        this.pubTime = pubTime;
        this.url = url;
    }

    public String getTitle(){
        return title;
    }

    public String getCategory(){
        return category;
    }

    public String getAuthor(){
        return author;
    }

    public String getPubTime(){
        return pubTime;
    }

    public String getUrl(){
        return url;
    }
}
