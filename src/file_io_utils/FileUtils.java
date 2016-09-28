package file_io_utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileUtils {
	private File file;
	private BufferedWriter bw;
	
	public FileUtils(File f) throws IOException {
		file = f;
		bw = new BufferedWriter(new FileWriter(file));
	}
	
	public void writeString(String str) throws IOException {
		bw.write(str);
		bw.newLine();
		bw.flush();	
	}
	
	public void finish() throws IOException {
		bw.flush();
		bw.close();
	}

}
