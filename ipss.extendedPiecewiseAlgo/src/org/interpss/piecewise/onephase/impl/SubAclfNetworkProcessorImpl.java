 /*
  * @(#)SubAreaProcessorImpl.java   
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
import org.interpss.piecewise.net.impl.BaseSubAreaProcessorImpl;
import org.interpss.piecewise.onephase.CuttingBranch;
import org.interpss.piecewise.onephase.SubAclfNetwork;

import com.interpss.common.exp.InterpssException;
import com.interpss.core.aclf.AclfBranch;
import com.interpss.core.aclf.AclfBus;
import com.interpss.core.aclf.AclfNetwork;

/**
 * Class for SubArea processing. It begins by defining a set of cutting branches.
 * It finds SubAreas in the network and SubArea interface buses.
 * 
 * @author Mike
 *
 */
		
public class SubAclfNetworkProcessorImpl extends BaseSubAreaProcessorImpl<AclfBus, AclfBranch, SubAclfNetwork, Complex> {
	/**
	 * Constructor
	 * 
	 * @param net AclfNetwork object
	 */
	public SubAclfNetworkProcessorImpl(AclfNetwork net) {
		super(net);
	}

	/**
	 * Constructor
	 * 
	 * @param net AclfNetwork object
	 * @param cuttingBranches cutting branch set
	 */
	public SubAclfNetworkProcessorImpl(AclfNetwork net, CuttingBranch[] cuttingBranches) {
		super(net, cuttingBranches);
	}	
	
	/**
	 * create a SubArea object
	 * 
	 * @param flag
	 * @return
	 */
	@Override public SubAclfNetwork createSubArea(int flag) {
		return new SubAclfNetwork(flag);
	};
	
	@Override public List<SubAclfNetwork> processSubAreaNet() throws InterpssException {
		List<SubAclfNetwork> subNetList = super.processSubAreaNet();
		
		for (SubAclfNetwork subNet : subNetList ) {
			subNet.buildSubNet((AclfNetwork)this.getNetwork());
		};
		
		return subNetList;
	}
}
