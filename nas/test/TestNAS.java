
/**
 * Copyright 1997-2015 Stefan Nilsson, 2015-2017 Douglas Wikstrom.
 * This file is part of the NIC/NAS software licensed under BSD
 * License 2.0. See LICENSE file.
 */

package se.kth.csc.nas.test;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

import se.kth.csc.nas.*;

/**
 * Unit tests for NAS.
 */
public final class TestNAS {

    /**
     * Magical header used for programs to be able to perform a basic
     * sanity check that a file contains an executable program.
     */
    final static String MAGICAL_HEADER = "1f1f1f1f";

    /**
     * Tail consists of a jump to address zero and halt, and a newline.
     */
    final static String TAIL = "0000f000\n";

    /**
     * Attempts to strip the magical header and default ending
     * instructions and throws an error otherwise.
     *
     * @param program Program to strip.
     */
    public static String stripHeaderAndTail(final String program)
        throws NASError {

        String res = null;

        // Magic header at the start, and at the end: move to address
        // zero, halt, and newline.
        if (program.length() < 8 + 4 + 4 + 1) {
            error(String.format("Too short program! (%d)", program.length()));
        } else if (!program.startsWith(MAGICAL_HEADER)) {
            error(String.format("Program does not start with magical "
                                + "header! (%s)", program.substring(0, 8)));
        } else if (!program.endsWith(TAIL)) {
            error(String.format("Program does not end properly! (%s)", TAIL));
        } else {
            res = program.substring(8, program.length() - 9);
        }
        return res;
    }

    public static String assemble(final String program) {
        String executable = null;
        try {
            executable =
                NAS.assemble("", program, Integer.MAX_VALUE, System.err);
        } catch (final IOException ioe) {
            throw new NASError("Failure in NAS!", ioe);
        }
        return executable;
    }

    public static void check(final String program, final String code) {

        final String executable = assemble(program);
        final String stripped = stripHeaderAndTail(executable);

        if (!stripped.equals(code)) {
            error(String.format("Program assembled incorrectly! "
                                + "(%s --> %s != %s)",
                                program, stripped, code));
        }
    }

    public static void ops() {
        System.out.print("halt ");
        check("halt", "0000");
        System.out.print("noop ");
        check("noop", "f001");
    }

    public static void opRegReg(final OpPair opPair)
        throws NASError{
        for (int i = 0; i < 1 << 8; i++) {
            final int r = i & 0xF;
            final int s = (i >> 4) & 0xF;

            final String line =
                String.format("%s r%d r%d", opPair.op, r, s);
            final String code = String.format("%x0%x%x", opPair.opCode, r, s);
            check(line, code);
        }
    }

    public static void opsRegReg() {

        final List<OpPair> opPairs = new ArrayList<OpPair>();
        opPairs.add(new OpPair("loadr", 0x3));
        opPairs.add(new OpPair("storer", 0x5));
        opPairs.add(new OpPair("move", 0x6));

        for (final OpPair opPair : opPairs) {
            opRegReg(opPair);
        }
    }

    public static void opRegRegReg(final OpPair opPair)
        throws NASError{
        for (int i = 0; i < 1 << 12; i++) {
            final int r = i & 0xF;
            final int s = (i >> 4) & 0xF;
            final int t = (i >> 8) & 0xF;

            final String line =
                String.format("%s r%d r%d r%d", opPair.op, r, s, t);
            final String code =
                String.format("%x%x%x%x", opPair.opCode, r, s, t);
            check(line, code);
        }
    }

    public static void opsRegRegReg() {

        final List<OpPair> opPairs = new ArrayList<OpPair>();
        opPairs.add(new OpPair("add", 0x7));
        opPairs.add(new OpPair("mul", 0x9));
        opPairs.add(new OpPair("sub", 0xa));
        opPairs.add(new OpPair("shift", 0xb));
        opPairs.add(new OpPair("and", 0xc));
        opPairs.add(new OpPair("or", 0xd));
        opPairs.add(new OpPair("xor", 0xe));

        for (final OpPair opPair : opPairs) {
            System.out.print(opPair.op + " ");
            opRegRegReg(opPair);
        }
    }

