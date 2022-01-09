package huffman;

import huffman.io.BitOutputStream;

import java.io.*;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;

public class HuffmanMain {

    public static void main(String[] args) throws IOException, InterruptedException {
//          var bos = new BitOutputStream(new BufferedOutputStream(new FileOutputStream(new File("test.bin"))));
//          bos.write(false);
//          bos.write(true);
//          bos.padToByte();
//          bos.write(true);
//          bos.write((byte) 0b01001001);
//          bos.write((byte) 0b01111111);
//          bos.close();
//        char[] literals = new char[]{'a','b','c','d','e','f'};
//        int[] count     = new int[]{45,13,12,16,9,5};
//        BufferedWriter writer = new BufferedWriter(new FileWriter("ex1.txt"));
//        for (int i = 0; i < literals.length; i++) {
//            for (int j = 0; j < count[i]; j++) {
//                writer.write(literals[i]);
//            }
//        }
//        writer.close();
        String filename = "ex1.txt";
        //var path      = new File("/media/amilo/Data/Users/polit/Projects/Java/University/Algorithms/Algorithms-project/gbbct10.seq").getPath();
        var path        = new File("/media/amilo/Data/Users/polit/Projects/Java/University/Algorithms/Algorithms-project/bigfile.txt").getPath();
        var blcksize    = 536_870_912;
        blcksize        = 268_435_456;
        //blcksize        = 20;

        long startTime  = System.nanoTime();
        var fhw       = new ParallelHuffmanEncoder(Path.of(path),blcksize,5);
        long endTime  = System.nanoTime();

        System.out.println("Duration: "+(endTime - startTime)/(1000000000));

        startTime  = System.nanoTime();
        fhw.constructHuffmanTree();
        endTime  = System.nanoTime();
        System.out.println("Duration: "+(endTime - startTime)/(1000000000));

//        final FileChannel channel = new FileInputStream(filename).getChannel();
//        byte[] by = new byte[(int) channel.size()];
//
//        MappedByteBuffer buffer = channel.map(
//                FileChannel.MapMode.READ_ONLY,
//                0, channel.size());
//        int i = 0;
//        long startTime  = System.nanoTime();
//
//        while(buffer.hasRemaining()){
//           buffer.get();
//        }
//        long endTime  = System.nanoTime();
//        System.out.println("Duration: "+(endTime - startTime)/(1000000000));
//        channel.close();
    }
}
