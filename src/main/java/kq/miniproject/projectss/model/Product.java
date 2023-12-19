package kq.miniproject.projectss.model;

import java.io.Serializable;

public class Product implements Serializable {

    private String title;
    private String itemUrl;
    private String image;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getItemUrl() {
        return itemUrl;
    }

    public void setItemUrl(String itemUrl) {
        this.itemUrl = itemUrl;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Product() {
    }

    public Product(String title, String itemUrl, String image) {
        this.title = title;
        this.itemUrl = itemUrl;
        this.image = image;
    }

    @Override
    public String toString() {
        return "isProduct";
    }    
}