    public static void opRegValue(final OpPair opPair)
        throws NASError{
        for (int i = 0; i < 1 << 12; i++) {
            final int r = i & 0xF;
            final int x = (i >> 4) & 0xFF;

            final String dline =
                String.format("%s r%d %d", opPair.op, r, x - 128);
            final String dcode =
                String.format("%x%x%02x", opPair.opCode, r, (x - 128) & 0xFF);
            check(dline, dcode);

            final String xline =
                String.format("%s r%d 0x%x", opPair.op, r, x);
            final String xcode =
                String.format("%x%x%02x", opPair.opCode, r, x);
            check(xline, xcode);
        }
    }

    public static void opsRegValue() {

        final List<OpPair> opPairs = new ArrayList<OpPair>();
        opPairs.add(new OpPair("load", 0x1));
        opPairs.add(new OpPair("loadc", 0x2));
        opPairs.add(new OpPair("store", 0x4));
        opPairs.add(new OpPair("addc", 0x8));

        for (final OpPair opPair : opPairs) {
            System.out.print(opPair.op + " ");
            opRegValue(opPair);
        }
    }

    public static void opsJump() {

        // jump <address>
        System.out.print("jump ");
        for (int x = 0; x < 1 << 8; x++) {
            check(String.format("jump 0x%x", x),
                  String.format("f0%02x", x));
        }

        final String[] ops = {"jumpe", "jumpn", "jumpl", "jumple"};

        for (int b = 0; b < 4; b++) {
            System.out.print(ops[b] + " ");
            for (int i = 0; i < 1 << 10; i++) {
                final int r = i & 0xF;
                final int x = (i >> 2) & 0xFC;

                final String line = String.format("%s r%d 0x%x", ops[b], r, x);
                final String code = String.format("f%x%02x", r, x + b);
                check(line, code);
            }
        }
    }

    public static void individualOps() {
        System.out.print("Testing individual instructions... ");
        ops();
        opsRegRegReg();
        opsRegReg();
        opsRegValue();
        opsJump();
        System.out.println("done.");
    }

    public static void word() {
        System.out.print("Testing word directive... ");

        for (int i = 0; i < 10; i++) {

            final StringBuilder sb = new StringBuilder();
            final StringBuilder csb = new StringBuilder();

            // Hexadecimal values.
            sb.append("word hexa");
            for (int j = 0; j < i; j++) {
                sb.append(String.format(" 0x%x", j));
                csb.append(String.format("%02x", j));
            }
            sb.append("\n");

            // Decimal positive values.
            sb.append("word pdeci");
            for (int j = 0; j < i; j++) {
                sb.append(String.format(" %d", j));
                csb.append(String.format("%02x", j));
            }
            sb.append("\n");

            // Decimal negative values.
            final int modulus = 1 << 8;
            sb.append("word ndeci");
            for (int j = 0; j < i; j++) {
                sb.append(String.format(" %d", -j));
                csb.append(String.format("%02x", (modulus - j) % modulus));
            }
            sb.append("\n");

            if (i == 0) {
                csb.append("000000");
            }

            // Some instructions to force relocation.
            final StringBuilder psb = new StringBuilder();
            for (int j = 0; j < i; j++) {
                sb.append("halt").append("\n");
                psb.append("halt").append("\n");
            }
            final String cleanExecutable = assemble(psb.toString());
            final String correctExecutable =
                cleanExecutable.substring(0, cleanExecutable.length() - 1)
                + csb.toString() + "\n";

            final String program = sb.toString();
            final String executable = assemble(program);

            if (!executable.equals(correctExecutable)) {
                error(String.format("Words relocated incorrectly! "
                                    + "(%s --> %s != %s)",
                                    program, executable, correctExecutable));
            }
        }
        System.out.println("done.");
    }

