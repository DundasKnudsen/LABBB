
/**
 * Copyright 1997-2015 Stefan Nilsson, 2015-2017 Douglas Wikstrom.
 * This file is part of the NIC/NAS software licensed under BSD
 * License 2.0. See LICENSE file.
 */

package se.kth.csc.nic.observable;

import java.util.Observable;

import se.kth.csc.nic.Computer;

/**
 * Computer with observable memory and processor.
 */
public class ObservableComputer extends Computer {

    /**
     * Creates an observable computer with the given parameters.
     *
     * @param MEMORYCELLS Size of memory in blocks.
     * @param BLOCKSIZE Blocksize of memory.
     * @param WORDBLOCKS Number of blocks in a word stored in general
     * purpose registers.
     * @param NOREGISTERS Number of registers. This must be
     * addressable by a word.
     */
    public ObservableComputer(final int MEMORYCELLS, final int BLOCKSIZE,
                              final int WORDBLOCKS, final int NOREGISTERS) {
        this.mem = new ObservableMemory(MEMORYCELLS, BLOCKSIZE);
        this.processor = new ObservableProcessor((ObservableMemory) this.mem,
                                                 WORDBLOCKS,
                                                 NOREGISTERS);
        this.running = new ObservableRunning(false);
    }

    /**
     * Returns the memory of this computer.
     *
     * @return Memory of this computer.
     */
    public ObservableMemory getMemory() {
        return (ObservableMemory) mem;
    }

    /**
     * Returns the processor of this computer.
     *
     * @return Processor of this computer.
     */
    public ObservableProcessor getProcessor() {
        return (ObservableProcessor) processor;
    }

    /**
     * Returns the non-interactive running state of this computer.
     *
     * @return Non-interactive running state of this computer.
     */
    public ObservableRunning getRunning() {
        return (ObservableRunning) running;
    }
}
