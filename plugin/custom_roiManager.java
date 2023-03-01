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

public class custom_roiManager extends PlugInFrame implements ActionListener {
    private static final DecimalFormat df = new DecimalFormat("0.00");
    private final String PYTHONSCRIPT_PATH = "plugins/CalciumSignal/pythonscript";

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
        setLayout(new BorderLayout());

        panel = new Panel();
        addTextFields();
        addButton("Multi Measure");
        addButton("Outlier Analysis");
        addButton("RC 1 STD");
        addButton("RC 2 STD");
        addButton("RC 3 STD");
        addButton("Grid Suggestions");
        addButton("Create Grid");
        addButton("Undo [Cntrl + Shift + E]");
        add(panel);

        pack();
        GUI.center(this);
        setVisible(true);
    }

    void addTextFields(){
        NumberFormat format = NumberFormat.getInstance();
        NumberFormatter formatter = new NumberFormatter(format);
        formatter.setValueClass(Integer.class);
        formatter.setMinimum(0);
        formatter.setMaximum(200);
        formatter.setAllowsInvalid(false);
        formatter.setCommitsOnValidEdit(true);

        JLabel min = new JLabel("Min Cell Diameter: ");
        minField = new JFormattedTextField(formatter);
        minField.setColumns(10);
        minField.setActionCommand("cellMin");
        minField.addActionListener(this);

        JLabel max = new JLabel("Max Cell Diameter: ");
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

        if(allRois.size() == 0) {
            Roi [] rois = rm.getRoisAsArray();
            allRois.addAll(Arrays.asList(rois));
        }
        rm.close();
        RoiManager newRm = new RoiManager();
        rm = newRm;        
        // Reset the rois on each update selection
        for(Roi roi: allRois){
            // Re-add selections based on if they fit within the bounds of the min/max calculations
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
            setMinMax(0);
        } else if (command.equals("RC 1 STD")) {
            setMinMax(1);
        } else if (command.equals("RC 2 STD")) {
            setMinMax(2);
        } else if (command.equals("RC 3 STD")) {
            setMinMax(3);
        } else if(command.equals("Grid Suggestions")) {
            // Get the top image to find the width.
            // Since the image is always a square, use this value the calculated constant to generate grid suggestions
            int[] idList = WindowManager.getIDList();
            ImagePlus img = WindowManager.getImage(idList[0]);
            double imgW = img.getWidth();
            double imgArea = Math.pow((imgW *1.24296875), 2);
            
            IJ.showMessage("Grid Value Help", "Grid Area Values:\n3x3: "+ Math.round(imgArea / 9) +"\n5x5: " + Math.round(imgArea / 25));
        } else if(command.equals("Create Grid")) {
            // Shortcut for the grid dialog window
            // Replace the area value with the suggested values to get the desired grid
            // NOTE: Centering grid from the dialog is hit or miss, workaround is to keep clicking the checkbox for "random offset"
            // TODO: Find out how to indroduce actual centering
            Grid g = new Grid();
            g.run(null);
        } else if(command.equals("Undo [Cntrl + Shift + E]")) {
            ImagePlus imp = new ImagePlus();
            imp = WindowManager.getCurrentImage();
            imp.restoreRoi();
        }

    }

    public void addRoi(Roi roi){
        rm.addRoi(roi);
        rm.runCommand("Show All");
    }

    public void setMinMax(int std) {

        ResultsTable results = ResultsTable.getResultsTable();

        double[] widths = results.getColumn("B-width");
        double[] heights = results.getColumn("B-height");

        int total = 0;
        ArrayList<Double> nums = new ArrayList<Double>();

        for(int i = 0; i < widths.length; i++) {
            nums.add((widths[i] + heights[i])/ 2);
            total += nums.get(nums.size() - 1);
        }
        // Calculate the min/max cell size from the mean and standard devitations
         double mean = total / (nums.size());
         double temp = 0;

         for (int i = 0; i < nums.size(); i++){
            double val = nums.get(i);
            double squrDiffToMean = Math.pow(val - mean, 2);
            temp += squrDiffToMean;
        }

        double meanOfDiffs = (double) temp / (double) (nums.size());
        double standardDeviation = Math.sqrt(meanOfDiffs);

        int minCell = (int)(mean - (standardDeviation * std));
        int maxCell = (int)(mean + (standardDeviation * std));
        
        if (minCell <= 0)
            minCell = 1;
        if(std == 1 || std == 2 || std == 3) {
            minField.setText(String.valueOf(minCell));
            maxField.setText(String.valueOf(maxCell));
       } else {
            //Create histogram
            PlotWindow.noGridLines = false;
            Plot plot = new Plot("Outlier Analysis", "Cell Diameter", "Frequency");
            double [] numsList = new double[nums.size()];

            for(int i = 0; i < nums.size(); i++) {
                numsList[i] = Math.round(nums.get(i));
            }
            plot.addHistogram(numsList, 1);

            plot.setLineWidth(5);
            plot.setColor(Color.BLACK);
            plot.changeFont(new Font("Helvetica", Font.PLAIN, 15));
            plot.addLabel(0.15, 0.95, "Mean: " + String.valueOf(mean));
            plot.addLabel(0.15, 0.90, "STD: " + String.valueOf(df.format(standardDeviation)));
            plot.addLabel(0.15, 0.85, "Min: " + String.valueOf(Collections.min(nums)));
            plot.addLabel(0.15, 0.80, "Max: " + String.valueOf(Collections.max(nums)));

            plot.changeFont(new Font("Helvetica", Font.PLAIN, 16));
            plot.setColor(Color.blue);
            plot.show();
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

