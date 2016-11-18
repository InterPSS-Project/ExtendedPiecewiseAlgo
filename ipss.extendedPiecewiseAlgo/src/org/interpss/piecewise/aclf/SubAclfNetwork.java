 /*

  * @(#)SubNetwork.java   
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

package org.interpss.piecewise.aclf;

import org.apache.commons.math3.complex.Complex;
import org.interpss.numeric.sparse.ISparseEqnComplex;
import org.interpss.piecewise.net.BaseSubNetwork;

import com.interpss.CoreObjectFactory;
import com.interpss.core.aclf.AclfBranch;
import com.interpss.core.aclf.AclfBus;
import com.interpss.core.aclf.AclfNetwork;

/**
 * Class for modeling the SubArea concept for representing positive sequence sub-network. 
 */
public class SubAclfNetwork extends BaseSubNetwork<AclfBus, AclfBranch, AclfNetwork, ISparseEqnComplex, Complex[][]>{
	
	/**
	 * default constructor
	 * 
	 * @param flag
	 */
	public SubAclfNetwork(int flag) {
		super(flag);
	}
	
	/**
	 * constructor
	 * 
	 * @param flag
	 * @param ids
	 */
	public SubAclfNetwork(int flag, String[] ids) {
		super(flag, ids);
	}
	
	@Override public AclfNetwork createSubNetwork() {
		return CoreObjectFactory.createAclfNetwork();
	};	
}	
