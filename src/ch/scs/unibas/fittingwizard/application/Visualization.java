package ch.scs.unibas.fittingwizard.application;

import ch.scs.unibas.fittingwizard.application.molecule.AtomType;
import ch.scs.unibas.fittingwizard.application.xyz.XyzAtom;

import javafx.stage.Stage;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.jmol.adapter.smarter.SmarterJmolAdapter;
import org.jmol.api.JmolViewer;

import javax.swing.*;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.*;

public class Visualization {

    private static final Logger logger = Logger.getLogger(Visualization.class);
    private final Stage primaryStage;
    public JFrame jmolWindow;
    public JmolViewer jmolViewer;

    public Visualization(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void show(File xyzFile) {
        if (jmolWindow == null) {
            logger.info("Creating Jmol window.");
            jmolWindow = new JFrame("Visualization of " + xyzFile.getName());
            jmolWindow.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    logger.info("Jmol window closing.");
                    jmolWindow = null;
                    jmolViewer = null;
                    currentOpenFile = null;
                }
            });
            jmolWindow.setSize(600, 600);

            Container contentPane = jmolWindow.getContentPane();
            JmolPanel jmolPanel = new JmolPanel();

            // main panel -- Jmol panel on top
            JPanel panel = new JPanel();
            panel.setLayout(new BorderLayout());
            panel.add(jmolPanel);
            contentPane.add(panel);
            jmolViewer = jmolPanel.viewer;

            alignWindowPositionToWizard();
            jmolWindow.setVisible(true);
        } else {
            logger.debug("Bringing existing Jmol window to front.");
        }
        openFile(xyzFile.getAbsoluteFile());
        jmolWindow.toFront();
    }

    private void alignWindowPositionToWizard() {
        double x = primaryStage.getX() - 50;
        double y = primaryStage.getY() - 50;

        logger.info(String.format("Positioning jmol window at position x=%f y=%f", x, y));

        jmolWindow.setLocation((int)x, (int)y);
    }

    private File currentOpenFile;
    public void openFile(File file) {
        if (file != null) {
            boolean isDifferentFile = currentOpenFile == null ||
                    !FilenameUtils.equalsNormalized(currentOpenFile.getAbsolutePath(), file.getAbsolutePath());
            if (jmolViewer != null && isDifferentFile) {
                String strError = jmolViewer.openFile(file.getAbsolutePath());
                if (strError == null) {
                    jmolViewer.scriptWait("select clear");
                    jmolViewer.clearSelection();
                    jmolViewer.setSelectionHalos(true);
                    //jmolViewer.evalString(strScript);
                    currentOpenFile = file;
                }
                else
                    logger.error("Error while loading XYZ file. " + strError);
            }
        }
    }

    public void selectAtom(XyzAtom atom) {
        selectAtomsAtIndex(new int[] {atom.getIndex()});
    }

    public void selectAtomTypes(AtomType atomType) {
        if (atomType != null) {
            selectAtomsAtIndex(atomType.getIndices());
        }
    }

    public void selectAtoms(ArrayList<XyzAtom> atoms) {
        int[] indices = new int[atoms.size()];
        for (int i = 0; i < atoms.size(); i++) {
            XyzAtom xyzAtom = atoms.get(i);
            indices[i] = xyzAtom.getIndex();
        }
        selectAtomsAtIndex(indices);
    }

    public void selectAtomsAtIndex(int[] indices) {
        if (jmolViewer != null) {
            String atomIdxString = "";
            for (int i = 0; i < indices.length; i++) {
                atomIdxString += "atomIndex=" + indices[i];
                if (i < indices.length - 1){
                    atomIdxString += " OR ";
                }
            }
            String scriptCmd = "select " + atomIdxString;
            logger.debug("Executing script command: " + scriptCmd);
            jmolViewer.scriptWait(scriptCmd);
        }
    }

    public void close() {
        if (jmolWindow != null) {
            jmolWindow.dispatchEvent(new WindowEvent(jmolWindow, WindowEvent.WINDOW_CLOSING));
        }
    }

    final static String strScript = "delay; move 360 0 0 0 0 0 0 0 4;";

    static class JmolPanel extends JPanel {

        JmolViewer viewer;

        private final Dimension currentSize = new Dimension();

        JmolPanel() {
            viewer = JmolViewer.allocateViewer(this, new SmarterJmolAdapter(),
                    null, null, null, null, null);
        }

        @Override
        public void paint(Graphics g) {
            getSize(currentSize);
            viewer.renderScreenImage(g, currentSize.width, currentSize.height);
        }
    }
}