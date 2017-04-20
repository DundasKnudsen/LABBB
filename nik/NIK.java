
/**
 * Copyright 1997-2015 Stefan Nilsson, 2015-2017 Douglas Wikstrom.
 * This file is part of the NIC/NAS software licensed under BSD
 * License 2.0. See LICENSE file.
 */

package se.kth.csc.nik;

import java.io.IOException;
import se.kth.csc.nic.Computer;

/**
 * Wrapper of Nilsson Instructional Computer (NIC) for use with the
 * Kattis {@link https://kth.kattis.com/} system.
 *
 * @author Douglas Wikstrom
 */
public class NIK {

    /**
     * Computer initialized with a program.
     */
    final Computer computer;

    /**
     * Creates a wrapper of a computer running the given program.
     *
     * @param program Program to execute.
     */
    public NIK(final String program) throws IOException {
        this.computer = new Computer();
        final String executable =
            NAS.assemble(null, program, NAS.MAX_ERRORS, errorStream);
        computer.setProgram(executable);
     }

    /**
     * Executes the program on the given input.
     *
     * @param input Input given as a hexadecimal string.
     * @return Output of program on the given input.
     */
    public String execute(final String input)
        throws NICException {
        return computer.execute(input);
    }
}