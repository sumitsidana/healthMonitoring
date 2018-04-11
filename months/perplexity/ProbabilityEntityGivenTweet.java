package perplexity;
import java.io.*;
import java.util.*;
import java.util.Map.Entry;
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
		File dir = new File(args[0]); //path up to InvertedIndex
		String[] chld = dir.list();
		Arrays.sort(chld);

		for(int chldIndex = 0 ; chldIndex < chld.length ; chldIndex++){
			if(new File(args[0]+"/"+chld[chldIndex]).isDirectory()){
				File dirRegion = new File(args[0]+"/"+chld[chldIndex]);
				String[] chldRegion = dirRegion.list();
				Arrays.sort(chldRegion);
				Map<String,Map<String,Double>>tokenStatsMap = new
						HashMap<String,Map<String,Double>>();
				for(int chldBoundaryIndex = 0 ; chldBoundaryIndex
						< chldRegion.length ; chldBoundaryIndex++){
					FileInputStream fstreamProbEnGivWordBD = new FileInputStream
							(args[0]+"/"+chld[chldIndex]+"/"+chldRegion[chldBoundaryIndex]);
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
							(args[1]+"/"+chld[chldIndex]+"/"+
									chldRegion[chldBoundaryIndex].replace
									("InputToATAMUsingLemmatizer", "").replace
									("_output_atam.txt_invertedindex", "").replace
									("_wordgivenentityprobability", "").replace(".assign", "")
									+"/testtweetstotrainon");
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
					new File(args[2]+"/"+chld[chldIndex]).mkdirs();
					PrintWriter out  = new PrintWriter(new BufferedWriter
							(new FileWriter(args[2]+"/"+chld[chldIndex]+"/"+
									chldRegion[chldBoundaryIndex]+
									"_entitygivenwordprobability",true)));
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
		}


	}

}
