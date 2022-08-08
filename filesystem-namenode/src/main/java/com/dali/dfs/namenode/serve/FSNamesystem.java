package com.dali.dfs.namenode.serve;

/**
 * 负责管理元数据的核心组件
 */
public class FSNamesystem {

	/**
	 * responsible for managing directory tree
	 */
	private FSDirectory directory;
	/**
	 * responsible for writing edits log to disk
	 */
	private FSEditlog editlog;
	
	public FSNamesystem() {
		this.directory = new FSDirectory();
		this.editlog = new FSEditlog();
	}
	
	/**
	 * create dir
	 */
	public Boolean mkdir(String path) throws Exception {
		this.directory.mkdir(path); 
		this.editlog.logEdit("创建了一个目录：" + path);   
		return true;
	}

}
