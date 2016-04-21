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

package org.interpss.piecewise;

import java.util.List;

/**
 * Class for SubArea processing. It begins by defining a set of cutting branches.
 * It finds SubAreas in the network and SubArea interface buses.
 * 
 * @author Mike
 *
 */
		
public interface SubAreaProcessor {
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
	List<SubArea> getSubAreaList();

	/**
	 * get SubArea by the area flag
	 * 
	 * @param flag the area flag
	 * @return the subarea object
	 */
	SubArea getSubArea(int flag);	
	
	/**
	 * Process SubArea
	 * 
	 */
	void processSubArea();	
}
