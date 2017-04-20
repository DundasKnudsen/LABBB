
/**
 * Copyright 1997-2015 Stefan Nilsson, 2015-2017 Douglas Wikstrom.
 * This file is part of the NIC/NAS software licensed under BSD
 * License 2.0. See LICENSE file.
 */

package se.kth.csc.nic.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import javax.swing.border.Border;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import se.kth.csc.nic.observable.ObservableMemory;

/**
 * A canvas that displays a memory. Each block is displayed as a
 * number of hexadecimal digits. The cells are editable.
 */
class MemoryPanel extends JPanel {
    final static int rows = 16, cols = 16;

    MemoryPanel(final ObservableMemory memory) {

        final Border border = BorderFactory.createLineBorder(Color.gray);
        setBorder(BorderFactory.createTitledBorder(border, "RAM"));

        final JPanel memoryPanel =
            new JPanel(new GridLayout(rows + 1, cols + 1, 1, 1));
        memoryPanel.setBorder(BorderFactory.createEmptyBorder(2, 10, 10, 10));
        memoryPanel.setBackground(Constants.backgroundColor);

        memoryPanel.add(new JLabel()); // top left position empty
        for (int i = 0; i < cols; i++) { // column headings
            memoryPanel.add(new HexLabel(i));
        }

        final MemoryCellLabel[] mcl = new MemoryCellLabel[256];
        for (int i = 0; i < 256; i++) {
            mcl[i] = new MemoryCellLabel(memory.getMemoryCell(i));
        }

        for (int i = 0; i < 256; i++) {

            if (i == 0) {
                mcl[i].setLeft(mcl[i]);
            } else {
                mcl[i].setLeft(mcl[(i - 1) & 0xff]);
            }

            if (i == 255) {
                mcl[i].setRight(mcl[i]);
            } else {
                mcl[i].setRight(mcl[(i + 1) & 0xff]);
            }

            if (i < 16) {
                mcl[i].setUp(mcl[i]);
            } else {
                mcl[i].setUp(mcl[(i - 16) & 0xff]);
            }

            if (i < 240) {
                mcl[i].setDown(mcl[(i + 16) & 0xff]);
            } else {
                mcl[i].setDown(mcl[i]);
            }
        }
        for (int i = 0; i < rows; i++) {
            memoryPanel.add(new HexLabel(i));  // row headings
            for (int j = 0; j < rows; j++) {
                memoryPanel.add(mcl[i * cols + j]);
            }
        }

        setLayout(new BorderLayout(0, 0));
        add("Center", memoryPanel);
        setBackground(Constants.backgroundColor);
    }
}
