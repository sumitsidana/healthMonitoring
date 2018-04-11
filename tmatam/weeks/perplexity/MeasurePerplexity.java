package perplexity;
import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
public class MeasurePerplexity {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
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
							(args[0]+"/"+chld[chldIndex]+"/"+chldRegion[chldBoundaryIndex]);
					DataInputStream inInvIndexBD = new DataInputStream(fstreamInvIndexBD);
					BufferedReader brInvIndexBD = new BufferedReader
							(new InputStreamReader(inInvIndexBD));
					String lineInvIndexBD = null;
					double sumofLogs = 0.0;
					while((lineInvIndexBD = brInvIndexBD.readLine())!=null){
						int indexofColon = lineInvIndexBD.lastIndexOf(":");
						double predictedProbabilityofWord = Double.parseDouble
								(lineInvIndexBD.substring(indexofColon+1));
						double logofProb = Math.log(predictedProbabilityofWord)/Math.log(2);
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
