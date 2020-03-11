package com.coconason.dtf.manager.log;

import java.io.Serializable;

public class LogMetadata implements Serializable {
    
    private int position;
    
    private int length;
    
    public LogMetadata(final int position, final int length) {
        this.position = position;
        this.length = length;
    }
    
    /**
     * Get position of record in async-request.log.
     * @return position of record in async-request.log
     */
    public int getPosition() {
        return position;
    }

    /**
     * Get length of record in async-request.log.
     * @return length of record in async-request.log
     */
    public int getLength() {
        return length;
    }
    
}
