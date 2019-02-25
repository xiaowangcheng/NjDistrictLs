package net.htwater.njdistrictfx.bean;

/**
 * Created by LZY on 2017/5/31.
 */

public class DownloadEvent {
    private String url;

    public DownloadEvent(String url) {
        this.url = url;
    }

    public String getData() {
        return url;
    }
}
