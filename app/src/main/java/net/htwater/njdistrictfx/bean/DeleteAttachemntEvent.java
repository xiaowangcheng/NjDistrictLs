package net.htwater.njdistrictfx.bean;

/**
 * Created by 96955 on 2017/7/3.
 */

public class DeleteAttachemntEvent {
    public DeleteAttachemntEvent(String type, int index) {
        this.type = type;
        this.index = index;
    }

    String type;
    int index;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
