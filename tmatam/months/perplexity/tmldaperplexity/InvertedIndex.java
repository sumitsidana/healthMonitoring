package tmldaperplexity;
import java.io.*;
import java.util.*;
public class InvertedIndex {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		FileInputStream fstreamAlltweetsBD = new FileInputStream
				(args[0]);
		DataInputStream inAllTweetsBD = new DataInputStream(fstreamAlltweetsBD);
		BufferedReader brAllTweetsBD = new BufferedReader
				(new InputStreamReader(inAllTweetsBD));
		String lineAllTweetsBD = null;
		PrintWriter out  = new PrintWriter(new BufferedWriter
				(new FileWriter(args[1],true)));
		Map<String,LinkedHashMap<String,Long>>InvertedIndex = new LinkedHashMap
				<String,LinkedHashMap<String,Long>>();

		while((lineAllTweetsBD = brAllTweetsBD.readLine())!=null){
			int indexofColon = lineAllTweetsBD.indexOf(":");
			String entity = lineAllTweetsBD.substring(0, indexofColon);
			String allTokenStats = lineAllTweetsBD.substring(indexofColon+1);
			String[] singleTokenStats = allTokenStats.split("\\|\\|#\\|\\|");
			for(String tokenVal: singleTokenStats){
				int indexofSpace = tokenVal.indexOf(" ");
				String token = tokenVal.substring(0, indexofSpace);
				String valueStr = tokenVal.substring(indexofSpace+1);
				Long value = Long.parseLong(valueStr);
				LinkedHashMap<String,Long>entityStats = null;
				if(InvertedIndex.containsKey(token)){
					entityStats = InvertedIndex.get(token);
				}
				else{
					entityStats = new LinkedHashMap
							<String,Long>();
				}

				entityStats.put(entity, value);
				InvertedIndex.put(token, entityStats);
			}
		}
		Iterator it = InvertedIndex.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry pairs = (Map.Entry)it.next();
			String token = (String) pairs.getKey();
			LinkedHashMap<String,Long> entityStats = (LinkedHashMap<String, Long>)
					pairs.getValue();
			Iterator statsIterator = entityStats.entrySet().iterator();
			out.write(token+"##,##");
			while(statsIterator.hasNext()){
				Map.Entry entityValuePair = (Map.Entry)statsIterator.next();
				String entity = (String) entityValuePair.getKey();
				Long value = (Long) entityValuePair.getValue();
				out.write(entity+":"+value+",");
			}
			out.println();
		}
		out.close();

	}

}
