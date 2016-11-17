 /*
  * @(#)BaseSubNetwork.java   
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

import com.interpss.common.exp.InterpssException;
import com.interpss.core.net.Branch;
import com.interpss.core.net.Bus;
import com.interpss.core.net.Network;

/**
 * Class for modeling the SubNetwork concept 
 * 
 *  */
public abstract class BaseSubNetwork<TBus extends Bus, 
                            TBranch extends Branch, 
                            TNet extends Network<TBus, TBranch>, 
                            TYmatrix, TZMatrix> 
								extends BaseSubArea<TYmatrix, TZMatrix>{
	/** a network object representing the sub-area*/
	private TNet subNet;
	
	/**
	 * default constructor
	 * 
	 * @param flag
	 */
	public BaseSubNetwork(int flag) {
		super(flag);
	}
	
	/**
	 * constructor
	 * 
	 * @param flag
	 * @param ids
	 */
	public BaseSubNetwork(int flag, String[] ids) {
		super(flag, ids);
	}
	
	/**
	 * @return the subNet
	 */
	public TNet getSubNet() {
		return subNet;
	}
	
	/**
	 * After the sub-area info have been defined, build the sub-net object from
	 * the parent network object.
	 * 
	 * @param parentNet
	 */
	public void buildSubNet(TNet parentNet) throws InterpssException {
		System.out.println("Build SubNetwork for " + this.getFlag());
		
		TNet subnet = createSubNetwork();
		
		for (Bus bus : parentNet.getBusList()) {
			if (bus.getIntFlag() == this.getFlag()) {
				//subnet.addBus((TBus)bus);
			}
		};
		
		for (Branch branch : parentNet.getBranchList()) {
			if (branch.getFromBus().getIntFlag() == this.getFlag() && branch.getToBus().getIntFlag() == this.getFlag()) {
				//subnet.addBranch((TBranch)branch, branch.getFromBus().getId(), branch.getToBus().getId(), branch.getCircuitNumber());
			}
		}
	}
	
	/**
	 * create the sub-network object
	 * 
	 * @return
	 */
	public abstract TNet createSubNetwork();
}	
