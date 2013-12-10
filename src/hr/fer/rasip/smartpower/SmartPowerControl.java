package hr.fer.rasip.smartpower;

import javax.swing.JPanel;
import java.awt.Frame;

import javax.swing.JDialog;

import hr.fer.rasip.smartpower.ConfigException;
import hr.fer.rasip.smartpower.PowerBoard;
import hr.fer.rasip.smartpower.PowerSupply;
import hr.fer.rasip.smartpower.PowerSupplyConfig;
import hr.fer.rasip.smartpower.ResponseTimeoutException;
import hr.fer.rasip.smartpower.SmartPowerException;
import hr.fer.rasip.smartpower.UnknownBoardException;
import hr.fer.rasip.smartpower.UnknownCommandException;
import hr.fer.rasip.smartpower.UnknownSocketException;

import javax.swing.JList;
import java.awt.Rectangle;
import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JLabel;
import java.awt.SystemColor;

public class SmartPowerControl extends JDialog
{
	private PowerSupply ps = null;
	private DefaultListModel boardLM;
	private static final long serialVersionUID = 1L;

	private JPanel jContentPane = null;
	private JList boardsList = null;
	private JTextField configFile = null;
	private JButton loadConfig = null;
	private JLabel jLabel = null;
	private JLabel jLabel1 = null;
	private JButton exitApp = null;
	private JLabel jLabel2 = null;
	private JLabel jLabel3 = null;
	private JLabel jLabel4 = null;
	private JLabel jLabel5 = null;
	private JButton socketOn = null;
	private JButton socketOff = null;
	private JButton socketReset = null;
	private JLabel jLabel6 = null;
	private JLabel jLabel7 = null;
	private JTextField socketNumber = null;
	private JLabel jLabel8 = null;
	private JTextField response = null;
	private JButton powerStatus = null;
	private JTextField boardID = null;
	private JTextField ipAddress = null;
	private JTextField numberOfSockets = null;
	private JLabel jLabel9 = null;
	private JLabel jLabel10 = null;
	private JTextField resetDuration = null;
	/**
	 * @param owner
	 */
	public SmartPowerControl(Frame owner)
	{
		super(owner);
		initialize();
	}
	
