package net.htwater.njdistrictfx.core;

import java.io.Serializable;

/**
 * Created by LZY on 2016/5/31.
 */
public class Event {
    private Serializable data;
    private String[] array;

    public Event(Serializable data) {
        this.data = data;
    }

    public Event(String[] array) {
        this.array = array;
    }

    public Event() {
    }

    public Serializable getData() {
        return data;
    }

    public String[] getArray() {
        return array;
    }
}
