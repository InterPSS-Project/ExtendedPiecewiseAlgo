 /*
  * @(#)PiecewiseAlgo1PImpl.java   
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

package org.interpss.piecewise.onephase.algo;

import java.util.List;
import java.util.function.Function;

import org.apache.commons.math3.complex.Complex;
import org.interpss.numeric.exp.IpssNumericException;
import org.interpss.numeric.matrix.ComplexMatrixEqn;
import org.interpss.numeric.matrix.MatrixUtil;
import org.interpss.numeric.sparse.ISparseEqnComplex;
import org.interpss.piecewise.base.BaseCuttingBranch;
import org.interpss.piecewise.base.BaseSubArea;
import org.interpss.piecewise.base.impl.AbstractPiecewiseAlgoAdapter;
import org.interpss.piecewise.onephase.SubNetwork1P;

import com.interpss.core.aclf.AclfBranch;
import com.interpss.core.aclf.AclfBus;
import com.interpss.core.aclf.AclfNetwork;

/**
 * Piecewise algorithm implementation for single phase or positive sequence network.
 * 
 * @author Mike
 *
 */
public class PiecewiseAlgo1PImpl<TSub extends BaseSubArea<ISparseEqnComplex, Complex[][]>> 
					extends AbstractPiecewiseAlgoAdapter<AclfBus, Complex, TSub> {
	// AclfNetwork object
	private AclfNetwork net;
	
	// Equivalent Z-matrix for cutting branch current calculation
	private ComplexMatrixEqn equivZMatrixEqn;
	
	/**
	 * Constructor
	 * 
	 * @param net AclfNetwork object
	 */
	public PiecewiseAlgo1PImpl(AclfNetwork net) {
		super();
		this.net = net;
	}

	/**
	 * Constructor
	 * 
	 * @param net AclfNetwork object
	 */
	public PiecewiseAlgo1PImpl(AclfNetwork net, List<TSub> subAreaNetList) {
		super();
		this.net = net;
		this.subAreaNetList = subAreaNetList;
	}
	
	@Override
	public void calculateOpenCircuitVoltage(Function<AclfBus,Complex> injCurrentFunc)  throws IpssNumericException {
  		for (TSub subarea: this.subAreaNetList) {
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
	 * Solve for SubArea/Network open circuit bus voltage. The bus injection current is based
	 * on gen bus load flow results. The bus voltage results are stored in the subarea
	 * Y-matrix sparse eqn object.
	 * 
	 * @param areaFlag subarea flag
	 * @throws IpssNumericException
	 */
	private void solveSubAreaNet(int areaFlag, Function<AclfBus,Complex> injCurrentFunc) throws IpssNumericException {
		// there is no need to form the Y matrix if it has not changed
		TSub subArea = this.getSubArea(areaFlag);
  		if (this.netYmatrixDirty) {
  			if (subArea instanceof SubNetwork1P)
  				((SubNetwork1P)subArea).formYMatrix();
  			else	
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
	
	@Override
	public void calcuateSubAreaNetVoltage(BaseCuttingBranch<Complex>[] cuttingBranches)  throws IpssNumericException {
		for(TSub subarea : this.subAreaNetList)
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
	private void calcuateSubAreaVoltage(TSub subArea, BaseCuttingBranch<Complex>[] cuttingBranches)  throws IpssNumericException {
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
		
		for (TSub subarea : this.subAreaNetList) {
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
	private void formSubAreaZMatrix(TSub subarea) throws IpssNumericException {
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
