package gsn.wrappers.rasip.PowerSupplyModel;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 * Class that represent a power board in the <i>SmartPower</i> power supply.
 * @version 1.0
 * @author Luka Lednicki
 */
public class PowerBoard
{
	private String id;
	private InetAddress ipAddress;
	private int numberOfSockets;
	private boolean numberOfSocketsSet;
	private final int udpPort = 31000;
	private final int numberOfRetries = 5;
	private final int responseTimeout = 1000;
	
	/*
	 * Command types
	 */
	private static class Command
	{
		public static final byte POWER_STATUS = 0x01;
		public static final byte POWER_OFF = 0x02;
		public static final byte POWER_ON = 0x03;
		public static final byte RESET = 0x04;
	}
	/*
	 * Response types.
	 */
	private static class Response
	{
		public static final byte OK = 0x00;
		public static final byte UNKNOWN_SOCKET = 0x01;
		public static final byte UNKNOWN_COMMAND = 0x02;
		public static final byte NO_POWER = 0x03;
	}
	
	/**
	 * Constructs new <i>PowerBoard</i> object. When usign this constructor, number of sockets
	 * on the board is not set, and object does not check if socket-number is correct when
	 * sending a command.
	 * @param id Id of the board;
	 * @param ipAddress IP address of the board.
	 * @throws UnknownHostException If the IP address was not in the correct format. 
	 */
	public PowerBoard(String id, String ipAddress) throws UnknownHostException
	{
		this(id,ipAddress,-1);
	}


	
	/**
	 * Constructs new <i>PowerBoard</i> object. If <i>numberOfSockets</i> is less than 1 the number of sockets
	 * is not set. 
	 * @param id Id of the board;
	 * @param ipAddress IP address of the board.
	 * @param numberOfSockets Number of sockets that the board contains.
	 * @throws UnknownHostException If the IP address was not in the correct format.
	 */
	public PowerBoard(String id, String ipAddress, int numberOfSockets) throws UnknownHostException
	{
		this.id = id;
		this.ipAddress = InetAddress.getByName(ipAddress);
		numberOfSocketsSet = (numberOfSockets>0);
		this.numberOfSockets = (numberOfSocketsSet)?numberOfSockets:0;
	}
	

	/**
	 * Turns the power on. 
	 * @param socketNumber Number of the socket.
	 * @throws UnknownCommandException If selected board does not support selected command.
	 * @throws UnknownSocketException If selected board does not have selected socket.
	 * @throws ResponseTimeoutException If selected board fails to respond.
	 * @throws SmartPowerException If there was error opening UDP socket, receiveing or sending UDP packet.
	 */
	public void socketOn(int socketNumber) throws UnknownCommandException, UnknownSocketException, ResponseTimeoutException,SmartPowerException
	{
		if (!isSocketNumberOK(socketNumber)) throw new UnknownSocketException("Invalid socket number.");
		sendCommand(Command.POWER_ON, (byte) socketNumber);
	}
	
	/**
	 * Turns the power off.
	 * @param socketNumber Number of the socket.
	 * @throws UnknownCommandException If selected board does not support selected command.
	 * @throws UnknownSocketException If selected board does not have selected socket.
	 * @throws ResponseTimeoutException If selected board fails to respond.
	 * @throws SmartPowerException If there was error opening UDP socket, receiveing or sending UDP packet.
	 */
	public void socketOff(int socketNumber) throws UnknownCommandException, UnknownSocketException, ResponseTimeoutException,SmartPowerException
	{
		if (!isSocketNumberOK(socketNumber)) throw new UnknownSocketException("Invalid socket number.");
		sendCommand(Command.POWER_OFF, (byte) socketNumber);
	}
	
	/**
	 * Resets the power.
	 * @param socketNumber Number of the socket.
	 * @param resetTime Duration of the reset. Power will be turned off for <i>resetTime</i> * 100 ms. If <i>resetTime</i> is greater than 255 it is set to 255.
	 * @throws UnknownCommandException If selected board does not support selected command.
	 * @throws UnknownSocketException If selected board does not have selected socket.
	 * @throws ResponseTimeoutException If selected board fails to respond.
	 * @throws SmartPowerException If there was error opening UDP socket, receiveing or sending UDP packet.
	 */
	public void socketReset(int socketNumber,int resetTime) throws UnknownCommandException, UnknownSocketException, ResponseTimeoutException,SmartPowerException
	{
		if (!isSocketNumberOK(socketNumber)) throw new UnknownSocketException("Invalid socket number.");
		if (resetTime>255) resetTime=255;
		sendCommand(Command.RESET, (byte) socketNumber, (byte) resetTime);
	}
	
	/**
	 * Returns the status of the board's power.
	 * @return True if there is power, false if there is no power.
	 * @throws UnknownCommandException If selected board does not support selected command.
	 * @throws UnknownSocketException If selected board does not have selected socket.
	 * @throws ResponseTimeoutException If selected board fails to respond
	 * @throws SmartPowerException If there was error opening UDP socket, receiveing or sending UDP packet. 
	 */	
	public boolean powerStatus() throws UnknownCommandException, UnknownSocketException, ResponseTimeoutException,SmartPowerException
	{
		byte[] response= sendCommand(Command.POWER_STATUS);
		
		if (response[2]==0x00) return true;
		return false;
	}
	
