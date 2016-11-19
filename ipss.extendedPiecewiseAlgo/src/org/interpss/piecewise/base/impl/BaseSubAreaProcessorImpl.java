 /*
  * @(#)BaseSubAreaProcessorImpl.java   
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

package org.interpss.piecewise.base.impl;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.interpss.piecewise.base.BaseCuttingBranch;
import org.interpss.piecewise.base.BaseSubArea;
import org.interpss.piecewise.base.SubAreaNetProcessor;

import com.interpss.common.exp.InterpssException;
import com.interpss.common.util.IpssLogger;
import com.interpss.core.net.Branch;
import com.interpss.core.net.Bus;
import com.interpss.core.net.Network;

/**
 * Base Class for SubArea processing. It begins by defining a set of cutting branches.
 * It finds SubAreas in the network and SubArea interface buses.
 * 
 * @author Mike
 *
 */
		
public abstract class BaseSubAreaProcessorImpl<
							TBus extends Bus, 
							TBra extends Branch, 
							TSub extends BaseSubArea<?, ?>, 
							TState> implements SubAreaNetProcessor<TBus, TBra, TSub, TState> {
	// SubArea or SubNetwork type
	protected SubAreaNetType subType;
	
	// Parent Network object
	private Network<TBus,TBra> net;
	
	// Cutting branch set
	private BaseCuttingBranch<TState>[] cuttingBranches;
	
	// Sub-area list 
	private List<TSub> subAreaNetList;
	
	/**
	 * Constructor
	 * 
	 * @param net AclfNetwork object
	 */
	public BaseSubAreaProcessorImpl(Network<TBus,TBra> net, SubAreaNetType subType) {
		this.net = net;
		this.subType = subType;
		this.subAreaNetList = new ArrayList<>();
	}

	/**
	 * Constructor
	 * 
	 * @param net AclfNetwork object
	 * @param cuttingBranches cutting branch set
	 */
	public BaseSubAreaProcessorImpl(Network<TBus,TBra> net, SubAreaNetType subType, BaseCuttingBranch<TState>[] cuttingBranches) {
		this(net, subType);
		this.cuttingBranches = cuttingBranches;
	}
	
	/**
	 * create a SubArea object
	 * 
	 * @param flag
	 * @return
	 */
	public abstract TSub createSubAreaNet(int flag);
	
	
	/**
	 * @return the net
	 */
	public Network<TBus, TBra> getNetwork() {
		return net;
	}

	/**
	 * return the cutting branch set
	 * 
	 * @return the cuttingBranches
	 */
	public BaseCuttingBranch<TState>[] getCuttingBranches() {
		return cuttingBranches;
	}

	/**
	 * set the cutting branch set
	 * 
	 * @param cuttingBranches the cuttingBranches to set
	 */
	public void setCuttingBranches(BaseCuttingBranch<TState>[] cuttingBranches) {
		this.cuttingBranches = cuttingBranches;
	}
	
	/**
	 * get the subarea list
	 * 
	 * @return the netVoltage
	 */
	public List<TSub> getSubAreaNetList() {
		return this.subAreaNetList;
	}

	/**
	 * get SubArea by the area flag
	 * 
	 * @param flag the area flag
	 * @return the subarea object
	 */
	public TSub getSubAreaNet(int flag) {
		for (TSub subarea: this.subAreaNetList) {
			if (subarea.getFlag() == flag)
				return subarea;
		}
		return null;
	}	

	/**
	 * Initialization for SubArea processing 
	 * 
	 */
	private void initialization() {
		// init bus IntFlag
		net.getBusList().forEach(bus -> { bus.setIntFlag(BaseCuttingBranch.DefaultFlag);});
		
		// init cutting branch set
  		int flag = 0;
  		for (BaseCuttingBranch<TState> cbra : cuttingBranches) {
  			Branch branch = net.getBranch(cbra.getBranchId());
  			branch.setStatus(false);
  			
  			if (branch.getFromBus().getIntFlag() == BaseCuttingBranch.DefaultFlag) {
  				cbra.setFromSubAreaFlag(++flag);
  				branch.getFromBus().setIntFlag(cbra.getFromSubAreaFlag());
  				//System.out.println("Bus " + branch.getFromBus().getId() + " assigned Bus Flag: " + flag);
  			}

  			if (branch.getToBus().getIntFlag() == BaseCuttingBranch.DefaultFlag) {
  				cbra.setToSubAreaFlag(++flag);
  				branch.getToBus().setIntFlag(cbra.getToSubAreaFlag());
  				//System.out.println("Bus " + branch.getToBus().getId() + " assigned Bus Flag: " + flag);
  			}
  		}		
	}
	
	/**
	 * Process SubArea
	 * 
	 */
	public List<TSub> processSubAreaNet() throws InterpssException {
		initialization();
		
		Hashtable<String, BusPair> busPairSet = new Hashtable<>();
			// There are two types of BusPair record in the busPairSet Hashtable
			// Type1 ["10",  "9",  -1] - indicating Bus "10" and Bus "9" are in the same SubArea 
			// Type2 ["61",  "61",  5] - the interface Bus current SubArea flag is 5, which needs to be 
			//                           consolidated to the smallest Bus.IntFlag in the SubAre
		
  		for (BaseCuttingBranch<TState> cbra : cuttingBranches) {
  			Branch branch = net.getBranch(cbra.getBranchId());

  			// starting from the fromBus, recursively set the branch opposite 
  			// bus IntFlag for SubArea processing 
  			BusPair pair = new BusPair(branch.getFromBus());
  			if (busPairSet.get(pair.getKey()) == null) {
					busPairSet.put(pair.getKey(), pair);
				}
  			setConnectedBusFlag(branch.getFromBus(), busPairSet);
  			
  			// starting from the toBus, recursively set the branch opposite 
  			// bus IntFlag for SubArea processing 
  			pair = new BusPair(branch.getToBus());
  			if (busPairSet.get(pair.getKey()) == null) {
					busPairSet.put(pair.getKey(), pair);
				}
  			setConnectedBusFlag(branch.getToBus(), busPairSet);
  		}	
		//System.out.println(this.busPairSet);
		
  		// consolidate bus.IntFlag and create SubArea flag
		busPairSet.forEach((key, pair) -> {
			// bus1 and bus2 are in the same SubArea and make sure only process Type1 record 
			if (!pair.bus1.getId().equals(pair.bus2.getId())) {
				// get Type2 records corresponding to bus1 and bus2
				BusPair p1 = busPairSet.get(BusPair.createKey(pair.bus1.getIntFlag()));
				BusPair p2 = busPairSet.get(BusPair.createKey(pair.bus2.getIntFlag()));
				
				// SubArea Flag is the smallest Bus.IntFlag in a SubArea. Bus.IntFlag has been initialized at
				// the interface bus in the initialization() method
				if (p1.subAreaFlag > p2.subAreaFlag) {
					// recursively set p1.subAreaFlag to p2.subAreaFlag
					setSubAreaFlag(p1, p2.subAreaFlag, busPairSet);
				}
				else if (p1.subAreaFlag < p2.subAreaFlag) {
					// recursively set p2.subAreaFlag to p1.subAreaFlag
					setSubAreaFlag(p2, p1.subAreaFlag, busPairSet);
				}
			}
		});
		//System.out.println(this.busPairSet);
		
		// create SubArea list and the subArea.interfaceBusIdList
		busPairSet.forEach((key, pair) -> {
			// make sure only Type2 records are processed
			if (pair.bus1.getIntFlag() == pair.bus2.getIntFlag()) {
				//System.out.println(pair);
				TSub subarea = getSubAreaNet(pair.subAreaFlag);
				if (subarea == null) {
					// create SubArea object and add to the SubArea list
					subarea = createSubAreaNet(pair.subAreaFlag);
					getSubAreaNetList().add(subarea);
				}
				// add the interface bus ID to the list
				subarea.getInterfaceBusIdList().add(pair.bus1.getId());
			}
		});		
		
		// update network bus SubArea flag
		net.getBusList().forEach(bus -> {
			BusPair p = busPairSet.get(BusPair.createKey(bus.getIntFlag()));
			bus.setIntFlag(p.subAreaFlag);
		});
		
		// consolidate the subarea number
		//  At this point the subarea number might not be continuous, for example, 1, 2, 7 ...
		//  We make it continuous 1, 2, 3, ...
		Hashtable<Integer, Integer> lookup = new Hashtable<>();
		Integer cnt = 0;
		for (BaseSubArea<?, ?> subarea : this.subAreaNetList) {
			lookup.put(subarea.getFlag(), ++cnt);
		};
		//System.out.println(lookup);

		// update the subarea flag
		this.subAreaNetList.forEach(subarea -> {
			subarea.setFlag(lookup.get(subarea.getFlag()));
		});
		
		// update network bus SubArea flag
		net.getBusList().forEach(bus -> {
			BusPair p = busPairSet.get(BusPair.createKey(bus.getIntFlag()));
			bus.setIntFlag(lookup.get(p.subAreaFlag));
		});
		
		// update cutting branch from/toBus subarea flag
		for (int i = 0; i < this.cuttingBranches.length; i++) {
			BaseCuttingBranch<TState> branch = this.cuttingBranches[i];
			Branch aclfBranch = net.getBranch(branch.getBranchId());
			branch.setFromSubAreaFlag(aclfBranch.getFromBus().getIntFlag());
			branch.setToSubAreaFlag(aclfBranch.getToBus().getIntFlag());
		}
		
		return this.subAreaNetList;
	}
	
	/**
	 * Recursively set Bus SubArea flag
	 * 
	 * @param startPair the starting Bus (Type2 record)
	 * @param flag SubArea flag
	 */
	private void setSubAreaFlag(BusPair startPair, int flag, Hashtable<String, BusPair> busPairSet) {
		// 5_5=["61",  "61",  5], 6_6=["9",  "9",  5], 2_2=["71",  "71",  2]
		// if "71" and "9" are connected, when set 6_6 flag to 2, 5_5 flag should be set also (recursively)
		if (startPair.bus1.getIntFlag() != startPair.subAreaFlag) {
			BusPair nextPair = busPairSet.get(BusPair.createKey(startPair.subAreaFlag));
			setSubAreaFlag(nextPair, flag, busPairSet);
		}
		startPair.subAreaFlag = flag;
	}

	/**
	 * recursive function, recursively visit the opposite bus of the bus connected branch
	 * to define SubArea in the network 
	 * 
	 * @param bus
	 */
	private void setConnectedBusFlag(Bus bus, Hashtable<String, BusPair> busPairSet) {
		bus.getBranchList().forEach(branch -> {
  			try {
  				if (branch.isActive()) {
  					Bus optBus = branch.getOppositeBus(bus);
  					if (optBus.getIntFlag() == BaseCuttingBranch.DefaultFlag) {
  						optBus.setIntFlag(bus.getIntFlag());
  						setConnectedBusFlag(optBus, busPairSet);
  					}
  					else {
  						// the optBus has been already visited with a non-DefaultFlag
  						//System.out.println("Bus " + optBus.getId() + " marked");
  						if (bus.getIntFlag() != optBus.getIntFlag()) {
  							// make sure that the bus and optBus are visited starting from different 
  							// interface bus
  							// store the bus and optBus pair to indicate that they belong to the same SubArea
  							String key = BusPair.createKey(bus.getIntFlag(), optBus.getIntFlag());
  							if (busPairSet.get(key) == null) {
  	  							busPairSet.put(key, new BusPair(bus, optBus));
  								//System.out.println(x + " belong to the same SubArea" );
  							}
  						}
  					}
  				}
			} catch (InterpssException e) {
				IpssLogger.logErr(e);
			}
  		});
	}	
	
	/**
	 * Class holding a pair of buses for SubArea processing. The bus pair are stored in
	 * such a way that bus1.IntFlag <= bus2.InfFlag. 
	 */
	private static class BusPair {
		// bus pair
		public Bus bus1, bus2;
		// store the smallest Bus.IntFlag in a SubArea
		public int subAreaFlag = BaseCuttingBranch.DefaultFlag;
		
		public BusPair(Bus bus) {
			this.bus1 = bus;
			this.bus2 = bus;
			this.subAreaFlag = bus.getIntFlag();
		}
		
		/**
		 * constructor
		 * 
		 * @param bus1
		 * @param bus2
		 */
		public BusPair(Bus bus1, Bus bus2) {
			// make sure that bus1.IntFlag <= bus2.InfFlag.
			if (bus1.getIntFlag() < bus2.getIntFlag()) {
				this.bus1 = bus1;
				this.bus2 = bus2;
			}
			else {
				this.bus1 = bus2;
				this.bus2 = bus1;
			}
		}
		
		/**
		 * get the bus pair key
		 * 
		 * @return the key
		 */
		public String getKey() {
			return createKey(bus1.getIntFlag(), bus2.getIntFlag());
		};
		
		/**
		 * static function to create bus pair key based on two flags
		 * 
		 * @param flag1
		 * @param flag2
		 * @return the key
		 */
		public static String createKey(int flag1, int flag2) {
			return flag1 + "_" + flag2;
		};
		
		/**
		 * static function to create bus pair key based on a flag
		 * 
		 * @param flag1
		 * @return the key
		 */
		public static String createKey(int flag1) {
			return flag1 + "_" + flag1;
		};
		
		public String toString() {
			return "[" + this.bus1.getIntFlag() + " \"" + this.bus1.getId() + "\",  "
					+ this.bus2.getIntFlag()  + " \""  + this.bus2.getId() + "\",  "
					+ this.subAreaFlag + "]";
		}
	}
}
