//  Created by cramakri on Thu Dec 13 2001.
//  Copyright (c) 2001 Illposed Software. All rights reserved.
//

/*
 * This implementation is based on Markus Gaelli and
 * Iannis Zannos' OSC implementation in Squeak:
 * http://www.emergent.de/Goodies/
 *
 */

package com.illposed.osc.test;
import com.illposed.osc.*;
import com.illposed.osc.utility.*;

public class OSCMessageTest extends junit.framework.TestCase {

	/**
	 * OscFloatTest constructor comment.
	 * @param name java.lang.String
	 */
	public OSCMessageTest(String name) {
		super(name);
	}

	/**
	 * Creation date: (2/23/2001 5:18:27 AM)
	 * @param result byte[]
	 * @param answer byte[]
	 */
	private void checkResultEqualsAnswer(byte[] result, byte[] answer) {
		if (result.length != answer.length)
			fail(
				"Result and answer aren't the same length "
					+ result.length
					+ " vs "
					+ answer.length);
		for (int i = 0; i < result.length; i++) {
			if (result[i] != answer[i]) {
				String errorString = "Didn't convert correctly: " + i;
				errorString = errorString + " result: " + result[i];
				errorString = errorString + " answer: " + answer[i];
				fail(errorString);
			}
		}
	}

	/**
	 * Creation date: (2/23/2001 3:31:46 AM)
	 */
	public void testDecreaseVolume() {
		Object[] args = { new Integer(1), new Float(0.2)};
		OSCMessage message = new OSCMessage("/sc/mixer/volume", args);
		byte[] answer =
			{
				47,
				115,
				99,
				47,
				109,
				105,
				120,
				101,
				114,
				47,
				118,
				111,
				108,
				117,
				109,
				101,
				0,
				0,
				0,
				0,
				44,
				105,
				102,
				0,
				0,
				0,
				0,
				1,
				62,
				76,
				-52,
				-51 };
		byte[] result = message.getByteArray();
		checkResultEqualsAnswer(result, answer);
	}

	/**
	 * Creation date: (2/23/2001 3:31:46 AM)
	 *
	 * See the comment in TestOscPacketByteArrayConverter::testFloat2OnStream
	 */
	public void testIncreaseVolume() {
		Object[] args = { new Integer(1), new Float(1.0)};
		OSCMessage message = new OSCMessage("/sc/mixer/volume", args);
		byte[] answer =
			{
				47,
				115,
				99,
				47,
				109,
				105,
				120,
				101,
				114,
				47,
				118,
				111,
				108,
				117,
				109,
				101,
				0,
				0,
				0,
				0,
				44,
				105,
				102,
				0,
				0,
				0,
				0,
				1,
				63,
				(byte) 128,
				0,
				0 };
		byte[] result = message.getByteArray();
		checkResultEqualsAnswer(result, answer);
	}

	/**
	 * Creation date: (2/23/2001 3:31:46 AM)
	 */
	public void testPrintStringOnStream() {
		OSCJavaToByteArrayConverter stream = new OSCJavaToByteArrayConverter();
		stream.write("/example1");
		stream.write(100);
		byte[] answer =
			{ 47, 101, 120, 97, 109, 112, 108, 101, 49, 0, 0, 0, 0, 0, 0, 100 };
		byte[] result = stream.toByteArray();
		checkResultEqualsAnswer(result, answer);
	}

	/**
	 * Creation date: (2/23/2001 3:31:46 AM)
	 */
	public void testRun() {
		OSCMessage message = new OSCMessage("/sc/run", null);
		byte[] answer = { 47, 115, 99, 47, 114, 117, 110, 0, 44, 0, 0, 0 };
		byte[] result = message.getByteArray();
		checkResultEqualsAnswer(result, answer);
	}

	/**
	 * Creation date: (2/23/2001 3:31:46 AM)
	 */
	public void testStop() {
		OSCMessage message = new OSCMessage("/sc/stop", null);
		byte[] answer = { 47, 115, 99, 47, 115, 116, 111, 112, 0, 0, 0, 0, 44, 0, 0, 0 };
		byte[] result = message.getByteArray();
		checkResultEqualsAnswer(result, answer);
	}
	
	public void testCreateSynth() {
		OSCMessage message = new OSCMessage("/s_new");
		message.addArgument(new Integer(1001));
		message.addArgument("freq");
		message.addArgument(new Float(440.0));
		byte[] answer = { 0x2F, 0x73, 0x5F, 0x6E, 0x65, 0x77, 0, 0, 0x2C, 0x69, 0x73, 0x66, 0, 0, 0, 0, 0, 0, 0x3, (byte) 0xE9, 0x66, 0x72, 0x65, 0x71, 0, 0, 0, 0, 0x43, (byte) 0xDC, 0, 0 };
		byte[] result = message.getByteArray();
		checkResultEqualsAnswer(result, answer);
	}

}