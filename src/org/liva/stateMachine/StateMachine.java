package org.liva.stateMachine;

import org.liva.core.worker.PeriodicWorkerThread;

public class StateMachine <T> extends PeriodicWorkerThread {
	private T context;
	private State state;
	
	public StateMachine(State initialState) {
		super(100);
		this.state = initialState;
	}
	
	public StateMachine(State initialState, long interval) {
		super(interval);
		this.state = initialState;
	}
	
	

	@Override
	public void doPeriodicWork(long realInterval) {
		if (state == null) {
			// TODO exception...
			stop();
			return;
		}
		state = state.runState(); 
		
	}

	public T getContext() {
		return context;
	}

	public void setContext(T context) {
		this.context = context;
	}
	
	
	
}
