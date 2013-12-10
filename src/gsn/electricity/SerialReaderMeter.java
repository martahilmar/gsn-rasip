package gsn.electricity;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

//import gnu.io.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SerialReaderMeter implements Runnable {
	private InputStream in;
	private OutputStream out;
	private RequestMeter output;
	private byte[] buffer = new byte[1024];
	private boolean run = true;

	public SerialReaderMeter(InputStream in, OutputStream out, RequestMeter output) {
		this.in = in;
		this.out = out;
		this.output = output;
	}

	public synchronized void run() {
		int data;
		byte[] close = new byte[] { 0x01, 0x42, 0x30, 0x03, 0x71 };
		int len = 0;
		while (run) {
			try {

				while ((data = in.read()) > -1) {
					if ((data == '\n' || data == 3) && !output.isDataSet()) {

						output.setDataSet(true);
						byte[] response = new byte[len];
						System.arraycopy(buffer, 0, response, 0, len);
						output.setAnswer(response);
						run = false;
					}
					// System.out.println(data);
					buffer[len++] = (byte) data;
				}
				// /System.out.print(new String(buffer,0,len));
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(-1);
			}
		}

	}
}
