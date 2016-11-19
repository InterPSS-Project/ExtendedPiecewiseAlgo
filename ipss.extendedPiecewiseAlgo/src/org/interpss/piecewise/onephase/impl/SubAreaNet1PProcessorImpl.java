 /*
  * @(#)SubAreaNet1PProcessorImpl.java   
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
import org.interpss.piecewise.base.BaseSubArea;
import org.interpss.piecewise.base.impl.BaseSubAreaProcessorImpl;
import org.interpss.piecewise.onephase.SubArea1P;
import org.interpss.piecewise.onephase.SubNetwork1P;

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
		
public class SubAreaNet1PProcessorImpl<TSub extends BaseSubArea<?, ?>> extends BaseSubAreaProcessorImpl<AclfBus, AclfBranch, TSub, Complex> {
	/**
	 * Constructor
	 * 
	 * @param net AclfNetwork object
	 * @param subType SubArea/Network processing type
	 */
	public SubAreaNet1PProcessorImpl(AclfNetwork net, SubAreaNetType subType) {
		super(net, subType);
	}

	/**
	 * Constructor
	 * 
	 * @param net AclfNetwork object
	 * @param subType SubArea/Network processing type
	 * @param cuttingBranches cutting branch set
	 */
	public SubAreaNet1PProcessorImpl(AclfNetwork net, SubAreaNetType subType, BaseCuttingBranch<Complex>[] cuttingBranches) {
		super(net, subType, cuttingBranches);
	}	
	
	@SuppressWarnings("unchecked")
	@Override public TSub createSubAreaNet(int flag) {
		if (this.subType == SubAreaNetType.SubArea)
			return (TSub)new SubArea1P(flag);
		else
			return (TSub)new SubNetwork1P(flag);
	};
	
	@Override public List<TSub> processSubAreaNet() throws InterpssException {
		List<TSub> subNetList = super.processSubAreaNet();
		
		// for each SubNetwork, we build the child/parent relationship.
		for (TSub subNet : subNetList ) {
			if (subNet instanceof SubNetwork1P)
				((SubNetwork1P)subNet).buildSubNet((AclfNetwork)this.getNetwork());
		};
		
		return subNetList;
	}
}
