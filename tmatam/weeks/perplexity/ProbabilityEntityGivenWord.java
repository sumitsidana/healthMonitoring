package perplexity;

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
				(args[5]);
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

		int numAilments = Integer.parseInt(args[2]);
		int numTopics = Integer.parseInt(args[3]);

		for(int chldIndex = 0 ; chldIndex < chld.length ; chldIndex++){
			if(new File(args[0]+"/"+chld[chldIndex]).isDirectory()){
				File dirRegion = new File(args[0]+"/"+chld[chldIndex]);
				String[] chldRegion = dirRegion.list();
				Arrays.sort(chldRegion);
				for(int chldBoundaryIndex = 0 ; chldBoundaryIndex
						< chldRegion.length ; chldBoundaryIndex++){
					FileInputStream fstreamInvIndexBD = new FileInputStream
							(args[0]+"/"+chld[chldIndex]+"/"+chldRegion[chldBoundaryIndex]);
					DataInputStream inInvIndexBD = new DataInputStream(fstreamInvIndexBD);
					BufferedReader brInvIndexBD = new BufferedReader
							(new InputStreamReader(inInvIndexBD));
					String lineInvIndexBD = null;
					Map<String,Map<String,Double>>invertedIndex = new
							HashMap<String,Map<String,Double>>();
					Map<String,Double>tokenCountTrainingSet = new
							LinkedHashMap<String,Double>();


					while((lineInvIndexBD = brInvIndexBD.readLine())!=null){
						int indexofColon = lineInvIndexBD.indexOf(":");
						String token = lineInvIndexBD.substring(0,indexofColon);
						String stats = lineInvIndexBD.substring(indexofColon+1);
						String [] arrayofStats = stats.split(",");
						LinkedHashMap<String,Double>statsMap = new
								LinkedHashMap<String,Double>();
						double tokenCount = 0.0;
						tokenCountTrainingSet.put(token, (double) 0);
						for(int i = 0 ; i < arrayofStats.length ; i++){
							int indexofDelimeter = arrayofStats[i].indexOf(":");
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
							(args[1]+"/"+chld[chldIndex]+"/"+
									chldRegion[chldBoundaryIndex].replace
									(".assign_output_atam.txt_invertedindex", "").replace
									("InputToATAMUsingLemmatizer", "")
									+"/testtweetstotrainon");
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
							(args[1]+"/"+chld[chldIndex]+"/"+
									chldRegion[chldBoundaryIndex].replace
									(".assign_output_atam.txt_invertedindex", "").replace
									("InputToATAMUsingLemmatizer", "")
									+"/testtweetstotrainon");			
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
							int tempBackground = numTopics + 2;
							int tempAilments = numTopics + 1;

							String backgroundTopic = "Topic "+tempBackground;
							//														String ailmentTopic = "Topic "+tempAilments;
							Map<String,Double>entityProbabilities = new 
									LinkedHashMap<String,Double>();

							if(invertedIndex.containsKey(token)){
								for(int j = 1 ; j <= numTopics+2 ; j++)
								{
									if(j == tempAilments){
										entityProbabilities.put("Topic "+j, (double) 0);
										continue;
									}
									entityProbabilities.put("Topic "+j, (double) 1/
											(tokenCountTrainingSet.get(token)+vocab));
								}
								for(int j = 1 ; j <= numAilments ;j++ )
								{
									entityProbabilities.put("Ailment "+j, (double)1/
											(tokenCountTrainingSet.get(token)+vocab));
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
												(tokenCountTrainingSet.get(token)+vocab);
										entityProbabilities.put(entity, initVal);
										//										totalAilmentTopicValue = totalAilmentTopicValue 
										//												+ value;
									}
									else	if(entity.equals("Background")){
										double initVal = entityProbabilities.get
												(backgroundTopic);
										initVal = initVal+value/
												(tokenCountTrainingSet.get(token)+vocab);
										entityProbabilities.put(backgroundTopic, initVal);
									}
									else
									{
										double initVal = entityProbabilities.get(entity);
										initVal = initVal+value/
												(tokenCountTrainingSet.get(token)+vocab);
										entityProbabilities.put(entity, initVal);
									}
								}								
							}
							else{
								for(int j = 1 ; j <= numTopics+2 ; j++)
								{
									if(j == tempAilments){
										entityProbabilities.put("Topic "+j, (double) 0);
										continue;
									}
									entityProbabilities.put("Topic "+j, (double) 1/vocab);
								}
								for(int j = 1 ; j <= numAilments ;j++ )
								{
									entityProbabilities.put("Ailment "+j, (double)1/vocab);
								}								
							}

							//								double initVal = entityProbabilities.get(ailmentTopic);
							//								initVal = initVal+totalAilmentTopicValue;
							//								initVal = initVal/entityCounts.get(ailmentTopic);
							//								entityProbabilities.put(ailmentTopic, initVal);

							//							else{
							//								Iterator it = entityProbabilities.entrySet().iterator();
							//								while(it.hasNext()){
							//									Map.Entry pairs = (Map.Entry)it.next();
							//									String entity = (String) pairs.getKey();
							//									double value = (double) pairs.getValue();
							//									value = value/vocab;
							//									entityProbabilities.put(entity, value);	
							//								}
							//							}
							tokenEntityProbabilities.put(token, entityProbabilities);
						}
					}
					new File(args[4]+"/"+chld[chldIndex]).mkdirs();
					PrintWriter out  = new PrintWriter(new BufferedWriter
							(new FileWriter(args[4]+"/"+chld[chldIndex]+"/"+
									chldRegion[chldBoundaryIndex]+
									"_wordgivenentityprobability",true)));
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
		}
	}
}
