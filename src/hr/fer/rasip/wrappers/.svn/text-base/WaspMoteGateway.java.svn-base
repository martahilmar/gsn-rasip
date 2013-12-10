package hr.fer.rasip.wrappers;

import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;
import gsn.beans.AddressBean;
import gsn.beans.DataField;
import gsn.utils.KeyValueImp;
import gsn.wrappers.AbstractWrapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.TooManyListenersException;

import javax.naming.OperationNotSupportedException;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * Omotac za prikupljanje podataka s WaspMoteGatewaya. Prikuplja podatke koje gateway
 * salje na serijski port. Koristene su RXTX (http://rxtx.qbang.org/wiki/index.php/Main_Page)
 * biblioteke koje je protrebno instalirati na racunalo prije upotrebe ovog omotaca. Jedini
 * obavezan parametar u konfiguracijskoj datoteci virtualnog senzora je adresa serijskog porta (serial-port). 
 * Pretopstavljene vrijednosti drugih parametara su 38400 8 N 1. Opcionalni parametri mogu se 
 * navesti u XML datoteci:
 * baudrate: serial port baudrate (38400)
 * flowcontrolmode: flow control mode, moguce vrijednosti su:
 * 					-FLOWCONTROL_NONE: iskljucen flowcontrol
 * 					-FLOWCONTROL_RTSCTS_IN: RTS/CTS flow control na ulazu
 * 					-FLOWCONTROL_RTSCTS_OUT: RTS/CTS flow control na izlazu
 * 					-FLOWCONTROL_XONXOFF_IN: XON/XOFF flow control na ulazu
 * 					-FLOWCONTROL_XONXOFF_OUT: XON/XOFF flow control na izlazu
 * databits: serialport databits (5, 6, 7 ili 8) predpostavljeno 8
 * stopbits: serialport stopbits (1, 2 ili 1.5) predpostavljeno 1
 * parity: paritet na serijskom portu. Moguce vrijednosti su:
 * 					-PARITY_EVEN: parni paritet 
 * 					-PARITY_MARK: MARK paritet
 *					-PARITY_NONE: bez pariteta (predpostavljeno)
 *					-PARITY_ODD: neparni paritet
 *					-PARITY_SPACE: SPACE paritet
 * data-string-length: najveca dopustena velicina data dijela primljenog paketa (100)
 * mote-id-length: najveca dopustena velicina moteID dijela paketa (20)
 * 
 * Omotac prima poruku s WaspMoteGateway-a spojenog na serijski port i parsira primljeni 
 * paket. Na izlazu omotaca poruka je razdvojena na dva varchar polja. Prvo polje je 
 * identifikacijki dio paketa (moteID) koji opisuje koji cvor je poslao poruku, a drugi dio
 * je podatkovni koji sadrzi mjerenja (data). Poruka koju waspmote cvor salje mora imati 
 * posebnu strukturu kako bi je omotac mogao parsirati. Struktura je:
 *  "#" + moteID "#" + dataID + "!end!"
 *  Znakove # koji ograduju moteID cvor sam dodaje u poruku a korisnik nakon data dijela
 *  potrebno je obavezno staviti "!end!" znakovni niz koji oznacava kraj poruke. Struktura dataID
 *  dijela je za ovaj omotac proizvoljna ali bi radi daljnjeg procesiranja u processing class razredima
 *  bilo dobro da je svaki izmjereni parametar ograden iznakom parametra izmedu znaka "!" npr. za 
 *  temperaturu !temp!25!temp!
 *  
 */

public class WaspMoteGateway extends AbstractWrapper implements SerialPortEventListener {

	private final transient Logger  logger = Logger.getLogger(WaspMoteGateway.class);
	private SerialConnection        waspMoteConnection;
	private int                     threadCounter = 0;
	public InputStream              inputStream;
	private AddressBean             addressBean;
	private String                  serialPort;
	private int                     flowControlMode;
	private int                     baudRate = 38400; //predpostavljeni baudrate
	private int                     dataBits = SerialPort.DATABITS_8; //8 databits
	private int                     stopBits = SerialPort.STOPBITS_1; //1 stopbi
	private int                     parity = SerialPort.PARITY_NONE; //nema pariteta
	private  DataField []			dataField;
	private int 					dataStringLength; //velicina data dijela paketa (100)
	private int 					moteIDLength; //velicina moteID dijela paketa (20)
	private byte []					inputBuffer;
	private String 					newMessage = new String();
	private String 					message = new String();
	private static final int 		MAXBUFFERSIZE = 1024;
	
	
	/**
	 * Uzima parametre iz XML datoteke. One koji nisu navedeni postavlja na 
	 * predpostavljene vrijednosti. Inicijalizira vezu za serijskim portom.  
	 */
	public boolean initialize ( ) {
		setName("WaspMoteGateway" + (++threadCounter ));
		addressBean = getActiveAddressBean();
		
		//citanje parametara iz XML datoteke
		serialPort = addressBean.getPredicateValue("serial-port");
		if (serialPort == null || serialPort.trim().length() == 0) {
			logger.error("The serialport parameter is missing from the SerialWrapper, wrapper initialization failed.");
			return false;
		}
		
		dataStringLength= addressBean.getPredicateValueAsInt("data-string-length",100);
		moteIDLength = addressBean.getPredicateValueAsInt("mote-id-length",20);

		String newBaudRate = addressBean.getPredicateValue("baudrate");
		if ((newBaudRate != null) && (newBaudRate.trim().length()>0)){
			baudRate = Integer.parseInt(newBaudRate);
		}

		String newDataBits = addressBean.getPredicateValue("databits");
		if (newDataBits != null && newDataBits.trim().length() > 0){
			switch (Integer.parseInt(newDataBits)){
			case 5:
				dataBits = SerialPort.DATABITS_5;
				break;
			case 6:
				dataBits = SerialPort.DATABITS_6;
				break;
			case 7 :
				dataBits = SerialPort.DATABITS_7;
				break;
			case 8 :
				dataBits = SerialPort.DATABITS_8;
				break;
			}
		}

		String newStopBits = addressBean.getPredicateValue("stopbits");
		if (newStopBits != null && newStopBits.trim().length() > 0){
			float newstopbits = Float.parseFloat( newStopBits );

			if (newstopbits == 1.0) stopBits = SerialPort.STOPBITS_1;
			if (newstopbits == 2.0) stopBits = SerialPort.STOPBITS_2;
			if (newstopbits == 1.5) stopBits = SerialPort.STOPBITS_1_5;
		}

		String newParity = addressBean.getPredicateValue("parity");
		if (newParity != null && newParity.trim().length() > 0){
			if (newParity.equals("PARITY_EVEN")) parity = SerialPort.PARITY_EVEN;
			if (newParity.equals("PARITY_MARK")) parity = SerialPort.PARITY_MARK;
			if (newParity.equals("PARITY_NONE")) parity = SerialPort.PARITY_NONE;
			if (newParity.equals("PARITY_ODD")) parity = SerialPort.PARITY_ODD;
			if (newParity.equals("PARITY_SPACE")) parity = SerialPort.PARITY_SPACE;
		}

		String newflowControlMode = addressBean.getPredicateValue("flowcontrolmode");
		if (newflowControlMode != null && newflowControlMode.trim().length() > 0){
			flowControlMode = 0;
			String modes[] = newflowControlMode.split( "\\|" );
			for (int i=0 ; i<modes.length; i++){
				if (modes[i].equals("FLOWCONTROL_NONE")) flowControlMode |= SerialPort.FLOWCONTROL_NONE;
				if (modes[i].equals("FLOWCONTROL_RTSCTS_IN")) flowControlMode |= SerialPort.FLOWCONTROL_RTSCTS_IN;
				if ( modes[i].equals("FLOWCONTROL_RTSCTS_OUT")) flowControlMode |= SerialPort.FLOWCONTROL_RTSCTS_OUT;
				if (modes[i].equals("FLOWCONTROL_XONXOFF_IN")) flowControlMode |= SerialPort.FLOWCONTROL_XONXOFF_IN;
				if (modes[i].equals("FLOWCONTROL_XONXOFF_OUT")) flowControlMode |= SerialPort.FLOWCONTROL_XONXOFF_OUT;
			}

			if (flowControlMode == 0){
				flowControlMode = -1; // ne postavljaj flowControl ako je prazan
			}
		}else{
			flowControlMode = -1;
		}

		//Inicijalizacija serijske veze
		waspMoteConnection = new SerialConnection(serialPort);
		if (waspMoteConnection.openConnection() == false){
			logger.error("Opening connection on port: " + serialPort + " failed!" );
			return false;
			}
		waspMoteConnection.addEventListener(this);
		inputStream= waspMoteConnection.getInputStream();
		if (logger.isDebugEnabled()) {
			logger.debug( "Serial port wrapper successfully opened port: " + serialPort + " and registered itself as listener." );
		}
		inputBuffer = new byte [MAXBUFFERSIZE];
		//dataField - struktura podataka koji izlaze i omotaca
		dataField = new DataField[] {new DataField("moteID","varchar(" + moteIDLength + ")" , "Identifier of WaspMote node" ), 
									 new DataField("data", "varchar(" + dataStringLength+ ")", "Data part of package")};	
		return true;
	}
	
	public void run (){
	}
	
	public DataField[] getOutputFormat(){
		return dataField;
	}

	public void dispose(){
		waspMoteConnection.closeConnection();
		threadCounter--;
	}
	
	public void serialEvent (SerialPortEvent e){
		String [] chunk;
		switch (e.getEventType()){
		
		//odrediti koji se event dogodio
		case SerialPortEvent.DATA_AVAILABLE :
			
			if (newMessage.length() != 0 && message.length() == 0){
				//prebaci newMessage u message i izbrisi newMessage
				message = newMessage;
				newMessage = "";
			}
			  			  
			try {
				//cita zadnju pristiglu poruku i sprema je u inputBuffer
				inputStream.read(inputBuffer);	
			} catch ( IOException ex ) {
				logger.warn( "Serial port wrapper couldn't read data : " + ex );
				return;
			}
			
			try{
				//dodaj zadnju pristiglu poruku na ono sto je prije procitano
				//encoding mora biti UTF-8 i potrebno je iz poruke izbrisati sve 0x00 znakove
				//jer Postgre baza s UTF-8 enkripcijom ne prihvaca te znakove 
				message = message.concat(new String(inputBuffer,"UTF-8").toString().trim().replaceAll("\u0000", ""));
			} catch (UnsupportedEncodingException encodingEx){
				logger.warn("Unsupported encoding exception UTF-8");
			}
			
			//pobrisi inputBuffer
			inputBuffer = new byte [MAXBUFFERSIZE];
			
			//ako poruka nije prazna i sadrzi terminator "!end!"
			while (message.contains("!end!") && !message.equals("")){
				chunk = message.split("!end!");
				for(int i=1;i<chunk.length;i++){
					//sve nakon prvog terminatora pospremi u newMessage
					if(i>1){
						//dodaj teriminator tamo gdje ga je split izbrisao
						newMessage = newMessage.concat("!end!");						
					}
					newMessage = newMessage.concat(chunk[i]);
				}
				if(newMessage.contains("!end!")){
					//pospremi newMessage u message i ponovi postupak
					message = newMessage;
					newMessage ="";
				}
				else{
					message = "";
				}
				//posalji neparsiranu poruku na obradu i izlaz
				postItem(chunk[0]);
			}	
			
			break;
			
			// If break event append BREAK RECEIVED message.
		case SerialPortEvent.BI :
			logger.warn( "SerialPortEvent Break Received");
		}
	}

	
	/**
	 * Parsira primljenu sirovu poruku, odvaja ju na moteID dio i
	 * data dio te ga salje na izlaz 
	 */
	private void postItem (String wholePackage){
		
		String [] stringArray; //pomocna varijabla
		stringArray = wholePackage.split("#");
		String moteID = stringArray [1].trim();
		String data = stringArray [2].trim();
		
		if(moteID.length()>moteIDLength){
			logger.error("MoteID part of package is too big. Increase mote-id-length parameter.");
		}
		if(data.length()>dataStringLength){
			logger.error("Data part of package is too big. Increase data-string-length parameter.");
		}	
		postStreamElement(new Serializable[]{moteID,data});		
	}
	
	
	public String getWrapperName() {
		return "Waspmote gateway serial port wrapper";
	}
	
	/**
	 * A class that handles the details of the serial connection.
	 */

	public class SerialConnection {

		protected OutputStream     os;
		protected InputStream      is;
		private CommPortIdentifier portId;
		public SerialPort          sPort;
		private String             serialPort;
		private boolean            open;

		/**
		 * Creates a SerialConnection object and initialiazes variables passed in
		 * as params.
		 * 
		 * @param serialPort A SerialParameters object.
		 */
		public SerialConnection (String serialPort) {
			open = false;
			this.serialPort = serialPort;
		}

		/**
		 * Attempts to open a serial connection (9600 8N1). If it is unsuccesfull
		 * at any step it returns the port to a closed state, throws a
		 * <code>SerialConnectionException</code>, and returns. <p/> Gives a
		 * timeout of 30 seconds on the portOpen to allow other applications to
		 * reliquish the port if have it open and no longer need it.
		 */
		public boolean openConnection ( ) {
			// Obtain a CommPortIdentifier object for the port you want to open.
			try {
				portId = CommPortIdentifier.getPortIdentifier(serialPort);
			} catch (NoSuchPortException e) {
				logger.error("Port doesn't exist : " + serialPort , e );
				return false;
			}
			// Open the port represented by the CommPortIdentifier object.
			// Give the open call a relatively long timeout of 30 seconds to
			// allow a different application to reliquish the port if the user
			// wants to.
			if (portId.isCurrentlyOwned()) {
				logger.error( "port owned by someone else" );
				return false;
			}
			try {
				sPort = (SerialPort)portId.open("GSNSerialConnection", 30*1000);

				sPort.setSerialPortParams(baudRate, dataBits, stopBits, parity);
				if (flowControlMode != -1){
					sPort.setFlowControlMode(flowControlMode);
				}
			} catch (PortInUseException e) {
				logger.error(e.getMessage(), e);
				return false;
			} catch (UnsupportedCommOperationException e) {
				logger.error(e.getMessage(), e);
				return false;
			}

			// Open the input and output streams for the connection. If they
			// won't
			// open, close the port before throwing an exception.
			try {
				os = sPort.getOutputStream();
				is = sPort.getInputStream();
			} catch (IOException e) {
				sPort.close();
				logger.error(e.getMessage(), e);
				return false;
			}
			sPort.notifyOnDataAvailable(true);
			sPort.notifyOnBreakInterrupt(false);

			// Set receive timeout to allow breaking out of polling loop
			// during
			// input handling.
			try {
				sPort.enableReceiveTimeout(30);
			}catch (UnsupportedCommOperationException e) {

			}
			open = true;
			return true;
		}

		/**
		 * Close the port and clean up associated elements.
		 */
		public void closeConnection (){
			// If port is already closed just return
			if (!open) {return;}
			// Check to make sure sPort has reference to avoid a NPE.
			if (sPort != null){
				try {
					os.close();
					is.close();
				} catch (IOException e){
					System.err.println(e);
				}
				sPort.close();
			}
			open = false;
		}

		/**
		 * Send a one second break signal.
		 */
		public void sendBreak () {
			sPort.sendBreak(1000);
		}

		/**
		 * Reports the open status of the port.
		 * 
		 * @return true if port is open, false if port is closed.
		 */
		public boolean isOpen(){
			return open;
		}

		public void addEventListener(SerialPortEventListener listener){
			try {
				sPort.addEventListener(listener);
			} catch (TooManyListenersException e) {
				sPort.close();
				logger.warn(e.getMessage(), e);
			}
		}

		/**
		 * Send a byte.
		 */
		 public void sendByte (int i){
			 try {
				 os.write(i);
			 } catch (IOException e){
				 System.err.println("OutputStream write error: " + e);
			 }
		 }

		 public InputStream getInputStream(){
			 return is;
		 }

		 public OutputStream getOutputStream(){
			 return os;
		 }

	}	
}

