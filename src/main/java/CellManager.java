import ij.IJ;
import ij.ImageJ;
import ij.WindowManager;
import ij.gui.GUI;
import ij.plugin.frame.PlugInFrame;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
                    break;
                }
            }
            System.out.println(selected);
        } else if (command == "Generate All Plots") {
            for (int i = 0; i < cells.size(); i++) {
                cells.get(i).writegraph();
            }
        }
    }
}
