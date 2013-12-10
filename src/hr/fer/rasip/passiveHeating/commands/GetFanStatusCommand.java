package hr.fer.rasip.passiveHeating.commands;

import org.liva.core.commands.Command;

public class GetFanStatusCommand extends Command {

	@Override
	public byte getCommandCode() {
		return CommandRegistry.GET_FAN_STATUS_COMMAND;
	}

	@Override
	public byte[] getBinaryArguments() {
		return new byte[0];
	}

	@Override
	public void setArgumentsFromBinary(byte[] arguments) {
	}

}
