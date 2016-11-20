 /*
  * @(#)SubAreaNet012ProcessorImpl.java   
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
  * @Date 11/15/2016
  * 
  *   Revision History
  *   ================
  *
  */

package org.interpss.piecewise.seq012.impl;

import java.util.List;

import org.interpss.numeric.datatype.Complex3x1;
import org.interpss.piecewise.base.BaseCuttingBranch;
import org.interpss.piecewise.base.BaseSubArea;
import org.interpss.piecewise.base.impl.BaseSubAreaProcessorImpl;
import org.interpss.piecewise.seq012.SubArea012;
import org.interpss.piecewise.seq012.SubNetwork012;
import org.interpss.piecewise.seqPos.SubNetworkPos;

import com.interpss.common.exp.InterpssException;
import com.interpss.core.acsc.AcscBranch;
import com.interpss.core.acsc.AcscBus;
import com.interpss.core.acsc.AcscNetwork;

/**
 * Class for 012 SubNetwork processing. It begins by defining a set of cutting branches.
 * It finds SubAreas in the network and SubArea interface buses. Then it "moves" the bus and branch 
 * objects to corresponding SubNetwork.
 * 
 * @author Mike
 *
 */
		
public class SubAreaNet012ProcessorImpl<TSub extends BaseSubArea<?, ?>> extends BaseSubAreaProcessorImpl<AcscBus, AcscBranch, TSub, Complex3x1> {
	/**
	 * Constructor
	 * 
	 * @param net AclfNetwork object
	 * @param subType SubArea/Network processing type
	 */
	public SubAreaNet012ProcessorImpl(AcscNetwork net, SubAreaNetType subType) {
		super(net, subType);
	}

	/**
	 * Constructor
	 * 
	 * @param net AclfNetwork object
	 * @param subType SubArea/Network processing type
	 * @param cuttingBranches cutting branch set
	 */
	public SubAreaNet012ProcessorImpl(AcscNetwork net, SubAreaNetType subType, BaseCuttingBranch<Complex3x1>[] cuttingBranches) {
		super(net, subType, cuttingBranches);
	}	
	
	@SuppressWarnings("unchecked")
	@Override public TSub createSubAreaNet(int flag) {
		if (this.subType == SubAreaNetType.SubArea)
			return (TSub)new SubArea012(flag);
		else
			return (TSub)new SubNetwork012(flag);
	};
	
	@Override public List<TSub> processSubAreaNet() throws InterpssException {
		List<TSub> subNetList = super.processSubAreaNet();
		
		// for each SubNetwork, we build the child/parent relationship.
		for (TSub subNet : subNetList ) {
			if (subNet instanceof SubNetwork012)
				((SubNetwork012)subNet).buildSubNet((AcscNetwork)this.getNetwork());
		};
		
		return subNetList;
	}
}
