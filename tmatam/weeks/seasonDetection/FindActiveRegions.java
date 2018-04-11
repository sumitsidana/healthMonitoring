package seasonDetection;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;

public class FindActiveRegions {

	/*
	 * /media/toshibasecond/healthmonitoringovertime/weeks
	 * /media/toshibasecond/healthmonitoringovertime/twitterintensiveregions
	 */
	public  void listFilesForFolder(final String sourceFolder, String destinationFolder ) throws IOException {
		File dir = new File(sourceFolder);
		String[] chld = dir.list();
		Arrays.sort(chld);
		for(int i = 0; i < chld.length ; i++){
			//	    for (final File fileEntry : folder.listFiles()) {
			File fileEntry = new File(dir+"/"+chld[i]);
			if (fileEntry.isDirectory()) {
				if(fileEntry.list().length>=34){
					File destination = new File(destinationFolder+"/"+chld[i]);
					FileUtils.copyDirectory(fileEntry,destination);
				}
			}    
		}
	}
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		final String folder = new String(args[0]);
		String destinationFolder = args[1];
		new FindActiveRegions().listFilesForFolder(folder,destinationFolder);
	}
}
