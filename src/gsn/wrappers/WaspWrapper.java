package gsn.wrappers;

import org.apache.log4j.Logger;

import gsn.beans.DataField;
import gsn.beans.StreamElement;
import gsn.beans.DataTypes;

import java.net.*;
import java.io.*;

public class WaspWrapper extends AbstractWrapper {

	private transient DataField[] outputStructureCache = new DataField[] {new DataField( "ID", "INTEGER", "Waspmote ID" ), new DataField( "TEMPERATURE", "DOUBLE", "Temperature measured by Waspmote" )};
	private final transient Logger logger = Logger.getLogger( WaspWrapper.class );
	private static ServerSocket serverSock;
	private static int port = 5555;
	private static final int maxClients = 10;

	@Override
	public DataField[] getOutputFormat() {
		return outputStructureCache;
	}

	@Override
	public boolean initialize() {
		logger.info( "Initializing Wasp Server. Creating socket." );

		try {
			serverSock = new ServerSocket( port, maxClients );
		} catch ( Exception e ) {
			logger.error( "Creating socket unsuccessful. " + e.getMessage() );
			return false;
		}

		return true;
	}

	@Override
	public void dispose() {
		try {
			serverSock.close();
		} catch ( IOException e ) {
			e.printStackTrace();
		}
	}

	@Override
	public String getWrapperName() {
		return "Wasp Server";
	}

	private void sendSE( String str ) {

		String[] bits = str.split( ";" );

		int ID;
		double value;
		StreamElement se;

		for ( String b : bits ) {
			String[] s = b.split( ":" );

			try {
				ID = Integer.parseInt( s[0] );
				value = Double.parseDouble( s[1] );

				se = new StreamElement( new String[] {"ID", "TEMPERATURE"}, new Byte[] {DataTypes.INTEGER, DataTypes.DOUBLE}, new Serializable[] {ID, value}, System.currentTimeMillis() );
				postStreamElement( se );
			} catch ( Exception e ) {
				System.out.println( e.getMessage() );
				System.out.println( "Data ignored." );
			}
		}
	}

	@Override
	public void run() {

		Socket clientSocket;

		BufferedReader in;
		PrintWriter out;

		while ( isActive() ) {
			try {

				clientSocket = serverSock.accept();
				logger.info( "Client connected." );

				clientSocket.setSoTimeout( 10000 );

				in = new BufferedReader( new InputStreamReader( clientSocket.getInputStream() ) );
				out = new PrintWriter( clientSocket.getOutputStream() );

				//Slanje upita
				out.println( "?" );
				out.flush();

				//Citanje odgovora koji je oblika <ID>:<value>;<ID>:<value>;...
				String line;

				try {
					line = in.readLine();
				} catch ( Exception e ) {
					System.out.println( e.getMessage() );
					out.println( "NO DATA" );
					out.flush();
					clientSocket.close();
					continue;
				}

				line = line.replace( "*HELLO*", "" ).trim();

				//Obrada podataka
				sendSE( line );

				//Slanje odgovora
				out.println( "DONE" );
				out.flush();

				//Zatvaranje komunikacije
				clientSocket.close();
				logger.info( "Client disconnected." );

			} catch ( IOException e ) {
				System.out.println( e.getMessage() );
			}
		}
	}
}
