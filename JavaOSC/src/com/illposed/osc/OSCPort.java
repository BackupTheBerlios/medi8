/**
 * @author cramakrishnan
 *
 * Copyright (C) 2003, C. Ramakrishnan / Illposed Software
 * All rights reserved.
 * 
 * See license.txt (or license.rtf) for license information.
 * 
 * 
 * OscPort is the class the sends OSC messages.
 *
 * In your code, you should instantiate and hold onto an OscPort
 * pointing at the address and port number for the receiver.
 *
 * When you want to send an OSC message, call send().
 *
 * An example from com.illposed.osc.ui.OSCUI.doSend() :
 * 
 OscSuperColliderMessage msg = new OscSuperColliderMessage(nodeWidget. getText());
 try {
    oscPort.send(msg);
 } catch (Exception e) {
     showError("Couldn't send");
 }
 
 */

package com.illposed.osc;

import java.net.*;
import java.io.IOException;
import com.illposed.osc.utility.OSCByteArrayToJavaConverter;
import com.illposed.osc.utility.OSCPacketDispatcher;

public class OSCPort implements Runnable {

	protected DatagramSocket socket;
	protected InetAddress address;
	protected int port;
	
	// state for listening
	protected boolean isListening;
	protected OSCByteArrayToJavaConverter converter = new OSCByteArrayToJavaConverter();
	protected OSCPacketDispatcher dispatcher = new OSCPacketDispatcher();

	/**
	 * The port that the SuperCollider synth engine ususally listens too
	 */
	public static int defaultSCOSCPort() {
		return 57110;
	}
	
	/**
	 * The port that the SuperCollider language engine ususally listens too
	 */
	public static int defaultSCLangOSCPort() {
		return 57120;
	}
	
	/**
	 * @see java.lang.Object#finalize()
	 */
	protected void finalize() throws Throwable {
		super.finalize();
		socket.close();
	}

	/**
	 * Create an OSCPort that sends to newAddress, newPort
	 * @param newAddress InetAddress
	 * @param newPort int
	 */
	public OSCPort(InetAddress newAddress, int newPort)
		throws SocketException {
		socket = new DatagramSocket();
		address = newAddress;
		port = newPort;
	}

	/**
	 * Create an OSCPort that sends to newAddress, on the standard SuperCollider port
	 * @param newAddress InetAddress
	 *
	 * Default the port to the standard one for SuperCollider
	 */
	public OSCPort(InetAddress newAddress) throws SocketException {
		this(newAddress, defaultSCOSCPort());
	}

	/**
	 * Create an OSCPort that sends to localhost, on the standard SuperCollider port
	 * Default the address to localhost
	 * Default the port to the standard one for SuperCollider
	 */
	public OSCPort() throws UnknownHostException, SocketException {
		this(InetAddress.getLocalHost(), defaultSCOSCPort());
	}
	
	/**
	 * Create an OSCPort that listens on port
	 * @param port
	 * @throws SocketException
	 */
	public OSCPort(int port) throws SocketException {
		socket = new DatagramSocket(port);
		address = null;
		this.port = port;
	}

	/**
	 * @param aPacket OscPacket
	 */

	public void send(OSCPacket aPacket) throws IOException {
		byte[] byteArray = aPacket.getByteArray();
		DatagramPacket packet =
			new DatagramPacket(byteArray, byteArray.length, address, port);
		socket.send(packet);
	}

	/**
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		byte[] buffer = new byte[1500];
		DatagramPacket packet = new DatagramPacket(buffer, 1500);
		while (isListening) {
			try {
				socket.receive(packet);
				OSCPacket oscPacket = converter.convert(buffer, packet.getLength());
				dispatcher.dispatchPacket(oscPacket);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Start listening for incoming OSCPackets
	 */
	public void startListening() {
		isListening = true;
		Thread thread = new Thread(this);
		thread.start();
	}
	
	/**
	 * Stop listening for incoming OSCPackets
	 */
	public void stopListening() {
		isListening = false;
	}
	
	/**
	 * Register the listener for incoming OSCPackets addressed to an Address
	 * @param anAddress  the address to listen for
	 * @param listener   the object to invoke when a message comes in
	 */
	public void addListener(String anAddress, OSCListener listener) {
		dispatcher.addListener(anAddress, listener);
	}
	
	/**
	 * Close the socket and free-up resources. It's recommended that clients call
	 * this when they are done with the port.
	 */
	public void close() {
		socket.close();
	}

}
