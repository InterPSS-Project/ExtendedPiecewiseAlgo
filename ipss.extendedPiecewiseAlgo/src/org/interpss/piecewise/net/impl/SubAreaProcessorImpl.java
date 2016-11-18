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

package org.interpss.piecewise.net.impl;

import org.interpss.piecewise.aclf.SubArea;
import org.interpss.piecewise.net.BaseCuttingBranch;

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
		
public class SubAreaProcessorImpl<TBus extends Bus, TBra extends Branch, TState> extends BaseSubAreaProcessorImpl<TBus, TBra, SubArea, TState> {
	/**
	 * Constructor
	 * 
	 * @param net AclfNetwork object
	 */
	public SubAreaProcessorImpl(Network<TBus,TBra> net) {
		super(net);
	}

	/**
	 * Constructor
	 * 
	 * @param net AclfNetwork object
	 * @param cuttingBranches cutting branch set
	 */
	public SubAreaProcessorImpl(Network<TBus,TBra> net, BaseCuttingBranch<TState>[] cuttingBranches) {
		super(net, cuttingBranches);
	}	
	
	/**
	 * create a SubArea object
	 * 
	 * @param flag
	 * @return
	 */
	@Override public SubArea createSubArea(int flag) {
		return new SubArea(flag);
	};
}
