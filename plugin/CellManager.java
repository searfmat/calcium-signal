import ij.IJ;
import ij.ImageJ;
import ij.WindowManager;
import ij.gui.GUI;
import ij.plugin.frame.PlugInFrame;
import javax.swing.*;

import com.opencsv.CSVWriter;

import java.awt.*;
import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class CellManager extends PlugInFrame implements ActionListener {

    Panel panel;
    JList list;
    ArrayList<Cell> cells;
    // JPanel panel;

    CellManager(ArrayList<Cell> cells) {
        super("Cell Manager");
        this.cells = cells;
        ImageJ ij = IJ.getInstance();
        addKeyListener(ij);
        WindowManager.addWindow(this);

        panel = new Panel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        Font font = new Font("Verdana", Font.BOLD, 16);

        panel.add(Box.createHorizontalStrut(200));

        Label cellDetectionLabel = new Label("Cell Plots", Label.CENTER);
        cellDetectionLabel.setFont(font);
        panel.add(cellDetectionLabel);

        String[] cellNames = new String[cells.size()];

        for (int i = 0; i < cells.size(); i++) {
            cellNames[i] = cells.get(i).toString();
        }

        list = new JList(cellNames);

        panel.add(new JScrollPane(list));
        addButton("Open Cell", false);
        addButton("Generate All Plots", false);
        addButton("Generate CSV", false);

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
        b.setEnabled(!isDisabled);
        panel.add(b);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        if (command == "Open Cell") {
            String selected = list.getSelectedValue().toString();

            for (int i = 0; i < cells.size(); i++) {
                if (cells.get(i).toString().equals(selected)) {
                    PeakManager pm = new PeakManager(cells.get(i));
                    pm.setLocation(247,190);
                    break;
                }
            }
            System.out.println(selected);
        } else if (command == "Generate All Plots") {
            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(new java.io.File("."));
            chooser.setDialogTitle("Select Folder");
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            chooser.setAcceptAllFileFilterUsed(false);
            chooser.showOpenDialog(null);
            File folder = chooser.getSelectedFile();
            for (int i = 0; i < cells.size(); i++) {
                cells.get(i).generateNewPlot(false);
                cells.get(i).writegraph(folder);
            }
            try {
                generateCSV(folder);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } else if (command == "Generate CSV") {
            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(new java.io.File("."));
            chooser.setDialogTitle("Select Folder");
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            chooser.setAcceptAllFileFilterUsed(false);
            chooser.showOpenDialog(null);
            File folder = chooser.getSelectedFile();
            try {
                generateCSV(folder);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    public void generateCSV(File f) throws IOException {
        ArrayList<String[]> csvData = new ArrayList<>();
        String[] header = {"Cell Number", "Peak Number", "Frame", "Intensity"};
        csvData.add(header);
        for(Cell c : cells) {
            for(int i = 0; i < c.getXPeaks().size(); i++) {
                String[] vals = {c.toString(), String.valueOf(i), String.valueOf(c.getXPeaks().get(i)), String.valueOf(c.getPeaks().get(i))};
                csvData.add(vals);
            }
        }

        try (CSVWriter writer = new CSVWriter(new FileWriter(f.toPath() + File.separator + "TestCells.csv"))) {
            writer.writeAll(csvData);
        }
    }
}
