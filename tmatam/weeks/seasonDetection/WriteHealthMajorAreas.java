package seasonDetection;
import java.io.*;
import java.util.*;
public class WriteHealthMajorAreas {
	/*
	 * /media/toshibasecond/healthmonitoringovertime/tweetregion
	 * /media/toshibasecond/healthmonitoringovertime/healthTweets 
	 * /media/toshibasecond/healthmonitoringovertime/healthMajorAreas
	 */
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		FileInputStream fileTweetRegion = new FileInputStream
				(args[0]);
		DataInputStream inTweetRegion = new DataInputStream(fileTweetRegion);
		BufferedReader brTweetRegion = new BufferedReader
				(new InputStreamReader(inTweetRegion,"UTF-8"));
		String strTweetRegion = null;
		Map<String,String>  tweetRegion = new HashMap<String,String>();
		while((strTweetRegion = brTweetRegion.readLine())!=null){
			String [] array = strTweetRegion.split(",");
			tweetRegion.put(array[0], array[1]);
		}
		FileInputStream fileHealthFile = new FileInputStream(args[1]);
		DataInputStream inHealthFile = new DataInputStream(fileHealthFile);
		BufferedReader brHealthFile = new BufferedReader
				(new InputStreamReader(inHealthFile,"UTF-8"));
		String strHealth = null;
		PrintWriter out  = new PrintWriter(new BufferedWriter
				(new FileWriter
						(args[2],true)));
		brHealthFile.readLine();
		while((strHealth = brHealthFile.readLine())!=null){
			int indexofFirstTab = strHealth.indexOf("\t");
			String tweetId = strHealth.substring(0,indexofFirstTab);
			if(tweetRegion.containsKey(tweetId)){
				out.println(strHealth+"##,##"+tweetRegion.get(tweetId));
			}
			else{
				out.println(strHealth+"##,##"+"NoPlaceFound#null");
			}
		}
		out.close();
	}

}
