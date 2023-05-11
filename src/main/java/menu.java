import java.awt.Button;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Panel;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import celldetection._3D_objects_counter;
import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.GUI;
import ij.gui.Roi;
import ij.gui.Overlay;
import ij.measure.ResultsTable;
import ij.plugin.frame.PlugInFrame;
import ij.plugin.frame.RoiManager;
import imageJ.plugins.PoorMan3DReg_;
import imageJ.plugins.TurboReg_;
import imageJ.plugins.Grouped_ZProjector;

import java.util.ArrayList;
import ij.gui.Plot;
import javax.swing.*;
import java.awt.*;


public class menu extends PlugInFrame implements ActionListener {

    Panel panel;
    ResultsTable results = ResultsTable.getActiveTable();
    Window crm_window = WindowManager.getFrame("Custom RoiManager");
    Frame customRoiManager;
    RoiManager rm;
    ImagePlus active_video;
    Window active_video_window;

    Button btnMakeCopy = new Button();
    Button btnRegistration = new Button();
    Button btnThreshold = new Button();
    Button btnCustomRoi = new Button();
    Button btnRoiManager = new Button();
    Button btnSaveRoi = new Button();
    Button btnInputRoi = new Button();
    Button btnApplyRoi = new Button();
    Button btnSetMeasurements = new Button();
    Button btnShowResults = new Button();
    Button btnSaveResults = new Button();
    Button btnGenerateChrats = new Button();

    menu(){
        super("Menu");

        ImageJ ij = IJ.getInstance();
        addKeyListener(ij);
        WindowManager.addWindow(this);

        panel = new Panel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        Font font = new Font("Verdana", Font.BOLD, 16);

        panel.add(Box.createHorizontalStrut(200));

        addButton("Make a Copy", true, btnMakeCopy);
        addButton("Registration", true, btnRegistration);
        
        Label cellDetectionLabel = new Label("Cell Detection", Label.CENTER);
        cellDetectionLabel.setFont(font);
        panel.add(cellDetectionLabel);
        addButton("Threshold Setting", false, btnThreshold);
        addButton("Detection Parameters", false, btnCustomRoi);
        addButton("ROI Manager", false, btnRoiManager);
        addButton("Save ROI set as...", false, btnSaveRoi);
        addButton("Input ROI set", true, btnInputRoi);
        addButton("Apply ROI to video", false, btnApplyRoi);

        Label showResults = new Label("Show results", Label.CENTER);
        showResults.setFont(font);
        panel.add(showResults);
        addButton("Set Measurements", false, btnSetMeasurements);
        addButton("Show Results Table", false, btnShowResults);
        addButton("Save Results", false, btnSaveResults);
    
        addButton("Peak Finding", false, btnGenerateChrats);
        add(panel);
        pack();
        GUI.center(this);
        setVisible(true);

    }

    void addButton(String label, boolean isEnabled, Button newButton) {
        // Allows easier button adding
        newButton.setLabel(label);
        newButton.setMaximumSize(new Dimension(200, 350));
        newButton.addActionListener(this);
        newButton.addKeyListener(IJ.getInstance());
        newButton.setEnabled(isEnabled);
        panel.add(newButton); 
    } 

