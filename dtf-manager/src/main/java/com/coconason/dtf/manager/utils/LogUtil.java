package com.coconason.dtf.manager.utils;

import com.coconason.dtf.manager.message.MessageInfo;
import com.coconason.dtf.manager.message.MessageInfoInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class LogUtil {
    
    /**
     * Logger of log utility.
     */
    private Logger logger = LoggerFactory.getLogger(LogUtil.class);
    
    private FileChannel channel;

    private MappedByteBuffer buffer;

    private String filePath;
    
    public static LogUtil getInstance() {
        return LogUtil.SingleHolder.INSTANCE;
    }
    
    public long getLength(){
        long result = -1;
        try{
            result = channel.size();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return result;
    }
    
    public int append(MessageInfoInterface message){
        int result = -1;
        try{
            FileLock fl = channel.lock(channel.position(),4096,false);
            //根据文本内容获取当前位置
            int position = buffer.position();
            buffer.put(objectToBytes(message));
            buffer.force();
            result = 4096 + position;
            fl.release();
        }catch (IOException e) {
            logger.error(e.getMessage());
        }
        return result;
    }
    
    public void put(MessageInfoInterface message){
        try{
            FileLock fl = channel.lock(channel.position(),channel.size(),false);
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
        private static URL url = LogUtil.class.getClassLoader().getResource("logs/async-request.log");
        private static final LogUtil INSTANCE = new LogUtil(url.getPath());
    }
    
    private LogUtil(String filePath){
        this.filePath = filePath;
        Path filename = Paths.get(this.filePath);
        try{
            channel = FileChannel.open(filename, StandardOpenOption.WRITE,StandardOpenOption.READ);
            buffer = channel.map(FileChannel.MapMode.READ_WRITE,0,4096*10000);
        }catch (IOException e){
            logger.error(e.getMessage());
        }
    }

    private byte[] objectToBytes(MessageInfoInterface obj){
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] result=null;
        try {
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            oos.flush();
            result = bos.toByteArray();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return result;
    }
    
}
