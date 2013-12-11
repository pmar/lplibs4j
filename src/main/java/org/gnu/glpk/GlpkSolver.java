//  GlpkSolver.java  (Java Native Interface for GLPK 4.8)
/* ---------------------------------------------------------------------
 * GLPK:
 * Copyright (C) 2005 Andrew Makhorin <mao@mai2.rcnet.ru>, Department
 * for Applied Informatics, Moscow Aviation Institute, Moscow, Russia.
 * All rights reserved.
 *
 *
 * Java Native Interface for GLPK 4.8 
 *
 * Author: Bjoern Frank, byuman (at] gmx {dot) net
 *         - Java interface for GLPK 4.8
 * Author: Yuri Victorovich, Software Engineer, yuri@gjt.org.
 *         - Java interface for GLPK 4.2 (previous JNI version)
 * Author: Chris Rosebrugh, cpr@pobox.com
 *         - added user-installable print and fault hooks.
 *
 * This file is a part of GLPK (GNU Linear Programming Kit).
 *
 * GLPK is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * GLPK is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public
 * License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GLPK; see the file COPYING. If not, write to the Free
 * Software Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 * ---------------------------------------------------------------------
*/
package org.gnu.glpk;


/**
 * This is the core class of the Java Native Interface for GLPK 4.8.<br>
 * Use GLPK in the following way:
 * <pre>
 *      GlpkSolver solver = new GlpkSolver();
 *      ... fill LP model
 *      solver.simplex();   // LP solver
 *      solver.integer();   // ILP solver
 *      ... obtain results
 * </pre>
 * For a detailed manual and the C version of GLPK, take a look at the
 * <a href="http://www.gnu.org/software/glpk/glpk.html">GLPK Homepage</a>
 *
 * @author  Bjoern Frank
 * @version GLPK 4.8
 */
public class GlpkSolver {

    private long lp = 0;

    static private GlpkHookIFC m_hook=null;

    /* Constants needed in GLPK.
     * Problem class: */
    public final static int    LPX_LP         = 100;
    public final static int    LPX_MIP        = 101;

    /* type of the structural / auxiliary variable: */
    public final static int    LPX_FR         = 110;
    public final static int    LPX_LO         = 111;
    public final static int    LPX_UP         = 112;
    public final static int    LPX_DB         = 113;
    public final static int    LPX_FX         = 114;

    /* optimization direction flag (objective "sense"): */
    public final static int    LPX_MIN        = 120;

    public final static int    LPX_MAX        = 121;

    /* basis status: */
    public final static int    LPX_B_UNDEF    = 130;
    public final static int    LPX_B_VALID    = 131;

    /* status of primal basic solution: */
    public final static int    LPX_P_UNDEF    = 132;
    public final static int    LPX_P_FEAS     = 133;
    public final static int    LPX_P_INFEAS   = 134;
    public final static int    LPX_P_NOFEAS   = 135;

    /* status of dual basic solution: */
    public final static int    LPX_D_UNDEF    = 136;
    public final static int    LPX_D_FEAS     = 137;
    public final static int    LPX_D_INFEAS   = 138;
    public final static int    LPX_D_NOFEAS   = 139;

    /* status of the structural / auxiliary variable: */
    public final static int    LPX_BS         = 140;
    public final static int    LPX_NL         = 141;
    public final static int    LPX_NU         = 142;
    public final static int    LPX_NF         = 143;
    public final static int    LPX_NS         = 144;

    /* status of interior-point solution: */
    public final static int    LPX_T_UNDEF    = 150;
    public final static int    LPX_T_OPT      = 151;

    /* kind of the structural variable: */
    public final static int    LPX_CV         = 160;
    public final static int    LPX_IV         = 161;

    /* status of integer solution: */
    public final static int    LPX_I_UNDEF    = 170;
    public final static int    LPX_I_OPT      = 171;
    public final static int    LPX_I_FEAS     = 172;
    public final static int    LPX_I_NOFEAS   = 173;

    /* status codes reported by the routine lpx_get_status: */
    public final static int    LPX_OPT        = 180;
    public final static int    LPX_FEAS       = 181;

    public final static int    LPX_INFEAS     = 182;
    public final static int    LPX_NOFEAS     = 183;
    public final static int    LPX_UNBND      = 184;
    public final static int    LPX_UNDEF      = 185;

