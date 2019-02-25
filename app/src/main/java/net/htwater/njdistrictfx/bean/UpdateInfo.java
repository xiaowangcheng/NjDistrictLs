package net.htwater.njdistrictfx.bean;

/**
 * Created by LZY on 2017/1/13.
 */

public class UpdateInfo {
    private String date;
    private String versionName;
    private String info;

    public UpdateInfo() {

    }

    public UpdateInfo(String date, String version, String info) {
        this.date = date;
        this.versionName = version;
        this.info = info;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
