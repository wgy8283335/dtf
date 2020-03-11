package com.coconason.dtf.manager.log;

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

    private final int metadataSize = 90;
    
    private LogUtil(final String logFilePath, final String metadataFilePath) {
        this.logFilePath = logFilePath;
        Path filename = Paths.get(this.logFilePath);
        try {
            logChannel = FileChannel.open(filename, StandardOpenOption.WRITE, StandardOpenOption.READ);
            logBuffer = logChannel.map(FileChannel.MapMode.READ_WRITE, 0, 2048 * size);
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
     * Get the position of metadata buffer has been written.
     *
     * @return the position of metadata buffer has been written
     */
    public long getInitialLength() {
        try {
            return metadataChannel.size();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return -1L;
    }
    
    /**
     * Get the position of metadata buffer has been written.
     * 
     * @return the position of metadata buffer has been written
     */
    public int getPosition() {
        int result = metadataBuffer.position();
        return result;
    }
    
    /**
     * Append message in buffer.
     * 
     * @param message message information interface
     * @return position in log metadata
     */
    public int append(final MessageInfoInterface message) {
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
    public void updateCommitStatus(final MessageInfoInterface message) {
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
            logger.info(e.getMessage());
        } catch (ClassNotFoundException e) {
            logger.error(e.getMessage());
        }
        return result;
    }
    
    private void update(final MessageInfoInterface message, final LogMetadata logMetadata) {
        try {
            FileLock fl = logChannel.lock(logMetadata.getPosition(), logMetadata.getLength(), false);
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
            result = metadataBuffer.position();
            metadataBuffer.put(bytes);
            metadataBuffer.force();
            fl.release();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return result;
    }
    
    private LogMetadata add(final MessageInfoInterface message) {
        int position = -1;
        byte[] bytes = objectToBytes(message);
        try {
            FileLock fl = logChannel.lock(logChannel.position(), size, false);
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
    
    private static class SingleHolder {
        private static URL urlForLog = LogUtil.class.getClassLoader().getResource("logs/async-request.log");
        private static URL urlForMetadata = LogUtil.class.getClassLoader().getResource("logs/catalog.log");
        private static final LogUtil INSTANCE = new LogUtil(urlForLog.getPath(), urlForMetadata.getPath());
    }
    
}
