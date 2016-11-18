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

package org.interpss.piecewise;

import java.util.Hashtable;
import java.util.List;
import java.util.function.Function;

import org.apache.commons.math3.complex.Complex;
import org.interpss.numeric.exp.IpssNumericException;
import org.interpss.piecewise.base.BaseCuttingBranch;
import org.interpss.piecewise.onephase.SubArea1Phase;

import com.interpss.common.exp.InterpssException;

/**
 * An Network could be divided into a set of SubArea or SubNetwork connected by a set of cutting branches. 
 * Each SubArea/Network is identified by an unique flag(int). The Bus.intFlag is used to indicate where the 
 * bus is located in terms of SubArea/Net. A set of interface buses are defined in a SubArea/Net to keep 
 * track of cutting branch connection relationship.
 * 
 * @author Mike
 *
 * @template TBus Bus object generic type
 * @template TState Network state (current, voltage) generic type, Complex for single phase analysis
 */
public interface PiecewiseAlgorithm<TBus, TState> {
	/**
	 * Flag to indicate if the Y-matrix of the SubArea/Network is dirty
	 * 
	 * @return the netYmatrixDirty boolean field
	 */
	boolean isNetYmatrixDirty();


	/**
	 * Set the netYmatrixDirty flag
	 * 
	 * @param netYmatrixDirty the netYmatrixDirty to set
	 */
	void setNetYmatrixDirty(boolean netYmatrixDirty);

	/**
	 * During the calculation process, the network voltage is cached in a hashtable.
	 * 
	 * @return the netVoltage
	 */
	Hashtable<String, TState> getNetVoltage();

	/**
	 * get the SubArea/Network list
	 * 
	 * @return the SubArea/Network list
	 */
	List<SubArea1Phase> getSubAreaList();

	/**
	 * get SubArea/Net by the area flag
	 * 
	 * @param flag the area flag
	 * @return the subarea object
	 */
	SubArea1Phase getSubArea(int flag);
	
	/**
	 * Solve for SubArea/Network open circuit bus voltage. The bus voltage results are stored in the netVoltage hashtable.
	 * 
	 * @param injCurrentFunc bus inject current calculation function
	 * @throws IpssNumericException
	 */
	void calculateOpenCircuitVoltage(Function<TBus, TState> injCurrentFunc)  throws IpssNumericException;
	
	/**
	 * calculate the bus voltage for the SubArea/Network based on the cutting branch current and 
	 * the bus open circuit voltage
	 * 
	 * @param cuttingBranches cutting branch where the branch current is stored
	 * @throws IpssNumericException
	 */
	void calcuateSubAreaVoltage(BaseCuttingBranch<TState>[] cuttingBranches)  throws IpssNumericException;
	
	/**
	 * calculate cutting branch current, the results are stored in the cuttingBranches object
	 * 
	 * @param cuttingBranches cutting branches
	 */
	void calculateCuttingBranchCurrent(BaseCuttingBranch<TState>[] cuttingBranches) throws IpssNumericException;
	
	/**
	 * Calculate network bus voltage based on a set of cutting branches and a bus injection current calculate function.
	 * SubAreas/Network based on the cutting branches will be automatically formed inside the method 
	 * 
	 * @param cbranches cutting branch set
	 * @param injCurrentFunc function for calculating bus injection current
	 * @return network bus voltage pairs <BusId, Voltage>
	 * @throws InterpssException, IpssNumericException
	 */
	Hashtable<String,Complex> calculateNetVoltage(
			BaseCuttingBranch<TState>[] cbranches, 
			Function<TBus, TState> injCurrentFunc) throws InterpssException, IpssNumericException;

}

