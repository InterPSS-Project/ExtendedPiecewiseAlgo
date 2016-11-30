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
import org.interpss.numeric.util.NumericUtil;
import org.interpss.piecewise.PiecewiseAlgorithm;
import org.interpss.piecewise.SubAreaNetProcessor;
import org.interpss.piecewise.base.BaseCuttingBranch;
import org.interpss.piecewise.seq012.CuttingBranch012;
import org.interpss.piecewise.seq012.SubArea012;
import org.interpss.piecewise.seq012.SubNetwork012;
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
	@Test
	public void subNetworkTest() throws Exception {
		IpssCorePlugin.init();
		
  		AcscNetwork net = getAcscNet();  	
  		
		SubAreaNetProcessor<AcscBus, AcscBranch, SubNetwork012, Complex3x1> proc = 
				new SubArea012ProcessorImpl<SubNetwork012>(net, new CuttingBranch012[] { 
						new CuttingBranch012("1->2(1)"),
						new CuttingBranch012("1->3(1)"),
						new CuttingBranch012("2->3(1)")});	
  		
  		proc.processSubAreaNet(); 		
  		
  		/*
  		 * Solve [Y][I] = [V] using the piecewise method
  		 * =============================================
  		 */
  		PiecewiseAlgorithm<AcscBus, Complex3x1, SubNetwork012> pieceWiseAlgo = new PiecewiseAlgo012Impl<>(net, proc.getSubAreaNetList());
  		
  		/*//////////////////////////////////
  		 * Step-1: Solve for the open-circuit voltage
  		 *//////////////////////////////////
  		
  		/*
  		 * Function to compute bus injection current for the testing purpose
  		 */
  		Function<AcscBus, Complex3x1> injCurFunc = bus -> {
  			if (bus.getId().equals("2")) {  // Bus '2' (0.0001,0.0), (1.0,0.0),  (0.05,0.0)
  				return new Complex3x1(new Complex(0.0001,0.0), new Complex(1.0,0.0),  new Complex(0.05,0.0));
  			}
  			else if (bus.getId().equals("3")) {  // Bus '3'
  				return new Complex3x1(new Complex(-0.0001,0.0), new Complex(-1.0,0.0),  new Complex(-0.05,0.0));
  			}
  			else 
  				return new Complex3x1();
  		};

  		pieceWiseAlgo.calculateOpenCircuitVoltage(injCurFunc);
  		//System.out.println("Open Circuit Voltage\n" + pieceWiseAlgo.getNetVoltage().toString());
  		assertTrue(NumericUtil.equals(pieceWiseAlgo.getNetVoltage().get("5").b_1.getReal(), -0.0034, 1.0e-4));
  		assertTrue(NumericUtil.equals(pieceWiseAlgo.getNetVoltage().get("5").c_2.getReal(), -0.00017, 1.0e-4));
  		
  		assertTrue(NumericUtil.equals(pieceWiseAlgo.getNetVoltage().get("3").a_0.getImaginary(), -1000000.0000, 1.0e-4));
  		assertTrue(NumericUtil.equals(pieceWiseAlgo.getNetVoltage().get("3").c_2.getReal(), -0.00045, 1.0e-4));

  		/*/////////////////////////////
  		 * Step-2: calculate cutting branch current
  		 */////////////////////////////
 
    	pieceWiseAlgo.calculateCuttingBranchCurrent(proc.getCuttingBranches());
    	for (BaseCuttingBranch<Complex3x1> cbra : proc.getCuttingBranches()) {
  			//System.out.println(cbra.getBranchId() + ": " + cbra.getCurrent());
  		}
		
  		/*//////////////////////////////////////////
  		 * Step-3
  		 *//////////////////////////////////////////
		
		pieceWiseAlgo.calcuateSubAreaNetVoltage(proc.getCuttingBranches());  		
  		//System.out.println("Closed Circuit Voltage\n" + pieceWiseAlgo.getNetVoltage().toString());
  		assertTrue(NumericUtil.equals(pieceWiseAlgo.getNetVoltage().get("5").b_1.getReal(), -0.00372, 1.0e-4));
  		assertTrue(NumericUtil.equals(pieceWiseAlgo.getNetVoltage().get("5").c_2.getReal(), -0.00019, 1.0e-4));
  		
  		assertTrue(NumericUtil.equals(pieceWiseAlgo.getNetVoltage().get("3").a_0.getImaginary(), -1693876.06345, 1.0e-4));
  		assertTrue(NumericUtil.equals(pieceWiseAlgo.getNetVoltage().get("3").c_2.getReal(), -0.00049, 1.0e-4));
	}

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
  		
  		/*
  		 * Function to compute bus injection current for the testing purpose
  		 */
  		Function<AcscBus, Complex3x1> injCurFunc = bus -> {
  			if (bus.getId().equals("2")) {  // Bus '2' (0.0001,0.0), (1.0,0.0),  (0.05,0.0)
  				return new Complex3x1(new Complex(0.0001,0.0), new Complex(1.0,0.0),  new Complex(0.05,0.0));
  			}
  			else if (bus.getId().equals("3")) {  // Bus '3'
  				return new Complex3x1(new Complex(-0.0001,0.0), new Complex(-1.0,0.0),  new Complex(-0.05,0.0));
  			}
  			else 
  				return new Complex3x1();
  		};

  		pieceWiseAlgo.calculateOpenCircuitVoltage(injCurFunc);
  		//System.out.println("Open Circuit Voltage\n" + pieceWiseAlgo.getNetVoltage().toString());
