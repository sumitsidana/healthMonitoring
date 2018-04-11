package perplexity;

import java.io.*;
import java.util.*;

public class EntityCounts {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		File dir = new File(args[0]); //path up to wordcounts
		String[] chld = dir.list();
		Arrays.sort(chld);
		for(int chldIndex = 0 ; chldIndex < chld.length ; chldIndex++){
			if(new File(args[0]+"/"+chld[chldIndex]).isDirectory()){
				File dirRegion = new File(args[0]+"/"+chld[chldIndex]);
				String[] chldRegion = dirRegion.list();
				Arrays.sort(chldRegion);
				new File(args[1]+"/"+chld[chldIndex]).mkdirs();
				for(int chldBoundaryIndex = 0 ;
						chldBoundaryIndex < chldRegion.length; chldBoundaryIndex++){
					Map<String,LinkedHashMap<String,Long>>InvertedIndex = new LinkedHashMap
							<String,LinkedHashMap<String,Long>>();
					FileInputStream fstreamAlltweetsBD = new FileInputStream
							(args[0]+"/"+chld[chldIndex]+"/"+chldRegion[chldBoundaryIndex]);
					DataInputStream inAllTweetsBD = new DataInputStream(fstreamAlltweetsBD);
					BufferedReader brAllTweetsBD = new BufferedReader
							(new InputStreamReader(inAllTweetsBD));
					String lineAllTweetsBD = null;
					PrintWriter out  = new PrintWriter(new BufferedWriter
							(new FileWriter(args[1]+"/"+chld[chldIndex]+"/"+
									chldRegion[chldBoundaryIndex]+
									"_entitycount",true)));

					Map<String,Double>entityCount = new 
							LinkedHashMap<String,Double>();
					Map<String,Double>ailmentCounts = new LinkedHashMap<String,Double>();
					Map<String,Double>topicCounts = new LinkedHashMap<String,Double>();
					int numAilments = Integer.parseInt(args[2]);
					int numTopics = Integer.parseInt(args[3]);
					for(int i = 1 ; i <= numTopics ; i++){
						topicCounts.put("Topic "+i, (double) 0);
					}
					for(int i = 1 ; i <= numAilments ; i++){
						ailmentCounts.put("Ailment "+i, (double) 0);
					}

					double ailmentWordCount = 0;
					double backgroundWordCount = 0;
					double totalAilmentWordCount = 0;
					double totalBackgroundWordCount = 0;

					boolean flag_bg = false;
					while((lineAllTweetsBD = brAllTweetsBD.readLine())!=null){
						ailmentWordCount = 0;
						backgroundWordCount = 0;
						flag_bg = false;
						int indexofColon = lineAllTweetsBD.indexOf(":");
						String tempEntity = lineAllTweetsBD.substring(0, indexofColon);
						String entity = null;
						int indexofOpBracket = lineAllTweetsBD.indexOf(" (");
						String numEntityInvoked = null;
						if(indexofOpBracket != -1){
							entity = tempEntity.substring(0,indexofOpBracket);
							int indexofClBracket = lineAllTweetsBD.indexOf(")");
							numEntityInvoked = tempEntity.substring
									(indexofOpBracket+2, indexofClBracket);
							double count = Double.parseDouble(numEntityInvoked);
							if(entity.contains("Topic")){
								topicCounts.put(entity, count);
							}
						}
						else{
							entity = tempEntity;
							flag_bg = true;
						}
						String allTokenStats = lineAllTweetsBD.substring(indexofColon+1);
						if(allTokenStats.equals(""))
						{
							continue;
						}
						String[] singleTokenStats = allTokenStats.split(",");
						for(String tokenVal: singleTokenStats ){
							int indexofSpace = tokenVal.indexOf(" ");
							String token = tokenVal.substring(0, indexofSpace);
							String valueStr = tokenVal.substring(indexofSpace+1);
							Double value = Double.parseDouble(valueStr);
							if(flag_bg == true){
								totalBackgroundWordCount = totalBackgroundWordCount + value;
								backgroundWordCount  = backgroundWordCount + value;
							}
							else {
								totalAilmentWordCount = totalAilmentWordCount + value;
								ailmentWordCount = ailmentWordCount+value;
							}
						}
						if(entity.contains("Ailment") && flag_bg == false){
							ailmentCounts.put(entity, ailmentWordCount);
						}
					}
					Iterator itAilments = ailmentCounts.entrySet().iterator();
					Iterator itTopics = topicCounts.entrySet().iterator();
					while(itTopics.hasNext()){
						Map.Entry pairs = (Map.Entry)itTopics.next();
						String key = (String) pairs.getKey();
						double doubleValue = (double) pairs.getValue();

						double value = doubleValue;
						entityCount.put(key, value);
					}
					int ailmentTopic = topicCounts.size() + 1;
					int backgroundTopic = topicCounts.size() + 2;
					String topicofAilments = "Topic "+ ailmentTopic;
					String topicofBackgrounds = "Topic "+backgroundTopic;

					//					entityCount.put(topicofAilments,
					//							totalAilmentWordCount);
					entityCount.put(topicofAilments,
							(double) 0);

					entityCount.put(topicofBackgrounds,
							totalBackgroundWordCount);
					while(itAilments.hasNext()){
						Map.Entry pairs = (Map.Entry)itAilments.next();
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
		}
	}
}
