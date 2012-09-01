package simulator.conf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class SConfigParser {
	private final String fileAdress;

	public SConfigParser(String fileAdress) {
		this.fileAdress = fileAdress;
	}
	
	public SOptions parse() {
		SOptions opts = new SOptions("main");
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(
					fileAdress)));
			String line = "";
			while ((line = reader.readLine()) != null) {
				String[] parts = line.split("=");
				opts.addOpt(parts[0], parts[1]);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return opts;
	}
}
