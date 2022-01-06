import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;

public class compareTwoFiles {
    public boolean memoryMappedFiles(Path path1 , Path path2) {
        boolean checker = false;
        try {
            /**Using RandomAccessFile class to open the file using it's path **/
            RandomAccessFile file1 = new RandomAccessFile(path1.toFile(), "r");
            RandomAccessFile file2 = new RandomAccessFile(path2.toFile(), "r");

            /**Access the file channels **/
            FileChannel channel1 = file1.getChannel();
            FileChannel channel2 = file2.getChannel();

            /**If the channels of the 2 files not equal then 2 files are different**/
            if (channel1.size() != channel2.size())
                return false;

            /**The size of the channel of the first file which is equal to the 2nd file**/
            long size = channel1.size();

            /**mapping the files in the mem.**/
            MappedByteBuffer map1 = channel1.map(FileChannel.MapMode.READ_ONLY, 0L, size);
            MappedByteBuffer map2 = channel2.map(FileChannel.MapMode.READ_ONLY, 0L, size);

            checker = map1.equals(map2);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  checker;
    }
}
