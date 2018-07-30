package com.coconason.dtf.common.object;


public interface MessageType {

    Byte BUSINESS_REQ = 0;

    Byte BUSINESS_RESP = 1;

    Byte BUSINESS_ONE_WAY = 2;

    Byte LOGIN_REQ = 3;

    Byte LOGIN_RESP = 4;

    Byte HEARTBEAT_REQ = 5;

    Byte HEARTBEAT_RESP = 6;
}

