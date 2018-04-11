package perplexity;
import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
/*
 * /media/toshibasecond/health/perplexity/predictedwordprobability
 * /media/toshibasecond/health/perplexity/predictedperplexitybiginteger/
 */
public class PerplexityNormalized {
	public static int countLines(String filename) throws IOException {
		InputStream is = new BufferedInputStream(new FileInputStream(filename));
		try {
			byte[] c = new byte[1024];
			int count = 0;
			int readChars = 0;
			boolean empty = true;
			while ((readChars = is.read(c)) != -1) {
				empty = false;
				for (int i = 0; i < readChars; ++i) {
					if (c[i] == '\n') {
						++count;
					}
				}
			}
			return (count == 0 && !empty) ? 1 : count;
		} finally {
			is.close();
		}
	}
	public static void main(String[] args) throws IOException {
		File dir = new File(args[0]); //path up to InvertedIndex
		String[] chld = dir.list();
		Arrays.sort(chld);
		BigDecimal numBoundaries = BigDecimal.ZERO;
		BigDecimal avgPerplexity = BigDecimal.ZERO;
		for(int chldIndex = 0 ; chldIndex < chld.length ; chldIndex++){
			if(new File(args[0]+"/"+chld[chldIndex]).isDirectory()){
				new File(args[1]+"/"+chld[chldIndex]).mkdirs();
				File dirRegion = new File(args[0]+"/"+chld[chldIndex]);
				String[] chldRegion = dirRegion.list();
				Arrays.sort(chldRegion);
				for(int chldBoundaryIndex = 0 ; chldBoundaryIndex
						< chldRegion.length ; chldBoundaryIndex++){
					numBoundaries = numBoundaries.add(BigDecimal.ONE);
					FileInputStream fstreamInvIndexBD = new FileInputStream
							(args[0]+"/"+chld[chldIndex]+"/"+chldRegion[chldBoundaryIndex]);
					DataInputStream inInvIndexBD = new DataInputStream(fstreamInvIndexBD);
					BufferedReader brInvIndexBD = new BufferedReader
							(new InputStreamReader(inInvIndexBD));
					String lineInvIndexBD = null;
					BigDecimal sumofLogs = new BigDecimal("0");
					while((lineInvIndexBD = brInvIndexBD.readLine())!=null){
						System.out.println(lineInvIndexBD);
						int indexofColon = lineInvIndexBD.lastIndexOf(":");
						BigDecimal predictedProbabilityofWord = new BigDecimal
								((lineInvIndexBD.substring(indexofColon+1)));
						if(predictedProbabilityofWord.compareTo(BigDecimal.ZERO)>0){
							BigDecimal logofProb = BigDecimalUtils
									.ln(predictedProbabilityofWord, 5);
							logofProb = logofProb.
									divide(BigDecimalUtils.ln(new BigDecimal("2"), 5),
											RoundingMode.HALF_UP);
							sumofLogs = sumofLogs.add(logofProb);
						}
					}
					sumofLogs = sumofLogs.multiply(new BigDecimal("-1"));
					BigDecimal perplexity = sumofLogs.pow(2);
					String fileToBeMade = "";
					if(chldRegion[chldBoundaryIndex].length() == 98)
						fileToBeMade = chldRegion[chldBoundaryIndex].substring(26, 32);
					else
						fileToBeMade = chldRegion[chldBoundaryIndex].substring(26,31);
					//					PrintWriter out  = new PrintWriter(new BufferedWriter
					//							(new FileWriter(args[1]+"/"+chld[chldIndex]+"/"+
					//									chldRegion[chldBoundaryIndex]+
					//									"",true)));
					PrintWriter out  = new PrintWriter(new BufferedWriter
							(new FileWriter(args[1]+"/"+chld[chldIndex]+"/"+
									fileToBeMade+
									"",true)));
					int numberofDataPoints = countLines
							(args[0]+"/"+chld[chldIndex]+"/"+
									chldRegion[chldBoundaryIndex]);

					out.write(perplexity.divide(new BigDecimal(numberofDataPoints)
					,RoundingMode.HALF_UP)+"\n");
					avgPerplexity = avgPerplexity.add(perplexity);
					out.close();
				}
			}
		}
		avgPerplexity = avgPerplexity.divide(numBoundaries,RoundingMode.HALF_UP);
		System.out.println("number of Boundaries: "+numBoundaries);
		System.out.println("Average Perplexity: "+avgPerplexity);
	}
}
