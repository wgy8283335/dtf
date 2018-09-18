package com.coconason.dtf.client.core.utils;

/**
 * @Author: Jason
 * @date: 2018/8/21-7:26
 */
public class GroupidGenerator {

    public static String getStringId(long workerId, long datacenterId){
        return String.valueOf(getId(workerId, datacenterId));
    }
    public static long getId(long workerId, long datacenterId){
        return SnowflakeIdWorker.nextId(workerId, datacenterId);
    }
    static class SnowflakeIdWorker {

        // ==============================Fields===========================================
        /** start time stamp (2015-01-01) */
        private static final long twepoch = 1420041600000L;

        /** work id bit width */
        private static final long workerIdBits = 5L;

        /** data center id bit width */
        private static final long datacenterIdBits = 5L;

        /** max work id is 31. bit shift operation could calculate the maximum value of the bit width*/
        private static final long maxWorkerId = -1L ^ (-1L << workerIdBits);

        /** max data center id is 31 */
        private static final long maxDatacenterId = -1L ^ (-1L << datacenterIdBits);

        /** sequence id bit width */
        private static final long sequenceBits = 12L;

        /** worker ID shift left 12 bits */
        private static final long workerIdShift = sequenceBits;

        /** datacenter id shift left 17 bits */
        private static final long datacenterIdShift = sequenceBits + workerIdBits;

        /** time stamp shift left 22 bits */
        private static final long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;

        /** generator sequence mask , this is 4095 (0b111111111111=0xfff=4095) */
        private static final long sequenceMask = -1L ^ (-1L << sequenceBits);

        /** worker ID(0~31) */
        private static long workerId;

        /** datacenter ID(0~31) */
        private static long datacenterId;

        /** sequence in millisecond(0~4095) */
        private static long sequence = 0L;

        /** the time stamp of the latest generated*/
        private static long lastTimestamp = -1L;

        // ==============================Methods==========================================
        /**
         * accquire a next ID (the method is thread safe)
         * @return SnowflakeId
         */
        public static long nextId(long workerIdTemp, long datacenterIdTemp) {
            if (workerId > maxWorkerId || workerId < 0) {
                throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0", maxWorkerId));
            }
            if (datacenterId > maxDatacenterId || datacenterId < 0) {
                throw new IllegalArgumentException(String.format("datacenter Id can't be greater than %d or less than 0", maxDatacenterId));
            }
            workerId = workerIdTemp;
            datacenterId = datacenterIdTemp;

            long timestamp = timeGen();

            //if current time is less than the last time stamp,means the system clock has been rolled back.Should throws exception.
            if (timestamp < lastTimestamp) {
                throw new RuntimeException(
                        String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
            }

            //if timestamp are generated in the same time, generate sequence in millisecond.
            if (lastTimestamp == timestamp) {
                sequence = (sequence + 1) & sequenceMask;
                //over flow sequence in millisecond.
                if (sequence == 0) {
                    //block until acquire new timestamp.
                    timestamp = tilNextMillis(lastTimestamp);
                }
            }
            //if timestamp changed, reset sequence in millisecond.
            else {
                sequence = 0L;
            }

            //the timestamp of the last generated id.
            lastTimestamp = timestamp;

            // calculate 64 bit id
            return ((timestamp - twepoch) << timestampLeftShift)|(datacenterId << datacenterIdShift)|(workerId << workerIdShift)|sequence;
        }

        /**
         * block until acquire new timestamp.
         * @param lastTimestamp
         * @return
         */
        protected static long tilNextMillis(long lastTimestamp) {
            long timestamp = timeGen();
            while (timestamp <= lastTimestamp) {
                timestamp = timeGen();
            }
            return timestamp;
        }

        /**
         * return current time in millisecond.
         * @return current time in millisecond
         */
        protected static long timeGen() {
            return System.currentTimeMillis();
        }
    }
}
