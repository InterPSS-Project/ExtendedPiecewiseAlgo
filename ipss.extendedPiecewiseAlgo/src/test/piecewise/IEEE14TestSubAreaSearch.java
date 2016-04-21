 /*
  * @(#)IEEE14TestSubAreaSearch.java   
  *
  * Copyright (C) 2006 www.interpss.org
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
  * @Date 09/15/2006
  * 
  *   Revision History
  *   ================
  *
  */

package test.piecewise;

import static org.junit.Assert.assertTrue;

import org.interpss.CorePluginObjFactory;
import org.interpss.fadapter.IpssFileAdapter;
import org.interpss.piecewise.CuttingBranch;
import org.interpss.piecewise.SubAreaProcessor;
import org.interpss.piecewise.impl.SubAreaProcessorImpl;
import org.junit.Test;

import com.interpss.core.aclf.AclfBranch;
import com.interpss.core.aclf.AclfNetwork;


public class IEEE14TestSubAreaSearch extends PiecewiseAlgoTestSetup {
	private static final int DefaultFlag = -1;
	
	@Test
	public void testCase1() throws Exception {
		AclfNetwork net = getTestNet();
		
		
		SubAreaProcessor proc = new SubAreaProcessorImpl(net, new CuttingBranch[] { 
					new CuttingBranch("4->71(1)"),
					new CuttingBranch("4->91(1)"),
					new CuttingBranch("5->61(1)")});	
  		
  		proc.processSubArea();
  		
  		proc.getSubAreaList().forEach(subarea -> {
  			//System.out.println(subarea);
  		});
  		
  		assertTrue(proc.getSubAreaList().size() == 2);
  		assertTrue(proc.getSubArea(1).interfaceBusIdList.size() == 2);
  		assertTrue(proc.getSubArea(2).interfaceBusIdList.size() == 3);
  		
  		net.getBusList().forEach(bus -> {
  			assertTrue(bus.getIntFlag() != DefaultFlag);
  			//System.out.println(bus.getId() + "," + bus.getIntFlag());
  			if (bus.getId().equals("2")) assertTrue(bus.getIntFlag() == 1);
  			if (bus.getId().equals("13")) assertTrue(bus.getIntFlag() == 2);
  		});
	}

	@Test
	public void testCase2() throws Exception {
		AclfNetwork net = getTestNet();
		
		SubAreaProcessor proc = new SubAreaProcessorImpl(net, new CuttingBranch[] { 
					new CuttingBranch("4->71(1)"),
					new CuttingBranch("4->91(1)"),
					new CuttingBranch("5->61(1)"),
					new CuttingBranch("9->14(1)"),
					new CuttingBranch("14->13(1)")});	
  		for (int i = 0; i < proc.getCuttingBranches().length; i++) {
  			AclfBranch branch = net.getBranch(proc.getCuttingBranches()[i].branchId);
  			assertTrue(proc.getCuttingBranches()[i].branchId + " not found!", branch != null);
  		};
  		
  		proc.processSubArea();
  		
  		proc.getSubAreaList().forEach(subarea -> {
  			//System.out.println(subarea);
  		});
  		
  		assertTrue(proc.getSubAreaList().size() == 3);
  		assertTrue(proc.getSubArea(1).interfaceBusIdList.size() == 2);
  		assertTrue(proc.getSubArea(2).interfaceBusIdList.size() == 5);
  		assertTrue(proc.getSubArea(7).interfaceBusIdList.size() == 1);  		
  		
  		net.getBusList().forEach(bus -> {
  			assertTrue(bus.getIntFlag() != DefaultFlag);
  			//System.out.println(bus.getId() + "," + bus.getIntFlag());
  			if (bus.getId().equals("2")) assertTrue(bus.getIntFlag() == 1);
  			if (bus.getId().equals("61")) assertTrue(bus.getIntFlag() == 2);
  			if (bus.getId().equals("14")) assertTrue(bus.getIntFlag() == 7);  			
  		});
	}
	
	private AclfNetwork getTestNet() throws Exception {
		/*
		 * Load the network and run Loadflow
		 */
		AclfNetwork net = CorePluginObjFactory
					.getFileAdapter(IpssFileAdapter.FileFormat.IpssInternal)
					.load("testdata/ieee14.ipssdat")
					.getAclfNet();	
		
  		//System.out.println(net.net2String());
  		assertTrue((net.getBusList().size() == 17 && net.getBranchList().size() == 23));

  		return net;
	}
}