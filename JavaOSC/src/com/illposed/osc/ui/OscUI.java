//
//  OscUI.java
//  JavaOSC
//
//  Created by cramakri on Thu Dec 13 2001.
//  Copyright (c) 2001 Illposed Software. All rights reserved.
//
//  Modified by JT March 2003

// include this
package com.illposed.osc.ui;

// import these packages as well
import com.illposed.osc.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.net.InetAddress;
import java.util.Date;

// OscUI is a subClass of JPanel
public class OscUI extends JPanel {

	// declare some variables
	protected JFrame parent;
	protected JTextField addressWidget;
	protected JTextField portWidget;
	protected JTextField textBox;
	protected JTextField textBox2;
	protected JTextField textBox3;
	protected JTextField textBox4 = new JTextField(String.valueOf(1000), 8);
	protected JLabel delayLabel;

	protected OSCPort oscPort;

	// create a constructor
	// OscUI takes an argument of myParent which is a JFrame
	public OscUI(JFrame myParent) {
		super();
		parent = myParent;
		makeDisplay();
		try {
			oscPort = new OSCPort();
		} catch (Exception e) {
			// this is just a demo program, so this is acceptable behavior
			e.printStackTrace();
		}
	}

	// create a method for widget building  
	public void makeDisplay() {

		// setLayout to be a BoxLayout
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		// call these methods ???? to be defined later

		addOscServerAddressPanel();
		addGlobalControlPanel();
		addFirstSynthPanel();
		addSecondSynthPanel();
		addThirdSynthPanel();

	}

