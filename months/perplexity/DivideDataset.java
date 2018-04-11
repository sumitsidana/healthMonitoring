package perplexity;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
public class DivideDataset {

	/**
	 * @param args
	 */
	/*
	 * Divides a  'timeline boundary' into train and test data following a simple invariant
	 *  that test data must contain last two months of the boundary.
	 *  If a 'timeline boundary' has less than four months - discard the boundary.
	 *   We obtain 69 boundaries for 66 regions as some boundaries do not have enough
	 *    tweets to test on.
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try{
			File dir = new File(args[0]);
			String[] chld = dir.list();
			Arrays.sort(chld);
			for(int chldIndex = 0; chldIndex < chld.length ; chldIndex++){ 
				if(new File(args[0]+"/"+chld[chldIndex]).isDirectory()){
					new File(args[1]+"/"+chld[chldIndex]).mkdirs();
					new File(args[2]+"/"+chld[chldIndex]).mkdirs();
					File regionalHealthFolder = new File(args[0]+"/"+chld[chldIndex]);
					String[] chldRegionalHealthFileList = regionalHealthFolder.list();
					Arrays.sort(chldRegionalHealthFileList);
					for(int i = 0 ; i < chldRegionalHealthFileList.length ; i++){

						FileInputStream fstreamFile = new FileInputStream
								(args[0]+"/"+chld[chldIndex]+"/"+chldRegionalHealthFileList[i]);
						DataInputStream inFile = new DataInputStream(fstreamFile);
						BufferedReader brFile = new BufferedReader(new InputStreamReader(inFile));
						String line = null;
						Map<String,Map<String,String>>monthlyHealthTweets = new
								TreeMap<String,Map<String,String>>();
						while((line = brFile.readLine())!=null){
							int indexofFirstDelimiter = line.indexOf("##,##");
							int indexofSecondDelimiter = line.lastIndexOf("##,##");
							String tweetId = line.substring(0,indexofFirstDelimiter);
							String text = line.substring
									(indexofFirstDelimiter+5, indexofSecondDelimiter);
							DateTimeFormatter formatter = DateTimeFormat.forPattern
									("yyyy-MM-dd'T'HH:mm:ss.SSSZZ");
							String dateStr = line.substring(indexofSecondDelimiter+5);
							DateTime dateTime = formatter.parseDateTime(dateStr);
							int year = dateTime.getYearOfCentury();
							int monthInt = dateTime.getMonthOfYear();
							String month = ""+monthInt;
							if(monthInt<10)
								month = "0"+month;
							String year_month = year+"_"+month;
							Map<String,String>tweet = null;
							if(monthlyHealthTweets.containsKey(year_month)){
								tweet = monthlyHealthTweets.get(year_month);
							}
							else{
								tweet = new LinkedHashMap<String,String>();
							}
							tweet.put(tweetId, text);
							monthlyHealthTweets.put(year_month , tweet);
						}
						if(monthlyHealthTweets.size()<4)
						{
							continue;
						}
						PrintWriter outTraining  = new PrintWriter(new BufferedWriter
								(new FileWriter(args[1]+"/"+chld[chldIndex]+"/"+
										chldRegionalHealthFileList[i],true)));
						PrintWriter outTesting = new PrintWriter(new BufferedWriter(new FileWriter
								(args[2]+"/"+chld[chldIndex]+"/"+chldRegionalHealthFileList[i],true)));
						ArrayList<String> keys = new ArrayList<String>
						(monthlyHealthTweets.keySet());
						Map<String,Map<String,String>>train = new TreeMap
								<String,Map<String,String>>();
						Map<String,Map<String,String>>test = new TreeMap
								<String,Map<String,String>>();
						Set<String>monthTrack = new TreeSet<String>();
						for(int monthIndex = keys.size() - 1 ;
								monthIndex >= 0 ; monthIndex--){
							String year_Month = keys.get(monthIndex);
							monthTrack.add(year_Month);
							if(monthTrack.size()<=2){
								test.put(year_Month,
										monthlyHealthTweets.get(keys.get(monthIndex)));
							}
							else
							{
								train.put(year_Month,
										monthlyHealthTweets.get(keys.get(monthIndex)));
							}
						}
						Iterator itTrain = train.entrySet().iterator();
						Iterator itTest = test.entrySet().iterator();
						while(itTrain.hasNext()){
							Map.Entry pairs = (Map.Entry)itTrain.next();
							String year_month = (String) pairs.getKey();
							Map<String,String>tweet = (Map<String, String>) pairs.getValue();
							Iterator tweetIterator = tweet.entrySet().iterator();
							while(tweetIterator.hasNext()){
								Map.Entry entry = (Map.Entry)tweetIterator.next();
								String tweetId = (String) entry.getKey();
								String text = (String) entry.getValue();
								outTraining.println(tweetId+"##,##"+text);
							}
						}
						while(itTest.hasNext()){
							Map.Entry pairs = (Map.Entry)itTest.next();
							String year_month = (String)pairs.getKey();
							Map<String,String>tweet = (Map<String, String>) pairs.getValue();
							Iterator tweetIterator = tweet.entrySet().iterator();
							while(tweetIterator.hasNext()){
								Map.Entry entry = (Map.Entry)tweetIterator.next();
								String tweetId = (String)entry.getKey();
								String text = (String)entry.getValue();
								outTesting.println(tweetId+"##,##"+text);
							}
						}
						outTraining.close();
						outTesting.close();
					}
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

}
