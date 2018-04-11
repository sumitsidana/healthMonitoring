package tmldaperplexity;
import java.io.*;
import java.util.*;

public class ProbabilityEntityGivenWord {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		Set<String> stopWords = new HashSet<String>();
		FileInputStream fileInputStreamSW = new FileInputStream
				(args[4]);
		DataInputStream dataInputStreamSW = new DataInputStream(fileInputStreamSW);
		BufferedReader bufferedReaderSW = new BufferedReader
				(new InputStreamReader(dataInputStreamSW));
		String strLineSW;
		int numTopics = Integer.parseInt(args[2]);
		while((strLineSW = bufferedReaderSW.readLine())!=null)
		{
			stopWords.add(strLineSW.toLowerCase());
		}
		FileInputStream fstreamInvIndexBD = new FileInputStream
				(args[0]);
		DataInputStream inInvIndexBD = new DataInputStream(fstreamInvIndexBD);
		BufferedReader brInvIndexBD = new BufferedReader
				(new InputStreamReader(inInvIndexBD));
		String lineInvIndexBD = null;
		Map<String,Map<String,Double>>invertedIndex = new
				HashMap<String,Map<String,Double>>();
		Map<String,Double>tokenCountTrainingSet = new
				LinkedHashMap<String,Double>();
		while((lineInvIndexBD = brInvIndexBD.readLine())!=null){
			System.out.println(lineInvIndexBD);
			int indexofColon = lineInvIndexBD.indexOf("##,##");
			String token = lineInvIndexBD.substring(0,indexofColon);
			String stats = lineInvIndexBD.substring(indexofColon+5);
			String [] arrayofStats = stats.split(",");
			LinkedHashMap<String,Double>statsMap = new
					LinkedHashMap<String,Double>();
			double tokenCount = 0.0;
			tokenCountTrainingSet.put(token, (double) 0);
			for(int i = 0 ; i < arrayofStats.length ; i++){
				int indexofDelimeter = arrayofStats[i].indexOf(":");
				System.out.println(arrayofStats[i]);
				String entity = arrayofStats[i].substring
						(0, indexofDelimeter);
				String count = arrayofStats[i].substring(indexofDelimeter+1);
				statsMap.put(entity, Double.parseDouble(count));
				tokenCount = tokenCount + Double.parseDouble(count);
			}
			invertedIndex.put(token , statsMap);
			tokenCountTrainingSet.put(token , tokenCount);
		}
		Map<String,Map<String,Double>>tokenEntityProbabilities =
				new LinkedHashMap<String,Map<String,Double>>();

		FileInputStream fileTestToTrain = new FileInputStream
				(args[1]);
		DataInputStream inTestToTrain = new DataInputStream(fileTestToTrain);
		BufferedReader brTestToTrain = new BufferedReader
				(new InputStreamReader
						(inTestToTrain));
		String lineTestToTrain = null;
		double vocab = 0;
		Set<String> allUniqueTokens = new TreeSet<String>();
		while((lineTestToTrain = brTestToTrain.readLine())!=null){
			int indexofDelimeter = lineTestToTrain.indexOf("##,##");
			String tweetText = lineTestToTrain.substring(indexofDelimeter+5);
			String [] tokens = tweetText.split(" ");
			for(int i = 0 ; i < tokens.length ; i++){
				String token = tokens[i];
				if(stopWords.contains(token.toLowerCase())){
					continue;
				}
				allUniqueTokens.add(token);
			}
		}
		vocab = allUniqueTokens.size();
		fileTestToTrain = new FileInputStream
				(args[1]);			
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
				Map<String,Double>entityProbabilities = new 
						LinkedHashMap<String,Double>();
				if(invertedIndex.containsKey(token)){
					for(int j = 1 ; j <= numTopics+1 ; j++)
					{
						entityProbabilities.put("Topic "+j, (double) 1/
								(tokenCountTrainingSet.get(token)+vocab));
					}
					LinkedHashMap<String,Double> statsMap = 
							(LinkedHashMap<String, Double>)
							invertedIndex.get(token);
					Iterator it = statsMap.entrySet().iterator();
					while(it.hasNext()){
						Map.Entry pairs = (Map.Entry)it.next();
						String entity = (String) pairs.getKey();
						double value = (double) pairs.getValue();

						if(entity.equals("Background")){
							double initVal = entityProbabilities.get
									(backgroundTopic);
							initVal = initVal+value/
									(tokenCountTrainingSet.get(token)+vocab);
							entityProbabilities.put(backgroundTopic, initVal);
						}
						else
						{
							System.out.println(entity);
							double initVal = entityProbabilities.get(entity);
							initVal = initVal+value/
									(tokenCountTrainingSet.get(token)+vocab);
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
				tokenEntityProbabilities.put(token, entityProbabilities);
			}
		}
		PrintWriter out  = new PrintWriter(new BufferedWriter
				(new FileWriter(args[3],true)));
		Iterator itEntity = tokenEntityProbabilities.entrySet().iterator();
		while(itEntity.hasNext()){
			Map.Entry pairsEntity = (Map.Entry)itEntity.next();
			String token = (String) pairsEntity.getKey();
			Map<String,Double>entityProbabilities =
					(Map<String, Double>) pairsEntity.getValue();
			Iterator itProb = entityProbabilities.entrySet().iterator();
			out.write(token+"##,##");
			while(itProb.hasNext())
			{
				Map.Entry pairsProb = (Map.Entry)itProb.next();
				out.write(pairsProb.getKey()+":"+pairsProb.getValue()+",");
			}
			out.println();
		}
		out.close();

	}

}
