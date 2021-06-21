

import javax.swing.*;

import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.layout.mxParallelEdgeLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;

import java.awt.*;
import java.util.*;


public class MaxFlow extends JFrame {

    private LinkedList<Integer> list_1;


    public static ArrayList<Object> listNodes = new ArrayList<>();
    public static ArrayList<Object> listEdges = new ArrayList<>();
    private static mxGraph graph;
    private static Map<String, Object> edgeStyle;
    public static boolean pauseVariable = true;
    public String pathDetails = "";
    private static JLabel lbl;
    private static JLabel lb2;
    private static Scanner sc;

    /**
     *
     */

//constructor
    public MaxFlow(int[][] matrix) {
        super("MAX, FLOW!");

        graph = new mxGraph();
        Object parent = graph.getDefaultParent();

        graph.getModel().beginUpdate();
        try {

            int x = 0;
            int y = 100;
            for (int i = 0; i < matrix.length; i++) {
                Object v15 = graph.insertVertex(parent, null, i, x, y, 100,
                        20);
                listNodes.add(v15);
                x += 10;
                y -= 10;
            }


            for (int i = 0; i < matrix.length; i++) {
                for (int j = 0; j < matrix[i].length; j++) {
                    if (matrix[i][j] != 0) {
                        Object v10 = graph.insertEdge(parent, null, matrix[i][j], listNodes.get(i), listNodes.get(j));
                        listEdges.add(v10);
                    }
                }
                System.out.println("");
            }


            new mxCircleLayout(graph).execute(graph.getDefaultParent());
            new mxParallelEdgeLayout(graph).execute(graph.getDefaultParent());

            edgeStyle = new HashMap<String, Object>();

            edgeStyle.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_CONNECTOR);
            edgeStyle.put(mxConstants.STYLE_ENDARROW, mxConstants.ARROW_CLASSIC);


            edgeStyle.put(mxConstants.STYLE_STROKECOLOR, "#000000");
            edgeStyle.put(mxConstants.STYLE_FONTCOLOR, "#000000");


            mxStylesheet stylesheet = new mxStylesheet();
            stylesheet.setDefaultEdgeStyle(edgeStyle);
            graph.setStylesheet(stylesheet);


        } finally {
            graph.getModel().endUpdate();
        }

        mxGraphComponent graphComponent = new mxGraphComponent(graph);

        add(graphComponent, BorderLayout.CENTER);


        JPanel panel_1 = new JPanel();
        panel_1.setSize(100, 100);


        lbl = new JLabel("");
        panel_1.add(lbl);

        lb2 = new JLabel("");
        panel_1.add(lb2);


        add(panel_1, BorderLayout.SOUTH);


    }

    public MaxFlow() {

    }


    public static void main(String[] args) {

        MaxFlow t1 = new MaxFlow();

        System.out.println("Do you want to enter a matrix manually or generate a matrix randomly to construct a graph");
        System.out.println("Press 1 to Enter Matrix manually");
        System.out.println("Press 2 to generate Matrix Automatically");
        System.out.println("Press 3 to use a pre-define Matrix");

        sc = new Scanner(System.in);
        int input;
        do {

            input = sc.nextInt();
            if (!(input == 1 || input == 2 || input==3)) {
                System.out.println("Please Enter a valid number");
            }
        } while (!(input == 1 || input == 2 || input==3));

        System.out.println(input);


        int[][] capacity = t1.getMatrixPreference(input);
        sc.close();
        int[][] flow = new int[capacity.length][capacity.length];
        int[][] residual = new int[capacity.length][capacity.length];

        t1.displayMatrix(capacity);
        t1.initialiseResidual(capacity, residual);

        MaxFlow frame = new MaxFlow(capacity);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 900);


        frame.setVisible(true);


        while (t1.BFS(residual, 0, capacity.length - 1, capacity.length)) {
            t1.generateResidual(residual, capacity, flow);
        }

        System.out.println("The max flow is " + t1.findMaximumFlow(flow, 0));
        lb2.setText("The Max flow is: " + t1.findMaximumFlow(flow, 0));

    }
// initially initialising the residual network
    public void initialiseResidual(int capacity[][], int residual[][]) {
        for (int i = 0; i < capacity.length; i++) {
            for (int j = 0; j < capacity[i].length; j++) {
                residual[i][j] = capacity[i][j];
            }
        }
    }
//Breadth first search
    public boolean BFS(int[][] residual, int source, int sink, int noNodes) {

        int[] parent = new int[noNodes];
        boolean[] visited = new boolean[noNodes];

        LinkedList<Integer> que = new LinkedList<>();
        que.add(source);

        boolean hasReached = false;

        while (!que.isEmpty()) {
            System.out.println(que);
            visited[0] = true;
            int x = que.pop();
            if (x == sink) {
                hasReached = true;
            }

            parent[source] = -1;
            for (int i = 0; i < noNodes; i++) {

                if (residual[x][i] > 0 && (!visited[i])) {
                    parent[i] = x;
                    que.add(i);
                    visited[i] = true;

                }
            }
        }

        if (hasReached) {
            generatePath(sink, parent);
        }
        return hasReached;
    }
