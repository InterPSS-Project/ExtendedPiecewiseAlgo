 /*
  * @(#)SubNet1PhaseProcessorImpl.java   
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

package org.interpss.piecewise.onephase.impl;

import java.util.List;

import org.apache.commons.math3.complex.Complex;
import org.interpss.piecewise.base.BaseCuttingBranch;
import org.interpss.piecewise.base.impl.BaseSubAreaProcessorImpl;
import org.interpss.piecewise.onephase.CuttingBranch1Phase;
import org.interpss.piecewise.onephase.SubNetwork1Phase;

import com.interpss.common.exp.InterpssException;
import com.interpss.core.aclf.AclfBranch;
import com.interpss.core.aclf.AclfBus;
import com.interpss.core.aclf.AclfNetwork;

/**
 * Class for single phase SubNetwork processing. It begins by defining a set of cutting branches.
 * It finds SubAreas in the network and SubArea interface buses. Then it "moves" the bus and branch 
 * objects to corresponding SubNetwork.
 * 
 * @author Mike
 *
 */
		
public class SubNet1PhaseProcessorImpl extends BaseSubAreaProcessorImpl<AclfBus, AclfBranch, SubNetwork1Phase, Complex> {
	/**
	 * Constructor
	 * 
	 * @param net AclfNetwork object
	 */
	public SubNet1PhaseProcessorImpl(AclfNetwork net) {
		super(net);
	}

	/**
	 * Constructor
	 * 
	 * @param net AclfNetwork object
	 * @param cuttingBranches cutting branch set
	 */
	public SubNet1PhaseProcessorImpl(AclfNetwork net, BaseCuttingBranch<Complex>[] cuttingBranches) {
		super(net, cuttingBranches);
	}	
	
	/**
	 * create a SubArea object
	 * 
	 * @param flag
	 * @return
	 */
	@Override public SubNetwork1Phase createSubArea(int flag) {
		return new SubNetwork1Phase(flag);
	};
	
	@Override public List<SubNetwork1Phase> processSubAreaNet() throws InterpssException {
		List<SubNetwork1Phase> subNetList = super.processSubAreaNet();
		
		for (SubNetwork1Phase subNet : subNetList ) {
			subNet.buildSubNet((AclfNetwork)this.getNetwork());
		};
		
		return subNetList;
	}
}
