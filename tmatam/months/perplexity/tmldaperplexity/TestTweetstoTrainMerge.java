package tmldaperplexity;
import java.io.*;
import java.util.*;

public class TestTweetstoTrainMerge {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		File dir = new File(args[0]); //path up to wordcounts
		String[] chld = dir.list();
		Arrays.sort(chld);
		PrintWriter out  = new PrintWriter(new BufferedWriter
				(new FileWriter(args[1]+"/"+"mergedtraintestfile",true)));
		for(int chldIndex = 0 ; chldIndex < chld.length ; chldIndex++){
			if(new File(args[0]+"/"+chld[chldIndex]).isDirectory()){
				File dirRegion = new File(args[0]+"/"+chld[chldIndex]);
				String[] chldRegion = dirRegion.list();
				//				Arrays.sort(chldRegion);
				Collections.reverse(Arrays.asList(chldRegion));
				//				new File(args[1]+"/"+chld[chldIndex]).mkdirs();
				for(int chldBoundaryIndex = 0 ;
						chldBoundaryIndex < chldRegion.length; chldBoundaryIndex++){
					FileInputStream fstreamAlltweetsBD = new FileInputStream
							(args[0]+"/"+chld[chldIndex]+"/"+
									chldRegion[chldBoundaryIndex]
									+"/testtweetstotrainon");
					DataInputStream inAllTweetsBD = new DataInputStream(fstreamAlltweetsBD);
					BufferedReader brAllTweetsBD = new BufferedReader
							(new InputStreamReader(inAllTweetsBD));
					String lineAllTweetsBD = null;

					while((lineAllTweetsBD = brAllTweetsBD.readLine())!=null){
						out.println(lineAllTweetsBD);
					}
				}
			}
		}
		out.close();

	}

}
