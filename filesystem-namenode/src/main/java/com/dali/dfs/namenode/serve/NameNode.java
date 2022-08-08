package com.dali.dfs.namenode.serve;

/**
 * NameNode start class
 *
 */
public class NameNode {

	/**
	 * NameNode isRun
	 */
	private volatile Boolean isRun;
	/**
	 * responsible data core conponment
	 */
	private FSNamesystem namesystem;
	/**
	 * responsible for all datanode
	 */
	private DataNodeManager datanodeManager;
	/**
	 * provide rpc
	 */
	private NameNodeRpcServer nameNodeRpcServer;
	
	public NameNode() {
		this.isRun = true;
	}
	
	/**
	 * initialize NameNode
	 */
	private void initialize() {
		this.namesystem = new FSNamesystem();
		this.datanodeManager = new DataNodeManager();
		this.nameNodeRpcServer = new NameNodeRpcServer(this.namesystem, this.datanodeManager);
	}

	private void start() {
		this.nameNodeRpcServer.start();;
		this.nameNodeRpcServer.blockUntilShutdown();;
	}

	public static void main(String[] args) throws Exception {		
		NameNode namenode = new NameNode();
		namenode.initialize();
		namenode.start();
	}



}
