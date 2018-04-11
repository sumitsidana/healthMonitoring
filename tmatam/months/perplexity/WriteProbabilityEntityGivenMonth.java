package perplexity;
import java.io.*;
import java.util.*;
public class WriteProbabilityEntityGivenMonth{

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
					FileInputStream fstreamProbEnGivWordBD = new FileInputStream
							(args[0]+"/"+chld[chldIndex]+"/"+chldRegion[chldBoundaryIndex]);
					DataInputStream inProbEnGivWordBD = new DataInputStream(fstreamProbEnGivWordBD);
					BufferedReader brProbEnGivWordBD = new BufferedReader
							(new InputStreamReader(inProbEnGivWordBD));
					String lineProbEnGivWordBD = null;
					PrintWriter out  = new PrintWriter(new BufferedWriter
							(new FileWriter(args[1]+"/"+chld[chldIndex]+"/"+
									chldRegion[chldBoundaryIndex]+
									"",true)));
					while((lineProbEnGivWordBD = brProbEnGivWordBD.readLine())!=null){
                                        out.println("\"\",\"V1\",\"V2\",\"V3\",\"V4\",\"V5\",\"V6\",\"V7\",\"V8\",\"V9\",\"V10\",\"V11\",\"V12\",\"V13\",\"V14\",\"V15\",\"V16\",\"V17\",\"V18\",\"V19\",\"V20\",\"V21\",\"V22\",\"V23\",\"V24\",\"V25\",\"V26\",\"V27\",\"V28\",\"V9\",\"V30\",\"V31\",\"V32\",\"V33\",\"V34\",\"V35\",\"V36\",\"V37\",\"V38\",\"V39\",\"V40\",\"V41\",\"V42\",\"V43\",\"V44\",\"V45\",\"V46\",\"V47\",\"V48\",\"V49\",\"V50\",\"V51\",\"V52\"");
                                                out.print("\"1\",");
						String [] array = lineProbEnGivWordBD.split(",");
						for(int i = 0 ; i < array.length ; i++){
							int indexofColon = array[i].indexOf(":");
							String value = array[i].substring(indexofColon+1);
							if(i <= array.length - 2)
							out.write(value+",");
							else
								out.write(value);
						}
						out.close();
					}
				}
			}
		}


	}

}
