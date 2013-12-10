package hr.fer.rasip.passiveHeating.control;

import hr.fer.rasip.passiveHeating.commands.AirflowStatusCommand;
import hr.fer.rasip.passiveHeating.commands.CommandServer;
import hr.fer.rasip.passiveHeating.commands.FanStatusCommand;
import hr.fer.rasip.passiveHeating.commands.FullStatusCommand;
import hr.fer.rasip.passiveHeating.commands.GetAirflowStatusCommand;
import hr.fer.rasip.passiveHeating.commands.GetFanStatusCommand;
import hr.fer.rasip.passiveHeating.commands.GetFullStatusCommand;
import hr.fer.rasip.passiveHeating.commands.GetHeaterStatusCommand;
import hr.fer.rasip.passiveHeating.commands.HeaterStatusCommand;
import hr.fer.rasip.passiveHeating.commands.ResultCommand;
import hr.fer.rasip.passiveHeating.commands.SetAirIntakeCommand;
import hr.fer.rasip.passiveHeating.commands.SetAirIntakeCommand.Intake;
import hr.fer.rasip.passiveHeating.commands.SetFanCommand;
import hr.fer.rasip.passiveHeating.commands.SetHeaterCommand;

import java.net.InetAddress;

import org.liva.core.commands.Command;
import org.liva.core.commands.UDPCommandEndpoint;


/**
 * Class used to communicate with heater control device.
 * @author Luka Lednicki
 *
 */
public class Control {
	
	private CommandServer commandServer;
	
	private UDPCommandEndpoint endpoint;
		
	/**
	 * Creates new control comunnicator.
	 * @param localInetAddress IP address of the local Ethernet interface to use. If only one interface is present, use <i>localhost</i> form <i>InetAddress</i>.
	 * @param localPort Local UDP port to use for communication with the control device.
	 * @param remoteInetAddress IP address of the control device.
	 */
	public Control(InetAddress localInetAddress, int localPort, InetAddress remoteInetAddress) {
		commandServer = new CommandServer(localInetAddress, localPort);
		endpoint = new UDPCommandEndpoint(remoteInetAddress);
		
	}

	/**
	 * Turns the heater on or off.
	 * @param on <i>True</i> to turn the heater on, <i>false</i> to turn it off.
	 * @throws Exception Thrown if the control command failed.
	 */
	public void setHeaterState(boolean on) throws Exception {
		Command result =  commandServer.sendCommandWithResponseUDP(new SetHeaterCommand(on), endpoint);
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
		}
	}
	
	/**
	 * Sets the desired fan speed.
	 * @param on <i>Int</i> in the interval [0 - 5], with 0 meaning that the fan should be off and 5 the top speed.
	 * @throws Exception Thrown if the control command failed.
	 */
	public void setFanPower(int targetPower) throws Exception {
		Command result = commandServer.sendCommandWithResponseUDP(new SetFanCommand(targetPower), endpoint);
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
		}
	}
	
	/**
	 * Selects air intake to use.
	 * @param airIntake <i>AirIntake</i> to to use.
	 * @throws Exception Thrown if the control command failed.
	 */
	public void setAirIntake(AirIntake airIntake) throws Exception {
		Intake commandIntake = Intake.NORMAL;
		switch (airIntake) {
		case NORMAL:
			commandIntake = Intake.NORMAL;
			break;
		case OVERRIDE:
			commandIntake = Intake.OVERRIDE;
		}
		Command result =  commandServer.sendCommandWithResponseUDP(new SetAirIntakeCommand(commandIntake), endpoint);
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
		}
	}
	
	/**
	 * Returns the status of the fan.
	 * @return <i>True</i> if the fan is on, <i>false</i> if not.
	 * @throws Exception Thrown if the control command failed.
	 */
	public boolean getFanStatus() throws Exception {
		Command result = commandServer.sendCommandWithResponseUDP(new GetFanStatusCommand(), endpoint);
		if (result instanceof FanStatusCommand) {
			FanStatusCommand fsc = (FanStatusCommand) result;
			return fsc.isOn();
		} else {
			throw new Exception("Unknown result command, id: " + result.getCommandCode() + ".");
		}
	}
	
	/**
	 * Returns the status of the heater.
	 * @return <i>True</i> if the heater is on, <i>false</i> if not.
	 * @throws Exception Thrown if the control command failed.
	 */
	public boolean getHeaterStatus() throws Exception {
		Command result = commandServer.sendCommandWithResponseUDP(new GetHeaterStatusCommand(), endpoint);
		if (result instanceof HeaterStatusCommand) {
			HeaterStatusCommand fsc = (HeaterStatusCommand) result;
			return fsc.isOn();
		} else {
			throw new Exception("Unknown result command, id: " + result.getCommandCode() + ".");
		}
	}
	
	/**
	 * Returns the airflow status.
	 * @return <i>True</i> if there is sufficient airflow to turn the heater on, <i>false</i> if not.
	 * @throws Exception Thrown if the control command failed.
	 */
	public boolean getAirflowStatus() throws Exception {
		Command result = commandServer.sendCommandWithResponseUDP(new GetAirflowStatusCommand(), endpoint);
		if (result instanceof AirflowStatusCommand) {
			AirflowStatusCommand fsc = (AirflowStatusCommand) result;
			return fsc.isAirflowPresent();
		} else {
			throw new Exception("Unknown result command, id: " + result.getCommandCode() + ".");
		}
	}
	
	/**
	 * Returns the full status of the control device.
	 * @return <i>ControlStatus</i> object containing all status variables.
	 * @throws Exception Thrown if the control command failed.
	 */
	public ControlStatus getFullStatus() throws Exception {
		Command result = commandServer.sendCommandWithResponseUDP(new GetFullStatusCommand(), endpoint);
		if (result instanceof FullStatusCommand) {
			FullStatusCommand fsc = (FullStatusCommand) result;
			return new ControlStatus(fsc);
		} else {
			throw new Exception("Unknown result command, id: " + result.getCommandCode() + ".");
		}
	}
}
