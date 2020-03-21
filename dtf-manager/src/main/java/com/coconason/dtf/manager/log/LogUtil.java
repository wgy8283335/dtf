package com.coconason.dtf.manager.log;

import com.coconason.dtf.manager.message.MessageInfoInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.LinkedList;
import java.util.List;

public final class LogUtil {
    
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
    
    private final int size = 100000;

    private final int metadataSize = 110;
    
    private final int messageSize = 2048;
    
    private int positionForAppendMetadata = 0;

    private int positionForAppendMessage = 0;
    
    private static final String LOG_DIR="./logs/";

    private static final String LOG_FILE_NAME="async-request.bin";

    private static final String META_FILE_NAME="catalog.bin";
    
    private LogUtil(final String logFilePath, final String metadataFilePath) {
        try {
            logger.debug(logFilePath);
            logger.debug(metadataFilePath);
            createFile(metadataFilePath);
            createFile(logFilePath);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        this.logFilePath = logFilePath;
        Path filename = Paths.get(this.logFilePath);
        try {
            logChannel = FileChannel.open(filename, StandardOpenOption.WRITE, StandardOpenOption.READ);
            logBuffer = logChannel.map(FileChannel.MapMode.READ_WRITE, 0, messageSize * size);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        this.metadataFilePath = metadataFilePath;
        Path filename2 = Paths.get(this.metadataFilePath);
        try {
            metadataChannel = FileChannel.open(filename2, StandardOpenOption.WRITE, StandardOpenOption.READ);
            metadataBuffer = metadataChannel.map(FileChannel.MapMode.READ_WRITE, 0, metadataSize * size);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
    
    /**
     * Get single instance of LogUtil.
     * 
     * @return object of LogUtil
     */
    public static LogUtil getInstance() {
        return LogUtil.SingleHolder.INSTANCE;
    }
    
    /**
     * Append message in buffer.
     * 
     * @param message message information interface
     * @return position in log metadata
     */
    public synchronized int append(final MessageInfoInterface message) {
        LogMetadata logMetadata = add(message);
        if (null == logMetadata) {
            return -1;
        }
        int result = add(logMetadata);
        return result;
    }
    
    /**
     * Update message information in log.
     * 
     * @param message message information interface
     */
    public synchronized void updateCommitStatus(final MessageInfoInterface message) {
        LogMetadata logMetadata = getMetadata(message.getPosition());
        update(message, logMetadata);
    }
    
    /**
     * Get message according to position.
     *
     * @param position position of message.
     * @return message information interface
     */
    public MessageInfoInterface get(final int position) {
        LogMetadata logMetadata = getMetadata(position);
        if(null == logMetadata){
            return null;
        }
        MessageInfoInterface result = getMessage(logMetadata);
        return result;
    }
    
    /**
     * Get message according to position.
     *
     * @return message information interface
     */
    public List<MessageInfoInterface> goThrough(int initialPosition) {
        int length = metadataSize * size;
        int i = 0;
        List<MessageInfoInterface> result = new LinkedList<>();
        while (i < length) {
            MessageInfoInterface message = LogUtil.getInstance().get((i + initialPosition) % length);
            if(null == message){
                break;
            }
            for(MessageInfoInterface each : result){
                if(each.getHttpAction().equals(message.getHttpAction())&&each.getUrl().equals(message.getHttpAction())){
                    if (message.isCommitted()) {
                        result.remove(each);
                    } else {
                        result.remove(each);
                        result.add(message);
                    }
                }
            }
            i = i + metadataSize;
            result.add(message);
        }
        return result;
    }
    
    /**
     * Get position for append.
     * 
     * @return position for append
     */
    public int getPositionForAppend() {
        return positionForAppendMetadata;
    }
    
    /**
     * Initialize metadata position.
     * 
     * @return metadata position
     */
    public int initializeMetadataPosition() {
        int length = metadataSize * size;
        int minimum = 0;
        MessageInfoInterface messageInfo = LogUtil.getInstance().get(0);
        if (null == messageInfo) {
            return minimum;
        }
        long timeStamp = messageInfo.getTimeStamp();
        for(int j = 0; j < length ; j=j+metadataSize){
            if (null == LogUtil.getInstance().get(j)) {
                return minimum;
            }
            if(timeStamp > LogUtil.getInstance().get(j).getTimeStamp()) {
                timeStamp = LogUtil.getInstance().get(j).getTimeStamp();
                minimum = j;
            }
        }
        positionForAppendMetadata = minimum;
        return minimum;
    }
    
    private LogMetadata getMetadata(final int position) {
        byte[] temp = new byte[metadataSize];
        metadataBuffer.position(position);
        metadataBuffer.get(temp, 0, metadataSize);
        ByteArrayInputStream bis = new ByteArrayInputStream(temp);
        ObjectInputStream ois;
        LogMetadata result = null;
        try {
            ois = new ObjectInputStream(bis);
            Object obj = ois.readObject();
            result = (LogMetadata) obj;
            ois.close();
            bis.close();
        } catch (IOException e) {
            metadataBuffer.position(position);
            logger.info(e.getMessage());
        } catch (ClassNotFoundException e) {
            metadataBuffer.position(position);
            logger.error(e.getMessage());
        }
        return result;
    }
    
    private void update(final MessageInfoInterface message, final LogMetadata logMetadata) {
        try {
            FileLock fl = logChannel.tryLock(logMetadata.getPosition(), logMetadata.getLength(), false);
            while(null == fl || (!fl.isValid())){
                fl = logChannel.tryLock(logMetadata.getPosition(), logMetadata.getLength(), false);
            }
            logBuffer.position(logMetadata.getPosition());
            logBuffer.put(objectToBytes(message));
            logBuffer.force();
            fl.release();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
    
    private MessageInfoInterface getMessage(final LogMetadata logMetadata) {
        byte[] temp = new byte[logMetadata.getLength()];
        logBuffer.position(logMetadata.getPosition());
        logBuffer.get(temp, 0, logMetadata.getLength());
        ByteArrayInputStream bis = new ByteArrayInputStream(temp);
        ObjectInputStream ois;
        MessageInfoInterface result = null;
        try {
            ois = new ObjectInputStream(bis);
            Object obj = ois.readObject();
            result = (MessageInfoInterface) obj;
            ois.close();
            bis.close();
        } catch (IOException e) {
            logger.error(e.getMessage());
        } catch (ClassNotFoundException e) {
            logger.error(e.getMessage());
        }
        return result;
    }
    
    private byte[] objectToBytes(final MessageInfoInterface obj) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] result = null;
        try {
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            oos.flush();
            result = bos.toByteArray();
            logger.info(obj.toString()+result.length);
            oos.close();
            bos.close();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return result;
    }
    
    private byte[] objectToBytes(final LogMetadata obj) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] result = null;
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
    
    private int add(final LogMetadata logMetadata) {
        int result = -1;
        byte[] bytes = objectToBytes(logMetadata);
        try {
            FileLock fl = metadataChannel.lock(metadataChannel.position(), size, false);
            while(null == fl || (!fl.isValid())){
                fl = metadataChannel.lock(metadataChannel.position(), size, false);
            }
            if ((positionForAppendMetadata+bytes.length) >= (size * metadataSize)) {
                positionForAppendMetadata = 0;
            }
            metadataBuffer.position(positionForAppendMetadata);
            metadataBuffer.put(bytes);
            metadataBuffer.force();
            fl.release();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        result = positionForAppendMetadata;
        positionForAppendMetadata = positionForAppendMetadata + metadataSize;
        return result;
    }
    
    private LogMetadata add(final MessageInfoInterface message) {
        byte[] bytes = objectToBytes(message);
        try {
            FileLock fl = logChannel.lock(logChannel.position(), size, false);
            while(null == fl || (!fl.isValid())){
                fl = logChannel.lock(logChannel.position(), size, false);
            }
            if ((positionForAppendMessage+bytes.length) >= (size * messageSize)) {
                positionForAppendMessage = 0;
            }
            logBuffer.position(positionForAppendMessage);
            logBuffer.put(bytes);
            logBuffer.force();
            fl.release();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        LogMetadata result = new LogMetadata(positionForAppendMessage, bytes.length, System.currentTimeMillis());
        positionForAppendMessage = positionForAppendMessage + bytes.length;
        return result;
    }
    
    private static class SingleHolder {
        private static String shortForLog = LOG_DIR + LOG_FILE_NAME;
        private static String shortForMetadata = LOG_DIR + META_FILE_NAME;
        private static final LogUtil INSTANCE = new LogUtil(shortForLog, shortForMetadata);
    }
    
    private static void createFile(String url) throws IOException{
        File directory = new File(LOG_DIR);
        if(!directory.exists()) {
            directory.mkdir();
        }
        File file = new File(url);
        if(!file.exists()){
            file.createNewFile();
        }
    }
    
}