	/**
	 * Sends a command to the board.
	 * @param command Command to be sent.
	 * @throws UnknownCommandException If selected board does not support selected command.
	 * @throws UnknownSocketException If selected board does not have selected socket.
	 * @throws ResponseTimeoutException If selected board fails to respond
	 * @throws SmartPowerException If there was error opening UDP socket, receiveing or sending UDP packet.
	 */
	private byte[] sendCommand(byte command)  throws UnknownCommandException, UnknownSocketException, ResponseTimeoutException,SmartPowerException
	{
		return sendCommand(command, (byte) 0x00, (byte) 0x00);
	}
	
	/**
	 * Sends a command to the board.
	 * @param command Command to be sent.
	 * @param arg1 First argument to be sent. Usually the socket number.
	 * @throws UnknownCommandException If selected board does not support selected command.
	 * @throws UnknownSocketException If selected board does not have selected socket.
	 * @throws ResponseTimeoutException If selected board fails to respond
	 * @throws SmartPowerException If there was error opening UDP socket, receiveing or sending UDP packet.
	 */
	private byte[] sendCommand(byte command, byte arg1)  throws UnknownCommandException, UnknownSocketException, ResponseTimeoutException,SmartPowerException
	{
		return sendCommand(command, arg1,(byte) 0x00);
	}
	
	/**
	 * Sends a command to the board.
	 * @param command Command to be sent.
	 * @param arg1 First argument to be sent. Usually the socket number.
	 * @param arg2 Second argument. Usually reset time.
	 * @throws UnknownCommandException If selected board does not support selected command.
	 * @throws UnknownSocketException If selected board does not have selected socket.
	 * @throws ResponseTimeoutException If selected board fails to respond
	 * @throws SmartPowerException If there was error opening UDP socket, receiveing or sending UDP packet.
	 */
	private byte[] sendCommand(byte command, byte arg1, byte arg2) throws UnknownCommandException, UnknownSocketException, ResponseTimeoutException,SmartPowerException
	{
		DatagramSocket socket;
				
		// setup the data to be sent
		byte[] data=new byte[3];
		data[0]=command;
		data[1]=arg1;
		data[2]=arg2;
		
		int retryNumber=0;
						
		while (true)
		{
			// open new datagram socket
			try
			{
				socket = new DatagramSocket();
				socket.connect(ipAddress,udpPort);
			}
			catch (SocketException e)
			{
				throw new SmartPowerException("Cannot open datagram socket: " + e.getMessage());
			}
			
			// send the packet
			DatagramPacket packet = new DatagramPacket(data,data.length,ipAddress,udpPort);
			try
			{
				socket.send(packet);
			}
			catch (IOException e)
			{
				socket.close();
				throw new SmartPowerException("Error sending UDP packet: "+e.getMessage());
			}
			
			// get the response
			byte[] buff = new byte[3];
			packet = new DatagramPacket(buff,buff.length);
			try
			{
				// set the timeout and try to recieve a packet
				socket.setSoTimeout(responseTimeout);
				socket.receive(packet);
			}
			catch (SocketTimeoutException e)
			{
				// timeou has occured
				if (retryNumber++<numberOfRetries) continue;
				throw new ResponseTimeoutException("Response timeout.");
			}
			catch (Exception e)
			{
				// other errors
				throw new SmartPowerException("Error recieveing UDP packet: "+e.getMessage());
			}
			finally
			{
				// in all cases close the socket
				socket.close();
			}
			
			// interpret recieved data
			buff=packet.getData();
			// see if the response structure is OK and if it is
			// response for the command that was sent 
			if (buff.length<3 || buff[0]!=0xFF || buff[1]!=command)
			{
				socket.close();
				if (buff[2] == Response.UNKNOWN_SOCKET) throw new UnknownSocketException("No such socket on power board.");
				if (buff[2] == Response.UNKNOWN_COMMAND) throw new UnknownCommandException("No such command on power board.");
				
				return buff;
			}
			else if (retryNumber++<numberOfRetries) continue;
			else
			{
				if (!socket.isClosed()) socket.close();
				throw new ResponseTimeoutException("Response timeout.");
			}
		}
	}

	/**
	 * Checks if number of the socket is ok
	 */
	private boolean isSocketNumberOK(int socketNumber) 
	{
		if (socketNumber<1 || (numberOfSocketsSet && socketNumber>numberOfSockets)) return false;
		return true;
	}
	
	/**
	 * Gets the ID of the board.
	 * @return The ID of the board.
	 */
	public String getID()
	{
		return id;
	}

	/**
	 * Gets the number of sockets the board contains. If the number of sockets is not set, it returns 0.
	 * @return Number of sockets on the board.
	 */
	public int getNumberOfSockets()
	{
		return numberOfSockets;
	}

	/**
	 * Returns <i>true</i> if the number of sockets has been set.
	 * @return <i>True</i> if the number of sockets has been set.
	 */
	public boolean isNumberOfSocketsSet()
	{
		return numberOfSocketsSet;
	}

	/**
	 * Returns the IP address of the board.
	 * @return IP address of the board as a <i>String</i>.
	 */
	public String getIpAddress()
	{
		return ipAddress.toString();
	}
	
	
}
