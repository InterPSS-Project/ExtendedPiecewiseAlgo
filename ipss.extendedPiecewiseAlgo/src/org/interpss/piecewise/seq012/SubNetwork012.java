 /* @(#)SubNetwork012.java   
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

package org.interpss.piecewise.seq012;

import org.apache.commons.math3.complex.Complex;
import org.interpss.numeric.sparse.ISparseEqnComplex;
import org.interpss.piecewise.base.BaseSubNetwork;

import com.interpss.CoreObjectFactory;
import com.interpss.common.exp.InterpssException;
import com.interpss.core.acsc.AcscBranch;
import com.interpss.core.acsc.AcscBus;
import com.interpss.core.acsc.AcscNetwork;
import com.interpss.core.acsc.SequenceCode;

/**
 * Class for modeling the SubArea concept for representing 012 sub-network. 
 */
public class SubNetwork012 extends BaseSubNetwork<AcscBus, AcscBranch, AcscNetwork, ISparseEqnComplex[], Complex[][][]>{
	
	/**
	 * default constructor
	 * 
	 * @param flag
	 */
	public SubNetwork012(int flag) {
		super(flag);
	}
	
	/**
	 * constructor
	 * 
	 * @param flag
	 * @param ids
	 */
	public SubNetwork012(int flag, String[] ids) {
		super(flag, ids);
	}
	
	@Override public AcscNetwork createSubNetwork() {
		return CoreObjectFactory.createAcscNetwork();
	};
	
	@Override public void buildSubNet(AcscNetwork parentNet) throws InterpssException {
		super.buildSubNet(parentNet);
	}
	
	/**
	 * form the Y-matrix for the SubNetwork
	 */
	public void formYMatrix() {
		this.setYSparseEqn(new ISparseEqnComplex[] { 
				this.subNet.formYMatrix(SequenceCode.POSITIVE, false),
				this.subNet.formYMatrix(SequenceCode.NEGATIVE, false),
				this.subNet.formYMatrix(SequenceCode.ZERO, false)});
	}
}	
