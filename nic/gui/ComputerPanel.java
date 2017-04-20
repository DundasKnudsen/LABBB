
/**
 * Copyright 1997-2015 Stefan Nilsson, 2015-2017 Douglas Wikstrom.
 * This file is part of the NIC/NAS software licensed under BSD
 * License 2.0. See LICENSE file.
 */

package se.kth.csc.nic.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Observer;
import java.util.Observable;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import se.kth.csc.nic.Computer;
import se.kth.csc.nic.NIC;
import se.kth.csc.nic.NICException;
import se.kth.csc.nic.Processor;
import se.kth.csc.nic.Util;
import se.kth.csc.nic.observable.ObservableComputer;

/**
 * Panel that displays the state of a computer.
 */
public class ComputerPanel extends JPanel implements Observer {

    final static String FETCH = "Fetch";
    final static String EXECUTE = "Execute";

    final static String START = "Start";
    final static String STOP = "Stop";

    /**
     * Underlying computer.
     */
    final ObservableComputer computer;

    /**
     * Message label to report status codes of the processor.
     */
    final JLabel userMessage;

    /**
     * Button to start or stop a non-interactive execution of the
     * computer.
     */
    final JButton startStopButton;

    /**
     * Button to step through the execution by letting the processor
     * perform a fetch or an execute.
     */
    final JButton fetchExecuteButton;

    /**
     * Button to stop and reset the computer.
     */
    final JButton resetButton;

    /**
     * to change the execution speed of the computer.
     */
    final JSlider speedSlider;

    protected String currentFileName;

    /**
     * Creates a computer panel for a given computer.
     *
     * @param computer Underlying computer.
     */
    public ComputerPanel(final ObservableComputer observableComputer) {
        this.computer = observableComputer;

        // Create messaging label.
        userMessage = new JLabel();
        userMessage.setBackground(Constants.backgroundColor);

        // Create start-stop button. The label of this button will
        // change depending on if the computer is running or not.
        startStopButton = new JButton(START);
        startStopButton.setEnabled(false);
        startStopButton.setBackground(Constants.backgroundColor);
        startStopButton.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent evt) {
                    computer.startStop();
                    updateVisualState();
                    if (!computer.isRunning()) {
                        setMessage("Paused execution of " + currentFileName);
                    }
                }
            });

        // Create fetch-execute button. The label of this button will
        // change depending on the state of the processor.
        fetchExecuteButton = new JButton(FETCH);
        fetchExecuteButton.setEnabled(false);
        fetchExecuteButton.setBackground(Constants.backgroundColor);
        fetchExecuteButton.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent evt) {
                    computer.step();
                    updateVisualState();
                }
            });

        // Create reset button.
        resetButton = new JButton("Reset");
        resetButton.setEnabled(false);
        resetButton.setBackground(Constants.backgroundColor);
        resetButton.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent evt) {
                    computer.reset();
                    updateVisualState();
                    setMessage("Reset " + currentFileName);
              }
            });

        // Create slider to determine the speed of execution in
        // non-interactive mode.
        speedSlider = new JSlider();
        final Dimension d = speedSlider.getSize();
        d.width = 120;
        speedSlider.setPreferredSize(d);
        speedSlider.setBackground(Constants.backgroundColor);
        speedSlider.addChangeListener(new ChangeListener() {
                public void stateChanged(final ChangeEvent evt) {
                    // Qubic response, 1-1000 Hz
                    int n = 100 - speedSlider.getValue();
                    int t = 1 + n*n*n/1000;
                    computer.setClockTick(t);
                }
            });

        // Combine buttons in a panel.
        final JPanel buttons =
            new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 5));
        buttons.setBackground(Constants.backgroundColor);
        buttons.add(startStopButton);
        buttons.add(fetchExecuteButton);
        buttons.add(resetButton);

        // Combine slider and buttons.
        final JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(Constants.backgroundColor);
        bottomPanel.add("West", buttons);
        bottomPanel.add("East", speedSlider);

        // Add processor panel, memory panel, and buttons panel to
        // this computer panel.
        setBackground(Constants.backgroundColor);
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 20));
        add("North", userMessage);
        add("West", new ProcessorPanel(computer.getProcessor()));
        add("Center", new MemoryPanel(computer.getMemory()));
        add("South", bottomPanel);

        computer.getRunning().addObserver(this);
    }

    // This method is called by the register of this panel when its
    // value changes.
    @Override
    public void update(final Observable o, final Object x) {
        updateVisualState();
    }

    /**
     * Returns the computer of this panel.
     *
     * @return Computer of this panel.
     */
    public Computer getComputer() {
        return computer;
    }

    /**
     * Set message.
     *
     * @param message Message.
     */
    public void setMessage(final String message) {
        userMessage.setText(message);
    }

    /**
     * Sets the visual state of this panel.
     */
    private void updateVisualState() {
        if (computer.isRunning()) {
            fetchExecuteButton.setEnabled(false);
            startStopButton.setText(STOP);
            resetButton.setEnabled(false);
        } else {
            if (computer.nextIsFetch()) {
                fetchExecuteButton.setText(FETCH);
            } else {
                fetchExecuteButton.setText(EXECUTE);
            }
            fetchExecuteButton.setEnabled(true);
            startStopButton.setText(START);
            resetButton.setEnabled(true);
        }
        switch (computer.getStatus()) {
        case Processor.HALT:
            setMessage("Completed execution of " + currentFileName);
            break;
        case Processor.BAD_INSTRUCTION:
            setMessage("Bad instruction!");
            break;
        case Processor.BAD_ALIGNMENT:
            setMessage("Bad alignment!");
            break;
        default:
            if (computer.isRunning()) {
                setMessage("Executing " + currentFileName);
            } else {
                setMessage("Stepping through " + currentFileName);
            }
        }
    }

    /**
     * Sets the program and displays status information.
     *
     * @param fileName Origin of program.
     * @param program Program.
     */
    public void setProgram(final String fileName, final String program)
        throws NICException {
        computer.setProgram(program);
        computer.reset();
        setMessage("Loaded " + fileName);
        fetchExecuteButton.setText(FETCH);
        startStopButton.setEnabled(true);
        fetchExecuteButton.setEnabled(true);
        resetButton.setEnabled(true);
        currentFileName = fileName;
    }

    /**
     * Sets the program and displays status information.
     *
     * @param file Program file.
     */
    public void setProgram(final File file) {
        try {
            final String program = Util.readString(file);
            setProgram(file.getName(), program);
        } catch (NICException nice) {
            setMessage(nice.getMessage());
        }
    }
}