    /* exit codes returned by solver routines: */
    public final static int    LPX_E_OK       = 200;
    public final static int    LPX_E_EMPTY    = 201;
    public final static int    LPX_E_BADB     = 202;
    public final static int    LPX_E_INFEAS   = 203;
    public final static int    LPX_E_FAULT    = 204;
    public final static int    LPX_E_OBJLL    = 205;
    public final static int    LPX_E_OBJUL    = 206;
    public final static int    LPX_E_ITLIM    = 207;
    public final static int    LPX_E_TMLIM    = 208;
    public final static int    LPX_E_NOFEAS   = 209;
    public final static int    LPX_E_INSTAB   = 210;
    public final static int    LPX_E_SING     = 211;
    public final static int    LPX_E_NOCONV   = 212;
    public final static int    LPX_E_NOPFS    = 213;
    public final static int    LPX_E_NODFS    = 214;

    /* control parameter identifiers: */
    public final static int    LPX_K_MSGLEV   = 300;
    public final static int    LPX_K_SCALE    = 301;
    public final static int    LPX_K_DUAL     = 302;
    public final static int    LPX_K_PRICE    = 303;
    public final static int    LPX_K_RELAX    = 304;
    public final static int    LPX_K_TOLBND   = 305;
    public final static int    LPX_K_TOLDJ    = 306;
    public final static int    LPX_K_TOLPIV   = 307;
    public final static int    LPX_K_ROUND    = 308;
    public final static int    LPX_K_OBJLL    = 309;
    public final static int    LPX_K_OBJUL    = 310;
    public final static int    LPX_K_ITLIM    = 311;
    public final static int    LPX_K_ITCNT    = 312;
    public final static int    LPX_K_TMLIM    = 313;
    public final static int    LPX_K_OUTFRQ   = 314;
    public final static int    LPX_K_OUTDLY   = 315;
    public final static int    LPX_K_BRANCH   = 316;
    public final static int    LPX_K_BTRACK   = 317;
    public final static int    LPX_K_TOLINT   = 318;
    public final static int    LPX_K_TOLOBJ   = 319;
    public final static int    LPX_K_MPSINFO  = 320;
    public final static int    LPX_K_MPSOBJ   = 321;
    public final static int    LPX_K_MPSORIG  = 322;
    public final static int    LPX_K_MPSWIDE  = 323;
    public final static int    LPX_K_MPSFREE  = 324;
    public final static int    LPX_K_MPSSKIP  = 325;
    public final static int    LPX_K_LPTORIG  = 326;
    public final static int    LPX_K_PRESOL   = 327;
    public final static int    LPX_K_BINARIZE   = 328;


    static {
//        /*
//         * The SolverFactory takes care of loading all necessary libraries,
//         * so SHUT UP!
//         */
//		System.loadLibrary("glpkjni");
    }


    public GlpkSolver() {
    }

    /** Delete LP object (garbage collection). */

    protected native void finalize();

    /** Manually delete LP object (instead of garbage collection). */
    public native void deleteProb();

    public native void enablePrints(boolean enable);



    /** Install a callback for handling printing and fault messages */
    public void setHook(GlpkHookIFC hook) {
        m_hook = hook;
    }

    private void faultHook(String s) {
        if (m_hook != null)
            m_hook.fault(s);
    }

    private void printHook(String s) {
        if (m_hook != null)
            m_hook.print(s);
    }




