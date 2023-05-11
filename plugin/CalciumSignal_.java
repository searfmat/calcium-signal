import celldetection._3D_objects_counter;
import ij.*;
import ij.gui.*;
import ij.measure.ResultsTable;
import ij.plugin.PlugIn;
import imageJ.plugins.PoorMan3DReg_;

import java.awt.*;
import java.io.IOException;

public class CalciumSignal_ implements PlugIn {
    // private final String EDGE_DATA_PATH = "plugins/CalciumSignal/edge_data";

    public void run(String arg) {
        //runRoiManager();
        runMenu();
    }
    void runRoiManager(){
        // NOT BEING USED
        //Creates RoiManager
        custom_roiManager crm = new custom_roiManager();
    
        //Vars
        double x;
        double y;
        double width;
        double height;
        int cornerDiameter = 20;

        ResultsTable results = ResultsTable.getResultsTable();

        double[] widths = results.getColumn("B-width");
        double[] heights = results.getColumn("B-height");
        double[] xs = results.getColumn("X");
        double[] ys = results.getColumn("Y");

        for(int i = 0; i < widths.length; i++) {

            // This is used to make sure we have x and y at the center of the detected region
            width = widths[i];
            height = heights[i];
            x = xs[i] - width/2;
            y = ys[i] - height/2;

            //Create ROI with Input: int x, int y, int width, int height, int cornerDiameter
            Roi roi = new Roi((int)x, (int)y, (int)width, (int)height, cornerDiameter);

            //Add Roi to RoiManager
            crm.addRoi(roi);
        }

    }

    void runMenu(){
       menu menu = new menu();
       menu.setLocation(100,200);
    }

    public static void main(String[] args) {
        // sanity check
        // System.out.println("Hello, world!");
    }
}

