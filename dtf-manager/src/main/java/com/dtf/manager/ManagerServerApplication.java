package com.dtf.manager;

import com.dtf.manager.protobufserver.NettyServer;

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
