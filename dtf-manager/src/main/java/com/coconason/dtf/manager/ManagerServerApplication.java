package com.coconason.dtf.manager;

import com.coconason.dtf.manager.protobufserver.NettyServer;

public class ManagerServerApplication {
    
    /**
     * Entrance of Server.
     * 
     * @param args parameters in command line
     * @throws Exception exception
     */
    public static void main(final String[] args) throws Exception {
        NettyServer.main();
    }
    
}
