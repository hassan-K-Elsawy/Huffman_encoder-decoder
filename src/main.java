import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class main {

	public static void main(String[] args) {
		try {
			FileInputStream in = new FileInputStream("input test.txt");
			FileOutputStream out = new FileOutputStream("output test.txt");
			BufferedInputStream buff = new BufferedInputStream(in);//improves performance of file input stream (not sure)
			int c;
			byte[] b = new byte[2]; //buffer holding bytes, size equals bytes read at a time (will be 1-5)
			while((c = in.read(b))!= -1) { //read = -1 means end of file
				for(int i=0 ; i<b.length ; i++) //loop printing bytes read per iteration for clarity
					System.out.print(b[i] + "\t");
				System.out.println();
				out.write(b,0,c);
			}

			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
