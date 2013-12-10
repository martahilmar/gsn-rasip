package org.liva.core.worker;

public interface Worker {

	public abstract void startAsync();
	
	public abstract void start();
	
	public abstract void stopAsync();

	public abstract void stop();

	public abstract boolean isStarting();

	public abstract boolean isStopping();

	public abstract boolean isRunning();
	
	public void addWorkerListener(WorkerListener listener);

	public void removeWorkerListener(WorkerListener listener);
}