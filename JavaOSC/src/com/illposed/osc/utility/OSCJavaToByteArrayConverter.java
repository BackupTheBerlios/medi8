/**
 * @author cramakrishnan
 *
 * Copyright (C) 2003, C. Ramakrishnan / Illposed Software
 * All rights reserved.
 * 
 * See license.txt (or license.rtf) for license information.
 * 
 * 
 * OSCJavaToByteArrayConverter is a helper class that translates
 * from Java types to the format the OSC spec specifies for those
 * types.
 *
 * This implementation is based on Markus Gaelli and
 * Iannis Zannos' OSC implementation in Squeak:
 * http://www.emergent.de/Goodies/
 */

package com.illposed.osc.utility;

import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.util.Enumeration;
import java.util.Vector;

public class OSCJavaToByteArrayConverter {

	protected ByteArrayOutputStream stream = new ByteArrayOutputStream();

	public OSCJavaToByteArrayConverter() {
		super();
	}

	/**
	 * Line up the LittleEnd of the bytes to a 4 byte boundry
	 * @return byte[]
	 * @param bytes byte[]
	 */
	private byte[] alignBigEndToFourByteBoundry(byte[] bytes) {
		int mod = bytes.length % 4;
		// if the remainder == 0 then return the bytes otherwise pad the bytes to
		// lineup correctly
		if (mod == 0)
			return bytes;
		int pad = 4 - mod;
		byte[] newBytes = new byte[pad + bytes.length];
		for (int i = 0; i < pad; i++)
			newBytes[i] = 0;
		for (int i = 0; i < bytes.length; i++)
			newBytes[pad + i] = bytes[i];
		return newBytes;
	}

	/**
	 * Line up the LittleEnd of the bytes to a 4 byte boundry
	 * 
	 * @return byte[]
	 * @param bytes byte[]
	 */
	private byte[] alignLittleEndToFourByteBoundry(byte[] bytes) {
		int mod = bytes.length % 4;
		// if the remainder == 0 then return the bytes otherwise pad the bytes to
		// lineup correctly
		if (mod == 4)
			return bytes;
		int pad = 4 - mod;
		byte[] newBytes = new byte[pad + bytes.length];
		for (int i = 0; i < bytes.length; i++)
			newBytes[i] = bytes[i];
		for (int i = 0; i < pad; i++)
			newBytes[bytes.length + i] = 0;
		return newBytes;
	}

	/**
	 * Creation date: (2/23/2001 2:43:25 AM)
	 * @param anArray java.lang.Object[]
	 *
	 */
	public void appendNullCharToAlignStream() {
		int mod = stream.size() % 4;
		int pad = 4 - mod;
		for (int i = 0; i < pad; i++)
			stream.write(0);
	}

	/**
	 * Creation date: (2/23/2001 2:21:53 AM)
	 * @return byte[]
	 */
	public byte[] toByteArray() {
		return stream.toByteArray();
	}

	/**
	 * Creation date: (2/23/2001 2:14:23 AM)
	 * @param bytes byte[]
	 */
	public void write(byte[] bytes) {
		writeUnderHandler(bytes);
	}

	/**
	 * Creation date: (2/23/2001 2:21:04 AM)
	 * @param i int
	 */
	public void write(int i) {
		BigInteger helper = BigInteger.valueOf(i);
		writeUnderHandler(helper.toByteArray());
	}

	/**
	 * Creation date: (2/23/2001 2:03:57 AM)
	 * @param f java.lang.Float
	 */
	public void write(Float f) {
		BigInteger helper =
			BigInteger.valueOf(Float.floatToIntBits(f.floatValue()));
		writeUnderHandler(helper.toByteArray());
	}

	/**
	 * Creation date: (2/23/2001 2:08:36 AM)
	 * @param i java.lang.Integer
	 */
	public void write(Integer i) {
		BigInteger helper = BigInteger.valueOf(i.longValue());
		writeUnderHandler(helper.toByteArray());
	}

	/**
	 * Creation date: (2/23/2001 2:08:36 AM)
	 * @param c char
	 */
	public void write(char c) {
		stream.write(c);
	}

	/**
	 * Creation date: (2/23/2001 2:02:54 AM)
	 * @param anObject java.lang.Object
	 */
	public void write(Object anObject) {
		// Can't do switch on class
		if (null == anObject)
			return;
		if (anObject instanceof Float) {
			write((Float) anObject);
			return;
		}
		if (anObject instanceof String) {
			write((String) anObject);
			return;
		}
		if (anObject instanceof Integer) {
			write((Integer) anObject);
			return;
		}
	}

