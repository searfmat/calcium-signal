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

    menu(){
        super("Menu");

        ImageJ ij = IJ.getInstance();
        addKeyListener(ij);
        WindowManager.addWindow(this);

        panel = new Panel();
        // panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // panel.setSize(600, 400);
        

        // panel.add(Box.createHorizontalStrut(200));
        // panel.setSize(700, 600);

        addButton("Make a Copy", false);
        addButton("Registration", false);
        
        Label cellDetectionLabel = new Label("Cell Detection", Label.CENTER);
        panel.add(cellDetectionLabel);
        addButton("Threshold Setting", false);
        addButton("Custom RoiManager", false);

        addButton("ROI Manager", true);

        addButton("Save ROI set as...", true);
        addButton("Input ROI set", true);
        addButton("Apply ROI to video", true);

        Label showResults = new Label("Show results", Label.CENTER);
        panel.add(showResults);
        addButton("Set Measurements", true);
        addButton("Save Results", true);

        add(panel);

        pack();
        GUI.center(this);
        setVisible(true);

    }

    void addButton(String label, boolean isDisabled) {
        Button b = new Button(label);
        b.setMaximumSize(new Dimension(150, 300));
        b.addActionListener(this);
        b.addKeyListener(IJ.getInstance());
        if (isDisabled) {b.setEnabled(isDisabled);}
        panel.add(b);        
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
            reg.run("");
        }
        else if (command == "Threshold Setting") {
            counter.run("");
        }
        else if (command == "Custom RoiManager") {
            custom_roiManager crm = new custom_roiManager();
        }
        else if (command == "ROI Manager") {
            RoiManager drm = new RoiManager();
        }   
        else if (command == "Save ROI set as...") {

        }
        else if (command == "Input ROI set") {
            
        }
        else if (command == "Apply ROI to video") {

        }
        else if (command == "Set Measurments") {

        }
        else if (command ==  "Save Results") {

        }
    }
}
