
/**
 * Copyright 1997-2015 Stefan Nilsson, 2015-2017 Douglas Wikstrom.
 * This file is part of the NIC/NAS software licensed under BSD
 * License 2.0. See LICENSE file.
 */

package se.kth.csc.nic.observable;

import java.util.Observable;

import se.kth.csc.nic.GenericMemoryCell;
import se.kth.csc.nic.MemoryCell;

/**
 * Memory cell wrapper that provides the functionality of the JDK
 * {@link Observable} class. Thus, it sends a message to its observers
 * every time the contents of the cell is changed. This makes it easy
 * to implement a user interface.
 */
public class ObservableMemoryCell extends Observable implements MemoryCell {

    /**
     * Underlying memory cell.
     */
    protected MemoryCell memoryCell;

    /**
     * Indicates that the program counter is active at this cell. This
     * has no functional meaning and is only meant for the user
     * interface.
     */
    protected boolean active;

    /**
     * Creates an observable memory cell initialized to the zero
     * block.
     *
     * @param BLOCKSIZE Bit-size of this memory cell.
     */
    public ObservableMemoryCell(final int BLOCKSIZE) {
        this.memoryCell = new GenericMemoryCell(BLOCKSIZE);
    }

    @Override
    public int get() {
        return memoryCell.get();
    }

    @Override
    public void set(final int block) {
        synchronized (this) {
            memoryCell.set(block);

            // Tell the observer that the value has changed.
            setChanged();
            notifyObservers();
        }
    }

    /**
     * Sets the state of this instance.
     *
     * @param state State of this instance.
     */
    public void setActive(final boolean state) {
        synchronized (this) {
            active = state;

            // Tell the observer that the non-functional state has
            // changed.
            setChanged();
            notifyObservers();
        }
    }

    /**
     * Returns the state of this instance.
     *
     * @return State of this instance.
     */
    public boolean isActive() {
        synchronized (this) {
            return active;
        }
    }
}
