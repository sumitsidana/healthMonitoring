package seasonDetection;
import java.io.*;
import java.util.*;

public class ActiveRegionIds {
/*
 * /media/toshibasecond/healthmonitoringovertime/twitterintensiveregions
 *  /media/toshibasecond/healthmonitoringovertime/dump.regions
 *  /media/toshibasecond/healthmonitoringovertime/activeregionnames
 */
	/**
	 * @param args
	 * @throws IOException 
	 */

	// TODO Auto-generated method stub
	public static void main(String [] args) throws IOException{
		File dir = new File(args[0]); //path up to copyfiles
		String[] chld = dir.list();
		Arrays.sort(chld);
		Set<String> regionIds = new HashSet<String>();
		for(int chldIndex = 0 ; chldIndex < chld.length ; chldIndex++){
			if(new File(args[0]+"/"+chld[chldIndex]).isDirectory()){
				regionIds.add(chld[chldIndex]);
			}
		}
		Map<String,String>regionIdName = new HashMap<String,String>();
		FileInputStream fileInputStream = new FileInputStream
				(args[1]);
		DataInputStream in = new DataInputStream(fileInputStream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in,"UTF-8"));
		String strLine;
		while((strLine = br.readLine())!=null){
			String [] array = strLine.split("\t");
			regionIdName.put(array[0], array[1]);
		}
		PrintWriter out  = new PrintWriter(new BufferedWriter
				(new FileWriter(args[2],true)));
		for(String regionId: regionIds){
//			String name = regionIdName.get(regionId);
//			out.println(name);
			out.println(regionId);
		}
		out.close();
	}
}