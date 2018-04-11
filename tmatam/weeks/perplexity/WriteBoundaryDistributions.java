package perplexity;

import java.io.*;
import java.util.Arrays;
public class WriteBoundaryDistributions {
	/*
	 * Run as:
	 * java -cp atlf.jar:commons-io-2.4.jar evaluation.WriteBoundaryDistributions 
	 * topicailmentdistributions/ topicailmenttraining/
	 */
	public static void writeFile(String sourcepath,String destinationPath){
		try{
			FileInputStream fileInputStream = new FileInputStream
					(sourcepath);
			PrintWriter out  = new PrintWriter(new BufferedWriter
					(new FileWriter(destinationPath,true)));

			DataInputStream in = new DataInputStream(fileInputStream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in,"UTF-8"));
			String strLine;
			while((strLine = br.readLine())!=null){
				int indexofDelim = strLine.indexOf("#|#");
				String remainingLine = strLine.substring(indexofDelim+3);
				String [] array = remainingLine.split("\\|:\\|");
				for (int i = 0 ; i < array.length ; i++){
					if(i <= array.length - 2)
						out.write(array[i].substring(array[i].indexOf(":")+1)+",");
					else
						out.write(array[i].substring(array[i].indexOf(":")+1)+"\n");
				}
			}
			out.close();
			br.close();
			in.close();
			fileInputStream.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	public static void main(String [] args){
		File dir = new File(args[0]); //path up to inferences
		String[] chld = dir.list();
		Arrays.sort(chld);
		for(int chldIndex = 0 ; chldIndex < chld.length ; chldIndex++){
			if(new File(args[0]+"/"+chld[chldIndex]).isDirectory()){
				File dirATAM = new File(args[0]+"/"+chld[chldIndex]); // args[0] = distributions/region
				String[] chldATAM = dirATAM.list();
				Arrays.sort(chldATAM);
				for(int chldATAMIndex = 0 ; chldATAMIndex < chldATAM.length; chldATAMIndex++){
					//					new File(args[1]+chld[chldIndex]+"/"+chldATAM[chldATAMIndex]+"/ailments").mkdirs(); 
					// args[1] = training/region
					new File(args[1]+chld[chldIndex]+"/"+chldATAM[chldATAMIndex]+"/topics").mkdirs();
					//					WriteBoundaryDistributions.writeFile
					//					(dirATAM+"/"+chldATAM[chldATAMIndex]+"/ailments/ailmentshistorical", args[1]+chld[chldIndex]+
					//							"/"+chldATAM[chldATAMIndex]+"/ailments/trainfile" );
					//					WriteBoundaryDistributions.writeFile
					//					(dirATAM+"/"+chldATAM[chldATAMIndex]+"/ailments/ailmentspresent", args[1]+chld[chldIndex]+"/"+chldATAM[chldATAMIndex]+
					//							"/ailments/testfile" );
					WriteBoundaryDistributions.writeFile
					(dirATAM+"/"+chldATAM[chldATAMIndex]+"/topics/topicshistorical", args[1]+chld[chldIndex]+"/"+chldATAM[chldATAMIndex]+
							"/topics/trainfile" );
					WriteBoundaryDistributions.writeFile
					(dirATAM+"/"+chldATAM[chldATAMIndex]+"/topics/topicspresent", args[1]+chld[chldIndex]+"/"+chldATAM[chldATAMIndex]+
							"/topics/testfile" );		
				}
			}
		}
	}

}
