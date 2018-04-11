package perplexity;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.*;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import java.io.*;

/*
 *  java -cp atlf.jar:joda-time-2.5.jar
 *   evaluation.ConvertBoundaryInferenceTopicalDistributions
 *   topicailmentcopyfiles/ 25 25 topics
 */
public class ConvertBoundaryInferenceTopicalDistributions {
	/*
	 * This class takes input as inferences for full region
	 * for the full time period and converts them into
	 * historical and present ailment distribution.
	 */
	/**
	 * @param args
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
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Map<DateTime , List<String> > weeklyInferences = new
				LinkedHashMap<DateTime,List<String>>();
		Map<String , String> healthtweetTime = new HashMap<String,String>();
		String numberofAilments = args[1];
		String numberofTopicsToFind = args[2];
		try{
			FileInputStream fileInputStream = new FileInputStream
					("data/healthMajorAreas");
			DataInputStream in = new DataInputStream(fileInputStream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in,"UTF-8"));
			String strLine;
			while((strLine = br.readLine())!=null){
				String [] array = strLine.split("\t");
				DateTimeFormatter formatter = DateTimeFormat.forPattern
						("yyyy-MM-dd HH:mm:ss+SSS");
				//				DateTimeFormatter formatter = DateTimeFormat.forPattern
				//("yyyy-MM-dd");
				DateTime dt = formatter.parseDateTime(array[2]);
				String uId = "0" ;
				if(dt.getMonthOfYear()>=10)
					uId = dt.getYearOfCentury()+"_"+dt.getMonthOfYear();
				else
					uId = dt.getYearOfCentury()+"_0"+dt.getMonthOfYear();
				healthtweetTime.put(array[0],uId);
			}
			File dir = new File(args[0]);
			///media/TOSHIBA EXT/regions/fullregion/activeregions
			String[] chld = dir.list();
			Arrays.sort(chld);
			for(int chldIndex = 0 ; chldIndex < chld.length ; chldIndex++){
				double time1=System.currentTimeMillis();
				if(new File(args[0]+"/"+chld[chldIndex]).isDirectory()){
					File dirATAM = new File(args[0]+"/"+chld[chldIndex]);
					String[] chldATAM = dirATAM.list();
					Arrays.sort(chldATAM);
					for(int chldATAMIndex = 0 ; chldATAMIndex < chldATAM.length ;
							chldATAMIndex++){
						String timeDirTobeMade = null;
						//if(chldATAM[chldATAMIndex].length() == 67)
						//	timeDirTobeMade = chldATAM[chldATAMIndex].substring(26, 31);
						//else
						//	timeDirTobeMade = chldATAM[chldATAMIndex].substring(26, 32);
System.out.println(chldATAM[chldATAMIndex]);
timeDirTobeMade = chldATAM[chldATAMIndex].substring(0,15);
						new File(args[0]+"/"+chld[chldIndex]+"/"+timeDirTobeMade).mkdirs();	
						new File(args[0]+"/"+chld[chldIndex]+"/"+timeDirTobeMade+"/"+args[3])
						.mkdirs();

						PrintWriter outAilmentsTrainingPhase  = new PrintWriter
								(new BufferedWriter
										(new FileWriter(args[0]+"/"+chld[chldIndex]+"/"+
												timeDirTobeMade+"/"+args[3]+
												"/topicshistorical",true)));

						PrintWriter outAilmentsTestingPhase  = new PrintWriter
								(new BufferedWriter
										(new FileWriter(args[0]+"/"+chld[chldIndex]+"/"+
												timeDirTobeMade+"/"+args[3]+
												"/topicspresent",true)));

						Map<String,Map<String,Double>>ailments = new
								LinkedHashMap<String, Map<String,Double>>();
						Map<String,Map<String,Double>>topics = new
								LinkedHashMap<String,Map<String,Double>>();

						fileInputStream = new FileInputStream
								(args[0]+"/"+chld[chldIndex]+"/"+chldATAM[chldATAMIndex]);

						in = new DataInputStream(fileInputStream);
						br = new BufferedReader(new InputStreamReader(in,"UTF-8"));
						Map<String,List<String>>monthlyInference = new TreeMap
								<String,List<String>>();
						while((strLine = br.readLine())!=null){
							int indexofFirstSpace = strLine.indexOf(" ");
							String tweetId = strLine.substring(0,indexofFirstSpace);
							String inference = strLine.substring(indexofFirstSpace+1);
							String mId = healthtweetTime.get(tweetId);
							List<String>inferences;
							if(monthlyInference.containsKey(mId)){
								inferences = monthlyInference.get(mId);
								inferences.add(inference);
							}
							else{
								inferences = new ArrayList<String>();
								inferences.add(inference);
							}
							monthlyInference.put(mId, inferences);
						}
						Iterator it = monthlyInference.entrySet().iterator();

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
							String mId  = (String) pairs.getKey();
							List<String> inferences = (List<String>) pairs.getValue();
							for(int i = 0 ; i < inferences.size() ; i++){
								String inference = inferences.get(i);	
								int ailmentIndex = inference.indexOf(" ");
								String ailment = inference.substring(0, ailmentIndex);
								int indexofFirstSpace = inference.indexOf(" ");
								String tokenLevelStatistics = inference.substring
										(indexofFirstSpace+1);
								String [] tokens = tokenLevelStatistics.split(" ");
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
									if(topic.equals("25")){
										ailmentVal.put(ailment , ailmentVal.get
												(ailment)+1.0/(inferences.size()*tokens.length));										
									}
									else{
										topicVal.put(topic, topicVal.get(topic)+1.0/
												(inferences.size()*tokens.length));
									}
								}
							}
							ailments.put(mId, ailmentVal);
							topics.put(mId, topicVal);
						}

						Iterator itTopics = topics.entrySet().iterator();
						Iterator itAilments = ailments.entrySet().iterator();
						int sizeofMonthlyTopics = 0;
						while(itTopics.hasNext()){
							Map.Entry pairs = (Map.Entry)itTopics.next();
							String monthId = (String) pairs.getKey();
							Map<String,Double> topicVal = (Map<String, Double>)
									pairs.getValue();
							Map.Entry pairs_Ail = (Map.Entry)itAilments.next();
							String monthId_ail = (String)pairs.getKey();
							Map<String,Double> ailmentVal = (Map<String,Double>)
									pairs_Ail.getValue();
							if(!(monthId_ail.equals(monthId))){
								System.out.println("Some Problem: Ailment WeekIds and " +
										"Topic" +
										"Ids do not match");
							}
							Iterator itWeights = topicVal.entrySet().iterator();
							Iterator itWeights_ail = ailmentVal.entrySet().iterator();

							if(sizeofMonthlyTopics == 0){
								outAilmentsTrainingPhase.write(monthId+"#|#");
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
							else if(sizeofMonthlyTopics == topics.size()-1){
								outAilmentsTestingPhase.write(monthId+"#|#");
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
								outAilmentsTrainingPhase.write(monthId+"#|#");
								outAilmentsTestingPhase.write(monthId+"#|#");
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
							sizeofMonthlyTopics++;
						}
						outAilmentsTestingPhase.close();
						outAilmentsTrainingPhase.close();
						double time2=System.currentTimeMillis();
						System.out.println("\n Total Time: "+(time2-time1));
						/*
						 * Here iterate over weeklyInference.
						 */
					}
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
}
