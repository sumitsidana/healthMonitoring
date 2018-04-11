package perplexity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class GetSecondMonthTestData {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		File dir = new File(args[0]); //path up to test
		String[] chld = dir.list();
		Arrays.sort(chld);

		for(int chldIndex = 0 ; chldIndex < chld.length ; chldIndex++){
			if(new File(args[0]+"/"+chld[chldIndex]).isDirectory()){
				File dirRegion = new File(args[0]+"/"+chld[chldIndex]);
				String[] chldRegion = dirRegion.list();
				Arrays.sort(chldRegion);
				for(int chldBoundaryIndex = 0 ; chldBoundaryIndex < chldRegion.length; chldBoundaryIndex++){
					FileInputStream fstreamAlltweetsBD = new FileInputStream
							(args[1]+"/"+chld[chldIndex]+"/"+chldRegion[chldBoundaryIndex]);
					DataInputStream inAllTweetsBD = new DataInputStream(fstreamAlltweetsBD);
					BufferedReader brAllTweetsBD = new BufferedReader(new InputStreamReader(inAllTweetsBD));
					String lineAllTweetsBD = null;
					Map<String,String> temporaltweetIdMap  = new HashMap<String,String>(); 
					while((lineAllTweetsBD = brAllTweetsBD.readLine())!=null){
						int indexofFirstDelimiter = lineAllTweetsBD.indexOf("##,##");
						int indexofLastDelimeter = lineAllTweetsBD.lastIndexOf("##,##");
						String tweetIdStr = lineAllTweetsBD.substring(0,indexofFirstDelimiter);
						String dateStr = lineAllTweetsBD.substring(indexofLastDelimeter + 5);
						temporaltweetIdMap.put(tweetIdStr, dateStr);
					}
					FileInputStream fstreamtesttweetsBD = new FileInputStream
							(args[0]+"/"+chld[chldIndex]+"/"+chldRegion[chldBoundaryIndex]);
					DataInputStream intestTweetsBD = new DataInputStream(fstreamtesttweetsBD);
					BufferedReader brTestTweetsBD = new BufferedReader(new InputStreamReader(intestTweetsBD));
					Map<String , LinkedHashMap<String,String> >monthTweetInfoMap = new TreeMap<String, 
							LinkedHashMap<String,String>>();
					String linetestTweetsBD = null;
					while((linetestTweetsBD = brTestTweetsBD.readLine())!=null){
						int indexofFirstDelimiter = linetestTweetsBD.indexOf("##,##");
						String text = linetestTweetsBD.substring(indexofFirstDelimiter+5);
						String tweetIdStr = linetestTweetsBD.substring(0,indexofFirstDelimiter);
						String dateStr = temporaltweetIdMap.get(tweetIdStr);
						DateTimeFormatter formatter = DateTimeFormat.forPattern
								("yyyy-MM-dd'T'HH:mm:ss.SSSZZ");
						DateTime dt = formatter.parseDateTime(dateStr);
						int month = dt.getMonthOfYear();
						int year = dt.getYearOfCentury();
						String yr_month = year+"_"+month;
						LinkedHashMap<String,String>tweetInfo =  null;
						if(monthTweetInfoMap.containsKey(yr_month)){
							tweetInfo = monthTweetInfoMap.get(yr_month);
						}
						else{
							tweetInfo =  new LinkedHashMap<String,String>();
						}
						tweetInfo.put(tweetIdStr, text);
						monthTweetInfoMap.put(yr_month, tweetInfo);
					}
					new File(args[2]+"/"+chld[chldIndex]).mkdirs();
					new File(args[2]+"/"+chld[chldIndex]+"/"+chldRegion[chldBoundaryIndex]).mkdirs();

					PrintWriter outAilmentsTrainingPhase  = new PrintWriter(new BufferedWriter
							(new FileWriter(args[2]+"/"+chld[chldIndex]+"/"+
									chldRegion[chldBoundaryIndex]+"/"+"testtweetstoteston",true)));
					Iterator it = monthTweetInfoMap.entrySet().iterator();
					int index = 0;
					while(it.hasNext()){
						Map.Entry pairs = (Map.Entry)it.next();
						if(index == 1)
						{	
							String year_month = (String) pairs.getKey();
							LinkedHashMap<String,String>tweetInfo = 
									(LinkedHashMap<String, String>) pairs.getValue();
							Iterator tweetIterator = tweetInfo.entrySet().iterator();
							while(tweetIterator.hasNext()){
								Map.Entry tweetPairs = (Map.Entry)tweetIterator.next();
								outAilmentsTrainingPhase.println(tweetPairs.getKey()+"##,##"+tweetPairs.getValue());
							}

						}
						index ++;
					}
					outAilmentsTrainingPhase.close();
				}
			}
		}

	}

}
