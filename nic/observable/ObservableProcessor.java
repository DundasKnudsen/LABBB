
/**
 * Copyright 1997-2015 Stefan Nilsson, 2015-2017 Douglas Wikstrom.
 * This file is part of the NIC/NAS software licensed under BSD
 * License 2.0. See LICENSE file.
 */

package se.kth.csc.nic.observable;

import se.kth.csc.nic.observable.ObservableMemory;
import se.kth.csc.nic.observable.ObservablePCRegister;
import se.kth.csc.nic.observable.ObservableRegister;
import se.kth.csc.nic.Processor;

/**
 * Processor that provides the functionality of the JDK {@link
 * Observable} class. Thus, it sends a message every time its state
 * changes. This makes it easy to implement a user interface.
 */
public class ObservableProcessor extends Processor {

    /**
     * Creates a processor with the given components.
     *
     * @param mem Underlying memory.
     * @param WORDBLOCKS Number of blocks in a word stored in general
     * purpose registers.
     * @param NOREGISTERS Number of registers. This must be
     * addressable by a block.
     */
    public ObservableProcessor(final ObservableMemory mem, final int WORDBLOCKS,
                               final int NOREGISTERS) {
        super(mem, WORDBLOCKS);

        if (NOREGISTERS > (1 << BLOCKSIZE)) {
            throw new Error("All registers cannot be addressed!");
        }
        this.NOREGISTERS = NOREGISTERS;

        reg = new ObservableRegister[NOREGISTERS];
        for (int i = 0; i < NOREGISTERS; i++) {
            reg[i] = new ObservableRegister(WORDSIZE);
        }
        pc = new ObservablePCRegister(WORDSIZE, mem, INSTRUCTIONBLOCKS);
        ir = new ObservableRegister(INSTRUCTIONBLOCKS * BLOCKSIZE);
        nr = new ObservableRegister(WORDSIZE);
        sr = new ObservableRegister(WORDSIZE);
    }

    /**
     * Return the given register.
     *
     * @param i Index of register.
     * @return Requested register.
     */
    public ObservableRegister getreg(final int i) {
        return (ObservableRegister) reg[i];
    }

    /**
     * Return the program counter register.
     *
     * @return Program counter register.
     */
    public ObservablePCRegister getpc() {
        return (ObservablePCRegister) pc;
    }

    /**
     * Return the instruction register.
     *
     * @return Instruction register.
     */
    public ObservableRegister getir() {
        return (ObservableRegister) ir;
    }

    /**
     * Return the next register.
     *
     * @return Next register.
     */
    public ObservableRegister getnr() {
        return (ObservableRegister) nr;
    }

    /**
     * Return the status register.
     *
     * @return Status register.
     */
    public ObservableRegister getsr() {
        return (ObservableRegister) sr;
    }
}
