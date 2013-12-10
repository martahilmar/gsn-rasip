package org.liva.core.commands;

import java.net.InetAddress;

public class UDPCommandEndpoint extends CommandEndpoint {
	public static final int DEFAULT_TARGET_PORT = 25352;
	
	InetAddress address;
	int	port;
	
	public UDPCommandEndpoint(InetAddress address, int port) {
		super();
		this.address = address;
		this.port = port;
	}

	public UDPCommandEndpoint(InetAddress targetAddress) {
		this(targetAddress, DEFAULT_TARGET_PORT);
	}

	/**
	 * @return the address
	 */
	public InetAddress getAddress() {
		return address;
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}
	
	
	
	
}
