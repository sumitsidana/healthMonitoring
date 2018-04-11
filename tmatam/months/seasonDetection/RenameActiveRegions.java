package seasonDetection;
import java.util.*;
import java.io.*;

import org.apache.commons.io.FileUtils;
public class RenameActiveRegions {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		FileInputStream fileInputStream = new FileInputStream
				(args[0]);
		DataInputStream in = new DataInputStream(fileInputStream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in,"UTF-8"));
		String strLine;
		Map<String,String>regionIdName = new HashMap<String,String>();
		while((strLine = br.readLine())!=null){
			String [] array = strLine.split("\t");
			regionIdName.put(array[0], array[1]);
		}
		File dir = new File(args[1]); //path up to InvertedIndex
		String[] chld = dir.list();
		Arrays.sort(chld);
		for(int chldIndex = 0 ; chldIndex < chld.length ; chldIndex++){
			File	 source = new File(args[1]+"/"+chld[chldIndex]);
			String regiontobeSearched = chld[chldIndex];
//					.replace(".png", "");

			File	 dest = new File(args[2]+"/"+chld[chldIndex].replace(regiontobeSearched, regionIdName.get(regiontobeSearched)));
			try {
				FileUtils.copyDirectory(source, dest);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
