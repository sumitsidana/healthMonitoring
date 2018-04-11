package seasonDetection;
import java.io.*;
import java.util.*;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class Distributions {
	/*
	 * java -cp hmot.jar:joda-time-2.5.jar
	 * seasonDetection.ConvertInferencesIntoWeeklyDistributions copyfiles/ 25 25 topics
	 * This class takes input as inferences for full region
	 * for the full time period and converts them into
	 * historical and present ailment distribution.
	 */
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		Map<String , String> healthtweetTime = new HashMap<String,String>();
		String numberofAilments = args[1];
		String numberofTopicsToFind = args[2];
		FileInputStream fileInputStream = new FileInputStream
				("/media/toshibasecond/months/healthMajorAreas");
		DataInputStream in = new DataInputStream(fileInputStream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in,"UTF-8"));
		String strLine;
		while((strLine = br.readLine())!=null){
			String [] array = strLine.split("\t");
			DateTimeFormatter formatter = DateTimeFormat.forPattern
					("yyyy-MM-dd HH:mm:ss+SSS");
			DateTime dt = formatter.parseDateTime(array[2]);
			String uId = "0" ;
			if(dt.getMonthOfYear()>=10)
				uId = dt.getYearOfCentury()+"_"+dt.getMonthOfYear();
			else
				uId = dt.getYearOfCentury()+"_0"+dt.getMonthOfYear();
			healthtweetTime.put(array[0],uId);
		}
		File dir = new File(args[0]);
		String[] chld = dir.list();
		Arrays.sort(chld);
		for(int chldIndex = 0 ; chldIndex < chld.length ; chldIndex++){
			double time1=System.currentTimeMillis();
			if(new File(args[0]+"/"+chld[chldIndex]).isDirectory()){
				new File(args[0]+"/"+chld[chldIndex]+"/"+args[3]).mkdirs();
				PrintWriter outAilmentsTrainingPhase  = new PrintWriter
						(new BufferedWriter
								(new FileWriter(args[0]+"/"+chld[chldIndex]+"/"+args[3]+
										"/topicshistorical",true)));

				PrintWriter outAilmentsTestingPhase  = new PrintWriter
						(new BufferedWriter
								(new FileWriter(args[0]+"/"+chld[chldIndex]+"/"+args[3]+
										"/topicspresent",true)));
				Map<String,Map<String,Double>>ailments = new
						LinkedHashMap<String, Map<String,Double>>();
				Map<String,Map<String,Double>>topics = new
						LinkedHashMap<String,Map<String,Double>>();
				fileInputStream = new FileInputStream
						(args[0]+"/"+chld[chldIndex]+"/"+
								"InputToATAMUsingLemmatizerfullfile.assign");

				in = new DataInputStream(fileInputStream);
				br = new BufferedReader(new InputStreamReader(in,"UTF-8"));
				Map<String,List<String>>weeklyInference = new TreeMap
						<String,List<String>>();

				while((strLine = br.readLine())!=null){
					int indexofFirstSpace = strLine.indexOf(" ");
					String tweetId = strLine.substring(0,indexofFirstSpace);
					String inference = strLine.substring(indexofFirstSpace+1);
					String wId = healthtweetTime.get(tweetId);
					List<String>inferences;
					if(weeklyInference.containsKey(wId)){
						inferences = weeklyInference.get(wId);
						inferences.add(inference);
					}
					else{
						inferences = new ArrayList<String>();
						inferences.add(inference);
					}
					weeklyInference.put(wId, inferences);
				}
				Iterator it = weeklyInference.entrySet().iterator();
				while(it.hasNext()){

					Map<String,Double>ailmentVal =
							new LinkedHashMap<String,Double>();
					for (Long i1 = (long) 0 ; i1 < Long.parseLong
							(numberofAilments); i1++)
						ailmentVal.put(i1.toString(), (double) 0);

					Map<String,Double>topicVal = new LinkedHashMap<String,Double>();
					for (Long i1 = (long) 0 ; i1 <= Long.parseLong
							(numberofTopicsToFind)+1; i1++)
						topicVal.put(i1.toString(), (double) 0);

					Map.Entry pairs = (Map.Entry)it.next();
					String wId  = (String) pairs.getKey();
					List<String> inferences = (List<String>) pairs.getValue();
					double actualLengthOfTimePeriod = inferences.size();
					boolean tweetFromBG = false;
					for(int i = 0 ; i < inferences.size() ; i++){
						tweetFromBG = false;
						String inference = inferences.get(i);	
						int ailmentIndex = inference.indexOf(" ");
						String ailment = inference.substring(0, ailmentIndex);
						int indexofFirstSpace = inference.indexOf(" ");
						String tokenLevelStatistics = inference.substring
								(indexofFirstSpace+1);
						String [] tokens = tokenLevelStatistics.split(" ");
						double actualLengthOfTweet = 0.0;
						boolean flagtweetFromBackground = true;
						for(int j = 0 ; j < tokens.length ; j++){
							String [] wordlevelStats = tokens[j].split(":");
							String topic = wordlevelStats[1];
							String b = wordlevelStats[5];
							if(!(b.equals("0"))){
								actualLengthOfTweet++;
							}
						}
						if(actualLengthOfTweet == 0)
						{
							actualLengthOfTimePeriod --;
						}
					}
					for(int i = 0 ; i < inferences.size() ; i++){
						String inference = inferences.get(i);	
						int ailmentIndex = inference.indexOf(" ");
						String ailment = inference.substring(0, ailmentIndex);
						int indexofFirstSpace = inference.indexOf(" ");
						String tokenLevelStatistics = inference.substring
								(indexofFirstSpace+1);
						String [] tokens = tokenLevelStatistics.split(" ");
						double actualLengthOfTweet = 0.0;
						boolean flagtweetFromBackground = true;
						for(int j = 0 ; j < tokens.length ; j++){
							String [] wordlevelStats = tokens[j].split(":");
							String topic = wordlevelStats[1];
							String b = wordlevelStats[5];
							if(!(b.equals("0"))){
								actualLengthOfTweet++;
							}
						}
						for(int j = 0 ; j < tokens.length ; j++){
							String [] wordlevelStats = tokens[j].split(":");
							String topic = wordlevelStats[1];
							String l = wordlevelStats[3];
							String b = wordlevelStats[5];
							if(
									(topic.equals("25")&& !(l.equals("1")))
									||
									(!(topic.equals("25")||topic.equals("26"))&&l=="1")
									||
									(topic.equals("26")&& !(b.equals("0")))
									||
									(!(topic.equals("26")) && b=="0")
									)
							{
								System.out.println("topic:"+topic+"l:"+l+"b:"+b);
								System.out.println("Some Problem");
							}
							if(topic.equals("26")){
								continue;
							}
							else if(topic.equals("25")){
								ailmentVal.put(ailment , ailmentVal.get
										(ailment)+1.0/(actualLengthOfTimePeriod*actualLengthOfTweet));										
							}
							else{
								topicVal.put(topic, topicVal.get(topic)+1.0/
										(actualLengthOfTimePeriod*actualLengthOfTweet));
							}
						}
					}
					ailments.put(wId, ailmentVal);
					topics.put(wId, topicVal);
				}
				Iterator itTopics = topics.entrySet().iterator();
				Iterator itAilments = ailments.entrySet().iterator();
				int sizeofWeeklyTopics = 0;
				while(itTopics.hasNext()){
					Map.Entry pairs = (Map.Entry)itTopics.next();
					String weekId = (String) pairs.getKey();
					Map<String,Double> topicVal = (Map<String, Double>)
							pairs.getValue();
					Map.Entry pairs_Ail = (Map.Entry)itAilments.next();
					String weekId_ail = (String)pairs_Ail.getKey();
					Map<String,Double> ailmentVal = (Map<String,Double>)
							pairs_Ail.getValue();
					if(!(weekId_ail.equals(weekId))){
						System.out.println("Some Problem: Ailment WeekIds and " +
								"Topic" +
								"Ids do not match");
					}
					Iterator itWeights = topicVal.entrySet().iterator();
					Iterator itWeights_ail = ailmentVal.entrySet().iterator();

					if(sizeofWeeklyTopics == 0){
						outAilmentsTrainingPhase.write(weekId+"#|#");
						while(itWeights.hasNext()){
							Map.Entry pairsWeights = (Map.Entry) itWeights.next();
							String topicId = (String) pairsWeights.getKey();
							Double weight = (Double) pairsWeights.getValue();
							outAilmentsTrainingPhase.write(topicId+":"+weight+"|:|");
						}
						while(itWeights_ail.hasNext()){
							Map.Entry pairsWeights_ail = (Map.Entry)
									itWeights_ail.next();
							String ailmentId = (String)pairsWeights_ail.getKey();
							Double weight = (Double) pairsWeights_ail.getValue();
							outAilmentsTrainingPhase.write
							(ailmentId+":"+weight+"|:|");
						}
						outAilmentsTrainingPhase.write("\n");
					}
					else if(sizeofWeeklyTopics == topics.size()-1){
						outAilmentsTestingPhase.write(weekId+"#|#");
						while(itWeights.hasNext()){
							Map.Entry pairsWeights = (Map.Entry) itWeights.next();
							String topicId = (String) pairsWeights.getKey();
							Double weight = (Double) pairsWeights.getValue();
							outAilmentsTestingPhase.write(topicId+":"+weight+"|:|");
						}
						while(itWeights_ail.hasNext()){
							Map.Entry pairsWeights_ail = (Map.Entry)
									itWeights_ail.next();
							String ailmentId = (String)pairsWeights_ail.getKey();
							Double weight = (Double) pairsWeights_ail.getValue();
							outAilmentsTestingPhase.write
							(ailmentId+":"+weight+"|:|");
						}
						outAilmentsTestingPhase.write("\n");
					}
					else{
						outAilmentsTrainingPhase.write(weekId+"#|#");
						outAilmentsTestingPhase.write(weekId+"#|#");
						while(itWeights.hasNext()){
							Map.Entry pairsWeights = (Map.Entry) itWeights.next();
							String topicId = (String) pairsWeights.getKey();
							Double weight = (Double) pairsWeights.getValue();
							outAilmentsTrainingPhase.write(topicId+":"+weight+"|:|");
							outAilmentsTestingPhase.write(topicId+":"+weight+"|:|");
						}
						while(itWeights_ail.hasNext()){
							Map.Entry pairsWeights_ail = (Map.Entry)
									itWeights_ail.next();
							String ailmentId = (String)pairsWeights_ail.getKey();
							Double weight = (Double) pairsWeights_ail.getValue();
							outAilmentsTrainingPhase.write
							(ailmentId+":"+weight+"|:|");
							outAilmentsTestingPhase.write
							(ailmentId+":"+weight+"|:|");									
						}
						outAilmentsTrainingPhase.write("\n");
						outAilmentsTestingPhase.write("\n");
					}
					sizeofWeeklyTopics++;
				}
				outAilmentsTestingPhase.close();
				outAilmentsTrainingPhase.close();
				double time2=System.currentTimeMillis();
				System.out.println("\n Total Time: "+(time2-time1));
			}
		}
	}
}
