package seasonDetection;
import java.io.*;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;

public class GetDistributions {
	/*
	 * Run as:
	 * java -cp atlf.jar:commons-io-2.4.jar operations.GetDistributions copyfiles/ distributions/
	 * Assumes distributions as empty directory.
	 * Assumes commons-io-2.4.jar in the directory from which class is being executed
	 */

	public static void main(String [] args){
		File dir = new File(args[0]); //path up to copyfiles
		String[] chld = dir.list();
		Arrays.sort(chld);

		for(int chldIndex = 0 ; chldIndex < chld.length ; chldIndex++){
			if(new File(args[0]+"/"+chld[chldIndex]).isDirectory()){
				File dirATAM = new File(args[0]+"/"+chld[chldIndex]);
				String[] chldATAM = dirATAM.list();
				Arrays.sort(chldATAM);
				//				new File(args[1]+chld[chldIndex]+"/ailments").mkdirs();
				new File(args[1]+chld[chldIndex]+"/topics").mkdirs();
				//				File source = new File(args[0]+"/"+chld[chldIndex]+"/"+"ailments");
				//				File dest = new File(args[1]+chld[chldIndex]+"/ailments");
				//				try {
				//				    FileUtils.copyDirectory(source, dest);
				//				} catch (IOException e) {
				//				    e.printStackTrace();
				//				}
				//				 source = new File(args[0]+"/"+chld[chldIndex]+"/"+"ailments");
				//				 dest = new File(args[1]+chld[chldIndex]+"/ailments");
				//				try {
				//				    FileUtils.copyDirectory(source, dest);
				//				} catch (IOException e) {
				//				    e.printStackTrace();
				//				}

				File	 source = new File(args[0]+"/"+chld[chldIndex]+"/"+"topics");
				File	 dest = new File(args[1]+chld[chldIndex]+"/topics/");
				try {
					FileUtils.copyDirectory(source, dest);
				} catch (IOException e) {
					e.printStackTrace();
				}

				//				 source = new File(args[0]+"/"+chld[chldIndex]+"/"+"topics");
				//				 dest = new File(args[1]+chld[chldIndex]+"/topics/");
				//				try {
				//				    FileUtils.copyDirectory(source, dest);
				//				} catch (IOException e) {
				//				    e.printStackTrace();
				//				}			
			}
		}
	}
}