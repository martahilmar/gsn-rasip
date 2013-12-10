package org.liva.core;

import org.liva.core.util.changeNotification.ChangeEvent;

public class SingleInstanceHelperEvent implements ChangeEvent {
	private byte[] data;
	
	
	public SingleInstanceHelperEvent(byte[] data) {
		super();
		this.data = data;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}
	
	

}
