 /*
  * @(#)PiecewiseAlgorithm.java   
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

import java.util.Hashtable;
import java.util.List;
import java.util.function.Function;

import org.apache.commons.math3.complex.Complex;
import org.interpss.numeric.exp.IpssNumericException;

import com.interpss.core.aclf.AclfBus;

/**
 * An AclfNetwork is divided into a set of SubArea connected by a set of cutting branches. Each SubArea is identified
 * by an unique flag(int). The AclfBus.intFlag is used to indicate where the bus is located in terms of SubArea. A set of
 * interface buses are defined in a SubArea to keep track of cutting branch connection relationship.
 * 
 * @author Mike
 *
 */
public interface PiecewiseAlgorithm {
	/**
	 * @return the netYmatrixDirty boolean field
	 */
	boolean isNetYmatrixDirty();


	/**
	 * @param netYmatrixDirty the netYmatrixDirty to set
	 */
	void setNetYmatrixDirty(boolean netYmatrixDirty);

	/**
	 * the network voltage cache hashtable
	 * 
	 * @return the netVoltage
	 */
	Hashtable<String, Complex> getNetVoltage();

	/**
	 * get the subarea list
	 * 
	 * @return the netVoltage
	 */
	List<SubArea> getSubAreaList();

	/**
	 * get SubArea by the area flag
	 * 
	 * @param flag the area flag
	 * @return the subarea object
	 */
	SubArea getSubArea(int flag);
	
	/**
	 * Solve for subarea open circuit bus voltage. The bus voltage results are stored in the netVoltage hashtable.
	 * 
	 * @param injCurrentFunc bus inject current calculation function
	 * @throws IpssNumericException
	 */
	void calculateOpenCircuitVoltage(Function<AclfBus,Complex> injCurrentFunc)  throws IpssNumericException;
	
	/**
	 * calculate the bus voltage for the subarea based on the cutting branch current and the bus open circuit voltage
	 * 
	 * @param cuttingBranches cutting branch storing the branch current
	 * @throws IpssNumericException
	 */
	void calcuateNetVoltage(CuttingBranch[] cuttingBranches)  throws IpssNumericException;
	
	/**
	 * calculate cutting branch current
	 * 
	 * @param cuttingBranches cutting branches
	 * @return the current array
	 */
	void calculateCuttingBranchCurrent(CuttingBranch[] cuttingBranches) throws IpssNumericException;
}

