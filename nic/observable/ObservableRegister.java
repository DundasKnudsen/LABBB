
/**
 * Copyright 1997-2015 Stefan Nilsson, 2015-2017 Douglas Wikstrom.
 * This file is part of the NIC/NAS software licensed under BSD
 * License 2.0. See LICENSE file.
 */

package se.kth.csc.nic.observable;

import java.util.Observable;

import se.kth.csc.nic.GenericRegister;
import se.kth.csc.nic.Register;

/**
 * Register wrapper that provides the functionality of the JDK {@link
 * Observable} class. Thus, it sends a message to its observers every
 * time the value of the register is updated. This makes it easier to
 * write a user interface.
 */
public class ObservableRegister extends Observable implements Register {

    /**
     * Underlying register.
     */
    protected final Register register;

    /**
     * Creates an observable register.
     */
    ObservableRegister(final int REGSIZE) {
        this.register = new GenericRegister(REGSIZE);
    }

    @Override
    public void set(final int value) {
        synchronized (this) {
            register.set(value);

            // Tell the observer that the value has changed.
            setChanged();
            notifyObservers();
        }
    }

    @Override
    public int get() {
        return register.get();
    }
}
