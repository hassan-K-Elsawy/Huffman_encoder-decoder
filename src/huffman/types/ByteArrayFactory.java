package huffman.types;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

public class ByteArrayFactory {
    private static ConcurrentHashMap<Integer,ImmutableByteArray> instances  = new ConcurrentHashMap<>(100);
    public static ImmutableByteArray newByteArray(byte[] b){
        int hash  = Arrays.hashCode(b);
        if(instances.containsKey(hash)){
            return instances.get(hash);
        }else{
            var o = new ImmutableByteArray(b);
            instances.put(hash,o);
            return o;
        }
    }
}
