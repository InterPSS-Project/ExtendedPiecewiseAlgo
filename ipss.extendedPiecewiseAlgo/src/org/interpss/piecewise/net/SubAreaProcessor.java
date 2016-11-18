 /*
  * @(#)SubAreaProcessor.java   
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

package org.interpss.piecewise.net;

import java.util.List;

import com.interpss.common.exp.InterpssException;
import com.interpss.core.net.Branch;
import com.interpss.core.net.Bus;

/**
 * Class for SubArea processing. It begins by defining a set of cutting branches.
 * It finds SubAreas in the network and SubArea interface buses.
 * 
 * @author Mike
 *
 */
		
public interface SubAreaProcessor <TBus extends Bus, TBra extends Branch, TSub extends BaseSubArea<?, ?>> {
	/**
	 * return the cutting branch set
	 * 
	 * @return the cuttingBranches
	 */
	CuttingBranch[] getCuttingBranches();

	/**
	 * set the cutting branch set
	 * 
	 * @param cuttingBranches the cuttingBranches to set
	 */
	void setCuttingBranches(CuttingBranch[] cuttingBranches);
	
	/**
	 * get the subarea list
	 * 
	 * @return the netVoltage
	 */
	List<TSub> getSubAreaList();

	/**
	 * get SubArea by the area flag
	 * 
	 * @param flag the area flag
	 * @return the subarea object
	 */
	TSub getSubArea(int flag);	
	
	/**
	 * Process SubArea by automatically group buses into subareas based
	 * on the defined cutting branches set. The subarea info is stored at
	 *   (1) SubArea.flag field
	 *   (2) Bus.infFlag field
	 *   (3) CuttingBranch.fromSubAreaFlag/toSubAreaFlag fields
	 * @return the subarea list
	 */
	List<TSub> processSubArea() throws InterpssException;	
}