    // Problem creating and modifying routines (manual p. 17)
    /** Assign (change) LP problem name */
    public native void setProbName(String name);
    /** Assign (change) the name of the objective funtion */
    public native void setObjName(String name);
    /** Set (change) optimization direction flag.<br>
     * Maximize: dir = GlpkSolver.LPX_MAX;<br>
     * Minimize: dir = GlpkSolver.LPX_MIN;
     */
    public native void setObjDir(int dir);
    /** Add nrs new rows (constraints) to problem object */
    public native void addRows(int nrs);
    /** Add new ncs columns (structural variables) to problem object*/
    public native void addCols(int ncs);
    /** Assign (change) row name */
    public native void setRowName(int i, String name);
    /** Assign (change) column name */
    public native void setColName(int j, String name);
    /** Set (change) row bounds */
    public native void setRowBnds(int i, int type, double lb, double ub);
    /** Set (change) column bounds */
    public native void setColBnds(int j, int type, double lb, double ub);
    /** Set (change) objective coefficient or constant term */
    public native void setObjCoef(int j, double coef);
    /** Set (replace) row i of the constraint matrix
     * @param len length of new row
     * @param ind indices of columns
     * @param val corresponding values (no zeros allowed) */
    public native void setMatRow(int i, int len, int[] ind, double[] val);
    /** Set (replace) column j of the constraint matrix
     * @param len length of new column
     * @param ind indices of rows
     * @param val corresponding values (no zeros allowed) */
    public native void setMatCol(int j, int len, int[] ind, double[] val);
    /** Load (replace) the whole constraint matrix
     * @param ne number of elements in matrix
     * @param ia row counter
     * @param ja column counter
     * @param ar value of matrix element (no zeros allowed) */
    public native void loadMatrix(int ne, int[] ia, int[] ja, double[] ar);
    /** Delete nrs rows from problem object
     * @param num contains ordinal row numbers */
    public native void delRows(int nrs, int[] num);
    /** Delete ncs columns from problem object
     * @param num contains ordinal column numbers */
    public native void delCols(int ncs, int[] num);





    // Problem retrieving routines (manual p. 23)
    /** Retrieve problem name */
    public native String getProbName();
    /** Retrieve objective function name */
    public native String getObjName();
    /** Retrieve optimization direction flag */
    public native int getObjDir();
    /** Retrieve number of rows */
    public native int getNumRows();
    /** Retrieve number of columns */
    public native int getNumCols();
    /** Retrieve name of row i*/
    public native String getRowName(int i);
    /** Retrieve name of column j */
    public native String getColName(int j);
    /** Retrieve type of row i*/
    public native int getRowType(int i);
    /** Retrieve lower bound of row i */
    public native double getRowLb(int i);
    /** Retrieve upper bound of row i */
    public native double getRowUb(int i);
    /** Retrieve type of column j*/
    public native int getColType(int j);
    /** Retrieve lower bound of column j */
    public native double getColLb(int j);
    /** Retrieve upper bound of column j */
    public native double getColUb(int j);
    /** Retrieve objective function coefficient or constant term */
    public native double getObjCoef(int j);
    /** Retrieve number of constraint coefficients (non-zero
     elements in constraint matrix)*/
    public native int getNumNz();
    /** Retrieve row i of the constraint matrix.
     *  @param ind contains column indices
     *  @param val contains the corresponding values*/
    public native int getMatRow(int i, int[] ind, double[] val);
    /** Retrieve column j of the constraint matrix.
     *  @param ind contains column indices
     *  @param val contains the corresponding values*/
    public native int getMatCol(int j, int[] ind, double[] val);





    // Problem scaling routines (manual p. 28)
    /** Scale problem data */
    public native void scaleProb();
    /** Unscale problem data */
    public native void unscaleProb();




    // LP Basis constructing routines (manual p. 29)
    /** Construct standard initial LP basis */
    public native void stdBasis();
    /** Construct advanced initial LP basis */
    public native void advBasis();
    /** Set (change) status of row i */
    public native void setRowStat(int i, int stat);
    /** Set (change) status of column j */
    public native void setColStat(int j, int stat);




    // Simplex method routine (manual p. 31)
    /** Solve LP problem using the simplex method */
    public native int simplex();



    // Basic solution retrieving routines (manual p. 33)
    /** Retrieve generic status of the basic solution */
    public native int getStatus();
    /** Retrieve primal status of the basic solution */
    public native int getPrimStat();
    /** Retrieve dual status of the basic solution */
    public native int getDualStat();
    /** Retrieve current value of the objective function */
    public native double getObjVal();
    /** Retrieve status of row i */
    public native int getRowStat(int i);
    /** Retrieve primal value of auxiliary variable of row i */
    public native double getRowPrim(int i);
    /** Retrieve dual value (reduced cost)
     * of auxiliary variable associated with row i */
    public native double getRowDual(int i);
    /** Retrieve status of column j */
    public native int getColStat(int j);
    /** Retrieve primal value of the structural variable (column) j */
    public native double getColPrim(int j);
    /** Retrieve dual value (reduced cost)
     * of the structural variable (column) j */
    public native double getColDual(int j);
    /** Retrieve non-basic variable which causes unboundness */
    public native int getRayInfo();
    /** Check Karush-Kuhn-Tucker conditions */
    public native void checkKkt(int scaled, GlpkSolverKktConditions kkt);




