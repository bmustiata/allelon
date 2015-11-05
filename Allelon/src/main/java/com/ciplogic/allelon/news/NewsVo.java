package com.ciplogic.allelon.news;

/**
 * A single news item that appears in the list of the user.
 */
public class NewsVo {
    public NewsVo() {
    }

    public NewsVo(String title, String description, String author, String url) {
        this.title = title;
        this.description = description;
        this.author = author;
        this.url = url;
    }

    public String title;
    public String description;
    public String author = "Allelon";
    public String url;
    public String date = "-";
    public String commentCount = "-";
}