	/**
	 * This method initializes exitApp	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getExitApp()
	{
		if (exitApp == null)
		{
			exitApp = new JButton();
			exitApp.setBounds(new Rectangle(390, 375, 121, 19));
			exitApp.setText("Exit");
			exitApp.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					System.exit(0);
				}
			});
		}
		return exitApp;
	}

	/**
	 * This method initializes socketOn	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getSocketOn()
	{
		if (socketOn == null)
		{
			socketOn = new JButton();
			socketOn.setBounds(new Rectangle(210, 240, 91, 16));
			socketOn.setText("Turn On");
			socketOn.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					String id = boardsList.getSelectedValue().toString();
					String responseString = "OK";
					
					try
					{
						int socket = Integer.parseInt(socketNumber.getText());
						ps.socketOn(id, socket);
					}
					catch (NumberFormatException nfe)
					{
						responseString="Socket number not selected";
					}
					catch (UnknownBoardException ube)
					{
						responseString="Unknown board";
					}
					catch (UnknownCommandException uce)
					{
						responseString="Unknown command";
					}
					catch (UnknownSocketException use)
					{
						responseString="Unknown socket";
					}
					catch (ResponseTimeoutException rte)
					{
						responseString="Response timeout";
					}
					catch (SmartPowerException spe)
					{
						responseString=spe.getMessage();
					}
					response.setText(responseString);
				}
			});
		}
		return socketOn;
	}

	/**
	 * This method initializes socketOff	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getSocketOff()
	{
		if (socketOff == null)
		{
			socketOff = new JButton();
			socketOff.setBounds(new Rectangle(315, 240, 91, 16));
			socketOff.setText("Turn Off");
			socketOff.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					String id = boardsList.getSelectedValue().toString();
					String responseString = "OK";
					
					try
					{
						int socket = Integer.parseInt(socketNumber.getText());
						ps.socketOff(id, socket);
					}
					catch (NumberFormatException nfe)
					{
						responseString="Socket number not selected";
					}
					catch (UnknownBoardException ube)
					{
						responseString="Unknown board";
					}
					catch (UnknownCommandException uce)
					{
						responseString="Unknown command";
					}
					catch (UnknownSocketException use)
					{
						responseString="Unknown socket";
					}
					catch (ResponseTimeoutException rte)
					{
						responseString="Response timeout";
					}
					catch (SmartPowerException spe)
					{
						responseString=spe.getMessage();
					}
					response.setText(responseString);
}
			});
		}
		return socketOff;
	}

	/**
	 * This method initializes socketReset	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getSocketReset()
	{
		if (socketReset == null)
		{
			socketReset = new JButton();
			socketReset.setBounds(new Rectangle(420, 240, 91, 17));
			socketReset.setText("Reset");
			socketReset.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					String id = boardsList.getSelectedValue().toString();
					String responseString = "OK";
					
					try
					{
						int socket = Integer.parseInt(socketNumber.getText());
						int time = Integer.parseInt(resetDuration.getText());
						ps.socketReset(id, socket, time);
					}
					catch (NumberFormatException nfe)
					{
						responseString="Socket number/time not selected";
					}
					catch (UnknownBoardException ube)
					{
						responseString="Unknown board";
					}
					catch (UnknownCommandException uce)
					{
						responseString="Unknown command";
					}
					catch (UnknownSocketException use)
					{
						responseString="Unknown socket";
					}
					catch (ResponseTimeoutException rte)
					{
						responseString="Response timeout";
					}
					catch (SmartPowerException spe)
					{
						responseString=spe.getMessage();
					}
					response.setText(responseString);
				}
			});
		}
		return socketReset;
	}

	/**
	 * This method initializes socketNumber	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getSocketNumber()
	{
		if (socketNumber == null)
		{
			socketNumber = new JTextField();
			socketNumber.setBounds(new Rectangle(330, 180, 181, 20));
		}
		return socketNumber;
	}

	/**
	 * This method initializes response	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getResponse()
	{
		if (response == null)
		{
			response = new JTextField();
			response.setBounds(new Rectangle(330, 300, 181, 20));
			response.setBackground(SystemColor.controlLtHighlight);
			response.setEditable(false);
		}
		return response;
	}

	/**
	 * This method initializes powerStatus	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getPowerStatus()
	{
		if (powerStatus == null)
		{
			powerStatus = new JButton();
			powerStatus.setBounds(new Rectangle(210, 270, 301, 16));
			powerStatus.setText("Power Status");
			powerStatus.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					String id = boardsList.getSelectedValue().toString();
					String responseString = "OK";
					
					try
					{
						boolean powerOn = ps.powerStatus(id);
						if (powerOn) responseString = "Power on";
						else responseString = "No power";
					}
					catch (UnknownBoardException ube)
					{
						responseString="Unknown board";
					}
					catch (UnknownCommandException uce)
					{
						responseString="Unknown command";
					}
					catch (UnknownSocketException use)
					{
						responseString="Unknown socket";
					}
					catch (ResponseTimeoutException rte)
					{
						responseString="Response timeout";
					}
					catch (SmartPowerException spe)
					{
						responseString=spe.getMessage();
					}
					response.setText(responseString);

				}
			});
		}
		return powerStatus;
	}

	/**
	 * This method initializes boardID	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getBoardID()
	{
		if (boardID == null)
		{
			boardID = new JTextField();
			boardID.setBounds(new Rectangle(330, 45, 181, 20));
			boardID.setBackground(SystemColor.controlLtHighlight);
			boardID.setEditable(false);
		}
		return boardID;
	}

	/**
	 * This method initializes ipAddress	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getIpAddress()
	{
		if (ipAddress == null)
		{
			ipAddress = new JTextField();
			ipAddress.setBounds(new Rectangle(330, 75, 181, 20));
			ipAddress.setBackground(SystemColor.controlLtHighlight);
			ipAddress.setEditable(false);
		}
		return ipAddress;
	}

	/**
	 * This method initializes numberOfSockets	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getNumberOfSockets()
	{
		if (numberOfSockets == null)
		{
			numberOfSockets = new JTextField();
			numberOfSockets.setBounds(new Rectangle(330, 105, 181, 20));
			numberOfSockets.setBackground(SystemColor.controlLtHighlight);
			numberOfSockets.setEditable(false);
		}
		return numberOfSockets;
	}

	/**
	 * This method initializes resetDuration	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getResetDuration()
	{
		if (resetDuration == null)
		{
			resetDuration = new JTextField();
			resetDuration.setBounds(new Rectangle(330, 210, 106, 20));
		}
		return resetDuration;
	}

	public static void main(String[] args)
	{
		SmartPowerControl spc = new SmartPowerControl(null);
		spc.setVisible(true);
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize()
	{
		this.setSize(526, 436);
		this.setTitle("SmartPower Control");
		this.setResizable(false);
		this.setContentPane(getJContentPane());
		
		boardLM = new DefaultListModel();
		boardsList.setModel(boardLM);
	}
	
	/**
	 * Loads PowerSupply from a config file.
	 */
	private void loadPowerSupply()
	{
		try
		{
			ps = PowerSupplyConfig.loadFromFile(configFile.getText());
		}
		catch (ConfigException ce)
		{
			// TODO obraditi iznimku
		}
		catch (IOException ioe)
		{
			// TODO obraditi iznimku
		}
		Vector boards = ps.getBoards();
		boardLM.clear();
		for (Iterator iter = boards.iterator(); iter.hasNext();)
		{
			PowerBoard board = (PowerBoard) iter.next();
			boardLM.addElement(board.getID());
		}
		
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane()
	{
		if (jContentPane == null)
		{
			jLabel10 = new JLabel();
			jLabel10.setBounds(new Rectangle(450, 210, 61, 16));
			jLabel10.setText("x100 ms");
			jLabel9 = new JLabel();
			jLabel9.setBounds(new Rectangle(210, 210, 103, 16));
			jLabel9.setText("Reset duration:");
			jLabel8 = new JLabel();
			jLabel8.setBounds(new Rectangle(210, 300, 103, 16));
			jLabel8.setText("Response:");
			jLabel7 = new JLabel();
			jLabel7.setBounds(new Rectangle(210, 180, 104, 16));
			jLabel7.setText("Socket number:");
			jLabel6 = new JLabel();
			jLabel6.setBounds(new Rectangle(195, 15, 91, 16));
			jLabel6.setText("Board info:");
			jLabel5 = new JLabel();
			jLabel5.setBounds(new Rectangle(195, 150, 91, 16));
			jLabel5.setText("Actions:");
			jLabel4 = new JLabel();
			jLabel4.setBounds(new Rectangle(210, 105, 106, 16));
			jLabel4.setText("Sockets:");
			jLabel3 = new JLabel();
			jLabel3.setBounds(new Rectangle(210, 75, 106, 16));
			jLabel3.setText("IP address:");
			jLabel2 = new JLabel();
			jLabel2.setBounds(new Rectangle(210, 45, 106, 16));
			jLabel2.setText("Board ID:");
			jLabel1 = new JLabel();
			jLabel1.setBounds(new Rectangle(15, 345, 136, 16));
			jLabel1.setText("Configuration file:");
			jLabel = new JLabel();
			jLabel.setBounds(new Rectangle(15, 15, 136, 16));
			jLabel.setText("Power boards:");
			jContentPane = new JPanel();
			jContentPane.setLayout(null);
			jContentPane.add(getBoardsList(), null);
			jContentPane.add(getConfigFile(), null);
			jContentPane.add(getLoadConfig(), null);
			jContentPane.add(jLabel, null);
			jContentPane.add(jLabel1, null);
			jContentPane.add(getExitApp(), null);
			jContentPane.add(jLabel2, null);
			jContentPane.add(jLabel3, null);
			jContentPane.add(jLabel4, null);
			jContentPane.add(jLabel5, null);
			jContentPane.add(getSocketOn(), null);
			jContentPane.add(getSocketOff(), null);
			jContentPane.add(getSocketReset(), null);
			jContentPane.add(jLabel6, null);
			jContentPane.add(jLabel7, null);
			jContentPane.add(getSocketNumber(), null);
			jContentPane.add(jLabel8, null);
			jContentPane.add(getResponse(), null);
			jContentPane.add(getPowerStatus(), null);
			jContentPane.add(getBoardID(), null);
			jContentPane.add(getIpAddress(), null);
			jContentPane.add(getNumberOfSockets(), null);
			jContentPane.add(jLabel9, null);
			jContentPane.add(jLabel10, null);
			jContentPane.add(getResetDuration(), null);
		}
		return jContentPane;
	}
	
	/**
	 * This method initializes boardsList	
	 * 	
	 * @return javax.swing.JList	
	 */
	private JList getBoardsList()
	{
		if (boardsList == null)
		{
			boardsList = new JList();
			boardsList.setBounds(new Rectangle(15, 45, 136, 271));
			boardsList
					.addListSelectionListener(new javax.swing.event.ListSelectionListener()
					{
						public void valueChanged(javax.swing.event.ListSelectionEvent e)
						{
							String id = boardsList.getSelectedValue().toString();
							PowerBoard board;
							try
							{
								board = ps.getBoard(id);
							}
							catch (UnknownBoardException ube)
							{
								// TODO mozda ovo ispisati u messageboxu
								boardID.setText("Unknown board");
								return;
							}
							boardID.setText(board.getID());
							ipAddress.setText(board.getIpAddress());
							if (board.isNumberOfSocketsSet())
								numberOfSockets.setText(Integer.toString(board.getNumberOfSockets()));
							else
								numberOfSockets.setText("Unknown");
							socketNumber.setText("1");
							resetDuration.setText("30");
							
							
						}
					});
		}
		return boardsList;
	}

	/**
	 * This method initializes configFile	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getConfigFile()
	{
		if (configFile == null)
		{
			configFile = new JTextField();
			configFile.setBounds(new Rectangle(165, 345, 211, 20));
			configFile.setText("napajanje.xml");
		}
		return configFile;
	}

	/**
	 * This method initializes loadConfig	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getLoadConfig()
	{
		if (loadConfig == null)
		{
			loadConfig = new JButton();
			loadConfig.setBounds(new Rectangle(390, 345, 122, 18));
			loadConfig.setText("Load config");
			loadConfig.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					loadPowerSupply();
				}
			});
		}
		return loadConfig;
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
