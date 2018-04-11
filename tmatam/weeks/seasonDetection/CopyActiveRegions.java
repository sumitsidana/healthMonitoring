package seasonDetection;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
public class CopyActiveRegions {
/*
 * /media/toshibasecond/healthmonitoringovertime/twitterintensiveregions/
 * /media/toshibasecond/healthmonitoringovertime/regionaggregation/
 * /media/toshibasecond/healthmonitoringovertime/activeregions/
 */
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		File dir = new File(args[0]); 
		String[] chld = dir.list();
		Arrays.sort(chld);
		for(int chldIndex = 0 ; chldIndex < chld.length ; chldIndex++){
			File source = new File(args[1]+chld[chldIndex]);
			new File(args[2]+chld[chldIndex]).mkdirs();
			File destination = new File(args[2]+chld[chldIndex]);
			try {
			    FileUtils.copyDirectory(source, destination);
			} catch (IOException e) {
			    e.printStackTrace();
			}	
		}
	}

}
