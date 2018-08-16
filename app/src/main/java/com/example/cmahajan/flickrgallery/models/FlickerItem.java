package com.example.cmahajan.flickrgallery.models;

public class FlickerItem {
    private String url;
    private String id;

    public FlickerItem(String key, String url) {
        this.id = key;
        this.url = url;
    }

    public FlickerItem() {

    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
