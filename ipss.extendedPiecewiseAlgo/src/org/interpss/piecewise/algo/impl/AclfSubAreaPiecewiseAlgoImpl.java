 /*
  * @(#)PiecewiseAlgorithmImpl.java   
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

package org.interpss.piecewise.algo.impl;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.function.Function;

import org.apache.commons.math3.complex.Complex;
import org.interpss.numeric.exp.IpssNumericException;
import org.interpss.numeric.matrix.ComplexMatrixEqn;
import org.interpss.numeric.matrix.MatrixUtil;
import org.interpss.numeric.sparse.ISparseEqnComplex;
import org.interpss.piecewise.algo.PiecewiseAlgorithm;
import org.interpss.piecewise.net.BaseCuttingBranch;
import org.interpss.piecewise.net.SubAreaNetProcessor;
import org.interpss.piecewise.net.impl.SubAreaProcessorImpl;
import org.interpss.piecewise.onephase.CuttingBranch;
import org.interpss.piecewise.onephase.SubArea;

import com.interpss.common.exp.InterpssException;
import com.interpss.core.aclf.AclfBranch;
import com.interpss.core.aclf.AclfBus;
import com.interpss.core.aclf.AclfNetwork;

/**
 * An AclfNetqork is divided into a set of SubArea connected by a set of cutting branches. Each SubArea is identified
 * by an unique flag(int). The AclfBus.intFlag is used to indicate where the bus is located in terms of SubArea. A set of
 * interface buses are defined in a SubArea to keep track of cutting branch connection relationship.
 * 
 * @author Mike
 *
 */
public class AclfSubAreaPiecewiseAlgoImpl implements  PiecewiseAlgorithm<AclfBus, Complex> {
	
	// AclfNetwork object
	private AclfNetwork net;
	
	// flag to indicate if the network subarea Y-matrix needs to be formed for the 
	// calculation
	private boolean netYmatrixDirty;
	
	// network bus voltage storage place
	private Hashtable<String, Complex> netVoltage;
	
	// Sub-area list 
	private List<SubArea> subareaList;
	
	// Equivalent Z-matrix for cutting branch current calculation
	private ComplexMatrixEqn equivZMatrixEqn;
	
	/**
	 * Constructor
	 * 
	 * @param net AclfNetwork object
	 */
	public AclfSubAreaPiecewiseAlgoImpl(AclfNetwork net) {
		this.net = net;
		this.netYmatrixDirty = true;
		this.netVoltage = new Hashtable<>();
		this.subareaList = new ArrayList<>();
	}
	
	/**
	 * Constructor
	 * 
	 * @param net AclfNetwork object
	 */
	public AclfSubAreaPiecewiseAlgoImpl(AclfNetwork net, List<SubArea> subareaList) {
		this(net);
		this.subareaList = subareaList;
	}
	
	/**
	 * @return the netYmatrixDirty
	 */
	public boolean isNetYmatrixDirty() {
		return netYmatrixDirty;
	}


	/**
	 * @param netYmatrixDirty the netYmatrixDirty to set
	 */
	public void setNetYmatrixDirty(boolean netYmatrixDirty) {
		this.netYmatrixDirty = netYmatrixDirty;
	}

	/**
	 * the network voltage cache hashtable
	 * 
	 * @return the netVoltage
	 */
	public Hashtable<String, Complex> getNetVoltage() {
		return netVoltage;
	}

	/**
	 * get the subarea list
	 * 
	 * @return the netVoltage
	 */
	public List<SubArea> getSubAreaList() {
		return this.subareaList;
	}

	/**
	 * get SubArea by the area flag
	 * 
	 * @param flag the area flag
	 * @return the subarea object
	 */
	public SubArea getSubArea(int flag) {
		for (SubArea subarea: this.subareaList) {
			if (subarea.getFlag() == flag)
				return subarea;
		}
		return null;
	}
	
