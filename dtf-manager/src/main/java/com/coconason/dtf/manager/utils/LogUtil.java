package com.coconason.dtf.manager.utils;

import com.coconason.dtf.manager.message.MessageInfoInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
    
    private FileChannel logChannel;

    private MappedByteBuffer logBuffer;
    
    private String logFilePath;

    private FileChannel metadataChannel;

    private MappedByteBuffer metadataBuffer;

    private String metadataFilePath;
    
    private final int SIZE = 100000; 
    
    public static LogUtil getInstance() {
        return LogUtil.SingleHolder.INSTANCE;
    }
    
    public long getLength(){
        metadataBuffer.flip();
        int result = metadataBuffer.limit();
        return result;
    }
    
    public int append(MessageInfoInterface message) {
        LogMetadata logMetadata = add(message);
        if (null == logMetadata) {
            return -1;
        }
        int result = add(logMetadata);
        return result;
    }
    
    private int add (LogMetadata logMetadata) {
        int result = -1;
        byte[] bytes = objectToBytes(logMetadata);
        try {
            FileLock fl = metadataChannel.lock(metadataChannel.position(),SIZE,false);
            result = metadataBuffer.position();
            metadataBuffer.put(bytes);
            metadataBuffer.force();
            fl.release();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return result;
    }
    
    private LogMetadata add(MessageInfoInterface message) {
        int position = -1;
        byte[] bytes = objectToBytes(message);
        try {
            FileLock fl = logChannel.lock(logChannel.position(),SIZE,false);
            position = logBuffer.position();
            logBuffer.put(bytes);
            logBuffer.force();
            fl.release();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        if (position == -1) {
            return null;
        }
        LogMetadata result = new LogMetadata(position, bytes.length);
        return result;
    }
    
    public void updateCommitStatus(MessageInfoInterface message){
        LogMetadata logMetadata = getMetadata(message.getPosition());
        update(message, logMetadata);
    }
    
    private LogMetadata getMetadata(int position) {
        byte[] temp = new byte[92];
//        metadataBuffer.flip();
        metadataBuffer.position(position);
        metadataBuffer.get(temp,0,92);
        ByteArrayInputStream bis = new ByteArrayInputStream(temp);
        ObjectInputStream ois;
        LogMetadata result = null;
        try {
            ois = new ObjectInputStream(bis);
            Object obj = ois.readObject();
            result = (LogMetadata)obj;
            ois.close();
            bis.close();
        } catch (IOException e) {
            logger.error(e.getMessage());
        } catch (ClassNotFoundException e) {
            logger.error(e.getMessage());
        }
        return result;
    }
    
    private void update(MessageInfoInterface message, LogMetadata logMetadata) {
        try{
            FileLock fl = logChannel.lock(logMetadata.getPosition(),logMetadata.getLength(),false);
            logBuffer.position(logMetadata.getPosition());
            logBuffer.put(objectToBytes(message));
            logBuffer.force();
            fl.release();
        }catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
    
    public MessageInfoInterface get(int position) {
        LogMetadata logMetadata = getMetadata(position);
        MessageInfoInterface result = getMessage(logMetadata);
        return result;
    }
    
    private MessageInfoInterface getMessage(LogMetadata logMetadata ){
        byte[] temp = new byte[logMetadata.getLength()];
//        logBuffer.flip();
        logBuffer.position(logMetadata.getPosition());
        logBuffer.get(temp,0,logMetadata.getLength());
        ByteArrayInputStream bis = new ByteArrayInputStream(temp);
        ObjectInputStream ois;
        MessageInfoInterface result = null;
        try {
            ois = new ObjectInputStream(bis);
            Object obj = ois.readObject();
            result = (MessageInfoInterface)obj;
            ois.close();
            bis.close();
        } catch (IOException e) {
            logger.error(e.getMessage());
        } catch (ClassNotFoundException e) {
            logger.error(e.getMessage());
        }
        return result;
    }
    
    private static class SingleHolder{
        private static URL urlForLog = LogUtil.class.getClassLoader().getResource("logs/async-request.log");
        private static URL urlForMetadata = LogUtil.class.getClassLoader().getResource("logs/catalog.log");
        private static final LogUtil INSTANCE = new LogUtil(urlForLog.getPath(),urlForMetadata.getPath());
    }
    
    private LogUtil(String logFilePath,String metadataFilePath){
        this.logFilePath = logFilePath;
        Path filename = Paths.get(this.logFilePath);
        try{
            logChannel = FileChannel.open(filename, StandardOpenOption.WRITE,StandardOpenOption.READ);
            logBuffer = logChannel.map(FileChannel.MapMode.READ_WRITE,0,2048*SIZE);
        }catch (IOException e){
            logger.error(e.getMessage());
        }
        this.metadataFilePath = metadataFilePath;
        Path filename2 = Paths.get(this.metadataFilePath);
        try{
            metadataChannel = FileChannel.open(filename2, StandardOpenOption.WRITE,StandardOpenOption.READ);
            metadataBuffer = metadataChannel.map(FileChannel.MapMode.READ_WRITE,0,92*SIZE);
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
            oos.close();
            bos.close();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return result;
    }

    private byte[] objectToBytes(LogMetadata obj){
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] result=null;
        try {
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            oos.flush();
            result = bos.toByteArray();
            oos.close();
            bos.close();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return result;
    }
    
}
