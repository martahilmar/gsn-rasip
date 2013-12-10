package org.liva.core.commands;

public interface CommandServer {
	public Command handleCommand(Command command);
	
	public void sendCommand(Command command, CommandEndpoint endpoint) throws Exception;
	
	public Command sendCommandWithResponse(Command command, CommandEndpoint endpoint) throws Exception;
}