    // LP basis and simplex table routines (manual p. 40)
    /** Warm up / refresh LP basis */
    public native int warmUp();
    /** Compute row of the simplex table */
    public native int evalTabRow(int k, int[] ind, double[] val);
    /** Compute column  of the simplex table */
    public native int evalTabCol(int k, int[] ind, double[] val);
    /** Transform explicitly specified row */
    public native int transformRow(int len, int[] ind, double[] val);
    /** Transform explicitly specified column */
    public native int transformCol(int len, int[] ind, double[] val);
    /** Perform primal ratio test */
    public native int primRatioTest(int len, int[] ind, double[] val,
                                    int how, double tol);
    /** Perform dual ratio test */
    public native int dualRatioTest(int len, int[] ind, double[] val,
                                    int how, double tol);




    // Interior point method routines (manual p. 46)
    /** Solve LP problem using the primal-dual
     * interior point method*/
    public native int interior();
    /** Retrieve status of interior-point solution */
    public native int iptStatus();
    /** Retrieve objective value from interior-point solution*/
    public native double iptObjVal();
    /** Retrieve primal value of auxiliary variable of row i*/
    public native double iptRowPrim(int i);
    /** retrieve dual value (reduced cost)
     * of auxiliary variable associated with row i*/
    public native double iptRowDual(int i);
    /** Retrieve primal value of structural variable of column j*/
    public native double iptColPrim(int j);
    /** Retrieve dual value (reduced cost)
     * of auxiliary variable associated with column j*/
    public native double iptColDual(int j);




    // MIP routines (manual p. 49)
    /** Set (change) problem class
     * @param klass LPX_LP: LP problem, LPX_MIP: MIP problem */
    public native void setClss(int klass);
    /** Retrieve problem class */
    public native int getClss();
    /** Set (change) kind of column (structural variable) j
     * @param kind LPX_CV: continuous, LPX_IV: integer */
    public native void setColKind(int j, int kind);
    /** Retrieve kind of column j */
    public native int getColKind(int j);
    /** Retrieve number of integer columns */
    public native int getNumInt();
    /** Retrieve number of binary columns */
    public native int getNumBin();
    /** Solve MIP problem using the branch-and-bound method */
    public native int integer();
    /** Retrieve status of MIP solution */
    public native int mipStatus();
    /** Retrieve value of the objective function*/
    public native double mipObjVal();
    /** Retrieve value of the auxiliary variable
     * associated with row i*/
    public native double mipRowVal(int i);
    /** Retrieve value of the structural variable
     * associated with column j*/
    public native double mipColVal(int j);




    // Control parameters and statistics routines (manual p. 53)
    /** Reset control parameters to default values */
    public native void resetParms();
    /** Set (change) integer control parameter */
    public native void setIntParm(int parm, int val);
    /** Query integer control parameter */
    public native int getIntParm(int parm);
    /** Set (change) real control parameter */
    public native void setRealParm(int parm, double val);
    /** Query real control parameter */
    public native double getRealParm(int parm);




    // Utility routines (manual p. 57)
    /** Read problem data in fixed MPS format */
    public native static GlpkSolver readMps(String fname);
    /** Write problem data in fixed MPS format */
    public native boolean writeMps(String fname);
    /** Read LP basis in fixed MPS format */
    public native boolean readBas(String fname);
    /** Write LP basis in fixed MPS format */
    public native boolean writeBas(String fname);
    /** Read problem data in free MPS format */
    public native static GlpkSolver readFreemps(String fname);
    /** Write problem data in free MPS format */
    public native boolean writeFreemps(String fname);
    /** Read problem data in CPLEX LP format
     * Example: GlpkSolver solver = solver.readFreemps("plan.lp"); */
    public native static GlpkSolver readCpxlp(String fname);
    /** Write problem data in CPLEX LP format */
    public native boolean writeCpxlp(String fname);
    /** Read model written in GNU MathProg modeling language */
    public native static GlpkSolver readModel(String model, String data, String output);
    /** Write problem data in plain text format */
    public native boolean printProb(String fname);
    /** Write basic solution in printable format */
    public native boolean printSol(String fname);
    /** Write bounds sensitivity information */
    public native boolean printSensBnds(String fname);
    /** Write interior point solution in printable format */
    public native boolean printIps(String fname);
    /** Write MIP solution in printable format */
    public native boolean printMip(String fname);

}
