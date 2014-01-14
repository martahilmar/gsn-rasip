package gsn.electricity;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import gnu.io.*;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KomunikatorMeter {
	private String portName = "COM3";
	private byte[] connectMessage = new byte[] { 0x2f, 0x3f, 0x21, 0x0d, 0x0a };
	private byte[] acknowledgeMessage = new byte[] { 0x06, 0x30, 0x30, 0x31,
			0x0D, 0x0A };

	private byte[] fullPowerMessage = new byte[] { 0x01, 0x52, 0x31, 0x02,
			0x31, 0x2e, 0x38, 0x2e, 0x30, 0x28, 0x29, 0x03, 0x5a };
	private byte[] fullPowerT1Message = new byte[] { 0x01, 0x52, 0x31, 0x02,
			0x31, 0x2e, 0x38, 0x2e, 0x31, 0x28, 0x29, 0x03, 0x5b };
	private byte[] fullPowerT2Message = new byte[] { 0x01, 0x52, 0x31, 0x02,
			0x31, 0x2e, 0x38, 0x2e, 0x32, 0x28, 0x29, 0x03, 0x58 };

	private byte[] maxCummulativePowerT1Message = new byte[] { 0x01, 0x52,
			0x31, 0x02, 0x31, 0x2e, 0x32, 0x2e, 0x31, 0x28, 0x29, 0x03, 0x51 };
	private byte[] maxcummulativePowerT2Message = new byte[] { 0x01, 0x52,
			0x31, 0x02, 0x31, 0x2e, 0x32, 0x2e, 0x32, 0x28, 0x29, 0x03, 0x52 };

	private byte[] currentPowerT1Message = new byte[] { 0x01, 0x52, 0x31, 0x02,
			0x31, 0x2e, 0x34, 0x2e, 0x31, 0x28, 0x29, 0x03, 0x57 };
	private byte[] currentPowerT2Message = new byte[] { 0x01, 0x52, 0x31, 0x02,
			0x31, 0x2e, 0x34, 0x2e, 0x32, 0x28, 0x29, 0x03, 0x54 };

	//private byte[] energyPhase1 = new byte[] { 0x01, 0x52, 0x31, 0x02, 
	//		0x32, 0x2e, 0x38, 0x2e, 0x31, 0x28, 0x29, 0x03, 0x58 };
			
	private byte[] energyPhase1 = new byte[] { 0x01, 0x52, 0x31, 0x02, 
			0x32, 0x31, 0x2e, 0x38, 0x2e, 0x30, 0x28, 0x29, 0x03, 0x68 };
	private byte[] energyPhase2 = new byte[] { 0x01, 0x52, 0x31, 0x02, 
			0x34, 0x31, 0x2e, 0x38, 0x2e, 0x30, 0x28, 0x29, 0x03, 0x6e };
	private byte[] energyPhase3 = new byte[] { 0x01, 0x52, 0x31, 0x02, 
			0x36, 0x31, 0x2e, 0x38, 0x2e, 0x30, 0x28, 0x29, 0x03, 0x6c };	
	
	private byte[] closeMessage = new byte[] { 0x01, 0x42, 0x30, 0x03, 0x71 };
	private int baudRate = 300;

	public KomunikatorMeter() {

	}

	public void Connect() throws Exception {
		CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
		if (portIdentifier.isCurrentlyOwned()) {
			System.out.println("Error: Port is currently in use");
		} else {
			CommPort commPort = portIdentifier.open(this.getClass().getName(), 200);

			if (commPort instanceof SerialPort) {
				RequestMeter req = new RequestMeter();

				SerialPort serialPort = (SerialPort) commPort;
				serialPort.setSerialPortParams(baudRate, SerialPort.DATABITS_7,	SerialPort.STOPBITS_1, SerialPort.PARITY_EVEN);

				InputStream in = serialPort.getInputStream();
				OutputStream out = serialPort.getOutputStream();
				Thread reader = new Thread(new SerialReaderMeter(in, out, req));
				reader.start();

				out.write(connectMessage);
				reader.join();
				if (req.isDataSet()) {
					// System.out.println(new String(req.getAnswer()));
				}

				RequestMeter ack = new RequestMeter();
				reader = new Thread(new SerialReaderMeter(in, out, ack));
				reader.start();
				out.write(acknowledgeMessage);
				reader.join();
				if (ack.isDataSet()) {
					// System.out.println(new String(ack.getAnswer()));
				}
				serialPort.close();
			} else {
				System.out.println("Port is not serial!");
			}
		}
	}

	public void Close() throws Exception {
		CommPortIdentifier portIdentifier = CommPortIdentifier
				.getPortIdentifier(portName);
		if (portIdentifier.isCurrentlyOwned()) {
			System.out.println("Error: Port is currently in use");
		} else {
			CommPort commPort = portIdentifier.open(this.getClass().getName(), 200);

			if (commPort instanceof SerialPort) {

				SerialPort serialPort = (SerialPort) commPort;
				serialPort.setSerialPortParams(baudRate, SerialPort.DATABITS_7,
						SerialPort.STOPBITS_1, SerialPort.PARITY_EVEN);

				OutputStream out = serialPort.getOutputStream();

				out.write(closeMessage);
				serialPort.close();

			} else {
				System.out.println("Port is not serial!");
			}
		}
		Thread.sleep(200);
	}

	public byte[] GetReading(byte[] request) throws Exception {
		CommPortIdentifier portIdentifier = CommPortIdentifier
				.getPortIdentifier(portName);
		if (portIdentifier.isCurrentlyOwned()) {
			System.out.println("Error: Port is currently in use");
			return null;
		} else {
			CommPort commPort = portIdentifier.open(this.getClass().getName(), 200);

			if (commPort instanceof SerialPort) {
				RequestMeter req = new RequestMeter();

				SerialPort serialPort = (SerialPort) commPort;
				serialPort.setSerialPortParams(baudRate, SerialPort.DATABITS_7, SerialPort.STOPBITS_1, SerialPort.PARITY_EVEN);

				InputStream in = serialPort.getInputStream();
				OutputStream out = serialPort.getOutputStream();

				Thread reader = new Thread(new SerialReaderMeter(in, out, req));
				reader.start();

				out.write(request);
				reader.join();
				if (req.isDataSet()) {
					System.out.println(new String(req.getAnswer()));
				}
				serialPort.close();
				return req.getAnswer();
			} else {
				System.out.println("Port is not serial!");
				return null;
			}
		}
	}

	public double GetPower() throws Exception {
		//this.Connect();
		double result = this.ParseResponse(this.GetReading(this.fullPowerMessage));
		//this.Close();
		return result;
	}

	public double GetPowerTariffOne() throws Exception {
		//this.Connect();
		double result = this.ParseResponse(this.GetReading(this.fullPowerT1Message));
		//this.Close();
		return result;
	}

	public double GetPowerTariffTwo() throws Exception {
		//this.Connect();
		double result = this.ParseResponse(this.GetReading(this.fullPowerT2Message));
		//this.Close();
		return result;
	}

	public double GetMaxCummulativePowerTariff1() throws Exception {
		//this.Connect();
		double result = this.ParseResponse(this.GetReading(this.maxCummulativePowerT1Message));
		//this.Close();
		return result;
	}

	public double GetMaxCummulativePowerTariff2() throws Exception {
		//this.Connect();
		double result = this.ParseResponse(this.GetReading(this.maxcummulativePowerT2Message));
		//this.Close();
		return result;
	}

	public double GetCurrentPowerTariff1() throws Exception {
		//this.Connect();
		double result = this.ParseResponse(this.GetReading(this.currentPowerT1Message));
		//this.Close();
		return result;
	}

	public double GetCurrentPowerTariff2() throws Exception {
		this.Connect();
		double result = this.ParseResponse(this.GetReading(this.currentPowerT2Message));
		this.Close();
		return result;
	}
	


	
	public double GetEnergyPhase1() throws Exception {
		//this.Connect();
		double result = this.ParseResponse(this.GetReading(this.energyPhase1));
		//this.Close();
		return result;
	}

	public double GetEnergyPhase2() throws Exception {
		//this.Connect();
		double result = this.ParseResponse(this.GetReading(this.energyPhase2));
		//this.Close();
		return result;
	}

	public double GetEnergyPhase3() throws Exception {
		//this.Connect();
		double result = this.ParseResponse(this.GetReading(this.energyPhase3));
		//this.Close();
		return result;
	}

	private double ParseResponse(byte[] response) {
		String s = new String(response);
		String pattern = "\\((\\d+.\\d+) [^\\)]+\\).*";
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(s);
		if (m.find()) {
			double i = Double.parseDouble(m.group(1));
			return i;

		}
		return -1;
	}
}
