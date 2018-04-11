package tmldaperplexity;
import java.io.*;
import java.util.*;
public class PredictedWordProbabilities {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		Set<String> stopWords = new HashSet<String>();
		FileInputStream fileInputStreamSW = new FileInputStream
				(args[3]);
		DataInputStream dataInputStreamSW = new DataInputStream(fileInputStreamSW);
		BufferedReader bufferedReaderSW = new BufferedReader
				(new InputStreamReader(dataInputStreamSW));
		String strLineSW;
		while((strLineSW = bufferedReaderSW.readLine())!=null)
		{
			stopWords.add(strLineSW.toLowerCase());
		}
		int numTopics = Integer.parseInt(args[4]);

		FileInputStream fstreamInvIndexBD = new FileInputStream
				(args[0]);
		DataInputStream inInvIndexBD = new DataInputStream(fstreamInvIndexBD);
		BufferedReader brInvIndexBD = new BufferedReader
				(new InputStreamReader(inInvIndexBD));
		String lineInvIndexBD = null;
		Map<String,Map<String,Double>>invertedIndex = new
				HashMap<String,Map<String,Double>>();
		Map<String,Double>entityCounts = new LinkedHashMap<String,Double>();
		Map<String,Double>entityProb = new LinkedHashMap<String,Double>();
		//					Map<String,Double>tokenCountTrainingSet = new
		//							LinkedHashMap<String,Double>();
		
		while((lineInvIndexBD = brInvIndexBD.readLine())!=null){
			int indexofColon = lineInvIndexBD.indexOf("##,##");
			String token = lineInvIndexBD.substring(0,indexofColon);
			String stats = lineInvIndexBD.substring(indexofColon+5);
			String [] arrayofStats = stats.split(",");
			LinkedHashMap<String,Double>statsMap = new
					LinkedHashMap<String,Double>();
			double tokenCount = 0.0;
			//						tokenCountTrainingSet.put(token, (double) 0);
			for(int i = 0 ; i < arrayofStats.length ; i++){
				int indexofDelimeter = arrayofStats[i].indexOf(":");
				String entity = arrayofStats[i].substring
						(0, indexofDelimeter);
				String count = arrayofStats[i].substring(indexofDelimeter+1);
				statsMap.put(entity, Double.parseDouble(count));
				tokenCount = tokenCount + Double.parseDouble(count);
			}
			invertedIndex.put(token , statsMap);
			//						tokenCountTrainingSet.put(token , tokenCount);
		}
		FileInputStream fstreamEntityCounts = new FileInputStream
				(args[1]);
		DataInputStream inEntityCounts = new DataInputStream(fstreamEntityCounts);
		BufferedReader brEntityCounts = new BufferedReader
				(new InputStreamReader(inEntityCounts));
		String lineEntityCounts = null;

		while((lineEntityCounts = brEntityCounts.readLine())!=null){
			int indexofColon = lineEntityCounts.indexOf(":");
			String entity = lineEntityCounts.substring(0,indexofColon);
			String countStr = lineEntityCounts.substring(indexofColon + 1);
			double count = Double.parseDouble(countStr);
			entityCounts.put(entity, count);
		}

		FileInputStream fstreamEntityProb = new FileInputStream(args[5]);
		DataInputStream inEntityProb = new DataInputStream(fstreamEntityProb);
		BufferedReader brEntityProb = new BufferedReader
				(new InputStreamReader(inEntityProb));
		String lineEntityProb = null;
		brEntityProb.readLine();
		while((lineEntityProb = brEntityProb.readLine())!=null){
			String [] array = lineEntityProb.split(",");
			for(int i = 1 ; i < array.length ;i++){
					String index = i+"";
					entityProb.put("Topic "+index, Double.parseDouble(array[i]));
			}
		}
		
