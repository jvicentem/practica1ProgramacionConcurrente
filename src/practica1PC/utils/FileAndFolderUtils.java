package practica1PC.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.apache.commons.io.FileUtils;

public class FileAndFolderUtils {
	private FileAndFolderUtils() {}
	
	public static void createFolder(String path) {
		File folder = new File(path);
		
		if (folder.isDirectory()) {
			try {
				FileUtils.forceDelete(folder);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}	
		
		folder.mkdirs();
	}
	
	public static void writeFile(String filePath, String text) throws IOException {
		File file = new File(filePath);
		PrintWriter out = new PrintWriter(file);
		out.println(text);
		out.close();		
	}
	
	public static void writeAtEndOfFile(String filePath, String text) throws IOException {
		FileWriter logfile = new FileWriter(filePath, true);
		BufferedWriter logbw = new BufferedWriter(logfile);
		logbw.write(text);
		logbw.newLine();
		logbw.close();		
	}
	
	public static void deleteFileIfExists(String filePath) {
		try {
			Files.deleteIfExists(Paths.get(filePath));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static boolean validFilePath(String filePath) {
		File f = new File(filePath);
		
		return f.exists() && f.isFile();
	}
	
	public static BufferedReader openFile(String filePath) throws FileNotFoundException {
		try {
			return new BufferedReader(new FileReader(filePath));
		} catch (FileNotFoundException e) {
			throw e;
		}
	}

}
