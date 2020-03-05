package com.coconason.dtf.manager.utils;

import com.coconason.dtf.manager.message.MessageInfoInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Path;
import java.nio.file.Paths;

public class LogUtil {
    
    /**
     * Logger of log utility.
     */
    private Logger logger = LoggerFactory.getLogger(LogUtil.class);
    
    FileChannel channel;
    
    MappedByteBuffer buffer;
    
    String filePath;
    
    public static LogUtil getInstance() {
        return LogUtil.SingleHolder.INSTANCE;
    }
    
    public LogUtil(String filePath){
        this.filePath = filePath;
        Path filename = Paths.get(this.filePath);
        try{
            channel = FileChannel.open(filename);
            buffer = channel.map(FileChannel.MapMode.READ_ONLY,0,1073741824);
        }catch (IOException e){
            logger.error(e.getMessage());
        }
        
    }
    
    public long getLength(){
        final File file = new File(filePath);
        return file.length();
    }
    
    public int append(MessageInfoInterface message){
        int position = -1;
        try{
            FileLock fl = channel.lock();
            //根据文本内容获取当前位置
            position = buffer.position();
            buffer.put(message.toBytes());
            buffer.force();
            fl.release();
        }catch (IOException e) {
            logger.error(e.getMessage());
        }
        return position;
    }
    
    public void put(MessageInfoInterface message){
        try{
            FileLock fl = channel.lock();
            int originalPosition = buffer.position();
            buffer.position(message.getPosition());
            buffer.put(message.toBytes());
            buffer.force();
            buffer.position(originalPosition);
            fl.release();
        }catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
    
    public MessageInfoInterface get(int position) {
        byte[] temp = new byte[4096]; 
        buffer.get(temp,position,4096);
        ByteArrayInputStream bais = new ByteArrayInputStream(temp);
        ObjectInputStream ois;
        MessageInfoInterface result = null;
        try {
            ois = new ObjectInputStream(bais);
            result = (MessageInfoInterface)ois.readObject();
        } catch (IOException e) {
            logger.error(e.getMessage());
        } catch (ClassNotFoundException e) {
            logger.error(e.getMessage());
        }
        return result;
    }
    
    private static class SingleHolder{
        private static final LogUtil INSTANCE = new LogUtil("resources/logs/async-request.log");
    }
    
}
