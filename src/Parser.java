import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Parser {
	
	private final String FILENAME = "TrafficCounters.csv";
	private BufferedReader br;
	
	
	public Parser() throws FileNotFoundException {
		br = new BufferedReader(new FileReader(this.FILENAME));
	}
	
	
	public Map<String, Double> readFile() throws IOException {
	
		Map<String, Double> sensors = new HashMap<>();
		String line = br.readLine();
		line = br.readLine();
		do {
//			System.out.println(line);
			String[] lineArray = line.split(",");
			sensors.put(lineArray[0], Double.parseDouble(lineArray[1]));
			line = br.readLine();
		} while(line != null);
		
		return sensors;
	}

//	public static void main (String[] args) throws Exception {
//		Parser parser = new Parser();
//		Map<String, Double> sensors = parser.readFile();
//		Set<String> ids = sensors.keySet();
//	}
	
	
}