/*
5=-0.0000 + j0.0000         -0.0034 + j-0.01908   -0.00017 + j-0.00095, 
4=-0.0000 + j0.0000          0.0013 + j0.02024     0.00007 + j0.00101, 
3=-0.0000 + j-1000000.0000  -0.00892 + j-0.05009  -0.00045 + j-0.0025, 
2=-0.0000 + j1000000.0000    0.00239 + j0.03719    0.00012 + j0.00186, 
1=-0.0000 + j0.0000          0.0000 + j0.0000      0.0000 + j0.0000
 */
  		assertTrue(NumericUtil.equals(pieceWiseAlgo.getNetVoltage().get("5").b_1.getReal(), -0.0034, 1.0e-4));
  		assertTrue(NumericUtil.equals(pieceWiseAlgo.getNetVoltage().get("5").c_2.getReal(), -0.00017, 1.0e-4));
  		
  		assertTrue(NumericUtil.equals(pieceWiseAlgo.getNetVoltage().get("3").a_0.getImaginary(), -1000000.0000, 1.0e-4));
  		assertTrue(NumericUtil.equals(pieceWiseAlgo.getNetVoltage().get("3").c_2.getReal(), -0.00045, 1.0e-4));

  		/*/////////////////////////////
  		 * Step-2: calculate cutting branch current
  		 */////////////////////////////
 
    	pieceWiseAlgo.calculateCuttingBranchCurrent(proc.getCuttingBranches());
    	for (BaseCuttingBranch<Complex3x1> cbra : proc.getCuttingBranches()) {
  			//System.out.println(cbra.getBranchId() + ": " + cbra.getCurrent());
  		}
		
  		/*//////////////////////////////////////////
  		 * Step-3
  		 *//////////////////////////////////////////
		
		pieceWiseAlgo.calcuateSubAreaNetVoltage(proc.getCuttingBranches());  		
  		//System.out.println("Closed Circuit Voltage\n" + pieceWiseAlgo.getNetVoltage().toString());
