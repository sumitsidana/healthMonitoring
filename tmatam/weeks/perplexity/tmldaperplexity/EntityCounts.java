package tmldaperplexity;
import java.io.*;
import java.util.*;
public class EntityCounts {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		Map<String,LinkedHashMap<String,Long>>InvertedIndex = new LinkedHashMap
				<String,LinkedHashMap<String,Long>>();
		FileInputStream fstreamAlltweetsBD = new FileInputStream
				(args[0]);
		DataInputStream inAllTweetsBD = new DataInputStream(fstreamAlltweetsBD);
		BufferedReader brAllTweetsBD = new BufferedReader
				(new InputStreamReader(inAllTweetsBD));
		String lineAllTweetsBD = null;
		PrintWriter out  = new PrintWriter(new BufferedWriter
				(new FileWriter(args[1],true)));

		Map<String,Double>entityCount = new 
				LinkedHashMap<String,Double>();

		Map<String,Double>topicCounts = new LinkedHashMap<String,Double>();
		int numTopics = Integer.parseInt(args[2]);
		for(int i = 1 ; i <= numTopics ; i++){
			topicCounts.put("Topic "+i, (double) 0);
		}

		double backgroundWordCount = 0;
		double topicWordCount = 0;


		int backgroundTopic = topicCounts.size() + 1;
		String topicofBackgrounds = "Topic "+backgroundTopic;

		boolean flag_bg = false;
		while((lineAllTweetsBD = brAllTweetsBD.readLine())!=null){
			backgroundWordCount = 0;
			topicWordCount = 0;
			flag_bg = false;
			int indexofColon = lineAllTweetsBD.indexOf(":");
			String entity = lineAllTweetsBD.substring(0, indexofColon);
			if(entity.contains("Background")){
				flag_bg = true;
			}
			String allTokenStats = lineAllTweetsBD.substring(indexofColon+1);
			String[] singleTokenStats = allTokenStats.split("\\|\\|#\\|\\|");
			for(String tokenVal: singleTokenStats ){
				int indexofSpace = tokenVal.indexOf(" ");
				String token = tokenVal.substring(0, indexofSpace);
				String valueStr = tokenVal.substring(indexofSpace+1);
				Double value = Double.parseDouble(valueStr);
				if(flag_bg == true){
					backgroundWordCount  = backgroundWordCount + value;
					topicCounts.put(topicofBackgrounds, backgroundWordCount);
				}
				else {
					topicWordCount = topicWordCount+value;
				}
			}
			topicCounts.put(entity, topicWordCount);


		}
		Iterator itTopics = topicCounts.entrySet().iterator();
		while(itTopics.hasNext()){
			Map.Entry pairs = (Map.Entry)itTopics.next();
			String key = (String) pairs.getKey();
			double doubleValue = (double) pairs.getValue();

			double value = doubleValue;
			entityCount.put(key, value);
		}
		Iterator itEntity = entityCount.entrySet().iterator();
		while(itEntity.hasNext()){
			Map.Entry pairs = (Map.Entry)itEntity.next();
			String entity = (String) pairs.getKey();
			double count = (double) pairs.getValue();
			out.println(entity+":"+count);
		}
		out.close();




	}

}
