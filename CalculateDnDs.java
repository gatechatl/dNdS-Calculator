
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;

public class CalculateDnDs {	
	public static void main(String[] args) {		
		try {
			String fileName = args[0];
			int count = 0;			
			String outputFileFor = "forward_macse.input.fa";            

			
			FileInputStream fstream = new FileInputStream(fileName);
			DataInputStream din = new DataInputStream(fstream); 
			BufferedReader in = new BufferedReader(new InputStreamReader(din));
            boolean findBat = false;
            String str1 = "";
            String str2 = "";
            String title1 = "";
            String title2 = "";
			while (in.ready()) {
				String str = in.readLine();
				if (str.contains(">")) {
					if (count % 2 == 0 && count != 0) {
						GrabGoodAlignment(outputFileFor, str1, str2, title2, title1);
						calculateKsKa(outputFileFor);                        
    						
					}				
                                        if (count % 2 == 0) {
                                            title1 = str;
                                        } else {
                                            title2 = str;
                                        }
		
					count++;
				} else if (count % 2 == 1) {
					str1 = str;
				} else if (count % 2 == 0) {
					str2 = str;
				}
			                
			}                        			
			in.close();
			GrabGoodAlignment(outputFileFor, str1, str2, title2, title1);
			calculateKsKa(outputFileFor);			
		} catch (Exception e) {
			e.printStackTrace();			
		}
	}
	
	public static void GrabGoodAlignment(String fileName, String seq, String seq2, String title1, String title2) {
		writeFile("QUERY.fa", ">QUERY" + "\n" + seq2);
		writeFile("TARGET.fa", ">TARGET" + "\n" + seq);
		writeFile("files", "QUERY.fa\nTARGET.fa");
		executeCommand("./align0 < files > output1");
		
		String alignment1 = grabAlignment("output1");
		//System.out.println(alignment1);
		
		String query = alignment1.split("\t")[0];
		String target = alignment1.split("\t")[1];
		String alignment1_trimmed = trimGap(query, target);
		
		
		writeFile("TARGET.fa", ">TARGET" + "\n" + convertReverseCompliment(seq));
		executeCommand("./align0 < files > output2");
		String alignment2 = grabAlignment("output2");
		query = alignment2.split("\t")[0];
		target = alignment2.split("\t")[1];			
		String alignment2_trimmed = trimGap(query, target);
		
		//System.out.println(alignment1_trimmed);
		//System.out.println(alignment2_trimmed);
		//System.out.println(alignment1_trimmed.length());
		//System.out.println(alignment2_trimmed.length());

		try {
			FileWriter fwriterfor = new FileWriter(fileName);
			BufferedWriter outfor = new BufferedWriter(fwriterfor);
			if (alignment1_trimmed.length() > alignment2_trimmed.length()) {				
				outfor.write(title1 + "\n" + alignment1_trimmed.split("\t")[0] + "\n" + title2 + "\n" + alignment1_trimmed.split("\t")[1]);
			} else {
				outfor.write(title1 + "\n" + alignment2_trimmed.split("\t")[0] + "\n" + title2 + "\n" + alignment2_trimmed.split("\t")[1]);
			}
			outfor.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
        public static String revcompl(String str) {
            str = str.toUpperCase();
            String finalstr = "";
            for (int i = str.length() - 1; i >= 0; i--) {
                finalstr += convertBP(str.substring(i, i + 1));
            }
            return finalstr;
        } 
        public static String convertBP(String c) {
            if (c.equals("A")) {
                return "T";
            }
            if (c.equals("G")) {
                return "C";
            }
            if (c.equals("T")) {
                return "A";
            }
            if (c.equals("C")) {
                return "G";
            }
            return c;
        }
	public static void calculateKsKa(String inputFile) {
		String outputFile = "macse.output.fa";
		runMACSE(inputFile, outputFile);
                //System.out.println("Finished MACSE");
		String actual_output = outputFile + "_DNA.fasta";
		String ensembl = trimMACSEOutput(actual_output, actual_output + ".trimmed.fa").replaceAll(">", "");
                //System.out.println("Finished Trimming");
		String kska = runCODEML(actual_output + ".trimmed.result");		
		//String kska = runCODEML(actual_output);
		System.out.println(ensembl + kska);
		//System.out.println(kska);
	}
	
	public static String runCODEML(String inputFile) {
		String codeml = "bin/codeml";
		executeCommand(codeml);
	        String kska = "";
		try {
			boolean startDnDs = false;
			
			
			FileInputStream fstream = new FileInputStream(inputFile);
			DataInputStream din = new DataInputStream(fstream); 
			BufferedReader in = new BufferedReader(new InputStreamReader(din));
			while (in.ready()) {
				String str = in.readLine();
				if (str.contains("dN/dS (dN, dS)")) {
					startDnDs = true;
				}
				if (startDnDs) {
					if (str.contains("ORGANISM2   ")) {
						String[] split = str.split(" ");
						for (int i = 1; i < split.length; i++) {
							if (!split[i].equals("")) {
								kska += "\t" + split[i].replaceAll("\\(", "").replaceAll("\\)", "");
								//i = split.length;
							}
						}
					}
				}
			}
			in.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return kska;
	}
	public static String trimMACSEOutput(String inputFile, String outputFile) {
		String title = "";
                String title2 = "";
		try {
			boolean batSeq = false;
			
			String seq1 = "";
			String seq1_title = ">ORGANISM1";
			String seq2 = "";
			String seq2_title = ">ORGANISM2";
			FileInputStream fstream = new FileInputStream(inputFile);
			DataInputStream din = new DataInputStream(fstream); 
			BufferedReader in = new BufferedReader(new InputStreamReader(din));
			while (in.ready()) {
				String str = in.readLine();
                                //System.out.println(str);
                                title = str;
                                seq1 = in.readLine();
                                title2 = in.readLine();
                                seq2 = in.readLine();
			        /*if (str.contains(">")) {
					if (str.contains("ENST")) {
						title = str;
                                                seq1 = in.readLine();
					} else {
                                                title2 = str;
                                                seq2 = in.readLine();
						batSeq = true;
					}
				} else {
					if (batSeq) {
						seq2 = str;
					} else {
						seq1 = str;
					}
				}*/
			}
			in.close();
		        //System.out.println(seq1);
                        //System.out.println(seq2);	
			String newSeq1 = "";
			String newSeq2 = "";
			for (int i = 0; i < seq1.length() - 3; i = i + 3) {				
				if (checkGoodAlignment(seq1.substring(i, i + 3), seq2.substring(i, i + 3))) {
					newSeq1 += seq1.substring(i, i + 3);
					newSeq2 += seq2.substring(i, i + 3);
				}								
			}
			
			
			FileWriter fwriter = new FileWriter(outputFile);
			BufferedWriter out = new BufferedWriter(fwriter);
			out.write(seq1_title + "\n" + newSeq1 + "\n" + seq2_title + "\n" + newSeq2);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return title + "-" + title2;
	}
	public static boolean checkGoodAlignment(String codon1, String codon2) {
		if (codon1.contains("!") || codon2.contains("!")) {
			return false;
		}
		if (codon1.contains("---")) {
			return false;
		}
		if (codon2.contains("---")) {
			return false;
		}
		return true;
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


