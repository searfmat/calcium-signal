import celldetection._3D_objects_counter;
import ij.*;
import ij.gui.*;
import ij.measure.ResultsTable;
import ij.plugin.PlugIn;
import imageJ.plugins.PoorMan3DReg_;

import java.awt.*;
import java.io.IOException;

public class CalciumSignal_ implements PlugIn {
    private final String EDGE_DATA_PATH = "plugins/CalciumSignal/edge_data";

    public void run(String arg) {

        IJ.showMessage("Calcium Signal", "Welcome to the Calcium Signal plugin!");
        // Frame roiWindow = WindowManager.getCurrentWindow();

        /*
        -- IMAGE REGISTRATION AND EDGE DETECTION --
         */
        int imageCount = WindowManager.getImageCount();
        int[] idList = WindowManager.getIDList();
        PoorMan3DReg_ reg = new PoorMan3DReg_();
        _3D_objects_counter counter = new _3D_objects_counter();

        if (imageCount < 1) {
            IJ.showMessage("Calcium Signal", "No image found.");
            return;
        }

        for (int id : idList) {
            ImagePlus img = WindowManager.getImage(id);
            WindowManager.setTempCurrentImage(img);
            reg.run(arg);

            IJ.run("Z Project...", "projection=[Max Intensity] title=Max");
            IJ.run("Enhance Contrast", "saturated=4 normalize");
            IJ.run("Duplicate...","title=post-reg");

            counter.run(arg);
        }

        /*
        -- ROI MANAGER --
         */

        //Gets active table and saves
        // String path =  "../src/main/java/Sample.csv";
        // ResultsTable results = ResultsTable.getResultsTable();

        // try {
        //     results.saveAs(path);
        // } catch (IOException e) {
        //     e.printStackTrace();
        // }
        // WindowManager.removeWindow(WindowManager.getFrontWindow());
        // WindowManager.removeWindow(WindowManager.getFrontWindow());
        // WindowManager.toFront(roiWindow);

        //runRoiManager();
        runMenu();
    }
    void runRoiManager(){

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
            x = xs[i] - width/2 ;
            y = ys[i] - height/2;

            //Create ROI with Input: int x, int y, int width, int height, int cornerDiameter
            Roi roi = new Roi((int)x, (int)y, (int)width, (int)height, cornerDiameter);

            //Add Roi to RoiManager
            crm.addRoi(roi);
        }

    }

    void runMenu(){
       menu menu = new menu();
    }

    public static void main(String[] args) {
        // sanity check
        // System.out.println("Hello, world!");
    }
}

