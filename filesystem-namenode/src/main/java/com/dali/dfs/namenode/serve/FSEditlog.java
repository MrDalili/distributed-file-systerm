package com.dali.dfs.namenode.serve;

import lombok.AllArgsConstructor;

import java.util.LinkedList;

/**
 * responsible for edits log
 *
 */
public class FSEditlog {

	/**
	 * id
	 */
	private long txidSeq = 0L;

	/**
	 * double buffer in memory
	 */
	private DoubleBuffer editLogBuffer = new DoubleBuffer();
	/**
	 * should take logs to disk
	 */
	private volatile Boolean isSyncRunning = false;
	/**
	 * is have edits log in memory and wait to take to disk
	 */
	private volatile Boolean isWaitSync = false;
	/**
	 * the biggest id in disk
	 */
	private volatile Long syncMaxTxid = 0L;
	/**
	 * dupilcate for  each threads
	 */
	private ThreadLocal<Long> localTxid = new ThreadLocal<Long>();

	
	/**
	 * record log
	 */
	public void logEdit(String content) {
		synchronized(this) {
			txidSeq++;
			long txid = txidSeq;
			localTxid.set(txid);

			EditLog log = new EditLog(txid, content);
			editLogBuffer.write(log);  
		}
		
		logSync();
	}
	
	/**
	 * 将内存缓冲中的数据刷入磁盘文件中
	 * 在这里尝试允许某一个线程一次性将内存缓冲中的数据刷入磁盘文件中
	 * 相当于实现一个批量将内存缓冲数据刷磁盘的过程
	 */
	private void logSync() {
		// 再次尝试加锁
		synchronized(this) {
			// 如果说当前正好有人在刷内存缓冲到磁盘中去
			if(isSyncRunning) {
				// if the txid is less than current id, continue
				long txid = localTxid.get(); // 获取到本地线程的副本
				if(txid <= syncMaxTxid) {
					return;
				}

				if(isWaitSync) {
					return;
				}
				isWaitSync = true;
				while(isSyncRunning) {
					try {
						wait(2000);
					} catch (Exception e) {
						e.printStackTrace();  
					}
				}
				isWaitSync = false;
			}
			
			editLogBuffer.setReadyToSync();

			syncMaxTxid = editLogBuffer.getSyncMaxTxid();
			isSyncRunning = true;
		}

		editLogBuffer.flush();  
		
		synchronized(this) {
			isSyncRunning = false;
			notifyAll();
		}
	}
	
	@AllArgsConstructor
	class EditLog {
	
		long txid;
		String content;
		
	}

	class DoubleBuffer {
		

		LinkedList<EditLog> currentBuffer = new LinkedList<EditLog>();

		LinkedList<EditLog> syncBuffer = new LinkedList<EditLog>();
		

		public void write(EditLog log) {
			currentBuffer.add(log);
		}
		

		public void setReadyToSync() {
			LinkedList<EditLog> tmp = currentBuffer;
			currentBuffer = syncBuffer;
			syncBuffer = tmp;
		}
		

		public Long getSyncMaxTxid() {
			return syncBuffer.getLast().txid;
		}
		

		public void flush() {
			for(EditLog log : syncBuffer) {
				System.out.println("edit log to disk：" + log);
			}
			syncBuffer.clear();  
		}
		
	}
	
}
