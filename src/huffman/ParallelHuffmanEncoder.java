package huffman;

import huffman.collections.HuffmanTree;
import huffman.io.BitOutputStream;
import huffman.types.ImmutableByteArray;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ParallelHuffmanEncoder {
    final int n;
    final Path path;
    private HuffmanTree tree;
    final long fileBlockSize;
    final ExecutorService executor;

    static final int cores                  = Runtime.getRuntime().availableProcessors();
    static final int maxThreads             = 2*cores + 1;
    List<HuffmanBlockEncoder> encoders;
    HashMap<ImmutableByteArray,Integer> map = new HashMap<>(100);


    public ParallelHuffmanEncoder(Path path,
                                  long blockSize,
                                  int  literalSze) throws IOException {
        long fileBlockSize1;
        this.path          = path;
        this.n             = literalSze;
        fileBlockSize1 = (long) (Math.ceil(blockSize*1.0/n)*n);

        long sze       = Files.size(path);
        int  nthreads  = (int)Math.ceil(sze/ (double) fileBlockSize1);
        nthreads       = Math.min(nthreads,maxThreads);

        fileBlockSize1 = Math.max(sze/nthreads,fileBlockSize1);

        this.fileBlockSize = fileBlockSize1;
        executor       = Executors.newFixedThreadPool(nthreads);
        long offset    = 0;
        long blk       = fileBlockSize;

        encoders      = new ArrayList<>(nthreads*2);

        for (int i = 0; i < nthreads; i++) {
            var thread = new HuffmanBlockEncoder(path,literalSze,offset,blk);
            encoders.add(thread);
            executor.execute(thread);
            System.out.println("Offset: "+offset+" Block-Size: "+blk);
            offset = offset + Math.min(fileBlockSize,sze - fileBlockSize*(i));
            blk    = Math.min(fileBlockSize,sze - (i+1)*fileBlockSize);
        }

        executor.shutdown();
        while (!executor.isTerminated()){};
        int max = 0;
        for (var en: encoders) {
           var innerMap = en.getMap().entrySet();
            for (Map.Entry<ImmutableByteArray,Integer> e: innerMap){
                if(this.map.containsKey(e.getKey())) {
                    int newfreq =  map.get(e.getKey()) + e.getValue();
                    this.map.put(e.getKey(),newfreq);
                    max = Math.max(newfreq,max);
                }else{
                    this.map.put(e.getKey(),e.getValue());
                    max = Math.max(e.getValue(),max);
                }
            }
        }
        //var merged = this.map.entrySet().toArray(new Map.Entry[0]);
        //radixsort(merged, max);
        //this.tree  = new HuffmanTree(merged);
        var tree1  = new HuffmanTree(this.map);
        this.tree= tree1;
        //this.tree.flatten();
        ///System.out.println(this.tree.literalEncodings.size());
        tree1.flatten();
        //System.out.println(this.tree.literalEncodings.equals(tree1.literalEncodings));

    }

    private static class ListNode {
        int index = 0;
        private final Map.Entry<ImmutableByteArray,Integer>[] e ;
        public ListNode(Map.Entry<ImmutableByteArray,Integer>[] lst){
            e = lst;
        }
        public int getFreq(){
            return e[index].getValue();
        }
        public ImmutableByteArray getLiteral(){
            return e[index].getKey();
        }
        public int next(){
            if(index == e.length -1)
                return -1;
            else{
                index++;
                return 1;
            }
        }
    }

    private static void countSort(Map.Entry<ImmutableByteArray, Integer>[] arr, int exp) {
        Map.Entry<ImmutableByteArray,Integer>[] output = new Map.Entry[arr.length];
        int i;
        int count[] = new int[10];
        Arrays.fill(count,0);

        for (i = 0; i < arr.length; i++)
            count[ (arr[i].getValue()/exp)%10 ]++;

        for (i = 1; i < 10; i++)
            count[i] += count[i - 1];

        for (i = arr.length - 1; i >= 0; i--) {
            output[count[ (arr[i].getValue()/exp)%10 ] - 1] = arr[i];
            count[ (arr[i].getValue()/exp)%10 ]--;
        }

        for (i = 0; i < arr.length; i++)
            arr[i] = output[i];
    }

    private static void radixsort(Map.Entry<ImmutableByteArray, Integer>[] arr, int max) {
        for (int exp = 1; max/exp > 0; exp *= 10)
            countSort(arr, exp);
    }

    public HashMap<ImmutableByteArray,Integer> getMap(){
        return this.map;
    }

    public void constructHuffmanTree(){
        //this.tree = new HuffmanTree( this.map);
        //this.tree = new HuffmanTree( this.map);
        //System.out.println(this.map);
        this.tree.printCode(this.tree.root,"");
        this.tree.flatten();
    }

    public void compress(String outFileName) throws FileNotFoundException {
        var bos = new BitOutputStream(new BufferedOutputStream(new FileOutputStream(outFileName)));
        final FileChannel channel;
        try {
            channel = new FileInputStream(path.toFile()).getChannel();

            MappedByteBuffer buffer = channel.map(
                    FileChannel.MapMode.READ_ONLY,
                    0, channel.size());

            while (buffer.hasRemaining()) {
                byte[] literal = new byte[n];
                for (int j = 0; j < n; j++) {
                    literal[j] = buffer.get();
                }
            }
            channel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void decompress(Path filepath){

    }
}