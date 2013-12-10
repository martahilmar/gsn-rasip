package gsn.wrappers.rasip.PowerSupplyModel;

import java.util.Iterator;
import java.util.Vector;

/**
 * Class that represents a <i>SmartPower</i> power supply.
 * @version 1.1
 * @author Luka Lednicki
 */
public class PowerSupply
{
	protected Vector boards;
		
	protected PowerSupply()
	{
		boards = new Vector();
	}
		

	/**
	 * Turns the power on using ID to select the board. 
	 * @param boardID ID of the board.
	 * @param socketNumber Number of the socket.
	 * @throws UnknownCommandException If selected board does not support selected command.
	 * @throws UnknownSocketException If selected board does not have selected socket.
	 * @throws UnknownBoardException If unknown board is selected
	 * @throws ResponseTimeoutException If selected board fails to respond
	 * @throws SmartPowerException If there was error opening UDP socket, receiveing or sending UDP packet.
	 */
	public void socketOn(String boardID,int socketNumber) throws UnknownCommandException, UnknownSocketException, UnknownBoardException,ResponseTimeoutException,SmartPowerException
	{
		PowerBoard board = getBoard(boardID);
		board.socketOn(socketNumber);
	}
	
	/**
	 * Turns the power off using ID to select the board.
	 * @param boardID ID of the board.
	 * @param socketNumber Number of the socket.
	 * @throws UnknownCommandException If selected board does not support selected command.
	 * @throws UnknownSocketException If selected board does not have selected socket.
	 * @throws UnknownBoardException If unknown board is selected
	 * @throws ResponseTimeoutException If selected board fails to respond
	 * @throws SmartPowerException If there was error opening UDP socket, receiveing or sending UDP packet.
	 */
	public void socketOff(String boardID,int socketNumber) throws UnknownCommandException, UnknownSocketException, UnknownBoardException,ResponseTimeoutException,SmartPowerException
	{
		PowerBoard board = getBoard(boardID);
		board.socketOff(socketNumber);
	}
	
	/**
	 * Resets the power using ID to select the board.
	 * @param boardID ID of the board.
	 * @param socketNumber Number of the socket.
	 * @param resetTime Duration of the reset. Power will be turned off for <i>resetTime</i> * 100 ms. If <i>resetTime</i> is greater than 255 it is set to 255.
	 * @throws UnknownCommandException If selected board does not support selected command.
	 * @throws UnknownSocketException If selected board does not have selected socket.
	 * @throws UnknownBoardException If unknown board is selected
	 * @throws ResponseTimeoutException If selected board fails to respond
	 * @throws SmartPowerException If there was error opening UDP socket, receiveing or sending UDP packet.
	 */
	public void socketReset(String boardID,int socketNumber,int resetTime) throws UnknownCommandException, UnknownSocketException, UnknownBoardException,ResponseTimeoutException, SmartPowerException
	{
		PowerBoard board = getBoard(boardID);
		board.socketReset(socketNumber, resetTime);
	}
	
	/**
	 * Returns the status of the board's power.
	 * @param boardID Id of the board.
	 * @return True if there is power, false if there is no power.
	 * @throws UnknownCommandException If selected board does not support selected command.
	 * @throws UnknownSocketException If selected board does not have selected socket.
	 * @throws UnknownBoardException If unknown board is selected
	 * @throws ResponseTimeoutException If selected board fails to respond
	 * @throws SmartPowerException If there was error opening UDP socket, receiveing or sending UDP packet.
	 */	
	public boolean powerStatus(String boardID) throws UnknownCommandException, UnknownSocketException, UnknownBoardException,ResponseTimeoutException, SmartPowerException
	{
		PowerBoard board = getBoard(boardID);
		return board.powerStatus();
	}
	
	/**
	 * Gets a board in the <i>boards Vector</i>.
	 * @param ID ID of the board to be found.
	 * @return <i>PowerBoard</i> object bith the selected ID
	 * @throws UnknownBoardException If no board with selected ID is found.
	 */
	public PowerBoard getBoard(String ID) throws UnknownBoardException
	{
		for (Iterator iter = boards.iterator(); iter.hasNext();)
		{
			PowerBoard board = (PowerBoard) iter.next();
			if (board.getID().equalsIgnoreCase(ID)) return board;
		}
		throw new UnknownBoardException("Board with ID '"+ID+"' not found!");
	}


	/**
	 * Returns <i>Vector</i> containg the <i>PowerBoard</i> objects.
	 * @return The boards.
	 */
	public Vector getBoards()
	{
		return boards;
	}
	
	

}
