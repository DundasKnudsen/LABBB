
/**
 * Copyright 1997-2015 Stefan Nilsson, 2015-2017 Douglas Wikstrom.
 * This file is part of the NIC/NAS software licensed under BSD
 * License 2.0. See LICENSE file.
 */

package se.kth.csc.nic.observable;

/**
 * Register wrapper for a program counter register that keeps the
 * non-functional state of the memory cells to which the contents of
 * this register points active, which in turn allows simple rendering
 * of the memory cells in user interfaces.
 */
public class ObservablePCRegister extends ObservableRegister {

    /**
     * Memory to which the contents of this register points.
     */
    private ObservableMemory mem;

    /**
     * Number of blocks used to represent an instruction.
     */
    private int INSTRUCTIONBLOCKS;

    /**
     * Creates a program counter register that points to the given
     * memory.
     *
     * @param REGSIZE Bit size of contents of this register.
     * @param mem Memory to which this register points.
     * @param instructionblocks Block size of an instruction.
     */
    ObservablePCRegister(final int REGSIZE, final ObservableMemory mem,
                         final int INSTRUCTIONBLOCKS) {
        super(REGSIZE);
        this.mem = mem;
        this.INSTRUCTIONBLOCKS = INSTRUCTIONBLOCKS;
    }

    @Override
    public synchronized void set(final int value) {
        synchronized (this) {

            // Deactivate memory we currently point to.
            int oldvalue = get();
            mem.setActive(oldvalue, oldvalue + INSTRUCTIONBLOCKS, false);

            // Activate memory we now point to.
            super.set(value);
            mem.setActive(value, value + INSTRUCTIONBLOCKS, true);
        }
    }
}
