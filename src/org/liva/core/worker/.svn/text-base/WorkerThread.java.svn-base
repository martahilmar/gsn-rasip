package org.liva.core.worker;

import org.liva.core.util.changeNotification.ChangeNotificationHelper;
import org.liva.core.worker.WorkerEvent.WorkerEventReason;

public abstract class WorkerThread implements Runnable, Worker {
	
	private ChangeNotificationHelper notificationHelper;
	
	private Object mutex;
	
	private boolean starting;
	private boolean stopping;
	private boolean running;
	
	public WorkerThread() {
		notificationHelper = new ChangeNotificationHelper();
		mutex = new Object();
		starting = false;
		stopping = false;
		running = false;
	}
	
	@Override
	public void startAsync() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				start();
			}
		}).start();
	}
	
	/* (non-Javadoc)
	 * @see org.liva.core.Worker#start()
	 */
	@Override
	public void start() {
		synchronized (mutex) {
			if (isStopping()) {
				setStopping(false);
			}
			if (isRunning() || isStarting()) {
				return;
			}
			setStarting(true);
			new Thread(this).start();
		}
	}
	
	@Override
	public void stopAsync() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				stop();
			}
		}).start();
	}
	
	/* (non-Javadoc)
	 * @see org.liva.core.Worker#stop()
	 */
	@Override
	public void stop() {
		synchronized (mutex) {
			if (isRunning() || isStarting()) {
				setStopping(true);
			}
		}
	}
	
	
	

	@Override
	public void run() {
		synchronized (mutex) {
			setRunning(true);
			setStarting(false);
		}
		
		doWork();
		
		synchronized (mutex) {
			setRunning(false);
			setStopping(false);
		}
	}
	
	public abstract void doWork();

	/* (non-Javadoc)
	 * @see org.liva.core.Worker#isStarting()
	 */
	@Override
	public boolean isStarting() {
		return starting;
	}

	private void setStarting(boolean starting) {
		this.starting = starting;
	}

	/* (non-Javadoc)
	 * @see org.liva.core.Worker#isStopping()
	 */
	@Override
	public boolean isStopping() {
		return stopping;
	}

	private void setStopping(boolean stopping) {
		this.stopping = stopping;
	}

	/* (non-Javadoc)
	 * @see org.liva.core.Worker#isRunning()
	 */
	@Override
	public boolean isRunning() {
		return running;
	}

	private void setRunning(boolean running) {
		this.running = running;
	}

	public Object getMutex() {
		return mutex;
	}

	public void addWorkerListener(WorkerListener listener) {
		notificationHelper.addListener(listener);
	}

	public void removeWorkerListener(WorkerListener listener) {
		notificationHelper.removeListener(listener);
	}
	
	protected void notifyWorkProgress(int progress) {
		notificationHelper.fireStateChanged(new WorkerEvent(WorkerEventReason.PROGRESS, progress, null));
	}
	
	protected void notifyWorkProgressMessage(int progress, String message) {
		notificationHelper.fireStateChanged(new WorkerEvent(WorkerEventReason.PROGRESS_AND_MESSAGE, progress, message));
	}
	
	protected void notifyMessage(String message) {
		notificationHelper.fireStateChanged(new WorkerEvent(WorkerEventReason.MESSAGE, -1, message));
	}
	
	protected void notifyWarning(String message) {
		notificationHelper.fireStateChanged(new WorkerEvent(WorkerEventReason.WARNING, -1, message));
	}
	
	protected void notifyError(String message) {
		notificationHelper.fireStateChanged(new WorkerEvent(WorkerEventReason.ERROR, -1, message));
	}

	
}