/*
5=-0.0000 + j0.0000          -0.00372 + j-0.0229   -0.00019 + j-0.00115, 
4=-0.0000 + j0.0000           0.00141 + j0.01623    0.00007 + j0.00081, 
3=-0.0000 + j-1693876.06345  -0.00976 + j-0.06012  -0.00049 + j-0.00301, 
2=-0.0000 + j306123.93655     0.0026 + j0.02983     0.00013 + j0.00149, 
1=-0.0000 + j-693876.06345   -0.06979 + j-0.04336  -0.00349 + j-0.00217}
 */
  		assertTrue(NumericUtil.equals(pieceWiseAlgo.getNetVoltage().get("5").b_1.getReal(), -0.00372, 1.0e-4));
  		assertTrue(NumericUtil.equals(pieceWiseAlgo.getNetVoltage().get("5").c_2.getReal(), -0.00019, 1.0e-4));
  		
  		assertTrue(NumericUtil.equals(pieceWiseAlgo.getNetVoltage().get("3").a_0.getImaginary(), -1693876.06345, 1.0e-4));
  		assertTrue(NumericUtil.equals(pieceWiseAlgo.getNetVoltage().get("3").c_2.getReal(), -0.00049, 1.0e-4));
	}
	
	@Test
	public void subAreaPosSeqOnlyTest() throws Exception {
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
  		
  		System.out.println("================== Pos Seq Only ====================");
  		
  		/*//////////////////////////////////
  		 * Step-1: Solve for the open-circuit voltage
  		 *//////////////////////////////////
  		
  		/*
  		 * Function to compute bus injection current for the testing purpose
  		 */
  		Function<AcscBus, Complex3x1> injCurFunc = bus -> {
  			if (bus.getId().equals("2")) {  // Bus '2' (0.0001,0.0), (1.0,0.0),  (0.05,0.0)
  				return new Complex3x1(new Complex(0.0,0.0), new Complex(1.0,0.0),  new Complex(0.0,0.0));
  			}
  			else if (bus.getId().equals("3")) {  // Bus '3'
  				return new Complex3x1(new Complex(0.0,0.0), new Complex(-1.0,0.0),  new Complex(0.0,0.0));
  			}
  			else 
  				return new Complex3x1();
  		};

  		pieceWiseAlgo.calculateOpenCircuitVoltage(injCurFunc);
  		//System.out.println("Open Circuit Voltage\n" + pieceWiseAlgo.getNetVoltage().toString());

  		assertTrue(NumericUtil.equals(pieceWiseAlgo.getNetVoltage().get("5").b_1.getReal(), -0.0034, 1.0e-4));
  		
  		/*/////////////////////////////
  		 * Step-2: calculate cutting branch current
  		 */////////////////////////////
 
    	pieceWiseAlgo.calculateCuttingBranchCurrent(proc.getCuttingBranches());
    	for (BaseCuttingBranch<Complex3x1> cbra : proc.getCuttingBranches()) {
  			//System.out.println(cbra.getBranchId() + ": " + cbra.getCurrent());
  		}
		
  		/*//////////////////////////////////////////
  		 * Step-3
  		 *//////////////////////////////////////////
		
		pieceWiseAlgo.calcuateSubAreaNetVoltage(proc.getCuttingBranches());  		
  		//System.out.println("Closed Circuit Voltage\n" + pieceWiseAlgo.getNetVoltage().toString());

		assertTrue(NumericUtil.equals(pieceWiseAlgo.getNetVoltage().get("5").b_1.getReal(), -0.00372, 1.0e-4));
	}

	@Test
	public void subAreaNegSeqOnlyTest() throws Exception {
		IpssCorePlugin.init();
		
  		AcscNetwork net = getAcscNet();  	
  		
		SubAreaNetProcessor<AcscBus, AcscBranch, SubArea012, Complex3x1> proc = 
				new SubArea012ProcessorImpl<SubArea012>(net, new CuttingBranch012[] { 
						new CuttingBranch012("1->2(1)"),
						new CuttingBranch012("1->3(1)"),
						new CuttingBranch012("2->3(1)")});	
  		
  		proc.processSubAreaNet(); 		

  		System.out.println("================== Neg Seq Only ====================");
  		
  		/*
  		 * Solve [Y][I] = [V] using the piecewise method
  		 * =============================================
  		 */
  		PiecewiseAlgorithm<AcscBus, Complex3x1, SubArea012> pieceWiseAlgo = new PiecewiseAlgo012Impl<>(net, proc.getSubAreaNetList());
  		
  		/*//////////////////////////////////
  		 * Step-1: Solve for the open-circuit voltage
  		 *//////////////////////////////////
  		
  		/*
  		 * Function to compute bus injection current for the testing purpose
  		 */
  		Function<AcscBus, Complex3x1> injCurFunc = bus -> {
  			if (bus.getId().equals("2")) {  // Bus '2' (0.0001,0.0), (1.0,0.0),  (0.05,0.0)
  				return new Complex3x1(new Complex(0.0,0.0), new Complex(0.0,0.0),  new Complex(0.05,0.0));
  			}
  			else if (bus.getId().equals("3")) {  // Bus '3'
  				return new Complex3x1(new Complex(0.0,0.0), new Complex(0.0,0.0),  new Complex(-0.05,0.0));
  			}
  			else 
  				return new Complex3x1();
  		};

  		pieceWiseAlgo.calculateOpenCircuitVoltage(injCurFunc);
  		//System.out.println("Open Circuit Voltage\n" + pieceWiseAlgo.getNetVoltage().toString());

  		assertTrue(NumericUtil.equals(pieceWiseAlgo.getNetVoltage().get("5").c_2.getReal(), -0.00017, 1.0e-4));
  		assertTrue(NumericUtil.equals(pieceWiseAlgo.getNetVoltage().get("3").c_2.getReal(), -0.00045, 1.0e-4));
  		
  		/*/////////////////////////////
  		 * Step-2: calculate cutting branch current
  		 */////////////////////////////
 
    	pieceWiseAlgo.calculateCuttingBranchCurrent(proc.getCuttingBranches());
    	for (BaseCuttingBranch<Complex3x1> cbra : proc.getCuttingBranches()) {
  			//System.out.println(cbra.getBranchId() + ": " + cbra.getCurrent());
  		}
		
  		/*//////////////////////////////////////////
  		 * Step-3
  		 *//////////////////////////////////////////
		
		pieceWiseAlgo.calcuateSubAreaNetVoltage(proc.getCuttingBranches());  		
  		//System.out.println("Closed Circuit Voltage\n" + pieceWiseAlgo.getNetVoltage().toString());

  		assertTrue(NumericUtil.equals(pieceWiseAlgo.getNetVoltage().get("5").c_2.getReal(), -0.00019, 1.0e-4));
  		assertTrue(NumericUtil.equals(pieceWiseAlgo.getNetVoltage().get("3").c_2.getReal(), -0.00049, 1.0e-4));
	}

	@Test
	public void subAreaZeroSeqOnlyTest() throws Exception {
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

  		System.out.println("================== Zero Seq Only ====================");
  		
  		/*//////////////////////////////////
  		 * Step-1: Solve for the open-circuit voltage
  		 *//////////////////////////////////
  		
  		/*
  		 * Function to compute bus injection current for the testing purpose
  		 */
  		Function<AcscBus, Complex3x1> injCurFunc = bus -> {
  			if (bus.getId().equals("2")) {  // Bus '2' (0.0001,0.0), (1.0,0.0),  (0.05,0.0)
  				return new Complex3x1(new Complex(0.0001,0.0), new Complex(0.0,0.0),  new Complex(0.0,0.0));
  			}
  			else if (bus.getId().equals("3")) {  // Bus '3'
  				return new Complex3x1(new Complex(-0.0001,0.0), new Complex(0.0,0.0),  new Complex(0.0,0.0));
  			}
  			else 
  				return new Complex3x1();
  		};

  		pieceWiseAlgo.calculateOpenCircuitVoltage(injCurFunc);
  		//System.out.println("Open Circuit Voltage\n" + pieceWiseAlgo.getNetVoltage().toString());

  		assertTrue(NumericUtil.equals(pieceWiseAlgo.getNetVoltage().get("3").a_0.getImaginary(), -1000000.0000, 1.0e-4));
  		
  		/*/////////////////////////////
  		 * Step-2: calculate cutting branch current
  		 */////////////////////////////
 
    	pieceWiseAlgo.calculateCuttingBranchCurrent(proc.getCuttingBranches());
    	for (BaseCuttingBranch<Complex3x1> cbra : proc.getCuttingBranches()) {
  			//System.out.println(cbra.getBranchId() + ": " + cbra.getCurrent());
  		}
		
  		/*//////////////////////////////////////////
  		 * Step-3
  		 *//////////////////////////////////////////
		
		pieceWiseAlgo.calcuateSubAreaNetVoltage(proc.getCuttingBranches());  		
  		//System.out.println("Closed Circuit Voltage\n" + pieceWiseAlgo.getNetVoltage().toString());

  		assertTrue(NumericUtil.equals(pieceWiseAlgo.getNetVoltage().get("3").a_0.getImaginary(), -1693876.06345, 1.0e-4));
	}
	
	/*
	 * Full matrix approach
	 */
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

