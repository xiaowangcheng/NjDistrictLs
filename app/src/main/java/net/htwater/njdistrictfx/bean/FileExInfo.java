package net.htwater.njdistrictfx.bean;

import java.io.Serializable;

/**
 * Created by zty on 2016/4/28.
 * 多媒体文件选择
 */
public class FileExInfo implements Serializable {
    /**
     * 文件名
     */
    private String name;
    /**
     * 文件路径全名
     */
    private String pathName;
    /**
     * 是否是目录
     */
    private boolean directory;
    /**
     * Mime类型
     */
    private String mimetype;
    /**
     * 最后修改日期
     */
    private String modifydate;

    public boolean isDirectory() {
        return directory;
    }

    public void setDirectory(boolean directory) {
        this.directory = directory;
    }

    public String getMimetype() {
        return mimetype;
    }

    public void setMimetype(String mimetype) {
        this.mimetype = mimetype;
    }

    public String getModifydate() {
        return modifydate;
    }

    public void setModifydate(String modifydate) {
        this.modifydate = modifydate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPathName() {
        return pathName;
    }

    public void setPathName(String pathName) {
        this.pathName = pathName;
    }
}
