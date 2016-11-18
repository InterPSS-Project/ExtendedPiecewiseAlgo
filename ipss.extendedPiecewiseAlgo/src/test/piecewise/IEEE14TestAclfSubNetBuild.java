 /*
  * @(#)IEEE14TestSubNetworkBuild.java   
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
  * @Date 04/15/2016
  * 
  *   Revision History
  *   ================
  *
  */

package test.piecewise;

import static org.junit.Assert.assertTrue;

import org.apache.commons.math3.complex.Complex;
import org.interpss.piecewise.base.SubAreaNetProcessor;
import org.interpss.piecewise.onephase.CuttingBranch1Phase;
import org.interpss.piecewise.onephase.SubNetwork1Phase;
import org.interpss.piecewise.onephase.impl.SubNet1PhaseProcessorImpl;
import org.junit.Test;

import com.interpss.core.aclf.AclfBranch;
import com.interpss.core.aclf.AclfBus;
import com.interpss.core.aclf.AclfNetwork;

/*
 * This test case is for testing sub-area search algorithm implementation.
 */
public class IEEE14TestAclfSubNetBuild extends PiecewiseAlgoTestSetup {
	@Test
	public void testCase1() throws Exception {
		AclfNetwork net = IEEE14TestSubAreaSearch.getTestNet();
		
		
		SubAreaNetProcessor<AclfBus, AclfBranch, SubNetwork1Phase, Complex> proc = new SubNet1PhaseProcessorImpl(net, new CuttingBranch1Phase[] { 
					new CuttingBranch1Phase("4->71(1)"),
					new CuttingBranch1Phase("4->91(1)"),
					new CuttingBranch1Phase("5->61(1)")});	
  		
  		proc.processSubAreaNet();
  		
  		assertTrue("We should have two sub-network objects", proc.getSubAreaNetList().size() == 2);
  		//System.out.println(proc.getSubAreaList().toString());
  		
  		//System.out.println("Bus-1 subarea flag: " + net.getBus("1").getIntFlag());
  		//System.out.println("Bus-14 subarea flag: " + net.getBus("14").getIntFlag());
  		assertTrue(net.getBus("1").getIntFlag() == 1);
  		assertTrue(net.getBus("14").getIntFlag() == 2);

  		assertTrue("SubArea 1 should have 5 buses", proc.getSubAreaNet(1).getSubNet().getBusList().size() == 5);
	
  		assertTrue("SubArea 2 should have 12 buses", proc.getSubAreaNet(2).getSubNet().getBusList().size() == 12);
	}

	@Test
	public void testCase2() throws Exception {
		AclfNetwork net = IEEE14TestSubAreaSearch.getTestNet();
		
		SubAreaNetProcessor<AclfBus, AclfBranch, SubNetwork1Phase, Complex> proc = new SubNet1PhaseProcessorImpl(net, new CuttingBranch1Phase[] { 
					new CuttingBranch1Phase("4->71(1)"),
					new CuttingBranch1Phase("4->91(1)"),
					new CuttingBranch1Phase("5->61(1)"),
					new CuttingBranch1Phase("9->14(1)"),
					new CuttingBranch1Phase("14->13(1)")});	
  		
  		proc.processSubAreaNet();
  		
  		// we should have three sub-network objects
  		assertTrue("we should have three sub-network objects", proc.getSubAreaNetList().size() == 3);

  		//System.out.println("Bus-1 subarea flag: " + net.getBus("1").getIntFlag());
  		//System.out.println("Bus-14 subarea flag: " + net.getBus("14").getIntFlag());
  		assertTrue(net.getBus("1").getIntFlag() == 1);
  		assertTrue(net.getBus("9").getIntFlag() == 2);
  		assertTrue(net.getBus("14").getIntFlag() == 3);

  		assertTrue("SubArea 1 should have 5 buses", proc.getSubAreaNet(1).getSubNet().getBusList().size() == 5);
  		
  		assertTrue("SubArea 2 should have 11 buses", proc.getSubAreaNet(2).getSubNet().getBusList().size() == 11);
  		
  		assertTrue("SubArea 3 should have 1 buses", proc.getSubAreaNet(3).getSubNet().getBusList().size() == 1);
	}
}
