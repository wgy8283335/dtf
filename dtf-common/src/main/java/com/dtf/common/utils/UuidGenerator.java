package com.dtf.common.utils;

import java.util.UUID;

/**
 * UUID Generator.
 * 
 * @author wangguangyuan
 */
public final class UuidGenerator {

    /**
     * Generate uuid.
     * 
     * @return uuid in string
     */
    public static String generateUuid() {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return uuid;
    }

    /**
     * Generate long id.
     * 
     * @return long
     */
    public static long generateLongId() {
        return System.currentTimeMillis();
    }

}
