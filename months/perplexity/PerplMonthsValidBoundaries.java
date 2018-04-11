package util;
import java.io.*;
import java.util.*;

import org.apache.commons.io.FileUtils;
public class PerplMonthsValidBoundaries {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		File dir = new File(args[0]);
		String[] chld = dir.list();
		Arrays.sort(chld);
		for(int chldIndex = 0 ; chldIndex < chld.length ; chldIndex++){
			if(new File(args[0]+"/"+chld[chldIndex]).isDirectory()){
				File dirRegion = new File(args[0]+"/"+chld[chldIndex]);
				String[] chldRegion = dirRegion.list();
				Arrays.sort(chldRegion);
				new File(args[2]+"/"+chld[chldIndex]).mkdirs();
				for(int chldBoundaryIndex = 0 ; chldBoundaryIndex
						< chldRegion.length ; chldBoundaryIndex++){
					File source = new File(args[1]+"/"+chld[chldIndex]+"/" +
							"InputToATAMUsingLemmatizer"+
							chldRegion[chldBoundaryIndex]+".assign");
					File dest = new File(args[2]+chld[chldIndex]+"/"+
							chldRegion[chldBoundaryIndex]);
					try {
						FileUtils.copyFile(source, dest);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}

	}

}
