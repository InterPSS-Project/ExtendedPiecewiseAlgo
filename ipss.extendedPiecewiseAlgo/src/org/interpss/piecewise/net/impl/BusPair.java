 /*
  * @(#)SubAreaProcessor.java   
  *
  * Copyright (C) 2006-2016 www.interpss.org
  *
  * This program is free software; you can redistribute it and/or
  * modify it under the terms of the GNU LESSER GENERAL PUBLIC LICENSE
  * as published by the Free Software Foundation; either version 2.1
  * of the License, or (at your option) any later version.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  * GNU General Public License for more details.
  *
  * @Author Mike Zhou
  * @Version 1.0
  * @Date 01/15/2016
  * 
  *   Revision History
  *   ================
  *
  */

package org.interpss.piecewise.net.impl;

import org.interpss.piecewise.net.BaseCuttingBranch;

import com.interpss.core.net.Bus;

/**
 * Class holding a pair of buses for SubArea processing. The bus pair are stored in
 * such a way that bus1.IntFlag <= bus2.InfFlag. 
 */
public class BusPair {
	// bus pair
	public Bus bus1, bus2;
	// store the smallest Bus.IntFlag in a SubArea
	public int subAreaFlag = BaseCuttingBranch.DefaultFlag;
	
	public BusPair(Bus bus) {
		this.bus1 = bus;
		this.bus2 = bus;
		this.subAreaFlag = bus.getIntFlag();
	}
	
	/**
	 * constructor
	 * 
	 * @param bus1
	 * @param bus2
	 */
	public BusPair(Bus bus1, Bus bus2) {
		// make sure that bus1.IntFlag <= bus2.InfFlag.
		if (bus1.getIntFlag() < bus2.getIntFlag()) {
			this.bus1 = bus1;
			this.bus2 = bus2;
		}
		else {
			this.bus1 = bus2;
			this.bus2 = bus1;
		}
	}
	
	/**
	 * get the bus pair key
	 * 
	 * @return the key
	 */
	public String getKey() {
		return createKey(bus1.getIntFlag(), bus2.getIntFlag());
	};
	
	/**
	 * static function to create bus pair key based on two flags
	 * 
	 * @param flag1
	 * @param flag2
	 * @return the key
	 */
	public static String createKey(int flag1, int flag2) {
		return flag1 + "_" + flag2;
	};
	
	/**
	 * static function to create bus pair key based on a flag
	 * 
	 * @param flag1
	 * @return the key
	 */
	public static String createKey(int flag1) {
		return flag1 + "_" + flag1;
	};
	
	public String toString() {
		return "[" + this.bus1.getIntFlag() + " \"" + this.bus1.getId() + "\",  "
				+ this.bus2.getIntFlag()  + " \""  + this.bus2.getId() + "\",  "
				+ this.subAreaFlag + "]";
	}
}
