package gsn.wrappers.rasip.RelayModel.commands;

import java.net.InetAddress;

import org.liva.core.commands.Command;
import org.liva.core.commands.UDPCommandServer;

public class CommandServer extends UDPCommandServer {

	public CommandServer(InetAddress inetAddress, int port) {
		super(inetAddress, port, 256);
		setResponseTimeout(5000);
	}

	@Override
	public Command handleCommand(Command command) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CommandRegistry getCommandRegistry() {
		return CommandRegistry.getInstance();
	}

}
