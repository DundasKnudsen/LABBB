
/**
 * Copyright 1997-2015 Stefan Nilsson, 2015-2017 Douglas Wikstrom.
 * This file is part of the NIC/NAS software licensed under BSD
 * License 2.0. See LICENSE file.
 */

package se.kth.csc.nic.gui;

import java.util.Observable;
import java.util.Observer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

import se.kth.csc.nic.observable.ObservablePCRegister;
import se.kth.csc.nic.observable.ObservableProcessor;
import se.kth.csc.nic.observable.ObservableRegister;

/**
 * Panel that displays the state of a processor.
 */
class ProcessorPanel extends JPanel {

    /**
     * Creates panel that displays the state of the given processor.
     *
     * @param processor Underlying processor.
     */
    ProcessorPanel(final ObservableProcessor processor) {

        final Border lineBorder = BorderFactory.createLineBorder(Color.gray);
        setBorder(BorderFactory.createTitledBorder(lineBorder, "CPU"));

        // Build panel for all generic registers.
        final JPanel regJPanel = new JPanel(new GridLayout(17, 1, 1, 1));
        regJPanel.setBorder(BorderFactory.createEmptyBorder(2, 10, 10, 2));
        regJPanel.setBackground(Constants.backgroundColor);
        regJPanel.add(new JLabel("r[i]", SwingConstants.RIGHT));
        for (int i = 0; i < processor.getNOREGISTERS(); i++) {
            regJPanel.add(new RegisterPanel(processor.getreg(i), i));
        }

        // Build panel for pc and ir registers.
        final JPanel pcirJPanel = new JPanel(new GridLayout(17, 1, 1, 1));
        pcirJPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 10, 10));
        pcirJPanel.setBackground(Constants.backgroundColor);

        pcirJPanel.add(new JLabel("pc", SwingConstants.RIGHT));
        pcirJPanel.add(new PCRegisterPanel(processor.getpc()));

        pcirJPanel.add(new JLabel());    // empty position
        pcirJPanel.add(new JLabel("ir", SwingConstants.RIGHT));
        pcirJPanel.add(new IRRegisterPanel(processor.getir()));

        pcirJPanel.add(new JLabel());    // empty position
        pcirJPanel.add(new JLabel("sr", SwingConstants.RIGHT));
        pcirJPanel.add(new SRRegisterPanel(processor.getsr()));

        // empty positions
        for (int i = 7; i < processor.getNOREGISTERS(); i++) {
            pcirJPanel.add(new JLabel());
        }

        // Put generic registers to the left and pc and ir to the right.
        setLayout(new BorderLayout(0, 0));
        add("West", regJPanel);
        add("East", pcirJPanel);
        setBackground(Constants.backgroundColor);
    }
}

/**
 * Label that displays the status of the most recent move of the
 * processor as a string.
 */
class StatusLabel extends JLabel implements Observer {

    /**
     * String representations of status codes from the processor.
     */
    final static String[] statusString = {
        "",
        "Halt",
        "Illegal instruction",
        "Bad memory alignment",
    };

    /**
     * Underlying move register.
     */
    final ObservableRegister register;

    /**
     * Create status label for the underlying status register.
     *
     * @param register Underlying status register.
     */
    StatusLabel(final ObservableRegister register) {
        this.register = register;
        this.setBackground(Constants.backgroundColor);
        this.setText(statusString[register.get()]);
    }

    @Override
    public void update(final Observable o, final Object x) {
        final int status = ((ObservableRegister) o).get();
        this.setText(statusString[status]);
    }
}
/**
 * A HexLabel with a different background color.
 */
class RegisterCellLabel extends HexLabel {

    @Override
    public void paint(Graphics g) {
        g.setColor(Constants.registerCellColor);
        g.fillRect(0, 0, getSize().width, getSize().height);
        super.paint(g);
    }
}

/**
 * A register is displayed using two RegisterCellLabels.
 */
class RegisterPanel extends JPanel implements Observer {

    /**
     * First register of this panel.
     */
    private final RegisterCellLabel b1;

    /**
     * Second register of this panel.
     */
    private final RegisterCellLabel b2;

    /**
     * Creates a register panel based on the given register and the
     * index of this register.
     *
     * @param reg Underlying register.
     * @param index Index of this register.
     */
    RegisterPanel(ObservableRegister reg, int index) {
        b1 = new RegisterCellLabel();
        b2 = new RegisterCellLabel();

        setLayout(new GridLayout(1, 3, 1, 1));

        // Display the register index
        add(new HexLabel(index));
        add(b1);
        add(b2);
        reg.addObserver(this);
        update(reg, null);
        setBackground(Constants.backgroundColor);
    }

