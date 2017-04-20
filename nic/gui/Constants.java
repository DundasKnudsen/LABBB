
/**
 * Copyright 1997-2015 Stefan Nilsson, 2015-2017 Douglas Wikstrom.
 * This file is part of the NIC/NAS software licensed under BSD
 * License 2.0. See LICENSE file.
 */

package se.kth.csc.nic.gui;

import java.awt.Color;
import java.awt.Font;

/**
 * Graphics constants used throughout the graphical user interface.
 */
class Constants {

    /**
     * Generic background color.
     */
    final static Color backgroundColor = new Color(0xFF, 0xFF, 0xFF);

    /**
     * Background color of a memory cell that is neither active nor in
     * focus.
     */
    final static Color memoryCellColor = new Color(0xB8, 0xCF, 0xE5);

    /**
     * Background color of a memory cell that is active.
     */
    final static Color activeMemoryCellColor = new Color(0x83, 0xA1, 0xD1);

    /**
     * Background color of a memory cell that is in focus.
     */
    final static Color focusMemoryCellColor = new Color(0xFF, 0xFF, 0xFF);

    /**
     * Background color of a register label.
     */
    final static Color registerCellColor = new Color(0xFF, 0xEC, 0xC0);

    /**
     * Font for hexadecimal digits.
     */
    final static Font numberFont = new Font("SansSerif", Font.PLAIN, 12);

    /**
     * Generic text color for values.
     */
    final static Color numberColor = Color.black;
}