//generating the residual graph
    public void generateResidual(int[][] residual, int[][] capacity, int[][] flow) {
        int k;
        int j;
        int min = 999;

        for (int i = 0; i < list_1.size() - 1; i++) {
            j = list_1.get(i);
            k = list_1.get(i + 1);

            if (residual[j][k] < min) {
                min = residual[j][k];
            }

        }
        for (int i = 0; i < list_1.size() - 1; i++) {
            j = list_1.get(i);
            k = list_1.get(i + 1);
            residual[j][k] -= min;
            if (flow[k][j] > 0) {
                flow[k][j] -= min;
                residual[k][j] += min;
            } else {
                flow[j][k] += min;
                residual[k][j] += min;
            }

        }
        System.out.println("min is " + min);
        list_1 = new LinkedList<>();

    }

//finding the path that was found
    public void generatePath(int sink, int[] parent) {
        int x = sink;
        list_1 = new LinkedList<Integer>();
        list_1.push(x);

        while (x != -1) {

            x = parent[x];
            if (x != -1) {
                list_1.push(x);

            }


        }

//changing color of the visualized graph
        for (int i = 0; i < list_1.size(); i++) {
            graph.setCellStyles(mxConstants.STYLE_FILLCOLOR, "#AAB7B8", new Object[]{listNodes.get(list_1.get(i))});
            if (i < list_1.size() - 1) {
                try {
                    graph.setCellStyles(mxConstants.STYLE_STROKECOLOR, "#FF0000", new Object[]{(mxCell) (graph.getEdgesBetween(listNodes.get(list_1.get(i)), listNodes.get(list_1.get(i + 1)), true)[0])});

                } catch (ArrayIndexOutOfBoundsException ex) {
                    graph.setCellStyles(mxConstants.STYLE_STROKECOLOR, "#FF0000", new Object[]{(mxCell) (graph.getEdgesBetween(listNodes.get(list_1.get(i + 1)), listNodes.get(list_1.get(i)), true)[0])});

                }
            }


        }
        pathDetails += list_1 + "<br>";
        System.out.println("The list is " + list_1);

        String end_html = "</html>";
        String start_html = "<html>The Augmented paths are : <br>";

        lbl.setText(start_html + pathDetails + end_html);


    }
//finding flow out from source
    public int findMaximumFlow(int flow[][], int source) {
        int sum = 0;
        for (int i = 0; i < flow[source].length; i++) {
            sum += flow[source][i];
        }
        return sum;

    }
//to generate a random matrix
    public int[][] generateRandomMatrix() {
        int a = generateNumbers(12, 6);
        int matrix[][] = new int[a][a];

        for (int i = 0; i < matrix.length - 1; i++) {
            for (int j = 1; j < matrix[i].length; j++) {
                if (i == j) {
                    matrix[i][j] = 0;
                } else {
                    matrix[i][j] = generateNumbers(20, 5);
                }
            }
        }
        matrix[0][matrix.length - 1] = 0;


        return matrix;
    }
//generate a number
    public static int generateNumbers(int max, int min) {

        int a = max - min;
        int b = max - ((int) (Math.random() * a) + 1);
        return b;
    }
//display the matrix if needed
    public void displayMatrix(int[][] capacity) {
        for (int i = 0; i < capacity.length; i++) {
            for (int j = 0; j < capacity[i].length; j++) {
                System.out.print(capacity[i][j] + " ");
            }
            System.out.println("");
        }
    }

//get user selection
    public int[][] getMatrixPreference(int id) {


        if (id == 1) {
            System.out.println(" Enter Size of matrix");
            int size = sc.nextInt();

            int[][] matrix_1 = new int[size][size];
            System.out.println("Enter the values pressing enter for each value");
            for (int i = 0; i < matrix_1.length; i++) {
                System.out.println("Considering row no " + i);
                for (int j = 0; j < matrix_1[i].length; j++) {
                    System.out.println("Enter value for column no " + j);
                    matrix_1[i][j] = sc.nextInt();
                }
            }

            return matrix_1;
        } else if (id == 2) {
            return generateRandomMatrix();
        }else if(id==3){
            int[][] matrixNew=new int[][]{
                    {0,5,10,15,0,0,0,0},
                    {0,0,0,4,8,0,6,0},
                    {0,4,0,0,15,9,0,0},
                    {0,0,0,0,0,0,16,0},
                    {0,0,0,0,0,0,15,10},
                    {0,0,0,0,15,0,0,10},
                    {0,6,0,0,0,0,0,10},
                    {0,0,0,0,0,0,0,0},

            };
            return matrixNew;
        }


        return generateRandomMatrix();

    }
}