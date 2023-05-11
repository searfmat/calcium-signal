import ij.IJ;
import ij.ImageJ;
import ij.WindowManager;
import ij.gui.GUI;
import ij.plugin.frame.PlugInFrame;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class PeakManager extends PlugInFrame implements ActionListener {

    Panel panel;
    JList list;
    Cell cell;
    String[] peakNames;
    JScrollPane scroller;
    // JPanel panel;

    PeakManager(Cell cell){
        super("Peak Manager");
        this.cell = cell;
        ImageJ ij = IJ.getInstance();
        addKeyListener(ij);
        WindowManager.addWindow(this);

        panel = new Panel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        Font font = new Font("Verdana", Font.BOLD, 16);

        panel.add(Box.createHorizontalStrut(200));


        Label cellDetectionLabel = new Label("Cell Peaks", Label.CENTER);
        cellDetectionLabel.setFont(font);
        panel.add(cellDetectionLabel);

        peakNames = new String[cell.getPeaks().size()];

        for(int i = 0; i < cell.getPeaks().size(); i++) {
            peakNames[i] = "Peak " + i;
        }

    
        list= new JList(peakNames);
        scroller = new JScrollPane(list);
        panel.add(scroller);
        addButton("Delete Peak", false);


        add(panel);

        pack();
        GUI.center(this);
        setVisible(true);

        cell.gePlot().show();
        
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

        if (command == "Delete Peak") {
            int[] selections = list.getSelectedIndices();
            ArrayList<Double> peaksRemove = new ArrayList<>();
            ArrayList<Double> xpeaksRemove = new ArrayList<>();
            IJ.selectWindow(cell.toString());
            IJ.runMacro("Close", cell.toString());
            for (int j : selections) {
                String selected = list.getModel().getElementAt(j).toString();
                for (int i = cell.getPeaks().size() - 1; i >= 0; i--) {
                    if (peakNames[i].equals(selected)) {
                        peaksRemove.add(cell.peaks.get(i));
                        xpeaksRemove.add(cell.xpeaks.get(i));
                    }
                }

            }
            cell.peaks.removeAll(peaksRemove);
            cell.xpeaks.removeAll(xpeaksRemove);
            cell.generateNewPlot(true);
            cell.gePlot().show();
            PeakManager pm = new PeakManager(cell);
            pm.setLocation(247,190);
            close();
            
        } 

    }
}
