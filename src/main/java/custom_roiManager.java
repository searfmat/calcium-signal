import ij.IJ;
import ij.ImageJ;
import ij.WindowManager;
import ij.gui.GUI;
import ij.gui.Roi;
import ij.measure.ResultsTable;
import ij.plugin.frame.PlugInFrame;
import ij.plugin.frame.RoiManager;

import javax.swing.*;
import javax.swing.text.NumberFormatter;

import java.io.FileReader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.*;

import static com.sun.java.accessibility.util.AWTEventMonitor.addKeyListener;

public class custom_roiManager extends PlugInFrame implements ActionListener {

    private final String PYTHONSCRIPT_PATH = "plugins/CalciumSignal/pythonscript";
    private final String EDGE_DATA_PATH = "plugins/CalciumSignal/edge_data";
    Panel panel;
    RoiManager rm;
    int cellMin;
    int cellMax;
    JFormattedTextField minField;
    JFormattedTextField maxField;
    Set<Roi> allRois = new HashSet<Roi>();

    custom_roiManager(){
        super("Custom RoiManager");
        rm = new RoiManager();
        cellMin = 20;
        cellMax = 200;


        ImageJ ij = IJ.getInstance();
        addKeyListener(ij);
        WindowManager.addWindow(this);
        //setLayout(new FlowLayout(FlowLayout.CENTER,5,5));
        setLayout(new BorderLayout());

        panel = new Panel();
        addTextFields();
        addButton("Multi Measure");
        addButton("Outlier Analysis");
        addButton("RC 1 STD");
        addButton("RC 2 STD");
        addButton("RC 3 STD");
        add(panel);

        pack();
        //list.delItem(0);
        GUI.center(this);
        show();


    }

    void addTextFields(){
        NumberFormat format = NumberFormat.getInstance();
        NumberFormatter formatter = new NumberFormatter(format);
        formatter.setValueClass(Integer.class);
        formatter.setMinimum(0);
        formatter.setMaximum(200);
        formatter.setAllowsInvalid(false);
        formatter.setCommitsOnValidEdit(true);

        JLabel min = new JLabel("Min Cell Size: ");
        minField = new JFormattedTextField(formatter);
        minField.setColumns(10);
        minField.setActionCommand("cellMin");
        minField.addActionListener(this);

        JLabel max = new JLabel("Max Cell Size: ");
        maxField = new JFormattedTextField(formatter);
        maxField.setColumns(10);
        maxField.setActionCommand("cellMax");
        maxField.addActionListener(this);

        panel.add(min);
        panel.add(minField);
        panel.add(max);
        panel.add((maxField));

        addButton("Update Cells");

    }

    void addButton(String label) {
        Button b = new Button(label);
        b.addActionListener(this);
        b.addKeyListener(IJ.getInstance());
        panel.add(b);
    }

    void updateCellSize(String minSize, String maxSize){
        int min = Integer.parseInt(minSize);
        int max = Integer.parseInt(maxSize);

        cellMin = min;
        cellMax = max;

        Roi [] rois = rm.getRoisAsArray();
        allRois.addAll(Arrays.asList(rois));

        rm.close();
        RoiManager newRm = new RoiManager();
        rm = newRm;

        for(Roi roi: allRois){
            allRois.add(roi);
            if (roi.getBounds().getHeight() >= min && roi.getBounds().getWidth() >= min && roi.getBounds().getWidth() <= max && roi.getBounds().getWidth() <= max){
                addRoi(roi);
            }
        }


    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if (command.equals("Update Cells")){
            updateCellSize(minField.getText(),maxField.getText());
        }else if (command.equals("Multi Measure")){
            rm.runCommand("multi-measure");

            //Gets active table and saves
            String path =  "plugins/CalciumSignal/pythonscript/cell_data/realResults.csv";
            ResultsTable results = ij.measure.ResultsTable.getResultsTable();
            try {
                results.saveAs(path);
            } catch (IOException f) {
                f.printStackTrace();
            }
            rm.close();
            setVisible(false);
            runPythonScripts(1);
        } else if (command.equals("Outlier Analysis")) {
            runPythonScripts(1);
        } else if (command.equals("RC 1 STD")) {
            setMinMax(1);
        } else if (command.equals("RC 2 STD")) {
            setMinMax(2);
        } else if (command.equals("RC 3 STD")) {
            setMinMax(3);
        }
    }