	@Override
	public Hashtable<String,Complex> calculateNetVoltage(BaseCuttingBranch<Complex>[] cbranches, Function<AclfBus,Complex> injCurrentFunc)  throws InterpssException, IpssNumericException {
		SubAreaNetProcessor<AclfBus, AclfBranch, SubArea, Complex> proc = new SubAreaProcessorImpl<>(net, cbranches);	
		this.subareaList = proc.processSubAreaNet();
  		
		/* 
		 * this.subareaList = new SubAreaProcessorImpl<>(net, cbranches).processSubArea();
		 */
  		
  		// Solve for the open-circuit voltage
  		calculateOpenCircuitVoltage(injCurrentFunc);

  		// calculate cutting branch current
    	calculateCuttingBranchCurrent(cbranches);

  		// calculate bus voltage by superposition of the open-circuit voltage and voltage by inject the 
    	// cutting branch current in the subsrea network
  		calcuateSubAreaVoltage(cbranches);  		

		return this.netVoltage;
	}
	
	/**
	 * Solve for subarea open circuit bus voltage. The bus voltage results are stored in the netVoltage hashtable.
	 * 
	 * @param injCurrentFunc bus inject current calculation function
	 * @throws IpssNumericException
	 */
	@Override
	public void calculateOpenCircuitVoltage(Function<AclfBus,Complex> injCurrentFunc)  throws IpssNumericException {
  		for (SubArea subarea: this.subareaList) {
  			solveSubAreaNet(subarea.getFlag(), injCurrentFunc);
  	  		//System.out.println("y1: \n" + y1.toString());
  			
  			// cache bus voltage stored in the subarea Y-matrix sparse eqn into the hashtable
  	  		net.getBusList().forEach(bus -> {
  	  			if (bus.getIntFlag() == subarea.getFlag()) {
  	  				this.netVoltage.put(bus.getId(), subarea.getYSparseEqn().getX(bus.getSortNumber()));
  	  			}
  	  		});   	  		
  		}
	}

	/**
	 * Solve for subarea open circuit bus voltage. The bus injection current is based
	 * on gen bus load flow results. The bus voltage results are stored in the subarea
	 * Y-matrix sparse eqn object.
	 * 
	 * @param areaFlag subarea flag
	 * @throws IpssNumericException
	 */
	private void solveSubAreaNet(int areaFlag, Function<AclfBus,Complex> injCurrentFunc) throws IpssNumericException {
		// there is no need to form the Y matrix if it has not changed
		SubArea subArea = this.getSubArea(areaFlag);
  		if (this.netYmatrixDirty) {
  			subArea.setYSparseEqn(net.formYMatrix(areaFlag));
  			subArea.getYSparseEqn().luMatrix(1.0e-10);
  		}
  		//System.out.println("y1: \n" + y1.toString());
  		
  		net.getBusList().forEach(bus -> {
				if (bus.getIntFlag() == areaFlag) {
					// we use the function to calculate the bus injection current
					subArea.getYSparseEqn().setBi(injCurrentFunc.apply(bus), bus.getSortNumber());
				}
			});
  		
  		subArea.getYSparseEqn().solveEqn();	
	}
	
	/**
	 * calculate the bus voltage for the subarea based on the cutting branch current and the bus open circuit voltage
	 * 
	 * @param cuttingBranches cutting branch storing the branch current
	 * @throws IpssNumericException
	 */
	@Override
	public void calcuateSubAreaVoltage(BaseCuttingBranch<Complex>[] cuttingBranches)  throws IpssNumericException {
		for(SubArea subarea : this.subareaList)
			calcuateSubAreaVoltage(subarea, cuttingBranches);  		
	}
	
