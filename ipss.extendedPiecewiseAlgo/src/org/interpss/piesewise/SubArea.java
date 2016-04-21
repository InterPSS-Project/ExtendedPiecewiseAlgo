 /*
  * @(#)SubArea.java   
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

package org.interpss.piesewise;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.complex.Complex;
import org.interpss.numeric.sparse.ISparseEqnComplex;

/**
 * Class for modeling the SubArea concept
 */
public class SubArea {
	// SubArea flag, which should be unique
	public int flag;
	
	// interface bus ID array
	public List<String> interfaceBusIdList;
	
	// SubArea Y-matrix sparse eqn 
	public ISparseEqnComplex ySparseEqn;
	
	// SubArea Norton equivalent Z-matrix
	public Complex[][] zMatrix;
	
	/**
	 * default constructor
	 * 
	 * @param flag
	 */
	public SubArea(int flag) {
		this.flag = flag;
		this.interfaceBusIdList = new ArrayList<>();
	}
	
	/**
	 * constructor
	 * 
	 * @param flag
	 * @param ids
	 */
	public SubArea(int flag, String[] ids) {
		this.flag = flag;
		this.interfaceBusIdList = new ArrayList<>();
		for (String id : ids)
			this.interfaceBusIdList.add(id);
	}
	
	public String toString() {
		return "SubArea flag: " + this.flag + "\n"
				+ "Interface bus id set: " + this.interfaceBusIdList + "\n";
	}
}	
