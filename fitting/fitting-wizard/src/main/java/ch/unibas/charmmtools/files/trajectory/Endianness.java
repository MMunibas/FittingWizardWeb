/*
 * Copyright (c) 2016, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */

package ch.unibas.charmmtools.files.trajectory;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 *
 * @author hedin
 */
public class Endianness {

    // Buffers
    ByteBuffer bf = null;

    public Endianness() {
        bf = ByteBuffer.allocateDirect(Double.SIZE / 8);
    }

    // java is big endian but not the C or Fortran we read, file so me need to change that
    // this reads a little endian integer and converts it to java's big endians integers
    public int little2big(int i) {
        bf.clear();
        bf.order(ByteOrder.BIG_ENDIAN);
        bf.putInt(i);
        bf.flip();
        bf.order(ByteOrder.LITTLE_ENDIAN);
        return bf.getInt();
    }

    // The same for Double
    public double little2big(double i) {
        bf.clear();
        bf.order(ByteOrder.BIG_ENDIAN);
        bf.putDouble(i);
        bf.flip();
        bf.order(ByteOrder.LITTLE_ENDIAN);
        return bf.getDouble();
    }

    // The same for Float
    public float little2big(float i) {
        bf.clear();
        bf.order(ByteOrder.BIG_ENDIAN);
        bf.putFloat(i);
        bf.flip();
        bf.order(ByteOrder.LITTLE_ENDIAN);
        return bf.getFloat();
    }

} // end of class
