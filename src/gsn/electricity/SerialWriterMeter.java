package gsn.electricity;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.IOException;
import java.io.OutputStream;

public class SerialWriterMeter implements Runnable {
	OutputStream out;

	public SerialWriterMeter(OutputStream out) {
		this.out = out;
	}

	public void run() {
		try {
			byte[] helloMessage = new byte[] { 0x2f, 0x3f, 0x21, 0x0d, 0x0a };
			this.out.write(helloMessage);
			int c = 0;
			while ((c = System.in.read()) > -1) {
				this.out.write(c);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
