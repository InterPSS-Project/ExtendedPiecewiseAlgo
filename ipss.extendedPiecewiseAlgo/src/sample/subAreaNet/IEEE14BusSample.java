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

package sample.subAreaNet;

import org.apache.commons.math3.complex.Complex;
import org.interpss.CorePluginFactory;
import org.interpss.IpssCorePlugin;
import org.interpss.fadapter.IpssFileAdapter;
import org.interpss.piecewise.SubAreaNetProcessor;
import org.interpss.piecewise.onephase.CuttingBranch1P;
import org.interpss.piecewise.onephase.SubArea1P;
import org.interpss.piecewise.onephase.SubNetwork1P;
import org.interpss.piecewise.onephase.impl.SubAreaNet1PProcessorImpl;

import com.interpss.common.exp.InterpssException;
import com.interpss.core.aclf.AclfBranch;
import com.interpss.core.aclf.AclfBus;
import com.interpss.core.aclf.AclfNetwork;

public class IEEE14BusSample {
	
	public static void main(String args[]) throws Exception {
		// initialize InterPSS plugin
		IpssCorePlugin.init();
		
		System.out.println("======================SubArea processing ==================");
		subAreaProcessing();
		
		System.out.println("======================SubNetwork processing ==================");
		subNetworkProcessing();	
		
	}
	
	static void subAreaProcessing() throws InterpssException {
		AclfNetwork net = CorePluginFactory
				.getFileAdapter(IpssFileAdapter.FileFormat.IpssInternal)
				.load("testdata/ieee14.ipssdat")
				.getAclfNet();
				
		SubAreaNetProcessor<AclfBus, AclfBranch, SubArea1P, Complex> 
			proc = new SubAreaNet1PProcessorImpl<>(net, SubAreaNetProcessor.SubAreaNetType.SubArea, new CuttingBranch1P[] { 
						new CuttingBranch1P("4->71(1)"),
						new CuttingBranch1P("4->91(1)"),
						new CuttingBranch1P("5->61(1)")});	
		
		proc.processSubAreaNet();
		
		proc.getSubAreaNetList().forEach(subArea -> {
			System.out.println("----------------------");
			System.out.println(subArea);
		});		
	}
	
	static void subNetworkProcessing() throws InterpssException {
		AclfNetwork net = CorePluginFactory
				.getFileAdapter(IpssFileAdapter.FileFormat.IpssInternal)
				.load("testdata/ieee14.ipssdat")
				.getAclfNet();		
		
		SubAreaNetProcessor<AclfBus, AclfBranch, SubNetwork1P, Complex> 
				proc = new SubAreaNet1PProcessorImpl<>(net, SubAreaNetProcessor.SubAreaNetType.SubNetwork, new CuttingBranch1P[] { 
							new CuttingBranch1P("4->71(1)"),
							new CuttingBranch1P("4->91(1)"),
							new CuttingBranch1P("5->61(1)")});	
		
		proc.processSubAreaNet();
		
		proc.getSubAreaNetList().forEach(subNet -> {
			System.out.println("----------------------");
			System.out.println(subNet);
		});		
	}
	
}