    public static void createCellRoi(ResultsTable results, RoiManager rm){

        //Vars
        double x;
        double y;
        double width;
        double height;
        int cornerDiameter = 20;
        System.out.println("CREATE CELL ROI");
        System.out.println(results);

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
    public void actionPerformed(ActionEvent event) {

        // Listens for button preses and calls the corresponding functions

        String command = event.getActionCommand();
        _3D_objects_counter counter = new _3D_objects_counter();
        PoorMan3DReg_ reg = new PoorMan3DReg_();

        if (command == "Make a Copy") {
            // ImagePlus img = WindowManager.getImage("post-reg");
            try{
                IJ.run("Duplicate...");
            }
            catch(Exception e){
                System.out.println(e);
            }
        } 
        else if (command == "Registration") {

            int imageCount = WindowManager.getImageCount();
            

            if (imageCount < 1) {
                IJ.showMessage("Calcium Signal", "No image found.");
            return;
            }
            
            new SwingWorker<Void, Void>() {
                protected Void doInBackground() {
                    PoorMan3DReg_ reg = new PoorMan3DReg_();
                    Grouped_ZProjector z_proj = new Grouped_ZProjector();
                    int[] idList = WindowManager.getIDList();
                    for (int id : idList) {
                    active_video = WindowManager.getImage(id);
                    String[] titles = WindowManager.getImageTitles();
                    active_video_window = WindowManager.getWindow(titles[titles.length-1]);
                    System.out.println(idList.length);
                    WindowManager.setTempCurrentImage(active_video);
                    reg.run("run");
                    // z_proj.run("run");
                    // IJ.run("Z Project...", "projection=[Max Intensity] title=Max");

                }
                return null;
                }
             }.execute();

            

            btnThreshold.setEnabled(true);
            }

        else if (command == "Threshold Setting") {
            counter.run("run");
            results = ResultsTable.getActiveTable();
            if (rm != null){
                rm.reset();
                
            }
            rm = RoiManager.getRoiManager();
            createCellRoi(results, rm);
            btnRoiManager.setEnabled(true);
            btnCustomRoi.setEnabled(true);
            btnSaveRoi.setEnabled(true);
            btnShowResults.setEnabled(true);
            btnSaveResults.setEnabled(true);
            btnSetMeasurements.setEnabled(true);
            btnApplyRoi.setEnabled(true);
            btnGenerateChrats.setEnabled(true);
        }

        else if (command == "Detection Parameters") {
            Window crm_window = WindowManager.getFrame("Custom RoiManager");
            if (crm_window == null){
                customRoiManager = new custom_roiManager();
                }
            else {
                // bring to front
                WindowManager.toFront(crm_window);
                Window[] wins = WindowManager.getAllNonImageWindows();
                for (Window w : wins){
                    System.out.println(w);
                }
            }
            
        }
        
        else if (command == "ROI Manager") {
            RoiManager rm = RoiManager.getInstance();
            if (rm == null){
                rm = RoiManager.getRoiManager();
                menu.createCellRoi(results, rm);

                }  
            else {
                WindowManager.toFront(rm);
                rm.runCommand("Show All");
            }   
        } 
        
        else if (command.equals("Peak Finding")){

            new SwingWorker<Void, Void>() {
                protected Void doInBackground() {
                    WindowManager.setTempCurrentImage(active_video);
                    rm.runCommand("multi-measure");
                    ResultsTable results = ij.measure.ResultsTable.getResultsTable();
                    wavletDenoise(results);
                  return null;
                }
             }.execute();
           
        } 
         
        else if (command == "Save ROI set as...") {
            JFileChooser fileChooser = new JFileChooser();
            FileFilter roiFilter = new FileNameExtensionFilter("ROI file", ".roi");
            fileChooser.setFileFilter(roiFilter);
            fileChooser.setDialogTitle("Select Folder");
            fileChooser.setAcceptAllFileFilterUsed(false);

            int returnVal = fileChooser.showSaveDialog(fileChooser);

            if(returnVal == JFileChooser.APPROVE_OPTION) {
                String fileName = fileChooser.getSelectedFile().getAbsolutePath();
                if (fileName.endsWith(".roi")) {
                    results.save(fileName);
                }
                else {
                    results.save(fileName + ".roi");
                }
            }
        }

        else if (command == "Input ROI set") {
            rm = RoiManager.getInstance();

            if (rm==null){
                rm = new RoiManager();
            }
            if (results==null){
                results = new ResultsTable();
            }

            Roi[] rois = rm.getRoisAsArray();
            
            if (rois.length > 0){
                rm.reset();
            }

            JFileChooser fileChooser = new JFileChooser();

            fileChooser.setDialogTitle("Select ROI File");

            int returnVal = fileChooser.showOpenDialog(fileChooser);

            if(returnVal == JFileChooser.APPROVE_OPTION) {
                String fileName = fileChooser.getSelectedFile().getAbsolutePath();
                BufferedReader br;
                if (fileName.endsWith(".roi")) {
                    File file = fileChooser.getSelectedFile();
                    try{
                        String line;
                        br = new BufferedReader(new FileReader(file));

                        results.reset();
                        results.updateResults();
                        String str_headings = br.readLine();
                        String[] headings = str_headings.split("\t");

                        while ((line = br.readLine()) != null){
                            String[] values = line.split("\t");
                            results.addRow();
                            for (int i=2; i<headings.length; i+=1){
                                results.addValue(headings[i], Double.parseDouble(values[i]));
                            }
                            results.addLabel(values[1]);
                            results.updateResults();
                            System.out.println(results.size());
                        }
                        createCellRoi(results, rm);
                    }           
                    catch(Exception ex){System.out.println(ex);}
                }
                else {
                    IJ.showMessage("Error", "NOT AN ROI FILE");
                }
            }
        }
        
        else if (command == "Apply ROI to video") {
            Overlay overlay = active_video.getOverlay();
            if (overlay != null){
                overlay.clear();
            }
            rm.moveRoisToOverlay(active_video);
        }
       
        else if (command == "Set Measurements") {
            // Buggy
            try{
                IJ.run("Set Measurements...");
            }
            catch (Exception ex){
                System.out.println(ex);
            }
        }
        
        else if (command == "Show Results Table"){

            if (WindowManager.getActiveTable() == null){
                results = new ResultsTable();
                results.updateResults();
            }
            if (RoiManager.getInstance()!=null){
                rm.runCommand("Measure");
            }
            
            else {
                WindowManager.toFront(WindowManager.getActiveTable());
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

    public void wavletDenoise(ResultsTable results) {
        ArrayList<Double> frames = new ArrayList<Double>();
        ArrayList<Cell> cells = new ArrayList<>();
        // Establish frames
        for(int i = 1; i <= results.getColumn(1).length; i++)
                frames.add(Double.valueOf(i));  
        // Convert to array
        double[] framesArr = new double[frames.size()];
        for (int i = 0; i < framesArr.length; i++)
            framesArr[i] = frames.get(i);

        // Establish Signal
        int cellCounter = 1;
        for(String s : results.getHeadings()) {
            if(s.contains("Mean")) {
                double[] signal = results.getColumn(s);
                generatePlot(framesArr, signal, cellCounter, cells);
                cellCounter++;
            }
        }
       CellManager cm = new CellManager(cells);
    //    cm.setLocation(100,350); // Use this if you want to set a default location for the Cell Manager Window

    }
    public static void generatePlot(double[] frames, double[] signal, int cellNumber, ArrayList<Cell> cells) {
        Cell cell = new Cell();
        cell.setSignal(signal);
        cell.setFrames(frames);
        cell.setCellNumber(cellNumber);
        Plot plot = new Plot("Cell " + cellNumber, "Video Frame (#)", "Calcium Intensity");
        plot.setColor(Color.BLACK);
        plot.setLineWidth(2);
        plot.add("line", frames, signal);
        double[] n = normalize(signal);
        plot.setColor(Color.RED);
        plot.add("line", frames, n);
        plot.setColor(Color.BLUE);
        plot.setLineWidth(3);
        findPeaks(n, frames, plot, cell);
        cell.setNormalize(n);
        cell.setPlot(plot);
        cells.add(cell);

    }
    public static void findPeaks(double[] input, double[] xinput, Plot plot, Cell cell) {
        ArrayList<Double> peaks = new ArrayList<>();
        ArrayList<Double> xpeaks = new ArrayList<>();
        for (int i = 1; i < input.length - 1; i++) {
            if (input[i - 1] < input[i] && input[i] > input[i + 1]) {
                peaks.add(input[i]);
                xpeaks.add(xinput[i]);
            }
        }
        for (int i = 1; i < input.length - 1; i++) {
            if (input[i - 1] > input[i] && input[i] < input[i + 1]) {
                peaks.add(input[i]);
                xpeaks.add(xinput[i]);
            }
        }
        plot.addPoints(xpeaks, peaks, 0);
        cell.setPeaks(peaks);
        cell.setXPeaks(xpeaks);      
        cell.arrangePoints();
        plot.setFontSize(24);
        plot.setColor(new Color(4, 138, 26));
        for(int i = 0; i < xpeaks.size(); i++) {
            plot.addText(String.valueOf(i),cell.getXPeaks().get(i), cell.getPeaks().get(i));
        }
        plot.setFontSize(12);

        
    }
    // https://stackoverflow.com/questions/59263100/how-to-easily-apply-a-gauss-filter-to-a-list-array-of-doubles
    public static double[] normalize(double[] vals) {
        double sigma= 6;
        int kernelRadius = (int)Math.ceil( sigma * 2.57 ); // significant radius
        double[] kernel = gaussianKernel1d( kernelRadius, sigma );
        double[] result = filter( vals, kernel );
        return result;
    }

    private static double[] gaussianKernel1d(int kernelRadius, double sigma) {
        double[] kernel = new double[kernelRadius + 1 + kernelRadius];
        for( int xx = -kernelRadius; xx <= kernelRadius; xx++ )
            kernel[kernelRadius + xx] = Math.exp( -(xx * xx) / (2 * sigma * sigma) ) /
                    (Math.PI * 2 * sigma * sigma);
        return kernel;
    }

    static double[] filter( double[] array, double[] kernel ){
        assert( kernel.length % 2 == 1 ); //kernel size must be odd.
        int kernelRadius = kernel.length / 2;
        int width  = array.length;
        double[] result = new double[width];
        for( int x = 0; x < width; x++ ) {
            double sumOfValues  = 0;
            double sumOfWeights = 0;
            for( int i = -kernelRadius; i <= kernelRadius; i++ ) {
                double value = array[clamp( x + i, 0, width - 1 )];
                double weight = kernel[kernelRadius + i];
                sumOfValues  += value * weight;
                sumOfWeights += weight;
            }
            result[x] = sumOfValues / sumOfWeights;
        }
        return result;
    }

    private static int clamp( int value, int min, int max ) {
        if( value < min )
            return min;
        if( value > max )
            return max;
        return value;
    }
}