Bus '2' injection current [(0.0001,0.0), (1.0,0.0),  (0.05,0.0)]
Bus '3' injection current [(-0.0001,0.0), (-1.0,0.0), (-0.05,0.0)]
*/
  		ISparseEqnComplex y1 = net.formScYMatrix(SequenceCode.POSITIVE, false);
  		y1.setBi(new Complex(1.0,0.0), 1);
  		y1.setBi(new Complex(-1.0,0.0), 2);
  		y1.solveEqn();
  		//System.out.println(y1);
/*
b(0): 0.00036 + j0.00061
b(1): 0.00271 + j0.02638
b(2): -0.00765 + j-0.03522
b(3): 0.00147 + j0.01436
b(4): -0.00291 + j-0.01342 		
 */
  		
  		ISparseEqnComplex y2 = net.formScYMatrix(SequenceCode.NEGATIVE, false);
  		y2.setBi(new Complex(0.05,0.0), 1);
  		y2.setBi(new Complex(-0.05,0.0), 2);
  		y2.solveEqn();
  		//System.out.println(y2);
/*
b(0): 0.00002 + j0.00003
b(1): 0.00014 + j0.00132
b(2): -0.00038 + j-0.00176
b(3): 0.00007 + j0.00072
b(4): -0.00015 + j-0.00067 		
 */
  		ISparseEqnComplex y0 = net.formScYMatrix(SequenceCode.ZERO, false);
  		y0.setBi(new Complex(0.0001,0.0), 1);
  		y0.setBi(new Complex(-0.0001,0.0), 2);
  		y0.solveEqn();
  		//System.out.println(y0);
