import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.GUI;
import ij.gui.Plot;
import ij.gui.PlotWindow;
import ij.gui.Roi;
import ij.measure.ResultsTable;
import ij.plugin.frame.PlugInFrame;
import ij.plugin.frame.RoiManager;
import imageJ.plugins.PoorMan3DReg_;
import ij.plugin.Grid;

import celldetection._3D_objects_counter;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.NumberFormatter;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.*;

public class menu extends PlugInFrame implements ActionListener {

    Panel panel;
    // JPanel panel;
    ResultsTable results = ResultsTable.getActiveTable();
    Window crm_window = WindowManager.getFrame("Custom RoiManager");
    Frame customRoiManager;

    menu(){
        super("Menu");

        ImageJ ij = IJ.getInstance();
        addKeyListener(ij);
        WindowManager.addWindow(this);

        panel = new Panel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        Font font = new Font("Verdana", Font.BOLD, 16);

        // panel.setSize(600, 400);
        

        panel.add(Box.createHorizontalStrut(200));
        // panel.setSize(700, 600);

        addButton("Make a Copy", false);
        addButton("Registration", false);
        
        Label cellDetectionLabel = new Label("Cell Detection", Label.CENTER);
        cellDetectionLabel.setFont(font);
        panel.add(cellDetectionLabel);
        addButton("Threshold Setting", false);
        addButton("Custom RoiManager", false);
        addButton("ROI Manager", false);
        addButton("Save ROI set as...", true);
        addButton("Input ROI set", true);
        addButton("Apply ROI to video", true);

        Label showResults = new Label("Show results", Label.CENTER);
        showResults.setFont(font);
        panel.add(showResults);
        addButton("Set Measurements", false);
        addButton("Show Results Table", false);
        addButton("Save Results", false);

        add(panel);

        // if (this.crm_window == null){
        //     this.customRoiManager = new custom_roiManager();
        // }
        // else{
        //     this.customRoiManager = WindowManager.getFrame("Custom RoiManager");
        // }

        pack();
        GUI.center(this);
        setVisible(true);

    }

    void addButton(String label, boolean isDisabled) {
        Button newButton = new Button(label);
        newButton.setMaximumSize(new Dimension(200, 350));
        newButton.addActionListener(this);
        newButton.addKeyListener(IJ.getInstance());
        newButton.setEnabled(!isDisabled);
        panel.add(newButton); 
    } 

    public static void createCellRoi(ResultsTable results, RoiManager rm){

        //if (crm == null){crm = new custom_roiManager();}
        System.out.println(rm);
        //Vars
        double x;
        double y;
        double width;
        double height;
        int cornerDiameter = 20;
        System.out.println("CREATE CELL ROI");
        System.out.println(results);
        //ResultsTable results = ResultsTable.getResultsTable();

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
        
        rm.addRoi(roi);
        rm.runCommand("Show All");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        _3D_objects_counter counter = new _3D_objects_counter();
        PoorMan3DReg_ reg = new PoorMan3DReg_();


       

        if (command == "Make a Copy") {
            // ImagePlus img = WindowManager.getImage("post-reg");
            IJ.run("Duplicate...","title=Copy");
        } 
        else if (command == "Registration") {

            int imageCount = WindowManager.getImageCount();
            int[] idList = WindowManager.getIDList();

            for (int id : idList) {
            ImagePlus img = WindowManager.getImage(id);
            WindowManager.setTempCurrentImage(img);
            reg.run("run");

            IJ.run("Z Project...", "projection=[Max Intensity] title=Max");
            IJ.run("Enhance Contrast", "saturated=4 normalize");
            IJ.run("Duplicate...","title=post-reg");

            }
        }
        else if (command == "Threshold Setting") {
            counter.run("run");
            this.results = ResultsTable.getActiveTable();
        }
        else if (command == "Custom RoiManager") {
            Window crm_window = WindowManager.getFrame("Custom RoiManager");
            if (crm_window == null){
                this.customRoiManager = new custom_roiManager();

                // custom_roiManager.createCellRoi(this.results, this.customRoiManager);

                    //Add Roi to RoiManager
                    //this.customRoiManager.addRoi(roi);
                }
            else {
                // bring to front
                WindowManager.toFront(crm_window);
            }
        }
            
        
        else if (command == "ROI Manager") {
            RoiManager rm = RoiManager.getInstance();
            // Window[] wins = WindowManager.getAllNonImageWindows();
            // for (Window w : wins){
            //     System.out.println(w);
            // }
            if (rm == null){

                // double x;
                // double y;
                // double width;
                // double height;
                // int cornerDiameter = 20;

                rm = RoiManager.getRoiManager();
                
                // double[] widths = this.results.getColumn("B-width");
                // double[] heights = this.results.getColumn("B-height");
                // double[] xs = this.results.getColumn("X");
                // double[] ys = this.results.getColumn("Y");       
                System.out.println(this.customRoiManager);
                // this.results = ResultsTable.getActiveTable();
                this.createCellRoi(this.results, rm);
                // for(int i = 0; i < widths.length; i++) {

                // // This is used to make sure we have x and y at the center of the detected region
                // width = widths[i];
                // height = heights[i];
                // x = xs[i] - width/2 ;
                // y = ys[i] - height/2;

                // //Create ROI with Input: int x, int y, int width, int height, int cornerDiameter
                // Roi roi = new Roi((int)x, (int)y, (int)width, (int)height, cornerDiameter);

                // //Add Roi to RoiManager
                // rm.addRoi(roi);
                // rm.runCommand("Show All");
            
                }  
                else {
                WindowManager.toFront(rm);
                rm.runCommand("Show All");
                }   
        }   
         
        else if (command == "Save ROI set as...") {

            
        }
        else if (command == "Input ROI set") {
            
        }
        else if (command == "Apply ROI to video") {

        }
        else if (command == "Set Measurements") {
            // Buggy
            IJ.run("Measure");
        }
        else if (command == "Show Results Table"){

            if (WindowManager.getActiveTable() == null){
                ResultsTable results = ResultsTable.getResultsTable();
                results.show("Results");
                //System.out.println();
            }
        }
        else if (command == "Save Results") {
            if (WindowManager.getActiveTable() == null) {
                IJ.showMessage("No results table found");
                return;
            }
            ResultsTable results = ResultsTable.getResultsTable();
            JFileChooser fileChooser = new JFileChooser();
            FileFilter csvFilter = new FileNameExtensionFilter("CSV file", ".csv");
            fileChooser.setFileFilter(csvFilter);
            fileChooser.setDialogTitle("Select Folder");
            fileChooser.setAcceptAllFileFilterUsed(false);

            int returnVal = fileChooser.showSaveDialog(fileChooser);
            
            if(returnVal == JFileChooser.APPROVE_OPTION) {
                String fileName = fileChooser.getSelectedFile().getAbsolutePath();
                if (fileName.endsWith(".csv")) {
                    results.save(fileName);
                }
                else {
                    results.save(fileName + ".csv");
                }
            }
        }
    }
}
