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
  * @Date 04/15/2016
  * 
  *   Revision History
  *   ================
  *
  */

package sample.piecewise;

import java.util.Hashtable;
import java.util.List;
import java.util.function.Function;

import org.apache.commons.math3.complex.Complex;
import org.interpss.CorePluginFactory;
import org.interpss.IpssCorePlugin;
import org.interpss.fadapter.IpssFileAdapter;
import org.interpss.numeric.datatype.ComplexFunc;
import org.interpss.piecewise.PiecewiseAlgorithm;
import org.interpss.piecewise.SubAreaNetProcessor.SubAreaNetType;
import org.interpss.piecewise.onephase.CuttingBranch1P;
import org.interpss.piecewise.onephase.SubArea1P;
import org.interpss.piecewise.onephase.SubNetwork1P;
import org.interpss.piecewise.onephase.algo.PiecewiseAlgo1PImpl;
import org.interpss.piecewise.onephase.impl.SubAreaNet1PProcessorImpl;

import com.interpss.CoreObjectFactory;
import com.interpss.core.aclf.AclfBus;
import com.interpss.core.aclf.AclfLoadCode;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.algo.LoadflowAlgorithm;

public class IEEE14BusSample {
	// define bus injection current calculation function
	static Function<AclfBus,Complex> injCurrentFunc = bus -> {   // this function calculates bus injection current
			// The bus injection current is based on gen bus load flow results.
  			return bus.isGen()? 
  						bus.getNetGenResults().divide(bus.getVoltage()) : 
  						new Complex(0.0, 0.0);
  		};
	
	public static void main(String args[]) throws Exception {
		// initialize InterPSS plugin
		IpssCorePlugin.init();

		subAreaSample();

		subNetworkSample();
	}
	
	static void subAreaSample() throws Exception {
		// Load the sample network
		AclfNetwork net = createSampleAclfNetwork();
		
		// ============ Solve network voltage by piecewise algo ====================//
  		
  		// define the cutting branch set
		CuttingBranch1P[] cuttingBranches = new CuttingBranch1P[] { 
					new CuttingBranch1P("4->71(1)"),
					new CuttingBranch1P("4->91(1)"),
					new CuttingBranch1P("5->61(1)")};	
		
		// process the SubArea or SubNetwork
		List<SubArea1P> subAreaList = 
				new SubAreaNet1PProcessorImpl<SubArea1P>(net, SubAreaNetType.SubArea, cuttingBranches)
								.processSubAreaNet();

		// define a piecewise algo object and calculate the network bus voltage
	  	PiecewiseAlgorithm<AclfBus, Complex, SubArea1P> pieceWiseAlgo = new PiecewiseAlgo1PImpl<>(net);
  		Hashtable<String,Complex> voltages = pieceWiseAlgo.calculateNetVoltage(
  				subAreaList, cuttingBranches, injCurrentFunc);
 		
  		// output network bus voltage
  		System.out.println("----------- SubArea -----------");
  		voltages.forEach((busId, voltage) -> {
  			System.out.println("Bus id:" + busId + ", v = " + ComplexFunc.toStr(voltage));
  		});

  		/* you should see output like the following
			Bus id:10, v = 0.84642 + j0.39398
			Bus id:1, v = 0.93198 + j0.68774
			...
  		 */
	}

	static void subNetworkSample() throws Exception {
		// Load the sample network
		AclfNetwork net = createSampleAclfNetwork();
		
		// ============ Solve network voltage by piecewise algo ====================//
  		
  		// define the cutting branch set
		CuttingBranch1P[] cuttingBranches = new CuttingBranch1P[] { 
					new CuttingBranch1P("4->71(1)"),
					new CuttingBranch1P("4->91(1)"),
					new CuttingBranch1P("5->61(1)")};	
		
		// process the SubArea or SubNetwork
		List<SubNetwork1P> subAreaList = 
				new SubAreaNet1PProcessorImpl<SubNetwork1P>(net, SubAreaNetType.SubNetwork, cuttingBranches)
								.processSubAreaNet();

		// define a piecewise algo object and calculate the network bus voltage
	  	PiecewiseAlgorithm<AclfBus, Complex, SubNetwork1P> pieceWiseAlgo = new PiecewiseAlgo1PImpl<>(net);
  		Hashtable<String,Complex> voltages = pieceWiseAlgo.calculateNetVoltage(
  				subAreaList, cuttingBranches, injCurrentFunc);
 		
  		// output network bus voltage
  		System.out.println("----------- SubNetwork -----------");
  		voltages.forEach((busId, voltage) -> {
  			System.out.println("Bus id:" + busId + ", v = " + ComplexFunc.toStr(voltage));
  		});

  		/* you should see output like the following
			Bus id:10, v = 0.84642 + j0.39398
			Bus id:1, v = 0.93198 + j0.68774
			...
  		 */
	}
	
	static AclfNetwork createSampleAclfNetwork() throws Exception {
		// Load the sample network
		AclfNetwork net = CorePluginFactory
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
  		
  		return net;
	}
}
