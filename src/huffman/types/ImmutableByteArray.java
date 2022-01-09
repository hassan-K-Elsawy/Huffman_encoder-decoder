package huffman.types;

import java.util.Arrays;

public class ImmutableByteArray {
    private final byte[] barr;

    public ImmutableByteArray(byte[] arr){
        barr = arr.clone();
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.barr);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)  return true;
        if (o == null)  return false;
        if (getClass() != o.getClass()) return false;
        ImmutableByteArray oarr = (ImmutableByteArray) o;
        return Arrays.equals(this.barr, oarr.barr);
    }

    @Override
    public String toString() {
        return new String(this.barr);
    }
}