	/**
	 * Creation date: (2/23/2001 1:57:35 AM)
	 * @param aString java.lang.String
	 */
	public void write(String aString) {
		writeUnderHandler(alignLittleEndToFourByteBoundry(aString.getBytes()));
	}

	/**
	 * Creation date: (2/23/2001 2:43:25 AM)
	 * @param aClass Class
	 */
	public void writeType(Class c) {
		// A big ol' case statement -- what's polymorphism mean, again?
		// I really wish I could extend the base classes!

		// use the appropriate flags to tell SuperCollider what kind of 
		// thing it is looking at      
		if (Integer.class.equals(c)) {
			stream.write('i');
			return;
		}
		if (java.math.BigInteger.class.equals(c)) {
			stream.write('h');
			return;
		}
		if (Float.class.equals(c)) {
			stream.write('f');
			return;
		}
		if (Double.class.equals(c)) {
			stream.write('d');
			return;
		}
		// this is no longer used in SC 3's version of OSC
		/*
		if (Symbol.class.equals(c)) {
		    stream.write('s');
		    return;
		}
		*/
		if (String.class.equals(c)) {
			stream.write('s');
			return;
		}
		if (Character.class.equals(c)) {
			stream.write('c');
			return;
		}

	}

	/**
	 * Creation date: (2/23/2001 2:43:25 AM)
	 * @param anArray java.lang.Object[]
	 */
	public void writeTypesArray(Object[] array) {
		// A big ol' case statement in a for loop -- what's polymorphism mean, again?
		// I really wish I could extend the base classes!

		for (int i = 0; i < array.length; i++) {
			if (null == array[i])
				continue;
			// if the array at i is a type of array write a [
			// This is used for nested arguments
			if (array[i].getClass().isArray()) {
				stream.write('[');
				// fill the [] with the SuperCollider types corresponding to the object
				// (i.e. Object of type String needs -s).
				writeTypesArray((Object[]) array[i]);
				// close the array
				stream.write(']');
				continue;
			}
			// Create a way to deal with Boolean type objects
			if (Boolean.TRUE.equals(array[i])) {
				stream.write('T');
				continue;
			}
			if (Boolean.FALSE.equals(array[i])) {
				stream.write('F');
				continue;
			}
			// go through the array and write the superCollider types as shown in the 
			// above method. the Classes derived here are used as the arg to the above method
			writeType(array[i].getClass());
		}
		// align the stream with padded bytes
		appendNullCharToAlignStream();
	}
	
	/**
	 * Same as writeSuperColliderTypes(Object[]), just that it takes a vector (for jdk1.1
	 * compatibility), rather than an array.
	 * @param vector  the collection I am to write out types for
	 */
	public void writeTypes(Vector vector) {
		// A big ol' case statement in a for loop -- what's polymorphism mean, again?
		// I really wish I could extend the base classes!

		Enumeration enum = vector.elements();
		Object nextObject;
		while (enum.hasMoreElements()) {
			nextObject = enum.nextElement();
			if (null == nextObject)
				continue;
			// if the array at i is a type of array write a [
			// This is used for nested arguments
			if (nextObject.getClass().isArray()) {
				stream.write('[');
				// fill the [] with the SuperCollider types corresponding to the object
				// (e.g., Object of type String needs -s).
				writeTypesArray((Object[]) nextObject);
				// close the array
				stream.write(']');
				continue;
			}
			// Create a way to deal with Boolean type objects
			if (Boolean.TRUE.equals(nextObject)) {
				stream.write('T');
				continue;
			}
			if (Boolean.FALSE.equals(nextObject)) {
				stream.write('F');
				continue;
			}
			// go through the array and write the superCollider types as shown in the 
			// above method. the Classes derived here are used as the arg to the above method
			writeType(nextObject.getClass());
		}
		// align the stream with padded bytes
		appendNullCharToAlignStream();
	}

	/**
	 * Creation date: (2/23/2001 2:15:31 AM)
	 * @param bytes byte[]
	 */
	private void writeUnderHandler(byte[] bytes) {

		try {
			stream.write(alignBigEndToFourByteBoundry(bytes));
		} catch (IOException e) {
			throw new RuntimeException("You're screwed: IOException writing to a ByteArrayOutputStream");
		}
	}
}