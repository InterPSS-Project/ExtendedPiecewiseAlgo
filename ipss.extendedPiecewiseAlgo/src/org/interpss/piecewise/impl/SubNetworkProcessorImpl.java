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

package org.interpss.piecewise.impl;

import java.util.List;

import org.interpss.piecewise.CuttingBranch;
import org.interpss.piecewise.SubNetwork;

import com.interpss.common.exp.InterpssException;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.net.Branch;
import com.interpss.core.net.Bus;
import com.interpss.core.net.Network;

/**
 * Class for SubArea processing. It begins by defining a set of cutting branches.
 * It finds SubAreas in the network and SubArea interface buses.
 * 
 * @author Mike
 *
 */
		
public class SubNetworkProcessorImpl<TBus extends Bus, TBra extends Branch> extends AbstractSubAreaProcessorImpl<TBus, TBra, SubNetwork> {
	/**
	 * Constructor
	 * 
	 * @param net AclfNetwork object
	 */
	public SubNetworkProcessorImpl(Network<TBus,TBra> net) {
		super(net);
	}

	/**
	 * Constructor
	 * 
	 * @param net AclfNetwork object
	 * @param cuttingBranches cutting branch set
	 */
	public SubNetworkProcessorImpl(Network<TBus,TBra> net, CuttingBranch[] cuttingBranches) {
		super(net, cuttingBranches);
	}	
	
	/**
	 * create a SubArea object
	 * 
	 * @param flag
	 * @return
	 */
	@Override public SubNetwork createSubArea(int flag) {
		return new SubNetwork(flag);
	};
	
	@Override public List<SubNetwork> processSubArea() throws InterpssException {
		List<SubNetwork> subNetList = super.processSubArea();
		
		for (SubNetwork subNet : subNetList ) {
			subNet.buildSubNet((AclfNetwork)this.getNetwork());
		};
		
		return subNetList;
	}
}