	/**
	 * calculate the bus voltage for the subarea based on the cutting branch current and the bus open circuit voltage,
	 * according to the superposition principle.
	 * 
	 * @param subArea subarea
	 * @param cuttingBranches cutting branch storing the branch current
	 * @throws IpssNumericException
	 */
	private void calcuateSubAreaVoltage(SubArea subArea, BaseCuttingBranch<Complex>[] cuttingBranches)  throws IpssNumericException {
  		// calculate the cutting branch current injection
		ISparseEqnComplex yMatrix = subArea.getYSparseEqn();
		yMatrix.setB2Zero();
  		for (int cnt = 0; cnt < cuttingBranches.length; cnt++) {
  			AclfBranch branch = net.getBranch(cuttingBranches[cnt].getBranchId());
  			// current into the network as the positive direction
  			if (branch.getFromBus().getIntFlag() == subArea.getFlag()) {
  				yMatrix.addToB(cuttingBranches[cnt].getCurrent().multiply(-1.0), branch.getFromBus().getSortNumber());
  			}
  			else if (branch.getToBus().getIntFlag() == subArea.getFlag()) {
  				yMatrix.addToB(cuttingBranches[cnt].getCurrent(), branch.getToBus().getSortNumber());
  			}
  		}
  		//System.out.println(yMatrix);
  		
  		// calculate sub-area bus voltage due to the cutting branch current injection
  		subArea.getYSparseEqn().solveEqn();
  		//System.out.println(ySubArea);
  		
  		// update the bus voltage based on the superposition principle
  		for (int i = 0; i < yMatrix.getDimension(); i++) {
  			String id = yMatrix.getBusId(i);
  			Complex v = netVoltage.get(id).add(yMatrix.getX(i));
  			//System.out.println("area1 voltage update -- " + id + ", " + netVoltage.get(id) + ", " + yAreaNet1.getX(i) + ", " + v.abs());
  			netVoltage.put(id, v);
  		}		
	}	
	
	
	/**
	 * calculate cutting branch current
	 * 
	 * @param cuttingBranches cutting branches
	 * @return the current array
	 */
	@Override
	public void calculateCuttingBranchCurrent(BaseCuttingBranch<Complex>[] cuttingBranches) throws IpssNumericException { 
		
		Complex[] eCutBranch = cuttingBranchOpenCircuitVoltage(cuttingBranches); 
		
		// we build the equiv Z only if the network Y matrix has changed
		if (this.netYmatrixDirty) {
			Complex[][] equivZMatrix = buildEquivZMtrix(cuttingBranches);
			this.equivZMatrixEqn = new ComplexMatrixEqn(equivZMatrix);
			this.equivZMatrixEqn.inverseMatrix();
		}
		
    	Complex[] curAry = this.equivZMatrixEqn.solveEqn(eCutBranch, false);	
    	
    	for (int i = 0; i < cuttingBranches.length; i++) {
    		//System.out.println("Branch cur: " + cuttingBranches[i] + " " + ComplexFunc.toStr(curAry[i]));
    		cuttingBranches[i].setCurrent(curAry[i]);
    	}
	}
	
	/**
	 * calculate open circuit voltage difference for the cutting branches. 
	 * Pre-condition: the network bus open-circuit voltage has been calculated
	 * and stored in the netVoltage hashtable. 
	 * 
	 * @param cuttingBranches cutting branch objects
	 * @return cutting branch voltage difference array
	 */
	private Complex[] cuttingBranchOpenCircuitVoltage(BaseCuttingBranch<Complex>[] cuttingBranches) {
		int nCutBranches = cuttingBranches.length;
		Complex[] eCutBranch = new Complex[nCutBranches];    // transport[M] x open circuit[E]
  		for ( int i = 0; i < nCutBranches; i++) {
  			AclfBranch branch = net.getBranch(cuttingBranches[i].getBranchId());
  			// assume branch current from bus -> to bus
  			// current positive direction - into the sub-area network
  			eCutBranch[i] = getNetVoltage().get(branch.getFromBus().getId())
  					        	.subtract(getNetVoltage().get(branch.getToBus().getId()));
  			//System.out.println("E open: " + cnt + ", " + ComplexFunc.toStr(eCutBranch[cnt]));
  		}
  		return eCutBranch;
	}
	
