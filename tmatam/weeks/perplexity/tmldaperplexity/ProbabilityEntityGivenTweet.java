package tmldaperplexity;

import java.io.*;
import java.util.*;

public class ProbabilityEntityGivenTweet {

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

		Map<String,Map<String,Double>>tokenStatsMap = new
				HashMap<String,Map<String,Double>>();

		FileInputStream fstreamProbEnGivWordBD = new FileInputStream
				(args[0]);
		DataInputStream inProbEnGivWordBD = new DataInputStream(fstreamProbEnGivWordBD);
		BufferedReader brProbEnGivWordBD = new BufferedReader
				(new InputStreamReader(inProbEnGivWordBD));
		String lineProbEnGivWordBD = null;
		while((lineProbEnGivWordBD = brProbEnGivWordBD.readLine())!=null){
			LinkedHashMap<String,Double>statsMap = new
					LinkedHashMap<String,Double>();
			int indexofDelimiter =  lineProbEnGivWordBD.indexOf("##,##");
			String token = lineProbEnGivWordBD.substring(0,indexofDelimiter);
			String stats = lineProbEnGivWordBD.substring(indexofDelimiter+5);
			String [] statsArray = stats.split(",");
			for(int i = 0 ; i < statsArray.length ; i++){
				String [] entityStat = statsArray[i].split(":");
				statsMap.put(entityStat[0],Double.parseDouble(entityStat[1]));
			}
			tokenStatsMap.put(token, statsMap);
		}


		FileInputStream fileTestToTrain = new FileInputStream
				(args[1]);
		DataInputStream inTestToTrain = new DataInputStream(fileTestToTrain);
		BufferedReader brTestToTrain = new BufferedReader
				(new InputStreamReader
						(inTestToTrain));
		String lineTestToTrain = null;
		Map<String,Map<String,Double>>probEntityTweetBDRMap = new 
				LinkedHashMap<String,Map<String,Double>> ();
		while((lineTestToTrain = brTestToTrain.readLine())!=null){
			Map<String,Double>probEntityTweetMap = new 
					LinkedHashMap<String,Double>();
			int indexofDelimeter = lineTestToTrain.indexOf("##,##");
			String tweetId = lineTestToTrain.substring(0,indexofDelimeter);
			String tweetText = lineTestToTrain.substring(indexofDelimeter+5);
			Map<String,Double>tokenProbabilities = new
					LinkedHashMap<String,Double>();
			String [] tokens = tweetText.split(" ");
			Map<String,Double>tokenTrackMap = new TreeMap<String,Double>();

			for(int i = 0 ; i < tokens.length ; i++){
				//							if(stopWords.contains(tokens[i].toLowerCase())){
				//								continue;
				//							}
				if(tokenTrackMap.containsKey(tokens[i]))
					tokenTrackMap.put(tokens[i], tokenTrackMap.get(tokens[i])+1);
				else
					tokenTrackMap.put(tokens[i], 1.0);
			}
			for(int i = 0 ; i < tokens.length ; i++){
				//							if(stopWords.contains(tokens[i].toLowerCase())){
				//								continue;
				//							}
				tokenProbabilities.put(tokens[i],tokenTrackMap.get
						(tokens[i])/tokens.length);
			}

			boolean firstToken = true;
			for(int i = 0 ; i < tokens.length ; i++){
				String token = tokens[i];
				if(stopWords.contains(token.toLowerCase())){
					continue;
				}

				LinkedHashMap<String,Double> statsMap = 
						(LinkedHashMap<String, Double>)
						tokenStatsMap.get(token);
				Iterator it = statsMap.entrySet().iterator();
				while(it.hasNext()){
					Map.Entry pairs = (Map.Entry)it.next();
					String entity = (String) pairs.getKey();
					Double value = (Double) pairs.getValue();
					if(firstToken)
						probEntityTweetMap.put
						(entity, value*tokenProbabilities.get(token));
					else
						probEntityTweetMap.put
						(entity, probEntityTweetMap.get
								(entity)+value*tokenProbabilities.get(token));
				}
				if(firstToken)
					firstToken = false;
			}
			probEntityTweetBDRMap.put(tweetId, probEntityTweetMap);
		}

		PrintWriter out  = new PrintWriter(new BufferedWriter
				(new FileWriter(args[2],true)));
		Iterator it = probEntityTweetBDRMap.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry pairs = (Map.Entry) it.next();
			String tweetId = (String) pairs.getKey();
			Map<String,Double>probabEntityTweetMap = (Map<String, Double>) pairs.getValue();
			Iterator entityIterator = probabEntityTweetMap.entrySet().iterator();
			out.write(tweetId+"##,##");
			while(entityIterator.hasNext()){
				Map.Entry pairsEntity = (Map.Entry)entityIterator.next();
				String entity = (String) pairsEntity.getKey();
				double value = (double) pairsEntity.getValue();
				out.write(entity+":"+value+",");	
			}
			out.println(",");
		}
		out.close();




	}

}
