/**
 * This file was auto-generated by mofcomp -j version 1.0.0 on Wed Jan 12
 * 09:21:06 CET 2011.
 */

package net.i2cat.mantychore.model;

import java.io.*;

/**
 * This Class contains accessor and mutator methods for all properties defined in the CIM class SwitchPortStaticForwarding as well as methods
 * comparable to the invokeMethods defined for this class. This Class implements the SwitchPortStaticForwardingBean Interface. The CIM class
 * SwitchPortStaticForwarding is described as follows:
 * 
 * This association links a static database entry and the SwitchPort to which the entry applies.
 */
public class SwitchPortStaticForwarding extends Dependency implements
		Serializable {

	/**
	 * This constructor creates a SwitchPortStaticForwardingBeanImpl Class which implements the SwitchPortStaticForwardingBean Interface, and
	 * encapsulates the CIM class SwitchPortStaticForwarding in a Java Bean. The CIM class SwitchPortStaticForwarding is described as follows:
	 * 
	 * This association links a static database entry and the SwitchPort to which the entry applies.
	 */
	public SwitchPortStaticForwarding() {
	};

	/**
	 * This method create an Association of the type SwitchPortStaticForwarding between one SwitchPort object and StaticForwardingEntry object
	 */
	public static SwitchPortStaticForwarding link(SwitchPort
			antecedent, StaticForwardingEntry dependent) {

		return (SwitchPortStaticForwarding) Association.link(SwitchPortStaticForwarding.class, antecedent, dependent);
	}// link

} // Class SwitchPortStaticForwarding