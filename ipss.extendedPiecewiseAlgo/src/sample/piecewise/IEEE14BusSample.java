 /*
  * @(#)IEEE14BusSample.java   
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

package sample.piecewise;

import org.apache.commons.math3.complex.Complex;
import org.interpss.CorePluginObjFactory;
import org.interpss.IpssCorePlugin;
import org.interpss.fadapter.IpssFileAdapter;
import org.interpss.numeric.datatype.ComplexFunc;
import org.interpss.piecewise.CuttingBranch;
import org.interpss.piecewise.PiecewiseAlgorithm;
import org.interpss.piecewise.SubAreaProcessor;
import org.interpss.piecewise.impl.PiecewiseAlgorithmImpl;
import org.interpss.piecewise.impl.SubAreaProcessorImpl;

import com.interpss.CoreObjectFactory;
import com.interpss.core.aclf.AclfLoadCode;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.algo.LoadflowAlgorithm;

public class IEEE14BusSample {
	
	public static void main(String args[]) throws Exception {
		// initialize InterPSS plugin
		IpssCorePlugin.init();
		
		// Load the sample network
		AclfNetwork net = CorePluginObjFactory
					.getFileAdapter(IpssFileAdapter.FileFormat.IpssInternal)
					.load("testdata/ieee14.ipssdat")
					.getAclfNet();	
		
  		// calculate loadflow
	  	LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm(net);
	  	algo.loadflow();
  		//System.out.println(net.net2String());
	  	
  		// Turn all loads to Constant-Z load to create a linear network
  		net.getBusList().forEach(bus -> {
  				if (bus.isLoad()) 
  					bus.setLoadCode(AclfLoadCode.CONST_Z);
  			}); 			
  		/*
  		 * Break the network into two SubAreas according to the cutting branch set
  		 */	
		SubAreaProcessor proc = new SubAreaProcessorImpl(net, 
				new CuttingBranch[] { 
					new CuttingBranch("4->71(1)"),
					new CuttingBranch("4->91(1)"),
					new CuttingBranch("5->61(1)")});	
		proc.processSubArea();

		// define a piecewise algo object
  		PiecewiseAlgorithm pieceWiseAlgo = new PiecewiseAlgorithmImpl(net, proc.getSubAreaList());
  		
  		// Solve for the open-circuit voltage
  		pieceWiseAlgo.calculateOpenCircuitVoltage(bus -> {   // this function calculates bus injection current
			// The bus injection current is based on gen bus load flow results.
  			return bus.isGen()? bus.getNetGenResults().divide(bus.getVoltage()) : new Complex(0.0, 0.0);
  		});

  		// calculate cutting branch current
    	pieceWiseAlgo.calculateCuttingBranchCurrent(proc.getCuttingBranches());

  		// calculate bus voltage by superposition of the open-circuit voltage and voltage by inject the 
    	// cutting branch current in the subsrea network
  		pieceWiseAlgo.calcuateNetVoltage(proc.getCuttingBranches());  		
 		
  		// output network bus voltage
		pieceWiseAlgo.getNetVoltage().forEach((busId, voltage) -> {
  			System.out.println("Bus id:" + busId + ", v = " + ComplexFunc.toStr(voltage));
  		});
	}
}
