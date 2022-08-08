package com.dali.dfs.namenode.serve;

import lombok.Data;

import java.util.LinkedList;
import java.util.List;

/**
 * manage the directory tree
 */
public class FSDirectory {
	
	/**
	 * file directory tree
	 */
	private INodeDirectory dirTree;
	
	public FSDirectory() {
		this.dirTree = new INodeDirectory("/");  
	}
	
	/**
	 * 创建目录
	 * @param path 目录路径
	 */
	public void mkdir(String path) {

		synchronized(dirTree) {
			String[] pathes = path.split("/");
			INodeDirectory parent = dirTree;
			
			for(String splitedPath : pathes) {
				if(splitedPath.trim().equals("")) {
					continue;
				}
				
				INodeDirectory dir = findDirectory(parent, splitedPath);
				if(dir != null) {
					parent = dir;
					continue;
				}
				
				INodeDirectory child = new INodeDirectory(splitedPath); 
				parent.addChild(child);  
			}
		}
	}
	
	/**
	 * recursive the file directory tree
	 * @param dir
	 * @param path
	 * @return
	 */
	private INodeDirectory findDirectory(INodeDirectory dir, String path) {
		if(dir.getChildren().size() == 0) {
			return null;
		}
		
		INodeDirectory resultDir = null;
		
		for(INode child : dir.getChildren()) {
			if(child instanceof INodeDirectory) {
				INodeDirectory childDir = (INodeDirectory) child;
				
				if((childDir.getPath().equals(path))) {
					return childDir;
				} 
				
				resultDir = findDirectory(childDir, path);
				if(resultDir != null) {
					return resultDir;
				}
			}
		}
		
		return null;
	}
	
	
	/**
	 * a node in directory
	 */
	private interface INode {
		
	}
	
	/**
	 * a directory in tree
	 *
	 */
	@Data
	public static class INodeDirectory implements INode {
		
		private String path;
		private List<INode> children;
		
		public INodeDirectory(String path) {
			this.path = path;
			this.children = new LinkedList<INode>();
		}
		
		public void addChild(INode inode) {
			this.children.add(inode);
		}

	}


	/**
	 * a file in tree
	 */
	@Data
	public static class INodeFile implements INode {
		
		private String name;

	}
	
}