    public static void relocate() {

        for (int i = 1; i < 20; i++) {

            // Generate values for a word directive and the words in
            // code they will result in.
            final StringBuilder tsb = new StringBuilder();
            final StringBuilder tsbr = new StringBuilder();
            for (int j = 0; j < i; j++) {
                tsb.append(" 0x3");
                tsbr.append("03");
            }
            final String threes = tsb.toString();
            final String threesRaw = tsbr.toString();

            // Two word directives: a and b where we use b for testing
            // purposes.
            final StringBuilder sb = new StringBuilder();
            sb.append("word a" + threes);
            sb.append("\n");
            sb.append("word b");
            sb.append("\n");

            // Symbolic addresses with positive and negative offsets.
            sb.append("loadc r0 b");
            sb.append("\n");
            sb.append("loadc r0 b+1");
            sb.append("\n");
            sb.append("loadc r0 b-3");

            final String program = sb.toString();
            final String executable = assemble(program);

            // We can strip the header, since we have already tested
            // that headers are generated correctly.
            final String noheader = executable.substring(8);

            // Hard coded correct executable without leading header.
            final int bAddress = 5 * 4 + 2 * i;
            final String correctNoHeader =
                String.format("20%02x20%02x20%02x0000f000",
                              bAddress, bAddress + 1, bAddress - 3)
                + threesRaw + "00\n";

            if (!noheader.equals(correctNoHeader)) {
                error(String.format("Failed to relocate symbolic addresses! "
                                    + "(%s)", program));
            }
        }
    }

    public static void spaces(final Random random, final StringBuilder sb) {
        while (random.nextInt() % 3 != 0) {
            sb.append(' ');
        }
    }

    public static void comment(final Random random, final StringBuilder sb) {
        while (random.nextInt() % 7 == 0) {
            sb.append("// comment");
        }
    }

    public static void newlines(final Random random, final StringBuilder sb) {
        while (random.nextInt() % 2 == 0) {
            sb.append('\n');
        }
    }

    public static void whiteSpaceAndComments() {
        System.out.print("Testing handling of white space and comments...");

        // Generate a program containing all types of instructions.
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 20; i++) {

            final int type = i % 5;
            switch (type) {
            case 0:
                sb.append("noop");
                break;
            case 1:
                sb.append("jump 0x01");
                break;
            case 2:
                sb.append("storer r0 r1");
                break;
            case 3:
                sb.append("add r0 r1 r2");
                break;
            case 4:
                sb.append("addc r0 0x01");
                break;
            }
            sb.append("\n");
        }
        final String rawProgram = sb.toString();

        // We introduce random white space and comments in the code.
        final Random random = new Random();
        final StringBuilder sbm = new StringBuilder();
        for (int i = 0; i < rawProgram.length(); i++) {
            if (rawProgram.charAt(i) == ' ') {
                sbm.append(' ');
                spaces(random, sbm);
            } else if (rawProgram.charAt(i) == '\n') {
                spaces(random, sbm);
                comment(random, sbm);
                newlines(random, sbm);
                spaces(random, sbm);
                comment(random, sbm);
                newlines(random, sbm);
                spaces(random, sbm);
                newlines(random, sbm);
                sbm.append('\n');
            } else {
                sbm.append(rawProgram.charAt(i));
            }
        }
        final String modProgram = sbm.toString();

        // Check that the executables are the same.
        final String rawExecutable = assemble(rawProgram);
        final String modExecutable = assemble(modProgram);
        if (!modExecutable.equals(rawExecutable)) {
            error("Handling of white space failed!");
        }
        System.out.println("done.");
    }

    public static void error(final String message) throws NASError {
        throw new NASError(message);
    }

    public static void main(String[] args) {
        individualOps();
        word();
        whiteSpaceAndComments();
        relocate();
    }
}

class OpPair {

    final String op;
    final int opCode;

    OpPair(final String op, final int opCode) {
        this.op = op;
        this.opCode = opCode;
    }
}