		Map<String,Double>tokenEntityProbabilities =
				new LinkedHashMap<String,Double>();
		FileInputStream fileTestToTrain = new FileInputStream
				(args[2]);
		DataInputStream inTestToTrain = new DataInputStream(fileTestToTrain);
		BufferedReader brTestToTrain = new BufferedReader
				(new InputStreamReader
						(inTestToTrain));
		String lineTestToTrain = null;
		double vocab = 0;
		Set<String> allUniqueTokens = new TreeSet<String>();
		while((lineTestToTrain = brTestToTrain.readLine())!=null){
			int indexofDelimeter = lineTestToTrain.indexOf("##,##");
			String text = lineTestToTrain.substring(indexofDelimeter+5);
			String [] tokens = text.split(" ");
			for(String token: tokens){
				if(stopWords.contains(token.toLowerCase())){
					continue;
				}
				allUniqueTokens.add(token);
			}
		}
		vocab = allUniqueTokens.size();
		fileTestToTrain = new FileInputStream
				(args[2]);			
		inTestToTrain = new DataInputStream(fileTestToTrain);
		brTestToTrain = new BufferedReader(new InputStreamReader(inTestToTrain));
		while((lineTestToTrain = brTestToTrain.readLine())!=null){
			int indexofDelimeter = lineTestToTrain.indexOf("##,##");
			String tweetText = lineTestToTrain.substring(indexofDelimeter+5);
			String [] tokens = tweetText.split(" ");
			for(int i = 0 ; i < tokens.length ; i++){
				String token = tokens[i];
				if(stopWords.contains(token.toLowerCase())){
					continue;
				}
				int tempBackground = numTopics + 1;


				String backgroundTopic = "Topic "+tempBackground;
				//														String ailmentTopic = "Topic "+tempAilments;
				Map<String,Double>entityProbabilities = new 
						LinkedHashMap<String,Double>();

				if(invertedIndex.containsKey(token)){
					for(int j = 1 ; j <= numTopics+1 ; j++)
					{
						entityProbabilities.put("Topic "+j, (double) 1/
								(entityCounts.get("Topic "+j)+vocab));
					}

					LinkedHashMap<String,Double> statsMap = 
							(LinkedHashMap<String, Double>)
							invertedIndex.get(token);
					Iterator it = statsMap.entrySet().iterator();
					//								double totalAilmentTopicValue = 0;

					while(it.hasNext()){
						Map.Entry pairs = (Map.Entry)it.next();
						String entity = (String) pairs.getKey();
						double value = (double) pairs.getValue();
						if(entity.contains("Ailment")){
							double initVal = entityProbabilities.get(entity);
							initVal = initVal+value/
									(entityCounts.get(entity)+vocab);
							entityProbabilities.put(entity, initVal);
							//										totalAilmentTopicValue = totalAilmentTopicValue 
							//												+ value;
						}
						else	if(entity.equals("Background")){
							double initVal = entityProbabilities.get
									(backgroundTopic);
							initVal = initVal+value/
									(entityCounts.get(backgroundTopic)+vocab);
							entityProbabilities.put(backgroundTopic, initVal);
						}
						else
						{
							double initVal = entityProbabilities.get(entity);
							initVal = initVal+value/
									(entityCounts.get(entity)+vocab);
							entityProbabilities.put(entity, initVal);
						}
					}								
				}
				else{
					for(int j = 1 ; j <= numTopics+1 ; j++)
					{
						entityProbabilities.put("Topic "+j, (double) 1/vocab);
					}

				}

				Iterator it = entityProbabilities.entrySet().iterator();
				double sum = 0.0;
				while(it.hasNext()){
					Map.Entry pairs = (Map.Entry)it.next();
					String entity = (String) pairs.getKey();
					double value = (double) pairs.getValue();
					value = value * entityProb.get(entity);
					entityProbabilities.put(entity, value);
					sum = sum+value;
				}
				tokenEntityProbabilities.put(token, sum);
			}
		}
		PrintWriter out  = new PrintWriter(new BufferedWriter
				(new FileWriter(args[6]+
						"",true)));
		Iterator it = tokenEntityProbabilities.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry pairs = (Map.Entry)it.next();
			String token = (String) pairs.getKey();
			double probability = (double) pairs.getValue();
			out.println(token+"##,##"+probability);
		}
		out.close();
		
	}

}
