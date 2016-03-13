package practica1PC;

import java.io.FileNotFoundException;
import java.nio.file.NotDirectoryException;

public class Prueba {
	public static void main(String[] args) {
		WebProcessor wp = new WebProcessor("./websites",5,2);
		try {
			wp.process("./top_sites_themoz.txt");
		} catch (FileNotFoundException | NotDirectoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
