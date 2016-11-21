 /*
  * @(#)Acsc5BusTesPiecewiseAlgo.java   
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

import java.util.function.Function;

import org.apache.commons.math3.complex.Complex;
import org.interpss.IpssCorePlugin;
import org.interpss.numeric.datatype.Complex3x1;
import org.interpss.numeric.sparse.ISparseEqnComplex;
import org.interpss.piecewise.PiecewiseAlgorithm;
import org.interpss.piecewise.SubAreaNetProcessor;
import org.interpss.piecewise.seq012.CuttingBranch012;
import org.interpss.piecewise.seq012.SubArea012;
import org.interpss.piecewise.seq012.algo.PiecewiseAlgo012Impl;
import org.interpss.piecewise.seq012.impl.SubArea012ProcessorImpl;
import org.interpss.pssl.util.AcscSample;
import org.junit.Test;

import com.interpss.CoreObjectFactory;
import com.interpss.core.acsc.AcscBranch;
import com.interpss.core.acsc.AcscBus;
import com.interpss.core.acsc.AcscNetwork;
import com.interpss.core.acsc.SequenceCode;
import com.interpss.core.algo.LoadflowAlgorithm;

public class Acsc5BusTesPiecewiseAlgo {
	/*
	 * Function to compute bus injection current for the testing purpose
	 */
	Function<AcscBus, Complex3x1> injCurFunc = bus -> {
		if (bus.getId().equals("2")) {  // Bus '2'
			return new Complex3x1(new Complex(0.2,0.0),  new Complex(0.05,0.0),  new Complex(0.0001,0.0));
		}
		else if (bus.getId().equals("3")) {  // Bus '3'
			return new Complex3x1(new Complex(-0.2,0.0),  new Complex(-0.05,0.0),  new Complex(-0.0001,0.0));
		}
		else 
			return new Complex3x1();
	};
	
	@Test
	public void subAreaTest() throws Exception {
		IpssCorePlugin.init();
		
  		AcscNetwork net = getAcscNet();  	
  		
		SubAreaNetProcessor<AcscBus, AcscBranch, SubArea012, Complex3x1> proc = 
				new SubArea012ProcessorImpl<SubArea012>(net, new CuttingBranch012[] { 
						new CuttingBranch012("1->2(1)"),
						new CuttingBranch012("1->3(1)"),
						new CuttingBranch012("2->3(1)")});	
  		
  		proc.processSubAreaNet(); 		
  		
  		/*
  		 * Solve [Y][I] = [V] using the piecewise method
  		 * =============================================
  		 */
  		PiecewiseAlgorithm<AcscBus, Complex3x1, SubArea012> pieceWiseAlgo = new PiecewiseAlgo012Impl<>(net, proc.getSubAreaNetList());
  		
  		/*//////////////////////////////////
  		 * Step-1: Solve for the open-circuit voltage
  		 *//////////////////////////////////
  		
  		pieceWiseAlgo.calculateOpenCircuitVoltage(this.injCurFunc);
  		//System.out.println("\n" + netVoltage.toString());
  		
  		/*/////////////////////////////
  		 * Step-2: calculate cutting branch current
  		 */////////////////////////////
 
    	pieceWiseAlgo.calculateCuttingBranchCurrent(proc.getCuttingBranches());

		
  		/*//////////////////////////////////////////
  		 * Step-3
  		 *//////////////////////////////////////////
		
		pieceWiseAlgo.calcuateSubAreaNetVoltage(proc.getCuttingBranches());  		
  		System.out.println("\n" + pieceWiseAlgo.getNetVoltage().toString());
	}
	
	@Test
	public void fullMatrixTest() throws Exception {
		IpssCorePlugin.init();
		
  		AcscNetwork net = getAcscNet();  	
/*
a(0,0,'1'): 0.0000 + j-2.42857
a(1,1,'2'): 0.0000 + j-2.7619
a(2,2,'3'): 0.0000 + j-2.33333
a(3,3,'4'): 0.0000 + j-0.0000
a(4,4,'5'): 0.0000 + j-0.0000

Bus '2' injection current [(0.2,0.0),  (0.05,0.0),  (0.0001,0.0)]
Bus '3' injection current [(-0.2,0.0), (-0.05,0.0), (-0.0001,0.0)]
*/
  		ISparseEqnComplex y1 = net.formScYMatrix(SequenceCode.POSITIVE, false);
  		y1.setBi(new Complex(1.0,0.0), 2);
  		y1.setBi(new Complex(-1.0,0.0), 3);
  		y1.solveEqn();
  		//System.out.println(y1);
/*
b(0): 0.00267 + j0.00688
b(1): -0.00112 + j-0.01164
b(2): 0.0080 + j0.03793
b(3): -0.00061 + j-0.01491
b(4): 0.00305 + j0.01445  		
 */
  		
  		ISparseEqnComplex y2 = net.formScYMatrix(SequenceCode.NEGATIVE, false);
  		y2.setBi(new Complex(0.05,0.0), 2);
  		y2.setBi(new Complex(-0.05,0.0), 3);
  		y2.solveEqn();
  		//System.out.println(y2);
/*
b(0): 0.00013 + j0.00034
b(1): -0.00006 + j-0.00058
b(2): 0.0004 + j0.0019
b(3): -0.00003 + j-0.00075
b(4): 0.00015 + j0.00072   		
 */
  		ISparseEqnComplex y0 = net.formScYMatrix(SequenceCode.ZERO, false);
  		y0.setBi(new Complex(0.0001,0.0), 2);
  		y0.setBi(new Complex(-0.0001,0.0), 3);
  		y0.solveEqn();
  		//System.out.println(y0);
/*
b(0): -0.0000 + j166666899.57972
b(1): -0.0000 + j166666899.58091
b(2): -0.0000 + j166666899.59469
b(3): -0.0000 + j-500000000.0000
b(4): -0.0000 + j0.0000   		
 */
  	}
	
	private AcscNetwork getAcscNet() throws Exception {
  		AcscNetwork net = AcscSample.create5BusSampleNet();
  		//System.out.println(net.net2String());
  		
	  	LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm(net);
	  	algo.loadflow();
  		//System.out.println(net.net2String());
	  	
  		assertTrue(net.isLfConverged());  	
  		
  		return net;
	}
}