	/**
	 * build equivalent z-matrix for solving cutting branch current
	 * 
	 * @param cuttingBranches cutting branches
	 * @return the z-matrix
	 */
	private Complex[][] buildEquivZMtrix(BaseCuttingBranch<Complex>[] cuttingBranches)  throws IpssNumericException {
		int nCutBranches = cuttingBranches.length;
		Complex[][] matrix = new Complex[nCutBranches][nCutBranches];
	  	
		/*
		 * first add the cutting branch [Zl] part
		 */
		for ( int i = 0; i < nCutBranches; i++) {
  			AclfBranch branch = net.getBranch(cuttingBranches[i].getBranchId());
  			for (int j = 0; j < nCutBranches; j++) {
  				matrix[i][j] = i == j? branch.getZ() : new Complex(0.0,0.0);
  			}
		}
		
		for (SubArea subarea : this.subareaList) {
			formSubAreaZMatrix(subarea);
			
			/*
			 * form Mi matrix. Mi is the connection relationship matrix for
			 * the subarea interface bus and the cutting branch for SubArea i 
			 */
			int[][] M = new int[subarea.getInterfaceBusIdList().size()][nCutBranches];
			for ( int i = 0; i < nCutBranches; i++) {
	  			AclfBranch branch = net.getBranch(cuttingBranches[i].getBranchId());
	  			for (int j = 0; j < subarea.getInterfaceBusIdList().size(); j++)
	  				/*
	  				 * branch current positive direction fromBus -> toBus
	  				 * network injection current positive direction : into the network
	  				 */
	  				M[j][i] = branch.getFromBus().getId().equals(subarea.getInterfaceBusIdList().get(j))? -1 : 
	  							(branch.getToBus().getId().equals(subarea.getInterfaceBusIdList().get(j))? 1: 0);
			}
			
			/*
			 * add the transpose[Mi]x[Zi]x[M] part. 
			 */
			matrix = MatrixUtil.add(matrix, MatrixUtil.prePostMultiply(subarea.getZMatrix(), M));
		}	
		
		return matrix;
	}
	
	/**
	 * calculate subarea interface bus z-matrix. The matrix is stored in
	 * SubArea.zMatrix field.
	 * 
	 * @param subarea the subarea object
	 * @return the z-matrix
	 * @throws IpssNumericException
	 */
	private void formSubAreaZMatrix(SubArea subarea) throws IpssNumericException {
		int areaNodes = subarea.getInterfaceBusIdList().size();
		subarea.setZMatrix(new Complex[areaNodes][areaNodes]);
		for (int i = 0; i < areaNodes; i++) {
			int row = net.getBus(subarea.getInterfaceBusIdList().get(i)).getSortNumber();
			if (!subarea.getYSparseEqn().getBusId(row).equals(subarea.getInterfaceBusIdList().get(i))) {
				// the y-matrix row number and the bus.sortNumber should match
				throw new IpssNumericException("Programming error: PiecewiseAlgorithm.subAreaZMatrix()");
			}
			subarea.getYSparseEqn().setB2Unity(row);
			
			subarea.getYSparseEqn().solveEqn();
			
			for (int j = 0; j < areaNodes; j++) {
				int col = net.getBus(subarea.getInterfaceBusIdList().get(j)).getSortNumber();
				subarea.getZMatrix()[j][i] = subarea.getYSparseEqn().getX(col);
			}
		}
		/*
 		for (int i = 0; i < area1Nodes; i++)
 	 		for (int j = 0; j < area1Nodes; j++)
 	 			System.out.println("zMatrix [" + i + "," + j + "]" + ComplexFunc.toStr(zMatrix[i][j]));
		*/
	}	
}

