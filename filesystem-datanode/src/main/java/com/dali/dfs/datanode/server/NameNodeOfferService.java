package com.dali.dfs.datanode.server;

import lombok.SneakyThrows;

import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;

/**
 * component responsible for a namenode group
 */
public class NameNodeOfferService {

	/**
	 * responsible for NameNode master communicate
	 */
	private NameNodeServiceActor serviceActor;

	/**
	 * record NameNodeServiceActor list
	 */
	private CopyOnWriteArrayList<NameNodeServiceActor> serviceActors;

	public NameNodeOfferService() {
		this.serviceActor = new NameNodeServiceActor();
	}
	
	/**
	 * start OfferService
	 */
	public void start() {
		// use two ServiceActor register to the master and standby node
		register();
		// send heartbeat to namenode
		startHeartbeat();
	}

	/**
	 * send heartbeat request to namenode
	 */
	private void startHeartbeat() {
		this.serviceActor.startHeartbeat();
	}

	private void register() {
		this.serviceActor.register();
	}
	
	/**
	 * shut down ServiceActor
	 * @param serviceActor
	 */
	public void shutdown(NameNodeServiceActor serviceActor) { 
		this.serviceActors.remove(serviceActor);
	}
	
	/**
	 * iterate ServiceActor
	 */
	public void iterateServiceActors() {
		Iterator<NameNodeServiceActor> iterator = serviceActors.iterator();
		while(iterator.hasNext()) {
			iterator.next();
		}
	}
	  
}
