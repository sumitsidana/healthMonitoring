package seasonDetection;
import java.io.*;
import java.util.*;

public class TweetDistributionActiveRegions {
/*
 * /media/toshibasecond/healthmonitoringovertime/activeregions
 * /media/toshibasecond/healthmonitoringovertime/regioncount
 * /media/toshibasecond/healthmonitoringovertime/dump.regions
 */
	/**
	 * @param args
	 * @throws IOException 
	 */
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
		// TODO Auto-generated method stub	
		Map<String,Integer>regionalCount = new LinkedHashMap<String,Integer>();
		PrintWriter out  = new PrintWriter(new BufferedWriter
				(new FileWriter
						(args[1],true)));
		File dir = new File(args[0]); //path up to wordcounts
		String[] chld = dir.list();
		Arrays.sort(chld);
		for(int chldIndex = 0 ; chldIndex < chld.length ; chldIndex++){
			if(new File(args[0]+"/"+chld[chldIndex]).isDirectory()){
				regionalCount.put
				(chld[chldIndex], 
						countLines(args[0]+"/"+chld[chldIndex]+"/"+"fullfile"));
			}
		}


		List<Map.Entry<String, Integer>> entries =
				new ArrayList<Map.Entry<String, Integer>>(regionalCount.entrySet());
		Collections.sort(entries, new Comparator<Map.Entry<String, Integer>>() {
			public int compare(Map.Entry<String, Integer> a, Map.Entry<String, Integer> b){
				return a.getValue().compareTo(b.getValue());
			}
		});
		Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
		for (Map.Entry<String, Integer> entry : entries) {
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		
		Map<String,String>regionIdName = new HashMap<String,String>();
		FileInputStream fileInputStream = new FileInputStream
				(args[2]);
		DataInputStream in = new DataInputStream(fileInputStream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in,"UTF-8"));
		String strLine;
		while((strLine = br.readLine())!=null){
			String [] array = strLine.split("\t");
			regionIdName.put(array[0], array[1]);
		}

		Iterator it = sortedMap.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry pairs = (Map.Entry)it.next();
			String region = (String) pairs.getKey();
			int value = (Integer) pairs.getValue();
			out.println(regionIdName.get(region)+":"+value);
		}
		out.close();
	}
}