/*
b(0): -0.0000 + j0.0000
b(1): -0.0000 + j0.00002
b(2): -0.0000 + j-0.00003
b(3): -0.0000 + j0.0000
b(4): -0.0000 + j0.0000   		
 */
  	}
	
	/*
	 * Full matrix for calculating open circuit voltage
	 */
	@Test
	public void fullMatrixOpenCircuitTest() throws Exception {
		IpssCorePlugin.init();
		
  		AcscNetwork net = getAcscNet();
  		
  		net.getBranch("1->2(1)").setStatus(false);
  		net.getBranch("1->3(1)").setStatus(false);
  		net.getBranch("2->3(1)").setStatus(false);
/*
Bus '2' injection current [(0.0001,0.0), (1.0,0.0),  (0.05,0.0)]
Bus '3' injection current [(-0.0001,0.0), (-1.0,0.0), (-0.05,0.0)]
*/
  		ISparseEqnComplex y1 = net.formScYMatrix(SequenceCode.POSITIVE, false);
  		y1.setBi(new Complex(1.0,0.0), 1);
  		y1.setBi(new Complex(-1.0,0.0), 2);
  		y1.solveEqn();
  		//System.out.println(y1);
/*
b(0): 0.0000 + j0.0000
b(1): 0.00239 + j0.03719
b(2): -0.00892 + j-0.05009
b(3): 0.0013 + j0.02024
b(4): -0.0034 + j-0.01908	
 */
  		
  		ISparseEqnComplex y2 = net.formScYMatrix(SequenceCode.NEGATIVE, false);
  		y2.setBi(new Complex(0.05,0.0), 1);
  		y2.setBi(new Complex(-0.05,0.0), 2);
  		y2.solveEqn();
  		//System.out.println(y2);
/*
b(0): 0.0000 + j0.0000
b(1): 0.00012 + j0.00186
b(2): -0.00045 + j-0.0025
b(3): 0.00007 + j0.00101
b(4): -0.00017 + j-0.00095
 */
  		ISparseEqnComplex y0 = net.formScYMatrix(SequenceCode.ZERO, false);
  		y0.setBi(new Complex(0.0001,0.0), 1);
  		y0.setBi(new Complex(-0.0001,0.0), 2);
  		y0.solveEqn();
  		//System.out.println(y0);
/*
b(0): -0.0000 + j0.0000
b(1): -0.0000 + j1000000.0000
b(2): -0.0000 + j-1000000.0000
b(3): -0.0000 + j0.0000
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

