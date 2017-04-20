
/**
 * Copyright 1997-2015 Stefan Nilsson, 2015-2017 Douglas Wikstrom.
 * This file is part of the NIC/NAS software licensed under BSD
 * License 2.0. See LICENSE file.
 */

package se.kth.csc.nic.observable;

import java.util.Observable;

import se.kth.csc.nic.GenericRunning;
import se.kth.csc.nic.Running;

/**
 * Indicates if a computer is running on its own or interactively.
 *
 * @author Douglas Wikstrom
 */
public class ObservableRunning extends Observable implements Running {

    /**
     * Running status of computer.
     */
    protected boolean status;

    /**
     * Creates an instance with the given status.
     *
     * @param status Initial running status of computer.
     */
    public ObservableRunning(final boolean status) {
        this.status = status;
    }

    @Override
    public void set(final boolean status) {
        synchronized (this) {
            this.status = status;

            // Tell the observer that the value has changed.
            setChanged();
            notifyObservers();
        }
    }

    @Override
    public boolean get() {
        synchronized (this) {
            return status;
        }
    }
}
