 /*
  * @(#)SubAreaNetProcessor.java   
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

package org.interpss.piecewise.base;

import java.util.List;

import com.interpss.common.exp.InterpssException;
import com.interpss.core.net.Branch;
import com.interpss.core.net.Bus;

/**
 * Interface for SubArea or SubNetwork processing. It begins by defining a set of cutting branches.
 * It finds SubAreas in the network and SubArea interface buses. If it is SubNetwork processing, it
 * builds SubNetworks based on the SubArea info. Before the SubNetwork processing, all buses/branches in SubArea
 * are contained by the parent network (Bus/Branch.network = Parent Network). After the SubNetwork processing,
 * buses/branches in SubNetwork are contained by the SubNetwork (Bus/Branch.network = SubNetwork), although the
 * parent network still holds reference to all the buses and branches. 
 * 
 * @author Mike
 *
 */
		
public interface SubAreaNetProcessor <TBus extends Bus, TBra extends Branch, TSub extends BaseSubArea<?, ?>, TState> {
	/**
	 * return the cutting branch set
	 * 
	 * @return the cuttingBranches
	 */
	BaseCuttingBranch<TState>[] getCuttingBranches();

	/**
	 * set the cutting branch set
	 * 
	 * @param cuttingBranches the cuttingBranches to set
	 */
	void setCuttingBranches(BaseCuttingBranch<TState>[] cuttingBranches);
	
	/**
	 * get the SubArea or SubNetwork list
	 * 
	 * @return the netVoltage
	 */
	List<TSub> getSubAreaNetList();

	/**
	 * get SubArea/Network by the area flag
	 * 
	 * @param flag the area flag
	 * @return the SubArea/Net object
	 */
	TSub getSubAreaNet(int flag);	
	
	/**
	 * Process SubArea by automatically group buses into SubAreas based
	 * on the defined cutting branches set. If it is SubNetwork processing,
	 * SubNetwork relationship will be created
	 *  
	 * @return the SubArea/Net list
	 */
	List<TSub> processSubAreaNet() throws InterpssException;	
}
