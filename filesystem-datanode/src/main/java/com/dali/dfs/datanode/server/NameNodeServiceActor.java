package com.dali.dfs.datanode.server;

import com.dali.dfs.namenode.rpc.model.HeartbeatRequest;
import com.dali.dfs.namenode.rpc.model.HeartbeatResponse;
import com.dali.dfs.namenode.rpc.model.RegisterRequest;
import com.dali.dfs.namenode.rpc.model.RegisterResponse;
import com.dali.dfs.namenode.rpc.service.NameNodeServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.netty.shaded.io.grpc.netty.NegotiationType;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;

/**
 * component responsible for only one namenode
 */
@Slf4j
public class NameNodeServiceActor {


	private static final String NAMENODE_HOSTNAME = "localhost";
	private static final Integer NAMENODE_PORT = 50070;

	private NameNodeServiceGrpc.NameNodeServiceBlockingStub namenode;

	public NameNodeServiceActor() {
		ManagedChannel channel = NettyChannelBuilder
				.forAddress(NAMENODE_HOSTNAME, NAMENODE_PORT)
				.negotiationType(NegotiationType.PLAINTEXT)
				.build();
		this.namenode = NameNodeServiceGrpc.newBlockingStub(channel);
	}


	/**
	 * 向自己负责通信的那个NameNode进行注册
	 */
	@SneakyThrows
	public void register()  {
		Thread registerThread = new RegisterThread();
		registerThread.start();
		registerThread.join();
	}

	/**
	 * 开启发送心跳的线程
	 */
	public void startHeartbeat() {
		new HeartbeatThread().start();
	}

	/**
	 * register to namenode
	 */
	class RegisterThread extends Thread{
		@Override
		public void run() {
			log.info("send RPC request to NameNode.......");
			String ip = "127.0.0.1";
			String hostname = "dfs-data-01";
			// 通过RPC接口发送到NameNode他的注册接口上去

			RegisterRequest request = RegisterRequest.newBuilder()
					.setIp(ip)
					.setHostname(hostname)
					.build();
			RegisterResponse response = namenode.register(request);
			log.info("NameNode response state：" + response.getStatus());
		}
	}
	
	/**
	 * responsible for register
	 *
	 */
	class HeartbeatThread extends Thread {

		@SneakyThrows
		@Override
		public void run() {
			while(true) {
				System.out.println("发送RPC请求到NameNode进行心跳.......");

				String ip = "127.0.0.1";
				String hostname = "dfs-data-01";
				// 通过RPC接口发送到NameNode他的注册接口上去

				HeartbeatRequest request = HeartbeatRequest.newBuilder()
						.setIp(ip)
						.setHostname(hostname)
						.build();
				HeartbeatResponse response = namenode.heartbeat(request);
				System.out.println("接收到NameNode返回的心跳响应：" + response.getStatus());

				Thread.sleep(30 * 1000); // 每隔30秒发送一次心跳到NameNode上去
			}
		}
		
	}
	
}