    // This method is called by the register of this panel when its
    // value changes.
    @Override
    public void update(Observable o, Object x) {
        set(((ObservableRegister) o).get());
    }

    /**
     * Set the value displayed by this panel.
     *
     * @param value Value displayed by this panel.
     */
    void set(int value) {
        b1.set((value & 0xf0) >>> 4);
        b2.set(value & 0x0f);
    }
}

/**
 * A panel displaying the program counter register.
 */
class PCRegisterPanel extends JPanel implements Observer {

    /**
     * Component register cell label.
     */
    private final RegisterCellLabel b1;

    /**
     * Component register cell label.
     */
    private final RegisterCellLabel b2;

    /**
     * Creates a panel that displays the value of the given
     * program counter register.
     *
     * @param reg Underlying program counter register.
     */
    PCRegisterPanel(ObservablePCRegister reg) {
        b1 = new RegisterCellLabel();
        b2 = new RegisterCellLabel();
        setLayout(new GridLayout(1, 5, 1, 1));
        add(new JLabel());  // The three empty positions make
        add(new JLabel());  // this label line up nicely with
        add(new JLabel());  // the ir register label.
        add(b1);
        add(b2);
        reg.addObserver(this);
        setBackground(Constants.backgroundColor);
    }

    // This method is called by the register this label is observing
    // when its value changes.
    @Override
    public void update(Observable o, Object x) {
        set(((ObservablePCRegister) o).get());
    }

    /**
     * Set the value displayed by this panel.
     *
     * @param value Value displayed by this panel.
     */
    void set(int value) {
        b1.set((value & 0xf0) >>> 4);
        b2.set(value & 0x0f);
    }
}

/**
 * Panel displaying the value of the instruction register.
 */
class IRRegisterPanel extends JPanel implements Observer {

    /**
     * Component register cell label.
     */
    private final RegisterCellLabel b1;

    /**
     * Component register cell label.
     */
    private final RegisterCellLabel b2;

    /**
     * Component register cell label.
     */
    private final RegisterCellLabel b3;

    /**
     * Component register cell label.
     */
    private final RegisterCellLabel b4;

    /**
     * Creates a panel that displays the value of the given
     * instruction register.
     *
     * @param reg Underlying instruction register.
     */
    IRRegisterPanel(ObservableRegister reg) {
        b1 = new RegisterCellLabel();
        b2 = new RegisterCellLabel();
        b3 = new RegisterCellLabel();
        b4 = new RegisterCellLabel();
        setLayout(new GridLayout(1, 5, 1, 1));
        add(new JLabel());  // empty position
        add(b1);
        add(b2);
        add(b3);
        add(b4);
        reg.addObserver(this);
        setBackground(Constants.backgroundColor);
    }

    // This method is called by the instruction register this panel is
    // observing when its value changes.
    @Override
    public void update(Observable o, Object x) {
        set(((ObservableRegister) o).get());
    }

    /**
     * Set the value displayed by this panel.
     *
     * @param value Value displayed by this panel.
     */
    void set(int value) {
        b1.set((value & 0xf000) >>> 12);
        b2.set((value & 0x0f00) >>> 8);
        b3.set((value & 0x00f0) >>> 4);
        b4.set(value & 0x000f);
    }
}

/**
 * Panel displaying the value of the instruction register.
 */
class SRRegisterPanel extends JPanel implements Observer {

    /**
     * Component register cell label.
     */
    private final RegisterCellLabel b1;

    /**
     * Component register cell label.
     */
    private final RegisterCellLabel b2;

    /**
     * Creates a panel that displays the value of the given
     * instruction register.
     *
     * @param reg Underlying instruction register.
     */
    SRRegisterPanel(ObservableRegister reg) {
        b1 = new RegisterCellLabel();
        b2 = new RegisterCellLabel();
        setLayout(new GridLayout(1, 5, 1, 1));
        add(new JLabel());  // The three empty positions make
        add(new JLabel());  // this label line up nicely with
        add(new JLabel());  // the ir register label.
        add(b1);
        add(b2);
        reg.addObserver(this);
        setBackground(Constants.backgroundColor);
    }

    // This method is called by the instruction register this panel is
    // observing when its value changes.
    @Override
    public void update(Observable o, Object x) {
        set(((ObservableRegister) o).get());
    }

    /**
     * Set the value displayed by this panel.
     *
     * @param value Value displayed by this panel.
     */
    void set(int value) {
        b1.set((value & 0x00f0) >>> 4);
        b2.set(value & 0x000f);
    }
}