    public void addRoi(Roi roi){
        rm.addRoi(roi);
        rm.runCommand("Show All");
    }

    public void setMinMax(int std) {
        String path = EDGE_DATA_PATH + "/edgeDetectResults.csv";

        String[] vals;
        int total = 0;
        ArrayList<Double> nums = new ArrayList<Double>();
        String line;
        
         try (BufferedReader br = new BufferedReader(new FileReader(path))) {
             br.readLine();
             while((line = br.readLine()) != null){
                vals = line.split(",");
                nums.add(Double.valueOf(vals[3]));
                total += nums.get(nums.size() - 1);
             }
         } catch (Exception e){
             System.out.println(e);
         }

         double mean = total / nums.size();
         double temp = 0;

         for (int i = 0; i < nums.size(); i++){
            double val = nums.get(i);
            double squrDiffToMean = Math.pow(val - mean, 2);
            temp += squrDiffToMean;
        }

        double meanOfDiffs = (double) temp / (double) (nums.size());
        double standardDeviation = Math.sqrt(meanOfDiffs);

        if(std == 1) {
            minField.setText(String.valueOf((int) (mean - standardDeviation)));
            maxField.setText(String.valueOf((int) (mean + standardDeviation)));
        } else if(std == 2) {
            minField.setText(String.valueOf((int)(mean - (standardDeviation * 2))));
            maxField.setText(String.valueOf((int)(mean + (standardDeviation * 2))));
        } else if(std == 3){
            minField.setText(String.valueOf((int)(mean - (standardDeviation * 3))));
            maxField.setText(String.valueOf((int)(mean + (standardDeviation * 3))));
        } else {
            return;
        }

    }

    public void runPythonScripts(int val){
        /*
            Run external scripts.
            val = 0: peakScript
            val = 1: histogramScript
         */
        try {
            // Attempt to find the preferred command or path for python 3
            String systemPath = System.getenv("PATH");
            String[] pathLines = systemPath.split(":");
            String exePath = "python";
            String fileName;
            String os = System.getProperty("os.name");

            if (os.contains("Windows")) {
                for (String entry : pathLines) {
                    boolean pyPath = entry.contains("python") || entry.contains("python3");

                    if (pyPath) {
                        if (entry.contains("python3")) {
                            exePath = entry.substring(entry.indexOf(System.getProperty("file.separator")) + 1);
                        }
                    }
                }
            } else {
                String[] bin = new File("/usr/bin").list();
                for (String cmdName : bin) {
                    if (cmdName.equals("python") || cmdName.equals("python3")) {
                        exePath = cmdName;
                    }
                }
                String[] local = new File("/usr/local/bin").list();
                for (String cmdName : local) {
                    if (cmdName.equals("python") || cmdName.equals("python3")) {
                        // Prefer local distribution (/usr/local/bin)...use full path
                        exePath = "/usr/local/bin/" + cmdName;
                    }
                }
            }

            // RELATIVE TO LOCATION OF FIJI EXECUTABLE
            if(val == 0)
                fileName = "/peakscript.py";
            else
                fileName = "/histogramscript.py";
            
            ProcessBuilder processBuilder = new ProcessBuilder(exePath, PYTHONSCRIPT_PATH + fileName);
            System.out.println("Proc builder running with value of " + fileName + " path of "+ exePath);
            processBuilder.redirectErrorStream(true);

            Process process = processBuilder.start();
            //BufferedReader errout = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            //String line;

            //while ((line = errout.readLine()) != null) {
            //    IJ.log(line);
           // }
            process.waitFor();
            //process.destroy();

            // Use for debugging only
            /* 
            BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line2;

            while ((line2 = input.readLine()) != null) {
                IJ.log(line2);
            }
            */
            

        } catch (Exception ex) {
            IJ.log(ex.getMessage());
        }
    }

}

