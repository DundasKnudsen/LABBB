
/**
 * Copyright 1997-2015 Stefan Nilsson, 2015-2017 Douglas Wikstrom.
 * This file is part of the NIC/NAS software licensed under BSD
 * License 2.0. See LICENSE file.
 */

package se.kth.csc.nic.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import javax.swing.KeyStroke;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;

import se.kth.csc.nic.NIC;
import se.kth.csc.nic.observable.ObservableComputer;

/**
 * Provides a window for a computer.
 */
public class ComputerFrame extends JFrame {
    private JFileChooser chooser;
    private JMenuItem reload;

    /**
     * Content computer panel of this frame.
     */
    private ComputerPanel computerPanel;

    /**
     * Filter for choosing files with the .bi prefix.
     *
     * @param directory Directory.
     */
    private static class BiFilter extends javax.swing.filechooser.FileFilter {

        @Override
        public boolean accept(final File dir) {
            if (dir.isDirectory()) {
                return true;
            }
            final String name = dir.getName();
            return name.endsWith(".bi");
        }

        @Override
        public String getDescription() {
            return "*.bi";
        }
    }

    /**
     * Creates a computer frame with the given title and underlying
     * computer.
     *
     * @param title Window title.
     * @param computer Underlying computer.
     */
    public ComputerFrame(final String title,
                         final ObservableComputer computer) {
        super(title);

        this.computerPanel = new ComputerPanel(computer);
        getContentPane().add("Center", computerPanel);

        // Chooser of files in current working directory with
        // filtering on postfix ".bi".
        this.chooser = new JFileChooser(System.getProperty("user.dir"));
        final BiFilter filter = new BiFilter();
        chooser.addChoosableFileFilter(filter);
        chooser.setFileFilter(filter);

        final JMenuBar menuBar = new JMenuBar();

        // File menu.
        final JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        menuBar.add(fileMenu);

        final JMenuItem open = new JMenuItem("Open...", KeyEvent.VK_O);
        final KeyStroke ctrlo =
            KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK);
        open.setAccelerator(ctrlo);
        open.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                openFile();
            }
        });
        fileMenu.add(open);

        fileMenu.add(new JSeparator());

        final JMenuItem quit = new JMenuItem("Quit", KeyEvent.VK_Q);
        final KeyStroke ctrlq =
            KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_MASK);
        quit.setAccelerator(ctrlq);
        quit.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                System.exit(0);
            }
        });
        fileMenu.add(quit);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        // Help menu.
        final JMenu helpMenu = new JMenu("Help");
        menuBar.add(helpMenu);

        final JMenuItem about = new JMenuItem("NIC Specifications");
        about.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                final String description =
                    computerPanel.getComputer().getDescription();
                JOptionPane.showMessageDialog(ComputerFrame.this,
                                              description,
                                              "NIC Specifications",
                                              JOptionPane.INFORMATION_MESSAGE);
            }
        });

        final JMenuItem description = new JMenuItem("About");
        description.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                JOptionPane.showMessageDialog(ComputerFrame.this,
                                              NIC.ABOUT,
                                              "About",
                                              JOptionPane.INFORMATION_MESSAGE);
            }
        });

        helpMenu.add(about);
        helpMenu.add(description);
        setJMenuBar(menuBar);

        computerPanel.setMessage("Load a program!");
    }

    private void openFile() {
        final int val = chooser.showOpenDialog(this);
        if (val == JFileChooser.APPROVE_OPTION) {
            final File file = chooser.getSelectedFile();
            computerPanel.setProgram(file);
        }
    }
}
