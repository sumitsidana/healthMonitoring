package perplexity;
import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
public class TopRankedBoundries {
	/*
	 * This class chooses top dates for each region
	 */
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try{
			FileInputStream fileInputStream = new FileInputStream
					("data/healthMajorAreas");
			DataInputStream in = new DataInputStream(fileInputStream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in,"UTF-8"));
			String strLine;
			Map<String,DateTime> temporalTweet = new HashMap<String,DateTime>();
			Map<String,String> tweetText = new HashMap<String,String>();
			while((strLine = br.readLine())!=null){
				String [] array = strLine.split("\t");
				int indexofLastTab = strLine.lastIndexOf("\t");
				String mergedtweetText = strLine.substring(indexofLastTab+1);
				int indexofDelimiter = mergedtweetText.indexOf("##,##");
				String text = mergedtweetText.substring(0,indexofDelimiter);
				DateTimeFormatter formatter = DateTimeFormat.forPattern
						("yyyy-MM-dd HH:mm:ss+SSS");
				DateTime dt = formatter.parseDateTime(array[2]);
				DateTimeFormatter dtfOut = DateTimeFormat.forPattern("yyyy-MM-dd");
				String date = dtfOut.print(dt);
				DateTimeFormatter formatteragain = DateTimeFormat.forPattern("yyyy-MM-dd");
				DateTime dttobecompared = formatteragain.parseDateTime(date);
				String tweetId = array[0];
				temporalTweet.put(tweetId, dttobecompared);		
				tweetText.put(tweetId, text);
			}
			File dirofpredictedSeasons = new File(args[0]);//predicted seasons
			String[] chldPredictedSeasons = dirofpredictedSeasons.list();
			Arrays.sort(chldPredictedSeasons);
			Map<String,Set<DateTime>>regionPredictedSeasonalDates = new
					HashMap<String,Set<DateTime>>();
			for(int chldIndex = 0; chldIndex < chldPredictedSeasons.length ; chldIndex++){
				FileInputStream fstreamDates = new FileInputStream
						(args[0]+"/"+chldPredictedSeasons[chldIndex]);
				DataInputStream inDates = new DataInputStream(fstreamDates);
				BufferedReader brDates = new BufferedReader(new InputStreamReader(inDates));
				String lineDates = null;
				Set<DateTime>goodDates = new TreeSet<DateTime>();
				int index = 0 ;
				while((lineDates = brDates.readLine())!=null){
					index++;
					int indexofComma = lineDates.indexOf(",");
					String dateStr = lineDates.substring(0,indexofComma);
					DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
					DateTime dt1 = formatter.parseDateTime(dateStr);
					if(index > 4)
					{
						goodDates.add(dt1);
					}
				}
				regionPredictedSeasonalDates.put(chldPredictedSeasons
						[chldIndex].replace("_weekwisedifference", ""), goodDates);
			}
			File dirHealthFilesRegions = new File(args[1]);//active regions
			String[] chldHealthFilesRegions = dirHealthFilesRegions.list();
			Arrays.sort(chldHealthFilesRegions);

			for(int chldIndex = 0 ; chldIndex < chldHealthFilesRegions.length ; chldIndex++){

				/*
				 * Make a directory of region
				 */
				String region = chldHealthFilesRegions[chldIndex];
				Set<DateTime>setPredictedDates = regionPredictedSeasonalDates.get(region);
				DateTime dt3 = null;
				//				DateTime dt4 = null;
				int index = 0;
				for (DateTime dt2 : setPredictedDates) {
					index++;
					if(index == 1)
						dt3 = dt2;
					//					else
					//						dt4 = dt2;
				}
				new File(args[2]+"/"+region).mkdirs();
				PrintWriter outBoundary1 = new PrintWriter(new BufferedWriter(new FileWriter
						(args[2]+"/"+region+"/before"+dt3,true)));
				//				PrintWriter outBoundary2  = new PrintWriter(new BufferedWriter
				//						(new FileWriter(args[2]+"/"+region+"/between"+dt3+"_"+dt4,true)));
				PrintWriter outBoundary3 = new PrintWriter(new BufferedWriter(new FileWriter
						(args[2]+"/"+chldHealthFilesRegions[chldIndex]+"/after"+dt3,true)));

				FileInputStream fstreamRegionalHealth = new FileInputStream
						(args[1]+"/"+chldHealthFilesRegions[chldIndex]+"/"+"fullfile");
				DataInputStream inRegionalHealth = new DataInputStream(fstreamRegionalHealth);
				BufferedReader brRegionalHealth = new BufferedReader(new InputStreamReader(inRegionalHealth));
				String lineRegionalHealth = null;
				while((lineRegionalHealth = brRegionalHealth.readLine())!=null){
					int indexofDelimiter = lineRegionalHealth.indexOf("##,##");
					String tweetId = lineRegionalHealth.substring(0, indexofDelimiter);
					DateTime dt1 = temporalTweet.get(tweetId);
					String text = tweetText.get(tweetId);
					if(dt1.isBefore(dt3) || dt1.isEqual(dt3)){
						outBoundary1.println(tweetId+"##,##"+text+"##,##"+dt1);
					}
					//					else if(dt1.isAfter(dt3)&&(dt1.isBefore(dt4)||dt1.isEqual(dt4))){
					//						outBoundary2.println(tweetId+"##,##"+text+"##,##"+dt1);
					//					}
					else
					{
						outBoundary3.println(tweetId+"##,##"+text+"##,##"+dt1);
					}
				}
				outBoundary1.close();
				//				outBoundary2.close();
				outBoundary3.close();
			}
		}

		catch(Exception e){
			e.printStackTrace();
		}

	}

}
