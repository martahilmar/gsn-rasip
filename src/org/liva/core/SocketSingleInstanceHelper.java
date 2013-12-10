package org.liva.core;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import org.liva.core.util.changeNotification.ChangeNotificationHelper;
import org.liva.core.worker.PeriodicWorker;
import org.liva.core.worker.PeriodicWorkerThread;

public class SocketSingleInstanceHelper {

	private int port;
	private PeriodicWorker instanceChecker;
	private ServerSocket socket;
	private String key;
	ChangeNotificationHelper notificationHelper;

	public SocketSingleInstanceHelper(int port, String key) {
		this.port = port;
		this.key = key;
		notificationHelper = new ChangeNotificationHelper();
	}
	
	public boolean registerInstance() {
		return registerInstance(null);
	}
	
	public static InetAddress getLoopbackAddress() {
		try {
			return InetAddress.getByAddress(new byte[] {127, 0, 0, 1});
		} catch (UnknownHostException e) {
			return null;
		}
	}
		
	public boolean registerInstance(Serializable data) {
		try {
			socket = new ServerSocket(port, 10, getLoopbackAddress());
			instanceChecker = new InstanceChecker();
			instanceChecker.start();
			return true;
		} catch (UnknownHostException e) {
			// TODO What now?
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO notify instance
			try {
				Socket clientSocket = new Socket(getLoopbackAddress(), port);
				OutputStream os = clientSocket.getOutputStream();
				os.write(key.getBytes());
				if (data != null) {
					ObjectOutputStream oos = new ObjectOutputStream(os);
					oos.writeObject(data);
				}
				os.flush();
				os.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return false;
		}
	}
	
	public void unregisterInstance() {
		if (instanceChecker != null) {
			instanceChecker.stop();
			instanceChecker = null;
		}
		if (socket != null) {
			try {
				socket.close();
			} catch (IOException e) {
				// TODO What now?
				e.printStackTrace();
			}
			socket = null;
		}
	}
	
	public void addListener(SingleInstanceHelperListener listener) {
		notificationHelper.addListener(listener);
	}
	
	public void removeListener(SingleInstanceHelperListener listener) {
		notificationHelper.removeListener(listener);
	}
	
	
	
	
	
	public class InstanceChecker extends PeriodicWorkerThread {

		public InstanceChecker() {
			super(100);
		}

		@Override
		public void doPeriodicWork(long realInterval) {
			if (socket != null) {
				try {
					Socket clientSocket = socket.accept();
					/*
					InputStream is = clientSocket.getInputStream();
					BufferedReader reader = new BufferedReader(new InputStreamReader(is));
					byte[] keyBytes = key.getBytes();
					int keyLength = keyBytes.length;
					char[] buffer = new char[keyLength];
					if (reader.read(buffer, 0, keyLength) == keyLength) {
						boolean keyOk = true;
						for (int i = 0; i < keyLength; i++) {
							if (buffer[i] != keyBytes[i]) {
								keyOk = false;
								break;
							}
						}
						if (keyOk) {
							reader.
						}
					}*/
					notificationHelper.fireStateChanged(new SingleInstanceHelperEvent(null));
					clientSocket.close();
				} catch (IOException e) {
					if (socket == null || socket.isClosed()) {
						return;
					}
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
			}
			
		}
		
	}
}
