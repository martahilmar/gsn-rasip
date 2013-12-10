package gsn.wrappers.rasip.RelayModel;

import java.net.InetAddress;

import org.liva.core.commands.Command;
import org.liva.core.commands.UDPCommandEndpoint;

import gsn.wrappers.rasip.RelayModel.commands.CommandServer;
import gsn.wrappers.rasip.RelayModel.commands.GetOutputStatusCommand;
import gsn.wrappers.rasip.RelayModel.commands.OutputStatusCommand;
import gsn.wrappers.rasip.RelayModel.commands.ResultCommand;
import gsn.wrappers.rasip.RelayModel.commands.SetOutputCommand;

public class Control {
	private CommandServer commandServer;
	
	private UDPCommandEndpoint endpoint;
	
	/**
	 * Creates new control comunnicator.
	 * @param localInetAddress IP address of the local Ethernet interface to use. If only one interface is present, use <i>localhost</i> form <i>InetAddress</i>.
	 * @param localPort Local UDP port to use for communication with the control device.
	 * @param remoteInetAddress IP address of the control device.
	 * @throws Exception  If there is an error initializing the control object.
	 */
	public Control(InetAddress localInetAddress, int localPort, InetAddress remoteInetAddress, int remotePort) throws Exception {
		commandServer = new CommandServer(localInetAddress, localPort);
		endpoint = new UDPCommandEndpoint(remoteInetAddress, remotePort);
		
		if (commandServer == null) {
			throw new Exception("Could not start command server!");
		} else if (endpoint == null) {
			throw new Exception("Could not initialize endpoint.");
		}
		
	}

	/**
	 * Sets the state of a relay output.
	 * @param on <i>True</i> to turn the heater on, <i>false</i> to turn it off.
	 * @throws Exception Thrown if the control command failed.
	 */
	public void setOutputState(int output, boolean on) throws Exception {
		Command result =  commandServer.sendCommandWithResponseUDP(new SetOutputCommand(output, on), endpoint);
		if (result instanceof ResultCommand) {
			ResultCommand resultCommand = (ResultCommand) result;
			switch (resultCommand.getResult()) {
				case ResultCommand.RESULT_OK:
					break;
				case ResultCommand.COMMAND_RESULT_BAD_ARG:
					throw new IllegalArgumentException("Arguments sent to the device were bad.");
				default:
					throw new Exception("Unknown result command.");
			}
		} else {
			if (result != null) {
				throw new Exception("Unknown result command, id: " + result.getCommandCode() + ".");
			} else {
				throw new Exception("No result command received.");
			}
		}
	}
	
	/**
	 * Returns the status of the relay outputs.
	 * @return <i>boolean[]</i> singaling if an output is on or off, for each output.
	 * @throws Exception Thrown if the control command failed.
	 */
	public boolean[] getOutputStatus() throws Exception {
		Command result = commandServer.sendCommandWithResponseUDP(new GetOutputStatusCommand(), endpoint);
		if (result instanceof OutputStatusCommand) {
			OutputStatusCommand osc = (OutputStatusCommand) result;
			return osc.getStatus();
		} else {
			if (result != null) {
				throw new Exception("Unknown result command, id: " + result.getCommandCode() + ".");
			} else {
				throw new Exception("No result command received.");
			}
		}
	}

}
