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

import org.interpss.piecewise.base.BaseCuttingBranch;
import org.interpss.piecewise.base.impl.BaseSubAreaProcessorImpl;
import org.interpss.piecewise.onephase.SubArea1Phase;

import com.interpss.core.net.Branch;
import com.interpss.core.net.Bus;
import com.interpss.core.net.Network;

/**
 * Class for single SubArea processing. It begins by defining a set of cutting branches.
 * It finds SubAreas in the network and SubArea interface buses.
 * 
 * @author Mike
 *
 */
		
public class SubArea1PhaseProcessorImpl<TBus extends Bus, TBra extends Branch, TState> extends BaseSubAreaProcessorImpl<TBus, TBra, SubArea1Phase, TState> {
	/**
	 * Constructor
	 * 
	 * @param net AclfNetwork object
	 */
	public SubArea1PhaseProcessorImpl(Network<TBus,TBra> net) {
		super(net);
	}

	/**
	 * Constructor
	 * 
	 * @param net AclfNetwork object
	 * @param cuttingBranches cutting branch set
	 */
	public SubArea1PhaseProcessorImpl(Network<TBus,TBra> net, BaseCuttingBranch<TState>[] cuttingBranches) {
		super(net, cuttingBranches);
	}	
	
	/**
	 * create a SubArea object
	 * 
	 * @param flag
	 * @return
	 */
	@Override public SubArea1Phase createSubArea(int flag) {
		return new SubArea1Phase(flag);
	};
}
