
/**
 * Copyright 1997-2015 Stefan Nilsson, 2015-2017 Douglas Wikstrom.
 * This file is part of the NIC/NAS software licensed under BSD
 * License 2.0. See LICENSE file.
 */

package se.kth.csc.nic.observable;

import se.kth.csc.nic.Memory;

/**
 * Memory that provides the functionality of the JDK {@link
 * Observable} class. Thus, it sends a message to its observers every
 * time any block is changed. This makes it easy to implement a user
 * interface.
 */
public class ObservableMemory extends Memory {

    /**
     * Creates an observable memory with the given number of memory
     * cells, where each memory cell is initialized to zero.
     *
     * @param MEMORYCELLS Number of memory cells.
     * @param BLOCKSIZE Bit-size of a block.
     */
    public ObservableMemory(final int MEMORYCELLS, final int BLOCKSIZE) {
        super(BLOCKSIZE);
        this.cells = new ObservableMemoryCell[MEMORYCELLS];
        for (int i = 0; i < this.cells.length; i++) {
            cells[i] = new ObservableMemoryCell(BLOCKSIZE);
        }
    }

    /**
     * Returns the memory cell at the given address.
     *
     * @param p Address of memory cell.
     * @return Memory cell at the given address.
     */
    public ObservableMemoryCell getMemoryCell(final int p) {
        return (ObservableMemoryCell) cells[p];
    }

    /**
     * Activates/deactivates the memory cells within the given address
     * interval. This is non-functional method that only provides a
     * way to implement user interfaces, i.e., it does not change the
     * behavior of the memory.
     *
     * @param start Address of first memory cell to activate
     * (inclusive).
     * @param end Address of last memory cell to activate (exclusive).
     * @param value Boolean indicating the state of the memory cells.
     */
    public void setActive(final int start, final int end, final boolean value) {
        synchronized (this) {
            for (int p = start; p < end; p++) {
                ((ObservableMemoryCell) cells[p]).setActive(value);
            }
        }
    }
}
