
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;

public class AlignSequences {

	public static void main(String[] args) {
		
		String fileName = args[0];
		String fileName2 = args[1];
		try {
			String seq = "";
			FileInputStream fstream = new FileInputStream(fileName);
			DataInputStream din = new DataInputStream(fstream); 
			BufferedReader in = new BufferedReader(new InputStreamReader(din));
			while (in.ready()) {
				String str = in.readLine();
				if (!str.contains(">")) {
					seq += str.trim();
				}
			}
			in.close();

			String seq2 = "";
			fstream = new FileInputStream(fileName2);
			din = new DataInputStream(fstream); 
			in = new BufferedReader(new InputStreamReader(din));
			while (in.ready()) {
				String str = in.readLine();
				if (!str.contains(">")) {
					seq2 += str.trim();
				}
			}
			in.close();

			writeFile("QUERY.fa", ">QUERY" + "\n" + seq);
			writeFile("TARGET.fa", ">TARGET" + "\n" + seq2);
			writeFile("files", "QUERY.fa\nTARGET.fa");
			executeCommand("./align0 < files > output1");
			
			String alignment1 = grabAlignment("output1");
			//System.out.println(alignment1);
			
			String query = alignment1.split("\t")[0];
			String target = alignment1.split("\t")[1];
			String alignment1_trimmed = trimGap(query, target);
			
			
			writeFile("QUERY.fa", ">QUERY" + "\n" + convertReverseCompliment(seq));
			executeCommand("./align0 < files > output2");
			String alignment2 = grabAlignment("output2");
			query = alignment2.split("\t")[0];
			target = alignment2.split("\t")[1];			
			String alignment2_trimmed = trimGap(query, target);
			
			System.out.println(alignment1_trimmed);
			System.out.println(alignment2_trimmed);
			System.out.println(alignment1_trimmed.length());
			System.out.println(alignment2_trimmed.length());
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String trimGap(String query, String target) {
		String rquery = "";
		String rtarget = "";
		boolean start = false;
		for (int i = 0; i < query.length(); i++) {
			if (!(query.substring(i, i + 1).equals("-") || target.substring(i, i + 1).equals("-"))) {								
				start = true;
			}
			if (start) {
				rquery += query.substring(i, i + 1);
				rtarget += target.substring(i, i + 1);
			}
		}
		String fquery = "";
		String ftarget = "";
		rquery = convertReverseCompliment(rquery);
		rtarget = convertReverseCompliment(rtarget);
		start = false;
		
		for (int i = 0; i < rquery.length(); i++) {
			if (!(rquery.substring(i, i + 1).equals("-") || rtarget.substring(i, i + 1).equals("-"))) {								
				start = true;
			}
			if (start) {
				fquery += rquery.substring(i, i + 1);
				ftarget += rtarget.substring(i, i + 1);
			}
		}
		return convertReverseCompliment(fquery) + "\t" + convertReverseCompliment(ftarget);
	}
	public static String convertReverseCompliment(String str) {
		str = str.toUpperCase();
		String newstr = "";
		for (int i = str.length() - 1; i >= 0; i--) {
			if (str.substring(i, i + 1).equals("A")) {
				newstr += "T";
			} else if (str.substring(i, i + 1).equals("C")) {
				newstr += "G";
			} else if (str.substring(i, i + 1).equals("G")) {
				newstr += "C";
			} else if (str.substring(i, i + 1).equals("T")) {
				newstr += "A";
			} else {
				newstr += str.substring(i, i + 1);
			}
		}
		return newstr;
	}
	
	public static String grabAlignment(String fileName) {
		
		try {
			String alignment = "";
			String query = "";
			String target = "";
			FileInputStream fstream = new FileInputStream(fileName);
			DataInputStream din = new DataInputStream(fstream); 
			BufferedReader in = new BufferedReader(new InputStreamReader(din));
			
			boolean firstq = false;
			boolean firstT = false;
			while (in.ready()) {
				String str = in.readLine();
				if (firstq && firstT) {
					if (str.contains("QUERY")) {
						String seq = str.split("QUERY")[1].trim();
						query += seq;
					}
					if (str.contains("TARGET")) {
						String seq = str.split("TARGET")[1].trim();
						target += seq;						
					}
				}				
				if (str.contains("QUERY")) {
					firstq = true;
				}
				if (str.contains("TARGET")) {
					firstT = true;
				}
			}
			in.close();
			return query + "\t" + target; 
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	public static void runMACSE(String inputFile, String outputFile) {
		String macse = "java -jar macse_v0.8b2.jar -i " + inputFile + " -o " + outputFile;
		executeCommand(macse);
	}
	
	public static void executeCommand(String executeThis) {
		try {
			writeFile("tempexecuteCommand.sh", executeThis);
	        String[] command = {"sh", "tempexecuteCommand.sh"};
	        Process p1 = Runtime.getRuntime().exec(command);		        
            BufferedReader inputn = new BufferedReader(new InputStreamReader(p1.getInputStream()));            
            String line=null;
            while((line=inputn.readLine()) != null) {}                        
            inputn.close();
             
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void writeFile(String fileName, String command) {
		try {
		    FileWriter fwriter2 = new FileWriter(fileName);
		    BufferedWriter out2 = new BufferedWriter(fwriter2);
		    out2.write(command + "\n");		    		
		    out2.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

