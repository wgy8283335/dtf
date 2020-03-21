package com.coconason.dtf.manager.log;

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
import java.nio.file.StandardOpenOption;

public final class LogUtilForSyncApproveSubmit {
    
    /**
     * Logger of log utility.
     */
    private Logger logger = LoggerFactory.getLogger(LogUtilForSyncApproveSubmit.class);
    
    private FileChannel logChannel;
    
    private MappedByteBuffer logBuffer;
    
    private String logFilePath;
    
    private final int size = 100000;
    
    private final int messageSize = 2048;
    
    private int positionForAppendMessage = 0;
    
    private static final String LOG_DIR="./logs/";
    
    private static final String LOG_FILE_NAME="sync-approve-submit.log";
    
    private LogUtilForSyncApproveSubmit(final String logFilePath) {
        try {
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
    }
    
    /**
     * Get single instance of LogUtil.
     * 
     * @return object of LogUtil
     */
    public static LogUtilForSyncApproveSubmit getInstance() {
        return LogUtilForSyncApproveSubmit.SingleHolder.INSTANCE;
    }
    
    /**
     * Append message in buffer.
     * 
     * @param message message information interface
     * @return position in log metadata
     */
    public synchronized void append(final String message) {
        byte[] bytes = message.getBytes();
        try {
            FileLock fl = logChannel.lock(logChannel.position(), size, false);
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
        positionForAppendMessage = positionForAppendMessage + messageSize;
    }

    public String getMessage(final int position, final int size) {
        byte[] temp = new byte[size];
        logBuffer.position(position);
        logBuffer.get(temp, 0, size);
        String result = new String(temp);
        return result;
    }
    
    private static class SingleHolder {
        private static String shortForLog = LOG_DIR + LOG_FILE_NAME;
        private static final LogUtilForSyncApproveSubmit INSTANCE = new LogUtilForSyncApproveSubmit(shortForLog);
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
