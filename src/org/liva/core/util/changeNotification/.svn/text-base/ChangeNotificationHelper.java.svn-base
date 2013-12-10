package org.liva.core.util.changeNotification;

import java.util.ArrayList;


public class ChangeNotificationHelper {
	protected ArrayList<ChangeListener<?>> listeners = null;
	
	public ChangeNotificationHelper() {
		this.listeners	= new ArrayList<ChangeListener<?>>();
	}
	
	@SuppressWarnings("unchecked")
	public void addListener(ChangeListener<?> listener) {
		ArrayList<ChangeListener<?>> listeners;
		synchronized (this.listeners) {
			listeners = (ArrayList<ChangeListener<?>>) this.listeners.clone();
		
			if (listeners.contains(listener)) {
				return;
			}
			listeners.add(listener);
		
			this.listeners = listeners;
		}
	}

	@SuppressWarnings("unchecked")
	public void removeListener(ChangeListener<?> listener) {
		ArrayList<ChangeListener<?>> listeners;
		synchronized (this.listeners) {
			listeners = (ArrayList<ChangeListener<?>>) this.listeners.clone();
			listeners.remove(listener);
			this.listeners = listeners;
		}
	}
	
	@SuppressWarnings("unchecked")
	public void fireStateChanged(ChangeEvent event) {
		ArrayList<ChangeListener<?>> listeners;
		synchronized (this.listeners) {
			listeners = this.listeners;
		}
		for(@SuppressWarnings("rawtypes") ChangeListener listener : listeners) {
			listener.stateChanged(event);
		}
	}
}
