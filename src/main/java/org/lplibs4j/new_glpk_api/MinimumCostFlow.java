package org.lplibs4j.new_glpk_api;

import org.gnu.glpk.*;


/**
 * Example how to formulate and solve the Minimum-Cost-Flow (MCF) problem with Java interface to GLPK.
 * </p>
 * User: pmar@ppolabs.com
 * Date: 12/15/13
 * Time: 4:42 PM
 */
public class MinimumCostFlow {

    /**
     * Main method
     * @param args Command line arguments
     */
    public static void main(String[] args) {

        glp_prob lp;
        glp_arc arc;

        _glp_java_arc_data adata;
        _glp_java_vertex_data vdata;

        try {

            glp_graph graph = GLPK.glp_create_graph(GLPKConstants.GLP_JAVA_V_SIZE, GLPKConstants.GLP_JAVA_A_SIZE);
            GLPK.glp_set_graph_name(graph, MinimumCostFlow.class.getName());

            int ret = GLPK.glp_add_vertices(graph, 9);

            GLPK.glp_set_vertex_name(graph, 1, "v1");
            GLPK.glp_set_vertex_name(graph, 2, "v2");
            GLPK.glp_set_vertex_name(graph, 3, "v3");
            GLPK.glp_set_vertex_name(graph, 4, "v4");
            GLPK.glp_set_vertex_name(graph, 5, "v5");
            GLPK.glp_set_vertex_name(graph, 6, "v6");
            GLPK.glp_set_vertex_name(graph, 7, "v7");
            GLPK.glp_set_vertex_name(graph, 8, "v8");
            GLPK.glp_set_vertex_name(graph, 9, "v9");

            vdata = GLPK.glp_java_vertex_data_get(graph, 1);
            vdata.setRhs(20);
            vdata = GLPK.glp_java_vertex_data_get(graph, 9);
            vdata.setRhs(-20);

            arc = GLPK.glp_add_arc(graph, 1, 2);
            adata = GLPK.glp_java_arc_get_data(arc);
            adata.setLow(0); adata.setCap(14); adata.setCost(0);

            arc = GLPK.glp_add_arc(graph, 1, 4);
            adata = GLPK.glp_java_arc_get_data(arc);
            adata.setLow(0); adata.setCap(23); adata.setCost(0);

            arc = GLPK.glp_add_arc(graph, 2, 4);
            adata = GLPK.glp_java_arc_get_data(arc);
            adata.setLow(0); adata.setCap(9); adata.setCost(3);

            arc = GLPK.glp_add_arc(graph, 2, 3);
            adata = GLPK.glp_java_arc_get_data(arc);
            adata.setLow(0); adata.setCap(10); adata.setCost(2);
            
            arc = GLPK.glp_add_arc(graph, 4, 5);
            adata = GLPK.glp_java_arc_get_data(arc);
            adata.setLow(0); adata.setCap(26); adata.setCost(0);

            arc = GLPK.glp_add_arc(graph, 5, 2);
            adata = GLPK.glp_java_arc_get_data(arc);
            adata.setLow(0); adata.setCap(11); adata.setCost(1);

            arc = GLPK.glp_add_arc(graph, 3, 8);
            adata = GLPK.glp_java_arc_get_data(arc);
            adata.setLow(0); adata.setCap(18); adata.setCost(0);

            arc = GLPK.glp_add_arc(graph, 3, 5);
            adata = GLPK.glp_java_arc_get_data(arc);
            adata.setLow(2); adata.setCap(12); adata.setCost(1);

            arc = GLPK.glp_add_arc(graph, 5, 6);
            adata = GLPK.glp_java_arc_get_data(arc);
            adata.setLow(0); adata.setCap(25); adata.setCost(5);

            arc = GLPK.glp_add_arc(graph, 5, 7);
            adata = GLPK.glp_java_arc_get_data(arc);
            adata.setLow(0); adata.setCap(4); adata.setCost(7);

            arc = GLPK.glp_add_arc(graph, 6, 7);
            adata = GLPK.glp_java_arc_get_data(arc);
            adata.setLow(0); adata.setCap(7); adata.setCost(0);

            arc = GLPK.glp_add_arc(graph, 6, 8);
            adata = GLPK.glp_java_arc_get_data(arc);
            adata.setLow(4); adata.setCap(8); adata.setCost(0);

            arc = GLPK.glp_add_arc(graph, 8, 9);
            adata = GLPK.glp_java_arc_get_data(arc);
            adata.setLow(0); adata.setCap(20); adata.setCost(9);

            arc = GLPK.glp_add_arc(graph, 7, 9);
            adata = GLPK.glp_java_arc_get_data(arc);
            adata.setLow(0); adata.setCap(15); adata.setCost(3);

            // Write formulated problem in DIMACS CNF format 
            GLPK.glp_write_mincost(graph,
                    GLPKConstants.GLP_JAVA_V_RHS,
                    GLPKConstants.GLP_JAVA_A_LOW,
                    GLPKConstants.GLP_JAVA_A_CAP,
                    GLPKConstants.GLP_JAVA_A_COST,
                    "mincost.dimacs");

            lp = GLPK.glp_create_prob();

            // Write lp formulation in symbolic notation to file
            GLPK.glp_mincost_lp(lp, graph,
                    GLPKConstants.GLP_ON, // use symbolic names
                    GLPKConstants.GLP_JAVA_V_RHS,
                    GLPKConstants.GLP_JAVA_A_LOW,
                    GLPKConstants.GLP_JAVA_A_CAP,
                    GLPKConstants.GLP_JAVA_A_COST);

            // Solve (model) network
            glp_smcp parm = new glp_smcp();
            GLPK.glp_init_smcp(parm);
            ret = GLPK.glp_simplex(lp, parm);

            // Retrieve solution
            if (ret == 0) {
                write_lp_solution(lp);
            }

            // Write mincost lp formulation and free memory
            GLPK.glp_delete_graph(graph);
            GLPK.glp_write_lp(lp, null, "mincost.lp");
            GLPK.glp_delete_prob(lp);

        } catch (GlpkException ex) {
            System.out.println(ex.getMessage());
            System.exit(1);
        }

    }

    /**
     * write simplex solution
     *
     * @param lp problem
     */
    private static void write_lp_solution(glp_prob lp) {

        String name;
        double val;

        name = GLPK.glp_get_obj_name(lp);
        val = GLPK.glp_get_obj_val(lp);

        System.out.println(name + " = " + val);

        // Counter starts from 1!!!
        for (int i = 1; i <= GLPK.glp_get_num_cols(lp); i++) {
            name = GLPK.glp_get_col_name(lp, i);
            val = GLPK.glp_get_col_prim(lp, i);
            System.out.println(name + " = " + val);
        }
    }

}