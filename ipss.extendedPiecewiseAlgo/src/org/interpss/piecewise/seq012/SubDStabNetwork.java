 /* @(#)SubDStabNetwork.java   
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

package org.interpss.piecewise.seq012;

import com.interpss.DStabObjectFactory;
import com.interpss.common.exp.InterpssException;
import com.interpss.dstab.DStabBranch;
import com.interpss.dstab.DStabBus;
import com.interpss.dstab.DStabGen;
import com.interpss.dstab.DStabLoad;
import com.interpss.dstab.DStabilityNetwork;

/**
 * Class for modeling the SubNetwork concept for representing a sub-network of type DStabilityNetwork. 
 */
public class SubDStabNetwork extends SubNetwork012<DStabBus<DStabGen,DStabLoad>, DStabBranch, DStabilityNetwork>{
	
	/**
	 * default constructor
	 * 
	 * @param flag
	 */
	public SubDStabNetwork(int flag) {
		super(flag);
	}
	
	/**
	 * constructor
	 * 
	 * @param flag
	 * @param ids
	 */
	public SubDStabNetwork(int flag, String[] ids) {
		super(flag, ids);
	}
	
	@Override public DStabilityNetwork createSubNetwork() {
		return DStabObjectFactory.createDStabilityNetwork();
	};
	
	@Override public void buildSubNet(DStabilityNetwork parentNet) throws InterpssException {
		super.buildSubNet(parentNet);
		
		// TODO only bus and branch objects are copied to the SubNetwork in the super class. We
		//      may need to add missing objects
	}
}	
