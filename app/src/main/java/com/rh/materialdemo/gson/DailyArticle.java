package com.rh.materialdemo.gson;

/**
 *
 * @author RH
 * @date 2017/12/14
 */

public class DailyArticle {
    public String author;
    private String title;
    public String content;

    public DailyArticle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }
}
