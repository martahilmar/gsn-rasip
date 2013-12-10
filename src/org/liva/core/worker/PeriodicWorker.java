package org.liva.core.worker;

public interface PeriodicWorker extends Worker {

	public abstract long getInterval();

	public abstract void setInterval(long interval);

}