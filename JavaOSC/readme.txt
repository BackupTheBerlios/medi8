Overview

JavaOSC is a library for talking the OSC protocol in Java. It is not,
in itself, a usable program. Rather, it is a library designed for building
programs that need to communicate over OSC (e.g., SuperCollider,
Max/MSP, Reaktor, etc.)



To Run

JavaOSC is not a standalone application -- it is designed to be used in other applications. But, there is a very basic app, created by John Thompson, for demonstration purposes.

To run the demo app, you can either double click on the library "lib/javaosc.jar", or you can execute the "run" ant task by typing "ant run".

Next launch SuperCollider. Open the file located in the "supercollider/" directory and load the synthdef into SuperCollider. Start the SC local server running. 

Click the "All On" button an start moving the sliders. You should hear the sounds change.

To see what messages the UI is sending, run either the CNMAT dumpOSC, or turn on dumpOSC in SuperCollider.



Orientation

Open Sound Control (OSC) is a UDP-based protocol for transmission of musical control data over an IP network. Applications like SuperCollider, Max/MSP, and Reaktor (among others) use OSC for network communication.JavaOSC is a class library that gives Java programs the capability of sending and receiving OSC. 

The classes that deal with sending OSC data are located in the com.illposed.osc package. The core classes are com.illposed.osc.OSCPort and com.illposed.osc.OSCMessage.

For code-correctness, there are some associated JUnit tests for the OSC classes. These tests are in com.illposed.osc.test. These can be run from the "test" ant task.



Use

The way to use the library is to instantiate an OSCPort connected to the receiving machine and then call the send() message on the port with the packet to send as the argument.

To see examples, look at the tests or the simple UI located in com.illposed.osc.ui.OscUI



Thanks

Thanks to John Thompson for writing the demo app!
