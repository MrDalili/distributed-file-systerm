package com.dali.dfs.namenode.serve;

import com.dali.dfs.namenode.rpc.service.NameNodeServiceGrpc;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * rpc about namenode
 */
@Slf4j
public class NameNodeRpcServer {

	private static final int DEFAULT_PORT = 50070;

	private Server server = null;

	/**
	 * responsible for namenode
	 */
	private FSNamesystem namesystem;

	private DataNodeManager datanodeManager;
	
	public NameNodeRpcServer(
			FSNamesystem namesystem,
			DataNodeManager datanodeManager) {
		this.namesystem = namesystem;
		this.datanodeManager = datanodeManager;
	}
	

	public Boolean mkdir(String path) throws Exception {
		return this.namesystem.mkdir(path);
	}
	

	public Boolean register(String ip, String hostname) throws Exception {
		return datanodeManager.register(ip, hostname);
	}
	
	/**
	 * 启动这个rpc server
	 */
	@SneakyThrows
	public void start() {
		server = ServerBuilder.forPort(DEFAULT_PORT)
				.addService(new NameNodeServiceImpl(namesystem,datanodeManager))
				.build()
				.start();
		log.info("start NameNodeRPCServer, listening....");

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				NameNodeRpcServer.this.stop();
			}
		});
	}

	public void stop() {
		if (server != null) {
			server.shutdown();
		}
	}

	@SneakyThrows
	public void blockUntilShutdown(){
		if (server != null) {
			server.awaitTermination();
		}
	}
}
