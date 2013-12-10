package org.liva.core.worker;

public abstract class PeriodicWorkerThread extends WorkerThread implements PeriodicWorker {
	
	private long interval;

	public PeriodicWorkerThread(long interval) {
		super();
		this.interval = interval;
	}

	@Override
	public final void doWork() {
		long realInterval = -1;
		while(!isStopping()) {
			doPeriodicWork(realInterval);
			notifyWorkProgress();
			long startTime = System.currentTimeMillis();
			try {
				Thread.sleep(interval);
			} catch (InterruptedException e) {}
			long endTime = System.currentTimeMillis();
			realInterval = endTime - startTime;
		}
	}
	
	
	
	/* (non-Javadoc)
	 * @see org.liva.core.PeriodicWorker#getInterval()
	 */
	@Override
	public long getInterval() {
		return interval;
	}

	/* (non-Javadoc)
	 * @see org.liva.core.PeriodicWorker#setInterval(long)
	 */
	@Override
	public void setInterval(long interval) {
		this.interval = interval;
	}
	
	@Override
	protected void notifyWorkProgress(int progress) {
		// TODO Auto-generated method stub
		super.notifyWorkProgress(-1);
	}
	
	protected void notifyWorkProgress() {
		// TODO Auto-generated method stub
		super.notifyWorkProgress(-1);
	}

	protected void notifyWorkProgressMessage(String message) {
		// TODO Auto-generated method stub
		super.notifyWorkProgressMessage(-1, message);
	}

	public abstract void doPeriodicWork(long realInterval);

}
