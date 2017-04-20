
/**
 * Copyright 1997-2015 Stefan Nilsson, 2015-2017 Douglas Wikstrom.
 * This file is part of the NIC/NAS software licensed under BSD
 * License 2.0. See LICENSE file.
 */

package se.kth.csc.nic.gui;

import java.awt.Graphics;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Observable;
import java.util.Observer;

import se.kth.csc.nic.observable.ObservableMemoryCell;

/**
 * A graphical component that represents a memory cell in the memory.
 * It is possible to edit the value of the component (and hence also
 * the corresponding value in the memory cell).
 */
class MemoryCellLabel extends HexLabel implements Observer, KeyListener {

    /**
     * The underlying memory cell.
     */
    private final ObservableMemoryCell cell;

    private MemoryCellLabel left;
    private MemoryCellLabel right;
    private MemoryCellLabel up;
    private MemoryCellLabel down;

    /**
     * Set the label to the left of this label.
     *
     * @param left Label to the left.
     */
    void setLeft(MemoryCellLabel left) {
        this.left = left;
    }

    /**
     * Set the label to the right of this label.
     *
     * @param right Label to the right.
     */
    void setRight(MemoryCellLabel right) {
        this.right = right;
    }

    /**
     * Set the label above this label.
     *
     * @param up Label above this label.
     */
    void setUp(MemoryCellLabel up) {
        this.up = up;
    }

    /**
     * Set the label below this label.
     *
     * @param down Label below this label.
     */
    void setDown(MemoryCellLabel down) {
        this.down = down;
    }

    /**
     * Creates a memory cell label.
     *
     * @param cell Underlying memory cell.
     */
    MemoryCellLabel(ObservableMemoryCell cell) {

        // After setting the underlying memory cell its value can be
        // changed if this label is modified.
        this.cell = cell;

        // Add this as an observer to the underlying memory
        // cell. Thus, when the value in the memory cell is changed
        // this label is updated.
        cell.addObserver(this);
        update(cell, null);

        // We want focus if the user clicks in us.
        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                requestFocusInWindow();
            }
        });

        // We repaint ourselves when a mouse pointer goes in or out
        // our domain.
        addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                repaint();
            }
            public void focusLost(FocusEvent e) {
                repaint();
            }
        });

        // When we have focus we want all key strokes.
        addKeyListener(this);
    }

    // This method is called by the underlying memory cell through the
    // observer framework, when its value changes.
    @Override
    public void update(Observable o, Object x) {
        set(cell.get());
    }

    // Extension of the paint method of HexLabel to show a background.
    @Override
    public void paint(Graphics g) {

        // We use different background colors depending on if we have
        // focus, we are active without focus, or if we are inactive
        // without focus.
        if (isFocusOwner()) {
            g.setColor(Constants.focusMemoryCellColor);
        } else if (cell.isActive()) {
            g.setColor(Constants.activeMemoryCellColor);
        } else {
            g.setColor(Constants.memoryCellColor);
        }
        g.fillRect(0, 0, getSize().width, getSize().height);

        // We invoke the paint function of the superclass last to map
        // it on top of what we have painted.
        super.paint(g);
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        boolean hexDigit = false;
        char key = e.getKeyChar();

        // Hexadecimal key strokes triggers a change in the value of
        // the cell. This indirectly updates this label to the
        // corresponding value.
        if ('0' <= key && key <= '9') {
            cell.set(key - '0');
            hexDigit = true;
        }
        if ('a' <= key && key <= 'f') {
            cell.set(10 + key - 'a');
            hexDigit = true;
        }
        if ('A' <= key && key <= 'F') {
            cell.set(10 + key - 'A');
            hexDigit = true;
        }
        if (hexDigit) {
            return;
        }

        // Arrow key strokes triggers a move of focus to a neighboring
        // memory cell in the memory panel.
        switch (e.getKeyCode()) {
        case KeyEvent.VK_DOWN:
        case KeyEvent.VK_KP_DOWN:
            down.requestFocusInWindow();
            break;
        case KeyEvent.VK_UP:
        case KeyEvent.VK_KP_UP:
            up.requestFocusInWindow();
            break;
        case KeyEvent.VK_LEFT:
        case KeyEvent.VK_KP_LEFT:
            left.requestFocusInWindow();
            break;
        case KeyEvent.VK_RIGHT:
        case KeyEvent.VK_KP_RIGHT:
            right.requestFocusInWindow();
            break;
        case KeyEvent.VK_BACK_SPACE:
            left.requestFocusInWindow();
            left.cell.set(0);
            break;
        case KeyEvent.VK_SPACE:
            cell.set(0);
            right.requestFocusInWindow();
            break;
        }

        // All other key strokes are ignored.
    }
}
