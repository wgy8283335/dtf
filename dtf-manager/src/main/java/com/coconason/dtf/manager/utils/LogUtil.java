package com.coconason.dtf.manager.utils;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;

public class LogUtil {
    
    MappedByteBuffer buffer;
    
    public LogUtil(String filePath) throws IOException{
        Path filename = Paths.get(filePath);
        FileChannel channel = FileChannel.open(filename);
        buffer = channel.map(FileChannel.MapMode.READ_ONLY,0,1073741824);
        
    }
    
    public static void put() {
        return;
    }

    public static long get() {
        return 0;
    }
    
}
