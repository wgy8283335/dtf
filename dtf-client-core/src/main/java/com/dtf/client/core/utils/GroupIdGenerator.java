package com.dtf.client.core.utils;

/**
 * Generate snowflake id.
 * 
 * @Author: wangguangyuan
 */
public class GroupIdGenerator {
    
    /**
     * Generate snowflake id according to worker id and data center id.
     * 
     * @param workerId work id
     * @param dataCenterId data center id
     * @return snow flake id
     */
    public static String getStringId(final long workerId, final long dataCenterId) {
        return String.valueOf(getId(workerId, dataCenterId));
    }
    
    private static long getId(final long workerId, final long dataCenterId) {
        return SnowflakeIdWorker.nextId(workerId, dataCenterId);
    }
    
    private static class SnowflakeIdWorker {
        
        /** start time stamp (2015-01-01). */
        private static final long TW_EPOCH = 1420041600000L;

        /** work id bit width. */
        private static final long WORKER_ID_BITS = 5L;

        /** data center id bit width. */
        private static final long DATA_CENTER_ID_BITS = 5L;

        /** max work id is 31. bit shift operation could calculate the maximum value of the bit width. */
        private static final long MAX_WORKER_ID = -1L ^ (-1L << WORKER_ID_BITS);

        /** max data center id is 31. */
        private static final long MAX_DATA_CENTER_ID = -1L ^ (-1L << DATA_CENTER_ID_BITS);

        /** sequence id bit width. */
        private static final long SEQUENCE_BITS = 12L;

        /** worker ID shift left 12 bits. */
        private static final long WORKER_ID_SHIFT = SEQUENCE_BITS;

        /** datacenter id shift left 17 bits. */
        private static final long DATA_CENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;

        /** time stamp shift left 22 bits. */
        private static final long TIME_STAMP_LEFT_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATA_CENTER_ID_BITS;

        /** generator sequence mask , this is 4095 (0b111111111111=0xfff=4095). */
        private static final long SEQUENCE_MASK = -1L ^ (-1L << SEQUENCE_BITS);

        /** worker ID(0~31). */
        private static long workerId;

        /** datacenter ID(0~31). */
        private static long dataCenterId;

        /** sequence in millisecond(0~4095). */
        private static long sequence;

        /** the time stamp of the latest generated. */
        private static long lastTimestamp = -1L;
        
        /**
         * accquire a next ID (the method is thread safe).
         * 
         * @param workerIdTemp worker id
         * @param dataCenterIdTemp data center id
         * @return SnowflakeId
         */
        static long nextId(final long workerIdTemp, final long dataCenterIdTemp) {
            if (workerId > MAX_WORKER_ID || workerId < 0) {
                throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0", MAX_WORKER_ID));
            }
            if (dataCenterId > MAX_DATA_CENTER_ID || dataCenterId < 0) {
                throw new IllegalArgumentException(String.format("datacenter Id can't be greater than %d or less than 0", MAX_DATA_CENTER_ID));
            }
            workerId = workerIdTemp;
            dataCenterId = dataCenterIdTemp;
            
            long timestamp = timeGen();
            
            if (timestamp < lastTimestamp) {
                throw new RuntimeException(
                        String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
            }
            if (lastTimestamp == timestamp) {
                sequence = (sequence + 1) & SEQUENCE_MASK;
                if (sequence == 0) {
                    timestamp = tilNextMillis(lastTimestamp);
                }
            } else {
                sequence = 0L;
            }
            lastTimestamp = timestamp;
            return ((timestamp - TW_EPOCH) << TIME_STAMP_LEFT_SHIFT) | (dataCenterId << DATA_CENTER_ID_SHIFT) | (workerId << WORKER_ID_SHIFT) | sequence;
        }
        
        /**
         * block until acquire new timestamp.
         * 
         * @param lastTimestamp last time stamp
         * @return time stamp
         */
        private static long tilNextMillis(final long lastTimestamp) {
            long timestamp = timeGen();
            while (timestamp <= lastTimestamp) {
                timestamp = timeGen();
            }
            return timestamp;
        }
        
        /**
         * return current time in millisecond.
         * 
         * @return current time in millisecond
         */
        private static long timeGen() {
            return System.currentTimeMillis();
        }
    }
}
