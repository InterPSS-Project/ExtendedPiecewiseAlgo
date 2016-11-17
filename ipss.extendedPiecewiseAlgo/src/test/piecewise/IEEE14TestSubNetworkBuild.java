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

import org.interpss.piecewise.CuttingBranch;
import org.interpss.piecewise.SubArea;
import org.interpss.piecewise.SubAreaProcessor;
import org.interpss.piecewise.SubNetwork;
import org.interpss.piecewise.impl.SubAreaProcessorImpl;
import org.interpss.piecewise.impl.SubNetworkProcessorImpl;
import org.junit.Test;

import com.interpss.core.aclf.AclfBranch;
import com.interpss.core.aclf.AclfBus;
import com.interpss.core.aclf.AclfNetwork;

/*
 * This test case is for testing sub-area search algorithm implementation.
 */
public class IEEE14TestSubNetworkBuild extends PiecewiseAlgoTestSetup {
	private static final int DefaultFlag = -1;
	
	@Test
	public void testCase1() throws Exception {
		AclfNetwork net = IEEE14TestSubAreaSearch.getTestNet();
		
		
		SubAreaProcessor<AclfBus, AclfBranch, SubNetwork>proc = new SubNetworkProcessorImpl<>(net, new CuttingBranch[] { 
					new CuttingBranch("4->71(1)"),
					new CuttingBranch("4->91(1)"),
					new CuttingBranch("5->61(1)")});	
  		
  		proc.processSubArea();
  		
  		
  	}

	@Test
	public void testCase2() throws Exception {
		AclfNetwork net = IEEE14TestSubAreaSearch.getTestNet();
		
		SubAreaProcessor<AclfBus, AclfBranch, SubNetwork> proc = new SubNetworkProcessorImpl<>(net, new CuttingBranch[] { 
					new CuttingBranch("4->71(1)"),
					new CuttingBranch("4->91(1)"),
					new CuttingBranch("5->61(1)"),
					new CuttingBranch("9->14(1)"),
					new CuttingBranch("14->13(1)")});	
  		
  		proc.processSubArea();
  		
	}
}
