/*
 * Copyright (c) 2013, hedin
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package ch.unibas.charmmtools.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 *
 * @author hedin
 */
public final class Endianness {

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
