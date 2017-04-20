
/**
 * Copyright 1997-2015 Stefan Nilsson, 2015-2017 Douglas Wikstrom.
 * This file is part of the NIC/NAS software licensed under BSD
 * License 2.0. See LICENSE file.
 */

package se.kth.csc.nic.gui;

import java.awt.Graphics;
import java.awt.FontMetrics;
import java.awt.Dimension;

import javax.swing.JComponent;

/**
 * Canvas that displays one hexadecimal digit.
 */
class HexLabel extends JComponent {

    final static char[] hexDigit =
    {'0', '1', '2', '3', '4', '5', '6', '7',
     '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    private int value;

    /**
     * Creates an instance with a zero label.
     */
    HexLabel() {
        value = 0;
    }

    /**
     * Creates an instance.
     *
     * @param value Value of this label.
     */
    HexLabel(final int value) {
        this.value = value;
    }

    /**
     * Sets the value of this instance.
     *
     * @param value Value of this label.
     */
    void set(final int value) {
        this.value = value & 0xf;
        repaint();
    }

    @Override
    public void paint(final Graphics g) {
        final String digit = String.valueOf(hexDigit[value]);
        g.setFont(Constants.numberFont);
        g.setColor(Constants.numberColor);
        final FontMetrics f = g.getFontMetrics();
        final int width = getSize().width;
        final int height = getSize().height;
        final int textWidth = f.stringWidth(digit);
        final int textHeight = f.getAscent();
        g.drawString(digit, (width - textWidth)/2,
                     textHeight + (height - textHeight)/2 - 1);
        super.paint(g);
    }

    @Override
    public Dimension getMinimumSize() {
        return new Dimension(20, 20);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(20, 20);
    }
}
