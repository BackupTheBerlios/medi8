package com.illposed.osc.test;

//
//  JavaOSCTest.java
//  JavaOSC
//
//  Created by C. Ramakrishnan on Sat Mar 15 2003.
//  Copyright (c) 2003 __MyCompanyName__. All rights reserved.
//

public class JavaOSCTest {

	public static void main(String args[]) {
//		TestSuite ts = new TestSuite(TestOSCPort.class);
		junit.textui.TestRunner.run(OSCPortTest.class);
		
	}

}
