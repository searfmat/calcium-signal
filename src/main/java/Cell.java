import ij.ImagePlus;
import ij.gui.Plot;
import java.util.*;
import java.util.Map.Entry;

import javax.imageio.ImageIO;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Cell {
    int cellNumber;
    Plot plot;
    ArrayList<Double> peaks;
    ArrayList<Double> xpeaks;
    double[] frames;
    double[] signal;
    double[] normalize;

    public Cell() {
        this.cellNumber = 0;
        this.plot = null;
        this.peaks = null;
        this.xpeaks = null;
        this.frames = null;
        this.signal = null;
        this.normalize = null;
    }

    public Cell(int cellNumber, Plot plot, ArrayList<Double> peaks, ArrayList<Double> xpeaks) {
        this.cellNumber = cellNumber;
        this.plot = plot;
        this.peaks = peaks;
        this.xpeaks = xpeaks;
    }

    public Plot gePlot() {
        return this.plot;
    }

    public int getCellNumber() {
        return this.cellNumber;
    }

    public void setCellNumber(int cellNumber) {
        this.cellNumber = cellNumber;
    }

    public void setPeaks(ArrayList<Double> peaks) {
        this.peaks = peaks;
    }

    public void setXPeaks(ArrayList<Double> xpeaks) {
        this.xpeaks = xpeaks;
    }
    public ArrayList<Double> getPeaks() {
        return this.peaks;
    }

    public ArrayList<Double> getXPeaks() {
        return this.xpeaks;
    }

    public void setPlot(Plot plot) {
        this.plot = plot;
    }

    public void setFrames(double[] frames) {
        this.frames = frames;
    }

    public void setSignal(double[] signal) {
        this.signal = signal;
    }

    public void setNormalize(double[] normalize) {
        this.normalize = normalize;
    }

    public void generateNewPlot(Boolean showlabels) {
        Plot plot = new Plot("Cell " + this.cellNumber, "Video Frame (#)", "Calcium Intensity");
        plot.setColor(Color.BLACK);
        plot.setLineWidth(2);
        plot.add("line", this.frames, this.signal);
        plot.setColor(Color.RED);
        plot.add("line", this.frames, this.normalize);
        plot.setColor(Color.BLUE);
        plot.setLineWidth(3);
        plot.addPoints(this.xpeaks, this.peaks, 0);

        if(showlabels) {
            plot.setFontSize(24);
            plot.setColor(Color.BLUE);
            for(int i = 0; i < this.xpeaks.size(); i++) {
                plot.addText(String.valueOf(i),this.getXPeaks().get(i), this.getPeaks().get(i));
            }
            plot.setFontSize(12);
        }

        this.plot = plot;
    }

    public String toString() {
        return "Cell " + this.cellNumber;
    }

    public void arrangePoints() {

        Map<Double, Double> allPoints = new TreeMap<Double, Double>();

        for(int i = 0; i < this.peaks.size(); i++) {
            allPoints.put(this.xpeaks.get(i), this.peaks.get(i));
        }

        ArrayList<Double> newPeaks = new ArrayList<>();
        ArrayList<Double> newXPeaks = new ArrayList<>();

        for (Entry<Double, Double> entry : allPoints.entrySet()) {
            newXPeaks.add(entry.getKey());
            newPeaks.add(entry.getValue());
       }

       this.peaks = newPeaks;
       this.xpeaks = newXPeaks;

    }

    public void writegraph() {
        ImagePlus ipPlot = this.plot.getImagePlus();
        BufferedImage bufferedPlot = ipPlot.getBufferedImage();
        try {
            File outputfile = new File("C:/Users/Matthew/Desktop/TestCells/Cell" + cellNumber + ".png");
            ImageIO.write(bufferedPlot, "png", outputfile);
        } catch (IOException e) {
            System.out.println("Failed to write image file");
        }
    }

}
