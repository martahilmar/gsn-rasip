package hr.fer.rasip.passiveHeating.commands;

import java.net.InetAddress;

import org.liva.core.commands.Command;
import org.liva.core.commands.UDPCommandServer;
import org.liva.core.util.changeNotification.ChangeNotificationHelper;

public class CommandServer extends UDPCommandServer {
	
	ChangeNotificationHelper notificationHelper = new ChangeNotificationHelper();

	public CommandServer(InetAddress inetAddress, int port) {
		super(inetAddress, port, 256);
	}
	
	@Override
	public CommandRegistry getCommandRegistry() {
		return CommandRegistry.getInstance();
	}

	@Override
	public Command handleCommand(Command command) {
		// TODO Auto-generated method stub
		return null;
	}

	public void addListener(CommandServerListener listener) {
		notificationHelper.addListener(listener);
	}
	
	public void removeListener(CommandServerListener listener) {
		notificationHelper.removeListener(listener);
	}
}