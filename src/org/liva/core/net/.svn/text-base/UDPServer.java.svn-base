package org.liva.core.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import org.liva.core.worker.WorkerThread;

public abstract class UDPServer extends WorkerThread implements Runnable {

	
		
	public static final int DEFAULT_DATAGRAM_BUFFER_SIZE = 256;
	private int port;
	private InetAddress inetAddress;
	private int datagramBufferSize;
	DatagramSocket serverSocket;
	
	private UDPServer() {
		super();
		datagramBufferSize = UDPServer.DEFAULT_DATAGRAM_BUFFER_SIZE;
		this.inetAddress = null;
	}
	
	
	public UDPServer(InetAddress inetAddress, int port) {
		this();
		this.inetAddress = inetAddress;
		this.port = port;
	}
	
	public UDPServer(int port) {
		this();
		this.port = port;
	}



	public void start() {
		try {
			if (inetAddress != null) {
				serverSocket = new DatagramSocket(port, inetAddress);
			} else {
				serverSocket = new DatagramSocket(port);
			}
			super.start();
		} catch (SocketException se) {
			se.printStackTrace();
		}
	}
	
	

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}


	/**
	 * @param port the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}


	/**
	 * @return the inetAddress
	 */
	public InetAddress getInetAddress() {
		return inetAddress;
	}


	/**
	 * @param inetAddress the inetAddress to set
	 */
	public void setInetAddress(InetAddress inetAddress) {
		this.inetAddress = inetAddress;
	}

	/**
	 * @return the datagramBufferSize
	 */
	public int getDatagramBufferSize() {
		return datagramBufferSize;
	}


	/**
	 * @param datagramBufferSize the datagramBufferSize to set
	 */
	public void setDatagramBufferSize(int datagramBufferSize) {
		synchronized (getMutex()) {
			if (isRunning()) {
				this.datagramBufferSize = datagramBufferSize;
			}
		}
	}


	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void doWork() {
		while (!isStopping()) {
			byte[] buffer = new byte[datagramBufferSize];
			DatagramPacket packet = new DatagramPacket(buffer, datagramBufferSize);
			try {
				serverSocket.receive(packet);
				DatagramPacket responsePacket = handleDatagramRecieved(packet);
				
				if (responsePacket != null) {
					serverSocket.send(responsePacket);
				}
				
			} catch (IOException ioe) {
				ioe.printStackTrace();
			} finally {
				if (serverSocket != null) {
					//serverSocket.close();
				}
			}
		}
	}
	
	
	
	protected abstract DatagramPacket handleDatagramRecieved(DatagramPacket receivedPacket);

}
