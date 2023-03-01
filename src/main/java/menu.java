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
import ij.plugin.Grid;

import javax.swing.*;
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

    // Panel panel;
    JPanel panel;



    menu(){
        super("Menu!");

        ImageJ ij = IJ.getInstance();
        addKeyListener(ij);
        WindowManager.addWindow(this);

        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.add(Box.createHorizontalStrut(100));

        addButton("Make a Copy");
        addButton("Registration");
        
        // JLabel cellDetetionLabel = new JLabel("Cell Detection");
        // panel.add(cellDetetionLabel);
        addButton("Threshold Setting");
        addButton("Custom RoiManager");
        addButton("ROI Manager");
        addButton("Save ROI set as...");
        addButton("Input ROI set");
        addButton("Apply ROI to video");

        // JLabel showResults = new JLabel("Results");
        // panel.add(showResults);
        addButton("Set Measurments");
        addButton("Save Results");

        add(panel);

        pack();
        GUI.center(this);
        setVisible(true);

    }

    void addButton(String label) {
        System.out.println("addButton in menu class");
        Button b = new Button(label);
        b.addActionListener(this);
        b.addKeyListener(IJ.getInstance());
        panel.add(b);
    } 

    @Override
    public void actionPerformed(ActionEvent e) {
        
    }

}

