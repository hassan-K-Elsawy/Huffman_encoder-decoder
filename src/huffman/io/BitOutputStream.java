package huffman.io;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class BitOutputStream implements Closeable, Flushable {

    private static final byte[] bitMasks = new byte[8];

    static {
        for (byte i = 0; i < 8; i++)
            bitMasks[i] = (byte) (~(0xFF >>> i));
    }

    private final OutputStream out;

    private int bufferByte           = 0;
    private int bufferedNumberOfBits = 0;

    public BitOutputStream(OutputStream out) {
        this.out = out;
    }

    public void write(byte value) throws IOException {
        if (bufferedNumberOfBits == 0) {
            out.write(value);
        } else {
            bufferByte           =  (((value & 0xFF) >>> bufferedNumberOfBits) | bufferByte);
            out.write(bufferByte);
            bufferByte           = ((value << (8 - bufferedNumberOfBits)) & 0xFF);
        }
    }

    public void write(short value,ByteOrder order) throws IOException {
        var bytes = ByteBuffer
                .allocate(2).putShort(value)
                .array();

        if (order.equals(ByteOrder.LITTLE_ENDIAN)) {
            int start = 0;
            int end = bytes.length - 1;
            while (start < end) {
                byte temp    = bytes[start];
                bytes[start++] =  bytes[end];
                bytes[end--]   =  temp;
            }
            for (var b: bytes) write(b);
        } else {
            for (var b: bytes)
                write(b);
        }

    }

    public void write(long value,ByteOrder order) throws IOException {
        var bytes = ByteBuffer
                .allocate(8).putLong(value)
                .array();

        if (order.equals(ByteOrder.LITTLE_ENDIAN)) {
            int start = 0;
            int end = bytes.length - 1;
            while (start < end) {
                byte temp    = bytes[start];
                bytes[start++] =  bytes[end];
                bytes[end--]   =  temp;
            }
            for (var b: bytes) write(b);
        } else {
            for (var b: bytes)
                write(b);
        }
    }

    public void write(int value, ByteOrder order) throws IOException{
        var bytes = ByteBuffer
                .allocate(4).putInt(value)
                .array();

        if (order.equals(ByteOrder.LITTLE_ENDIAN)) {
            int start = 0;
            int end = bytes.length - 1;
            while (start < end) {
                byte temp    = bytes[start];
                bytes[start++] =  bytes[end];
                bytes[end--]   =  temp;
            }
            for (var b: bytes) write(b);
        } else {
            for (var b: bytes)
                write(b);
        }

    }

    public void write(boolean bit) throws IOException {
        if(bufferedNumberOfBits == 8) {
           out.write(bufferByte);
            bufferByte           = 0;
            bufferedNumberOfBits = 0;
        }

        bufferByte = bit
                ? bufferByte |  (1 << 8 - ++bufferedNumberOfBits)
                : bufferByte & ~(1 << 8 - ++bufferedNumberOfBits);
    }

    public void write(byte bitContainer, int nbits) throws IOException {
        if (nbits == 0)
            return;

        if (nbits < 1 || nbits > Byte.SIZE)
            throw new IllegalArgumentException("Expected 1 to 8 bits");

        if (nbits == 8)
            write(bitContainer);
        else {
            if (bufferedNumberOfBits == 0) {
                bufferByte = (bitContainer << (8 - nbits)) & 0xFF;
                bufferedNumberOfBits = nbits;
            } else {
                bitContainer = (byte) (bitContainer & ~bitMasks[8 - nbits]);
                int bits = 8 - bufferedNumberOfBits - nbits;
                if (bits < 0) {
                    bits = -bits;
                    bufferByte |= (bitContainer >>> bits);
                    out.write(bufferByte);
                    bufferByte = (bitContainer << (8 - bits)) & 0xFF;
                    bufferedNumberOfBits = bits;
                } else if (bits == 0) {
                    bufferByte |= bitContainer;
                    out.write(bufferByte);
                    bufferedNumberOfBits = 0;
                } else {
                    bufferByte |= (bitContainer << bits);
                    bufferedNumberOfBits = 8 - bits;
                }
            }
        }
    }

    public void write(short bitContainer, int nbits) throws IOException {
        if (nbits == 0)
            return;

        if (nbits < 1 || nbits > Short.SIZE)
            throw new IllegalArgumentException("Expected 1 to 16 bits");


        if (nbits <= 8)
            write((byte) bitContainer, nbits);
        else {
            for (int i = nbits - 8; i >= 0; i -= 8) {
                byte v = (byte) (bitContainer >>> i);
                write(v);
            }
            if (nbits % 8 != 0) {
                byte v = (byte) bufferByte;
                write(v, nbits % 8);
            }
        }

    }

    public void write(int bitContainer, int nbits) throws IOException {
        if (nbits == 0)
            return;

        if (nbits < 1 || nbits > Integer.SIZE)
            throw new IllegalArgumentException("Expected 1 to 32 bits");

        if (nbits <= 8)
            write((byte) bitContainer, nbits);
        else {
            for (int i = nbits - 8; i >= 0; i -= 8) {
                byte v = (byte) (bitContainer >>> i);
                write(v);
            }
            if (nbits % 8 != 0) {
                byte v = (byte) bufferByte;
                write(v, nbits % 8);
            }
        }

    }

    public void write(long bitContainer, int nbits) throws IOException {
        if (nbits == 0)
            return;

        if (nbits < 1 || nbits > Long.SIZE)
            throw new IllegalArgumentException("Expected 1 to 64 bits");

        if (nbits <= 8)
            write((byte) bitContainer, nbits);
        else {
            for (int i = nbits - 8; i >= 0; i -= 8) {
                byte v = (byte) (bitContainer >>> i);
                write(v);
            }
            if (nbits % 8 != 0) {
                final byte v = (byte) bitContainer;
                write(v, nbits % 8);
            }
        }
    }


    public void padToByte() throws IOException {
        int padAmt = Math.min(8,8-(bufferedNumberOfBits%8));
        bufferedNumberOfBits+=padAmt;
        out.write(bufferByte);
        bufferByte           = 0;
        bufferedNumberOfBits = 0;
    }

    public void padToNibble() throws IOException {
        int padAmt = Math.min(4,4-(bufferedNumberOfBits%4));
        bufferedNumberOfBits+=padAmt;
        if(bufferedNumberOfBits == 8){
            out.write(bufferByte);
            bufferByte = 0;
            bufferedNumberOfBits = 0;
        }
    }

    @Override
    public void close() throws IOException {
        flush();
        out.close();
    }

    @Override
    public void flush() throws IOException {
        if(bufferedNumberOfBits != 0){
            out.write(bufferByte);
        }
        bufferedNumberOfBits = 0;
        bufferByte           = 0;
    }
}
