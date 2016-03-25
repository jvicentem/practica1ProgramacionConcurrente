package practica1PC;

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
	
	public static void createFolder(String path) {
		File folder = new File(path);
		
		if(folder.isDirectory()){
			try {
				FileUtils.forceDelete(folder);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			folder.mkdirs();
		}		
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
		try{
			Files.deleteIfExists(Paths.get(filePath));
		} catch(IOException e) {
			
		}
	}
	
	public static boolean validFilePath(String filePath) {
		File f = new File(filePath);
		
		return f.exists() && f.isFile();
	}
	
	//ADVERTENCIA: Usar este método sólo cuando se esté seguro de que el archivo existe
	public static BufferedReader openFileNoException(String filePath) {
		try {
			return new BufferedReader(new FileReader(filePath));
		} catch(FileNotFoundException e) {
			return null;
		}
	}
}
