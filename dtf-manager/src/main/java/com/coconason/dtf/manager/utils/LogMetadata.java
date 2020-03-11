package com.coconason.dtf.manager.utils;

import java.io.Serializable;

public class LogMetadata implements Serializable {
    
    private int position;
    
    private int length;

    public int getPosition() {
        return position;
    }

    public int getLength() {
        return length;
    }

    public LogMetadata(int position, int length) {
        this.position = position;
        this.length = length;
    }
}
