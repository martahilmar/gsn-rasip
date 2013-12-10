package org.liva.core.worker;

import org.liva.core.util.changeNotification.ChangeEvent;

public class WorkerEvent implements ChangeEvent {
	private int progress;
	private WorkerEventReason reason;
	private String message;
	
	
	
	public WorkerEvent(WorkerEventReason reason, int progress, String message) {
		super();
		this.reason = reason;
		this.progress = progress;
		this.message = message;
	}



	public int getProgress() {
		return progress;
	}



	public WorkerEventReason getReason() {
		return reason;
	}



	public String getMessage() {
		return message;
	}



	public enum WorkerEventReason {
		PROGRESS,
		PROGRESS_AND_MESSAGE,
		MESSAGE,
		WARNING,
		ERROR
	}
}
