package com.hj.app.oschina.bean;

/**
 * Created by huangjie08 on 2016/8/23.
 */
public class Banner extends Base {
    public static final int BANNER_TYPE_URL = 0;//链接新闻
    public static final int BANNER_TYPE_SOFTWARE = 1;//
    public static final int BANNER_TYPE_POST = 2;//
    public static final int BANNER_TYPE_BLOG = 3;//
    public static final int BANNER_TYPE_TRANSLATEL = 4;//
    public static final int BANNER_TYPE_EVENT = 5;//
    public static final int BANNER_TYPE_NEWS = 6;
    private String name;
    private String detail;
    private String img;
    private String href;
    private String pubDate;
    private int type;
    private long id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
