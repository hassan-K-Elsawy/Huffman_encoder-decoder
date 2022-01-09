package huffman;

import huffman.types.ByteArrayFactory;
import huffman.types.ImmutableByteArray;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.util.*;

public class HuffmanBlockEncoder implements Runnable {
    private final HashMap<ImmutableByteArray, Integer> map;
    private final long offset;
    private final Path path;
    private final int  nbytes;
    private final long fileLen;
    private long MAX_COUNT = 1;

    public HuffmanBlockEncoder(Path path, int n, long offset, long blockSze) {
        this.path     = path;
        this.offset   = offset;
        this.nbytes   = n;
        this.fileLen  = blockSze;
        map           = new HashMap<>(200);
    }

    @Override
    public void run() {
        final FileChannel channel;
        try {
            channel = new FileInputStream(path.toFile()).getChannel();

            MappedByteBuffer buffer = channel.map(
                    FileChannel.MapMode.READ_ONLY,
                    offset, Math.min(fileLen,channel.size()));

            byte[] literal  = new byte[nbytes];

            while (buffer.hasRemaining()) {
                int remaining  =  buffer.remaining();
                if(remaining < nbytes)
                    literal = new byte[remaining];

                for (int j = 0; j < Math.min(nbytes,remaining); j++) {
                    literal[j] = buffer.get();
                }
                var arr = ByteArrayFactory.newByteArray(literal);
                if (map.containsKey(arr)) {
                    var n_freq= map.get(arr) + 1;
                    map.replace(arr, n_freq);
                    MAX_COUNT = Math.max(MAX_COUNT,n_freq);
                } else {
                    map.put(arr, 1);
                }
            }
            channel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public HashMap<ImmutableByteArray,Integer> getMap(){
        return this.map;
    }
}