	// create a method for adding ServerAddress Panel to the OscUI Panel
	protected void addOscServerAddressPanel() {

		// variable addressPanel holds an instance of JPanel.
		// instance of JPanel received from makeNewJPanel method
		JPanel addressPanel = makeNewJPanel1();
		addressPanel.setBackground(new Color(123, 150, 123));
		// variable addressWidget holds an instance of JTextField
		addressWidget = new JTextField("localhost");
		// variable setAddressButton holds an insatnce of JButton with 
		// a "Set Address" argument for its screen name
		JButton setAddressButton = new JButton("Set Address");
		setAddressButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// perform the addressChanged method when action is received
				addressChanged();
			}
		});

		// variable portWidget holds an instance of JLabel with the OscPort as the text
		// it looks like OscPort has a method to get the default SuperCollider port
		JLabel portWidget =
			new JLabel(Integer.toString(OSCPort.defaultSCOSCPort()));

		portWidget.setForeground(new Color(255, 255, 255));
		JLabel portLabel = new JLabel("Port");
		portLabel.setForeground(new Color(255, 255, 255));

		// add the setAddressButton to the addressPanel
		addressPanel.add(setAddressButton);
		// portWidget = new JTextField("57110");
		// add the addressWidget to the addressPanel
		addressPanel.add(addressWidget);
		// add the JLabel "Port" to the addressPanel
		addressPanel.add(portLabel);
		// add te portWidget tot eh addressPanel
		addressPanel.add(portWidget);

		//??? add address panel to the JPanel OscUI
		add(addressPanel);
	}

	public void addGlobalControlPanel() {
		JPanel globalControlPanel = makeNewJPanel();
		JButton globalOffButton = new JButton("All Off");
		JButton globalOnButton = new JButton("All On");
		textBox4 = new JTextField(String.valueOf(1000), 8);
		delayLabel = new JLabel("All Off delay in ms");
		delayLabel.setForeground(new Color(255, 255, 255));
		globalControlPanel.setBackground(new Color(13, 53, 0));

		globalOnButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				doSendGlobalOn(1000, 1001, 1002);
			}
		});
		// ??? have an anonymous class listen to the setAddressButton action
		globalOffButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				doSendGlobalOff(1000, 1001, 1002);
			}
		});

		globalControlPanel.add(globalOnButton);
		globalControlPanel.add(globalOffButton);
		globalControlPanel.add(textBox4);
		globalControlPanel.add(delayLabel);
		add(globalControlPanel);
	}

	// create method for adding a the buttons and synths of the
	// first synth on one panel
	public void addFirstSynthPanel() {
		// the variable firstSynthPanel holds an instance of Jpanel
		// created by the makeNewJPanel method
		JPanel firstSynthPanel = makeNewJPanel();
		// the variable firstSynthButytonOn holds an instance of JButton labeled "On"

		firstSynthPanel.setBackground(new Color(13, 23, 0));
		JButton firstSynthButtonOn = new JButton("On");
		firstSynthButtonOn.setBackground(new Color(123, 150, 123));
		// the variable firstSynthButtonOff holds an instance of JButton labeled "off"
		JButton firstSynthButtonOff = new JButton("Off");
		// the variable slider holds an instance of JSlider which is 
		// set to be a Horizontal slider
		JSlider slider = new JSlider(JSlider.HORIZONTAL);
		// set the minimum value of the slider to 20
		slider.setMinimum(0);
		slider.setMaximum(1000000000);
		// set the inital value of the slider to 400
		slider.setValue(1 / 5);
		textBox = new JTextField(String.valueOf((1 / 5) * 10000), 8);
		firstSynthButtonOn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// when the on button is pushed, doSendOn method is invoked
				// send the arguments for frequency and node
				doSendOn(400, 1000);
			}
		});
		// when the on button is pushed, doSendOff method is invoked
		// send the argument for node        
		firstSynthButtonOff.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// when the action occurs the doSend1 method is invoked
				doSendOff(1000);
			}
		});

		// when the slider is moved, doSendSlider method is invoked
		// send the argument for freq and node
		slider.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				JSlider slider = (JSlider) e.getSource();
				if (slider.getValueIsAdjusting()) {
					float freq = (float) slider.getValue();
					// exponential mapping (whew!)
					freq = (freq / 1000000000) * (freq / 1000000000);
					freq = freq * 10000;
					freq = freq + 20;
					doPrintValue(freq);
					doSendSlider(freq, 1000);
				}
			}
		});

		// add firstSynthButtonOn to the firstSynthPanel
		firstSynthPanel.add(firstSynthButtonOn);
		// add firstSendButtonOff to the firstSynthPanel
		firstSynthPanel.add(firstSynthButtonOff);
		// add slider to the firstSynthPanel
		firstSynthPanel.add(slider);
		firstSynthPanel.add(textBox);

		// add the firstSynthpanel to the OscUI Panel        
		add(firstSynthPanel);
	}

	///********************
	// create method for adding a the Second Synth Panel
	protected void addSecondSynthPanel() {
		// make a new JPanel called secondSynthPanel
		JPanel secondSynthPanel = makeNewJPanel();
		secondSynthPanel.setBackground(new Color(13, 23, 0));
		// the variable secondSynthButtonOn holds an instance of JButton
		JButton secondSynthButtonOn = new JButton("On");
		// the variable secondSynthButtonOff holds an instance of JButton
		JButton secondSynthButtonOff = new JButton("Off");
		// the variable slider2 holds an instance of JSlider positioned
		// horizontally
		JSlider slider2 = new JSlider(JSlider.HORIZONTAL);
		slider2.setMinimum(0);
		slider2.setMaximum(1000000000);
		// add the action for the On button
		slider2.setValue(2 / 5);
		textBox2 = new JTextField(String.valueOf((2 / 5) * 10000), 8);
		secondSynthButtonOn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// when the action occurs the doSendOn method is invoked
				// with the arguments for freq and node
				doSendOn(200, 1001);
			}
		});
		// add the action for the Off button       
		secondSynthButtonOff.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// when the action occurs the doSendOff method is invoked
				// with the argument for node
				doSendOff(1001);
			}
		});
		// add the action for the slider      
		slider2.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JSlider slider2 = (JSlider) e.getSource();
				if (slider2.getValueIsAdjusting()) {
					float freq = (float) slider2.getValue();
					freq = (freq / 1000000000) * (freq / 1000000000);
					freq = freq * 10000;
					freq = freq + 20;
					doPrintValue2(freq);
					// arguments for freq and node
					doSendSlider(freq, 1001);
				}
			}
		});

		// ******************
		// add Buttons and Slider to secondSynthPanel
		secondSynthPanel.add(secondSynthButtonOn);
		secondSynthPanel.add(secondSynthButtonOff);
		secondSynthPanel.add(slider2);
		secondSynthPanel.add(textBox2);
		// add the secondSynthPanel2 to the OscUI Panel        
		add(secondSynthPanel);

	}

	protected void addThirdSynthPanel() {
		JPanel thirdSynthPanel = makeNewJPanel();
		thirdSynthPanel.setBackground(new Color(13, 23, 0));
		JButton thirdSynthButtonOn = new JButton("On");
		JButton thirdSynthButtonOff = new JButton("Off");
		JSlider slider3 = new JSlider(JSlider.HORIZONTAL);
		slider3.setMinimum(0);
		slider3.setMaximum(1000000000);
		slider3.setValue(1 / 25);
		textBox3 = new JTextField(String.valueOf((1 / 25) * 10000), 8);
		thirdSynthButtonOn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// when the action occurs the doSendOn method is invoked
				// with arguments for freq and node
				doSendOn(80, 1002);
			}
		});

		thirdSynthButtonOff.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// when the action occurs the doSendOff method is invoked
				// with argument for node
				doSendOff(1002);
			}
		});

		slider3.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				//  JSlider source = (JSlider) e.getSource();
				JSlider slider3 = (JSlider) e.getSource();
				//if (source.getValueIsAdjusting()) {
				if (slider3.getValueIsAdjusting()) {
					// int freq = (int)source.getValue();
					float freq = (float) slider3.getValue();
					freq = (freq / 1000000000) * (freq / 1000000000);
					freq = freq * 10000;
					freq = freq + 20;
					doPrintValue3(freq);
					// when the action occurs the doSendSlider method is invoked
					// with arguments for freq and node
					doSendSlider(freq, 1002);
				}
			}
		});

		// ******************
		// add thirdSynthButtons and slider to the thirdSynthPanel
		thirdSynthPanel.add(thirdSynthButtonOn);
		thirdSynthPanel.add(thirdSynthButtonOff);
		thirdSynthPanel.add(slider3);
		thirdSynthPanel.add(textBox3);
		// add the sendButtonPanel2 to the OscUI Panel        
		add(thirdSynthPanel);

	}

	// here is the make new JPanel method    
	protected JPanel makeNewJPanel() {
		// a variable tempPanel holds an instance of JPanel
		JPanel tempPanel = new JPanel();
		// set the Layout of tempPanel to be a FlowLayout aligned left
		tempPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		// function returns the tempPanel
		return tempPanel;
	}

	// here is the make new JPanel method    
	protected JPanel makeNewJPanel1() {
		// a variable tempPanel holds an instance of JPanel
		JPanel tempPanel1 = new JPanel();
		// set the Layout of tempPanel to be a FlowLayout aligned left
		tempPanel1.setLayout(new FlowLayout(FlowLayout.RIGHT));
		// function returns the tempPanel
		return tempPanel1;
	}

	// actions
	// create a method for the addressChanged action (Set Address)
	public void addressChanged() {
		// the variable oscPort tries to get an instance of OscPort at the address
		// indicated by the addressWidget
		try {
			oscPort =
				new OSCPort(InetAddress.getByName(addressWidget.getText()));
			// if the oscPort variable fails to be instantiated then sent the error message
		} catch (Exception e) {
			showError("Couldn't set address");
		}
	}

	// create a method for the doSend action (Send)
	public void doSendOn(float freq, int node) {
		// if "Set Address" has not been performed then give the message to set it first
		if (null == oscPort) {
			showError("Please set an address first");
		}

		// send an OSC message to start the synth "pink" on node 1000.        
		Object[] args =
			{
				"javaosc-example",
				new Integer(node),
				new Integer(1),
				new Integer(0),
				"freq",
				new Float(freq)};
		// a comma is placed after /s_new in the code
		OSCMessage msg =
			new OSCMessage("/s_new", args);

		// Object[] args2 = {new Symbol("amp"), new Float(0.5)};
		// OscMessage msg2 = new OscMessage("/n_set", args2);
		//oscPort.send(msg);

		// try to use the send method of oscPort using the msg in nodeWidget
		// send an error message if this doesn't happen
		try {
			oscPort.send(msg);
		} catch (Exception e) {
			showError("Couldn't send");
		}
	}

	// create a method for the doSend1 action (Send)
	public void doSendOff(int node) {
		// if "Set Address" has not been performed then give the message to set it first
		if (null == oscPort) {
			showError("Please set an address first");
		}

		// send an OSC message to free the node 1000        
		Object[] args = { new Integer(node)};
		OSCMessage msg1 =
			new OSCMessage("/n_free", args);

		// try to use the send method of oscPort using the msg in nodeWidget
		// send an error message if this doesn't happen
		try {
			oscPort.send(msg1);
		} catch (Exception e) {
			showError("Couldn't send");
		}
	}

	public void doPrintValue(float freq) {
		String freqString = new String();
		//freqString.toString(freq);
		textBox.setText(String.valueOf(freq));
	}

	public void doPrintValue2(float freq) {
		String freqString = new String();
		//freqString.toString(freq);
		textBox2.setText(String.valueOf(freq));
	}

	public void doPrintValue3(float freq) {
		String freqString = new String();
		//freqString.toString(freq);
		textBox3.setText(String.valueOf(freq));
	}

	// create a method for the doSend3 action (Send)
	public void doSendSlider(float freq, int node) {
		// if "Set Address" has not been performed then give the message to set it first
		if (null == oscPort) {
			showError("Please set an address first");
		}

		// send an OSC message to set the node 1000      
		Object[] args = { new Integer(node), "freq", new Float(freq)};
		OSCMessage msg2 =
			new OSCMessage("/n_set", args);

		// try to use the send method of oscPort using the msg in nodeWidget
		// send an error message if this doesn't happen
		try {
			oscPort.send(msg2);
		} catch (Exception e) {
			showError("Couldn't send");
		}
	}

	public void doSendGlobalOff(int node, int node2, int node3) {
		if (null == oscPort) {
			showError("Please set an address first");
		}

		Object[] args = { new Integer(node), new Character(' ')};
		OSCMessage msg3 =
			new OSCMessage("/n_free", args);

		Object[] args2 = { new Integer(node2), new Character(' ')};
		OSCMessage msg4 =
			new OSCMessage("/n_free", args2);

		Object[] args3 = { new Integer(node3)};
		OSCMessage msg5 =
			new OSCMessage("/n_free", args3);

		// create a timeStamped bundle of the messages
		OSCPacket[] packets = { msg3, msg4, msg5 };
		Date newDate = new Date();
		long time = newDate.getTime();
		Integer delayTime = Integer.valueOf(textBox4.getText());
		//int delayTime = textBox4.getText;
		// implement changable GUI time;
		time = time + (delayTime.longValue());
		newDate.setTime(time);

		OSCBundle bundle = new OSCBundle(packets, newDate);

		try {
			oscPort.send(bundle);
		} catch (Exception e) {
			showError("Couldn't send");
		}

		//OscBundle bundle = new OscBundle (msg3, msg4, msg5);
		/*
		try {
		oscPort.send(msg3);
		} catch (Exception e){
		showError("Couldn't send");
		}
		
		try {
		oscPort.send(msg4);
		} catch (Exception e){
		showError("Couldn't send");
		}
		
		try {
		oscPort.send(msg5);
		} catch (Exception e){
		showError("Couldn't send");
		}
		*/
	}

	public void doSendGlobalOn(int node, int node2, int node3) {
		if (null == oscPort) {
			showError("Please set an address first");
		}

		Object[] args =
			{
				"javaosc-example",
				new Integer(node),
				new Integer(1),
				new Integer(0),
				new Character(' ')};
		OSCMessage msg3 =
			new OSCMessage("/s_new", args);

		Object[] args2 =
			{
				"javaosc-example",
				new Integer(node2),
				new Integer(1),
				new Integer(0),
				new Character(' ')};
		OSCMessage msg4 =
			new OSCMessage("/s_new", args2);

		Object[] args3 =
			{ "javaosc-example", new Integer(node3), new Integer(1), new Integer(0)};
		OSCMessage msg5 =
			new OSCMessage("/s_new", args3);

		try {
			oscPort.send(msg3);
		} catch (Exception e) {
			showError("Couldn't send");
		}

		try {
			oscPort.send(msg4);
		} catch (Exception e) {
			showError("Couldn't send");
		}

		try {
			oscPort.send(msg5);
		} catch (Exception e) {
			showError("Couldn't send");
		}
	}

	// create a showError method
	protected void showError(String anErrorMessage) {
		// tell the JOptionPane to showMessageDialog
		JOptionPane.showMessageDialog(parent, anErrorMessage);
	}
}
