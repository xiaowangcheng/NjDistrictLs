package net.htwater.njdistrictfx.bean;

/**
 * Created by LZY on 2017/6/23.
 */

public class NoticeBean {
    private String temp_type;
    private int list_id;
    private String list_title;
    private String author;
    private String time;
    private String list_content;

    public String getTemp_type() {
        return temp_type;
    }

    public void setTemp_type(String temp_type) {
        this.temp_type = temp_type;
    }

    public int getList_id() {
        return list_id;
    }

    public void setList_id(int list_id) {
        this.list_id = list_id;
    }

    public String getList_title() {
        return list_title;
    }

    public void setList_title(String list_title) {
        this.list_title = list_title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getList_content() {
        return list_content;
    }

    public void setList_content(String list_content) {
        this.list_content = list_content;
    }
}
