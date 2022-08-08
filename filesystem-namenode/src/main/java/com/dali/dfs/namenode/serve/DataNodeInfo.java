package com.dali.dfs.namenode.serve;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * record datanode information
 */
@Data
@AllArgsConstructor
public class DataNodeInfo {

	private String ip;
	private String hostname;
	private long latestHeartbeatTime;

	public DataNodeInfo(String ip, String hostname){
		this.ip = ip;
		this.hostname = hostname;
	}

}
