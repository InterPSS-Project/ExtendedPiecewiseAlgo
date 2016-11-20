 /*
  * @(#)BaseSubArea.java   
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
  * @Date 04/15/2016
  * 
  *   Revision History
  *   ================
  *
  */

package org.interpss.piecewise.base;

import java.util.ArrayList;
import java.util.List;

/**
 * Base Class for modeling the SubArea concept. SubArea is a logical grouping concept
 * to group a set of buses and connected branches in the parent network together as a 
 * SubArea, using Bus.intFlag = SubArea.flag . The bus objects are still contained by 
 * the parent network, that is Bus.network = the parent network for all buses and branches 
 * in a SubArea.
 * 
 * @param <TYMatrix> generic type for defining the sub-area Y matrix
 * @param <TZMatrix> generic type for defining the cutting branch Z matrix
 */
public class BaseSubArea<TYmatrix, TZMatrix> {
	// SubArea flag, which should be unique
	private int flag;
	
	// interface bus ID array
	private List<String> interfaceBusIdList;
	
	// SubArea Y-matrix sparse eqn 
	private TYmatrix ySparseEqn;
	
	// SubArea Norton equivalent Z-matrix
	private TZMatrix zMatrix;
	
	/**
	 * default constructor
	 * 
	 * @param flag
	 */
	public BaseSubArea(int flag) {
		this.flag = flag;
		this.interfaceBusIdList = new ArrayList<>();
	}
	
	/**
	 * constructor
	 * 
	 * @param flag
	 * @param ids
	 */
	public BaseSubArea(int flag, String[] ids) {
		this.flag = flag;
		this.interfaceBusIdList = new ArrayList<>();
		for (String id : ids)
			this.interfaceBusIdList.add(id);
	}
	
	/**
	 * @return the flag
	 */
	public int getFlag() {
		return flag;
	}

	/**
	 * @param flag the flag to set
	 */
	public void setFlag(int flag) {
		this.flag = flag;
	}

	/**
	 * @return the interfaceBusIdList
	 */
	public List<String> getInterfaceBusIdList() {
		return interfaceBusIdList;
	}

	/**
	 * @return the ySparseEqn
	 */
	public TYmatrix getYSparseEqn() {
		return ySparseEqn;
	}

	/**
	 * @param ySparseEqn the ySparseEqn to set
	 */
	public void setYSparseEqn(TYmatrix ySparseEqn) {
		this.ySparseEqn = ySparseEqn;
	}

	/**
	 * @return the zMatrix
	 */
	public TZMatrix getZMatrix() {
		return zMatrix;
	}

	/**
	 * @param zMatrix the zMatrix to set
	 */
	public void setZMatrix(TZMatrix zMatrix) {
		this.zMatrix = zMatrix;
	}

	public String toString() {
		return "SubArea flag: " + this.flag + "\n"
				+ "Interface bus id set: " + this.interfaceBusIdList + "\n";
	}
}	
