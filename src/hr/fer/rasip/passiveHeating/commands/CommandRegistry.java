package hr.fer.rasip.passiveHeating.commands;

import org.liva.core.commands.Command;


public class CommandRegistry extends org.liva.core.commands.CommandRegistry {
	public static final byte SET_FAN_COMMAND = 0x00;
	public static final byte SET_HEATER_COMMAND = 0x01;
	public static final byte GET_FAN_STATUS_COMMAND = 0x02;
	public static final byte GET_HEATER_STATUS_COMMAND = 0x03;
	public static final byte GET_AIRFLOW_STATUS_COMMAND = 0x04;
	public static final byte FAN_STATUS_COMMAND = 0x05;
	public static final byte HEATER_STATUS_COMMAND = 0x06;
	public static final byte AIRFLOW_STATUS_COMMAND = 0x07;
	public static final byte GET_FULL_STATUS_COMMAND = 0x08;
	public static final byte FULL_STATUS_COMMAND = 0x09;
	public static final byte SET_AIR_INTAKE_COMMAND = 0x0A;
	
	public static final byte RESULT_COMMAND = (byte) 0xff;
	
	
	public static CommandRegistry instance = null;
	
	public static CommandRegistry getInstance() {
		if (instance == null) {
			instance = new CommandRegistry();
		}
		return instance;
	}
		
	public Command getCommandByCode(byte code) {
		if (code == CommandRegistry.SET_FAN_COMMAND) {
			return new SetFanCommand();
		} else if (code == CommandRegistry.SET_HEATER_COMMAND) {
			return new SetHeaterCommand();
		} else if (code == CommandRegistry.GET_FAN_STATUS_COMMAND) {
			return new GetFanStatusCommand();
		} else if (code == CommandRegistry.GET_HEATER_STATUS_COMMAND) {
			return new GetHeaterStatusCommand();
		} else if (code == CommandRegistry.GET_AIRFLOW_STATUS_COMMAND) {
			return new GetAirflowStatusCommand();
		} else if (code == CommandRegistry.FAN_STATUS_COMMAND) {
			return new FanStatusCommand();
		} else if (code == CommandRegistry.HEATER_STATUS_COMMAND) {
			return new HeaterStatusCommand();
		} else if (code == CommandRegistry.AIRFLOW_STATUS_COMMAND) {
			return new AirflowStatusCommand();
		} else if (code == CommandRegistry.GET_FULL_STATUS_COMMAND) {
			return new GetFullStatusCommand();
		} else if (code == CommandRegistry.FULL_STATUS_COMMAND) {
			return new FullStatusCommand();
		} else if (code == CommandRegistry.SET_AIR_INTAKE_COMMAND) {
			return new SetAirIntakeCommand();
		} else if (code == CommandRegistry.RESULT_COMMAND) {
			return new ResultCommand();
		}
		return null;
	}
}
