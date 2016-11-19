 /*
  * @(#)PiecewiseAlgo1PhaseImpl.java   
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

package org.interpss.piecewise.base.impl;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.function.Function;

import org.apache.commons.math3.complex.Complex;
import org.interpss.numeric.exp.IpssNumericException;
import org.interpss.numeric.sparse.ISparseEqnComplex;
import org.interpss.piecewise.PiecewiseAlgorithm;
import org.interpss.piecewise.base.BaseCuttingBranch;
import org.interpss.piecewise.base.BaseSubArea;

import com.interpss.common.exp.InterpssException;

/**
 * A Network is divided into a set of SubArea/Network connected by a set of cutting branches. Each SubArea/Network is identified
 * by an unique flag(int). The Bus.intFlag is used to indicate where the bus is located in terms of SubArea. A set of
 * interface buses are defined in a SubArea to keep track of cutting branch connection relationship.
 * 
 * @author Mike
 *
 */
public abstract class BasePiecewiseAlgoImpl<TBus, TState, TSub extends BaseSubArea<ISparseEqnComplex, Complex[][]>> 
					implements  PiecewiseAlgorithm<TBus, TState, TSub> {
	// flag to indicate if the network subarea Y-matrix needs to be formed for the 
	// calculation
	protected boolean netYmatrixDirty;
	
	// network bus voltage storage place
	protected Hashtable<String, TState> netVoltage;
	
	// Sub-area/network list 
	protected List<TSub> subAreaNetList;
	
	/**
	 * Constructor
	 * 
	 * @param net AclfNetwork object
	 */
	public BasePiecewiseAlgoImpl() {
		this.netYmatrixDirty = true;
		this.netVoltage = new Hashtable<>();
		this.subAreaNetList = new ArrayList<>();
	}
	
	/**
	 * @return the netYmatrixDirty
	 */
	public boolean isNetYmatrixDirty() {
		return netYmatrixDirty;
	}


	/**
	 * @param netYmatrixDirty the netYmatrixDirty to set
	 */
	public void setNetYmatrixDirty(boolean netYmatrixDirty) {
		this.netYmatrixDirty = netYmatrixDirty;
	}

	/**
	 * the network voltage cache hashtable
	 * 
	 * @return the netVoltage
	 */
	public Hashtable<String, TState> getNetVoltage() {
		return netVoltage;
	}

	/**
	 * get the subarea list
	 * 
	 * @return the netVoltage
	 */
	public List<TSub> getSubAreaList() {
		return this.subAreaNetList;
	}

	/**
	 * get SubArea by the area flag
	 * 
	 * @param flag the area flag
	 * @return the subarea object
	 */
	public TSub getSubArea(int flag) {
		for (TSub subarea: this.subAreaNetList) {
			if (subarea.getFlag() == flag)
				return subarea;
		}
		return null;
	}
	
	@Override
	public Hashtable<String, TState> calculateNetVoltage(
				List<TSub> subAreaNetList,
				BaseCuttingBranch<TState>[] cbranches, 
				Function<TBus,TState> injCurrentFunc)  throws InterpssException, IpssNumericException {
		this.subAreaNetList = subAreaNetList;
  		
  		// Solve for the open-circuit voltage
  		calculateOpenCircuitVoltage(injCurrentFunc);

  		// calculate cutting branch current
    	calculateCuttingBranchCurrent(cbranches);

  		// calculate bus voltage by superposition of the open-circuit voltage and voltage by inject the 
    	// cutting branch current in the subsrea network
  		calcuateSubAreaVoltage(cbranches);  		

		return this.netVoltage;
	}
}

