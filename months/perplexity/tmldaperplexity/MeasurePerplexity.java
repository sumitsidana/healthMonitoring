package tmldaperplexity;

import java.io.*;
import java.util.*;
import java.math.*;

public class MeasurePerplexity {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws NumberFormatException 
	 */
	public static void main(String[] args) throws NumberFormatException, IOException {
		// TODO Auto-generated method stub
		FileInputStream fstream = new FileInputStream
				(args[2]);
		DataInputStream in = new DataInputStream(fstream);
		BufferedReader br = new BufferedReader
				(new InputStreamReader(in));
		String line = null;
		Map<String,Double>wordProbabilities = new LinkedHashMap<String,Double>();
		while((line = br.readLine())!=null){
			String [] array = line.split("##,##");
			wordProbabilities.put(array[0], Double.parseDouble(array[1]));
		}
		File dir = new File(args[0]); //path up to InvertedIndex
		String[] chld = dir.list();
		Arrays.sort(chld);



		for(int chldIndex = 0 ; chldIndex < chld.length ; chldIndex++){
			if(new File(args[0]+"/"+chld[chldIndex]).isDirectory()){
				File dirRegion = new File(args[0]+"/"+chld[chldIndex]);
				String[] chldRegion = dirRegion.list();
				Arrays.sort(chldRegion);
				new File(args[1]+"/"+chld[chldIndex]).mkdirs();
				for(int chldBoundaryIndex = 0 ; chldBoundaryIndex
						< chldRegion.length ; chldBoundaryIndex++){
					FileInputStream fstreamInvIndexBD = new FileInputStream
							(args[0]+"/"+chld[chldIndex]+"/"+
									chldRegion[chldBoundaryIndex]);
					DataInputStream inInvIndexBD = new DataInputStream(fstreamInvIndexBD);
					BufferedReader brInvIndexBD = new BufferedReader
							(new InputStreamReader(inInvIndexBD));
					String lineInvIndexBD = null;
					double sumofLogs = 0.0;
					while((lineInvIndexBD = brInvIndexBD.readLine())!=null){
						int indexofColon = lineInvIndexBD.lastIndexOf(":");
						double predictedProbabilityofWord = wordProbabilities.
								get(lineInvIndexBD.substring(0, indexofColon));
						double logofProb = Math.log
								(predictedProbabilityofWord)/Math.log(2);
						sumofLogs = sumofLogs+logofProb;
					}
					sumofLogs = sumofLogs * (-1);
					double perplexity = Math.pow(2, sumofLogs);
					PrintWriter out  = new PrintWriter(new BufferedWriter
							(new FileWriter(args[1]+"/"+chld[chldIndex]+"/"+
									chldRegion[chldBoundaryIndex]+
									"",true)));
					out.write(perplexity+"");
					out.close();
				}	
			}
		}
	}

}
