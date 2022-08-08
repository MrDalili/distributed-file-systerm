package com.dali.dfs.namenode.serve;

import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * responsible for registered datanode
 */
public class DataNodeManager {

	/**
	 * registered datanode map
	 */
	private Map<String, DataNodeInfo> datanodes =
			new ConcurrentHashMap<String, DataNodeInfo>();
	
	/**
	 * datanode registering
	 * @param ip 
	 * @param hostname
	 */
	public Boolean register(String ip, String hostname) {
		DataNodeInfo datanode = new DataNodeInfo(ip, hostname);
		datanodes.put(ip + "-" + hostname, datanode);
		return true;
	}

	/**
	 * datanode heartbeat
	 * @param ip
	 * @param hostname
	 * @return
	 */
	public Boolean heartbeat(String ip, String hostname) {
		DataNodeInfo datanode = datanodes.get(ip + "-" + hostname);
		datanode.setLatestHeartbeatTime(System.currentTimeMillis());
		return true;
	}

	class DataNodeAliveMonitor extends Thread{
		@SneakyThrows
		@Override
		public void run() {
			while (true) {
				List<String> toRemoveDataModes = datanodes.values().stream().filter(dataNodeInfo ->
						System.currentTimeMillis() - dataNodeInfo.getLatestHeartbeatTime() > 90 * 1000)
						.map(dataNodeInfo -> {return dataNodeInfo.getIp() + "-" + dataNodeInfo.getHostname();})
						.collect(Collectors.toList());
				toRemoveDataModes.forEach(datanode -> datanodes.remove(datanode));

				Thread.sleep(30 * 1000);
			}
		}
	}
}
