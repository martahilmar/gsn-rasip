/**
 * 
 */
package org.liva.core.commands;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

import org.liva.core.net.UDPServer;

/**
 * @author Luka Lednicki
 *
 */
public abstract class UDPCommandServer extends UDPServer implements CommandServer{
	private static final int DEFAULT_RESPONSE_TIMEOUT = 5000;
	
	private InetAddress localSendAddress = null;
	private int localSendPort = 0;
	private int responseTimeout = UDPCommandServer.DEFAULT_RESPONSE_TIMEOUT;
	
	
	
	public UDPCommandServer(InetAddress inetAddress, int port, int dataBufferSize, InetAddress localSendAddress, int localSendPort) {
		this(inetAddress, port, dataBufferSize);
		this.localSendAddress = localSendAddress;
		this.localSendPort = localSendPort;
	}
	
	
	public UDPCommandServer(InetAddress inetAddress, int port, int dataBufferSize) {
		super(inetAddress, port);
		this.setDatagramBufferSize(dataBufferSize);
		
		this.localSendAddress = inetAddress;
		localSendPort = 0;
	}

	/* (non-Javadoc)
	 * @see org.liva.core.net.UDPServer#handleDatagramRecieved(java.net.DatagramPacket)
	 */
	@Override
	protected DatagramPacket handleDatagramRecieved(
			DatagramPacket receivedPacket) {
		DatagramPacket responsePacket = null;
		byte[] data = receivedPacket.getData();
		try {
			Command c = getCommandRegistry().getCommandByBinary(data);
			c.setSender(new UDPCommandEndpoint(receivedPacket.getAddress(), receivedPacket.getPort()));
			Command responseCommand = handleCommand(c);
			if (responseCommand != null) {
				// TODO 1 should port be defined this way?
				//responsePacket = getDatagram(responseCommand, receivedPacket.getAddress(), receivedPacket.getPort());
				//InetAddress returnAddress = ((UDPCommandEndpoint) c.getSender()).getAddress();
				//sendCommand(responseCommand, new UDPCommandEndpoint(returnAddress, 25352));
				// TODO 5 response is sent from port 25352, is that ok?
				responsePacket = getDatagram(responseCommand, ((UDPCommandEndpoint) c.getSender()).getAddress(), 25352);
			}
		} catch (CommandException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return responsePacket;
	}

	/**
	 * @return the commandRegistry
	 */
	public abstract CommandRegistry getCommandRegistry();

	public static DatagramPacket getDatagram(Command command, InetAddress targetAddress, int targetPort) {
		byte[] binaryCommand = command.getBinary();
		return new DatagramPacket(binaryCommand, binaryCommand.length, targetAddress, targetPort);
	}

	/* (non-Javadoc)
	 * @see org.liva.core.commands.CommandServer#sendCommand(org.liva.core.commands.Command, org.liva.core.commands.CommandEndpoint)
	 */
	@Override
	public void sendCommand(Command command, CommandEndpoint endpoint) throws Exception {
		if (endpoint instanceof UDPCommandEndpoint) {
			sendCommandUDP(command, (UDPCommandEndpoint) endpoint, getLocalSendAddress(), getLocalSendPort());
		}
	}
	
	public static void sendCommandUDP(Command command, UDPCommandEndpoint endpoint, InetAddress localAddress, int localPort) throws Exception {
		DatagramSocket s = new DatagramSocket(localPort, localAddress);
		DatagramPacket p = getDatagram(command, endpoint.getAddress(), endpoint.getPort());
		s.connect(endpoint.getAddress(), endpoint.getPort());
		s.send(p);
	}
	
	public static void sendCommandUDP(Command command, UDPCommandEndpoint endpoint) throws Exception {
		sendCommandUDP(command, endpoint, InetAddress.getLocalHost(), 0);
	}

	/* (non-Javadoc)
	 * @see org.liva.core.commands.CommandServer#sendCommandWithResponse(org.liva.core.commands.Command, org.liva.core.commands.CommandEndpoint)
	 */
	@Override
	public Command sendCommandWithResponse(Command command,
			CommandEndpoint endpoint) throws Exception {
		if (endpoint instanceof UDPCommandEndpoint) {
			return sendCommandWithResponseUDP(command, (UDPCommandEndpoint) endpoint);
		}
		return null;
	}
	
	public Command sendCommandWithResponseUDP(Command command,
			UDPCommandEndpoint endpoint) throws Exception {
		return sendCommandWithResponseUDP(command, endpoint, getCommandRegistry(), getDatagramBufferSize(), getLocalSendAddress(), getLocalSendPort());
	}
	
	public Command sendCommandWithResponseUDP(Command command,
			UDPCommandEndpoint endpoint, CommandRegistry registry, int datagramBufferSize) throws Exception {
		return sendCommandWithResponseUDP(command, endpoint, registry, datagramBufferSize, InetAddress.getLocalHost(), 0);
	}
	
	public Command sendCommandWithResponseUDP(Command command,
			UDPCommandEndpoint endpoint, CommandRegistry registry, int datagramBufferSize, InetAddress localAddress, int localPort) throws Exception {
		DatagramSocket s = new DatagramSocket(localPort, localAddress);
		DatagramPacket p = getDatagram(command, endpoint.getAddress(), endpoint.getPort());
		try {
			//System.out.println("sending...");
			s.send(p);
		} catch (Throwable t) {
			t.printStackTrace();
		}
		try {
			s.setSoTimeout(responseTimeout);
			DatagramPacket response = new DatagramPacket(new byte[datagramBufferSize], datagramBufferSize);
			//System.out.println("receiveing on IP: " + s.getLocalAddress() + ", port: " + s.getLocalPort());
			s.receive(response);
			//System.out.println("received!");
			return registry.getCommandByBinary(response.getData());
		} catch (SocketTimeoutException ste){
			//System.out.println("timeout...");
			return null;
		} finally {
			if (s != null) {
				s.close();
			}
		}
	}
	
	
	public InetAddress getLocalSendAddress() {
		return localSendAddress;
	}


	public void setLocalSendAddress(InetAddress localSendAddress) {
		this.localSendAddress = localSendAddress;
	}


	public int getLocalSendPort() {
		return localSendPort;
	}


	public void setLocalSendPort(int localSendPort) {
		this.localSendPort = localSendPort;
	}


	public int getResponseTimeout() {
		return responseTimeout;
	}


	public void setResponseTimeout(int responseTimeout) {
		this.responseTimeout = responseTimeout;
	}
	
	
	
	
	
}
