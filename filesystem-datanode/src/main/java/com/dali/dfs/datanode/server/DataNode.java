package com.dali.dfs.datanode.server;

import lombok.Data;
import lombok.SneakyThrows;

/**
 * DataNode start class
 */

@Data
public class DataNode {

    /**
     * isRun
     */
    private volatile Boolean isRun;

    /**
     * responsible for communicating with NameNode
     */
    private NameNodeOfferService offerService;

    /**
     * initialize DataNode
     */
    private void initialize() {
        this.isRun = true;
        this.offerService = new NameNodeOfferService();
        this.offerService.start();
    }

    /**
     * run DataNode
     */
    @SneakyThrows
    private void run() {
        Thread.sleep(1000);
    }

    public static void main(String[] args) {
        DataNode datanode = new DataNode();
        datanode.initialize();
        datanode.run();
    }


}
