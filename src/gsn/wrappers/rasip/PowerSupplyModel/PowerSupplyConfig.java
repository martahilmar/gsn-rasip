package gsn.wrappers.rasip.PowerSupplyModel;

import gsn.wrappers.rasip.PowerSupplyModel.sslxml.XMLElement;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.Vector;


/**
 * Used to configure a <i>PowerSupply</i> object.
 * @version 1.0
 * @author Luka Lednicki
 */
public class PowerSupplyConfig
{
	/**
	 * If set to true, error in configuration of <i>PowerBoard</i> items will not throw <i>ConfigException</i>.
	 * <i>PowerBoard</i> items that contain errors will not be added to the <i>PowerSupply</i> object.
	 */
	public static boolean ignoreErrors = false;
	
	/**
	 * Creates new <i>PowerSupply</i> object from a config file.
	 * @param fileName Name of the config file.
	 * @throws ConfigException If there were errors and <i>ignoreErrors</i> is set to <i>false</i>.
	 * @throws FileNotFoundException If the file is not found.
	 * @throws IOException If there were problems reading file.
	 */
	public static PowerSupply loadFromFile(String fileName) throws FileNotFoundException, IOException, ConfigException
	{
		PowerSupply supply = new PowerSupply();
		XMLElement root;
		
		FileReader reader = new FileReader(fileName);
		
		root= new XMLElement();
		root.parseFromReader(reader);
		reader.close();
		
		if (!(root.getName().equalsIgnoreCase("PowerSupply")))
			throw new ConfigException("File is not SmartPower config file.");
		
		Vector children = root.getChildren();
		
		for (Iterator iter = children.iterator(); iter.hasNext();)
		{
			XMLElement element = (XMLElement) iter.next();
			if (element.getName().equalsIgnoreCase("PowerBoard"))
			{
				PowerBoard board = parsePowerBoard(element);
				if (board!=null) supply.boards.add(board);
			}
			
		}
		
		return supply;
		
	}
	
	/**
	 * Parses <i>XMLElement</i> that contains configuration for a power board
	 * @param pbElement <i>XMLElement</i> to be parsed.
	 * @return New PowerBoardObject on success. If there were errors and <i>ignoreErrors</i> was set to <i>true</i>, it returns <i>null</i>
	 * @throws ConfigException If there were errors and <i>ignoreErrors</i> is set to <i>false</i>.
	 */
	private static PowerBoard parsePowerBoard(XMLElement pbElement) throws ConfigException
	{
		String boardID = null;
		String ipAddress = null;
		int numberOfSocets = -1;
		
		Vector children = pbElement.getChildren();
		for (Iterator iter = children.iterator(); iter.hasNext();)
		{
			XMLElement element = (XMLElement) iter.next();
			if (element.getName().equalsIgnoreCase("ID"))
			{
				boardID = element.getContent();
			}
			else if (element.getName().equalsIgnoreCase("IPAddress"))
			{
				ipAddress = element.getContent();
			}
			else if (element.getName().equalsIgnoreCase("Sockets"))
			{
				try
				{
					numberOfSocets = Integer.parseInt(element.getContent());
				}
				catch (NumberFormatException e)
				{
					if (ignoreErrors) numberOfSocets=-1;
					else throw new ConfigException("Number of sockets not a number.");
				}
			}
		}
		if (boardID==null || ipAddress==null)
		{
			if (ignoreErrors) return null;
			else throw new ConfigException("PowerBoard config error.");
		}
		
		PowerBoard board;
		try
		{
			board = new PowerBoard(boardID,ipAddress,numberOfSocets);
		}
		catch (UnknownHostException e)
		{
			if (ignoreErrors) return null;
			else throw new ConfigException("Bad IP format.");
		}
		
		return board;
		
	}
	
}
