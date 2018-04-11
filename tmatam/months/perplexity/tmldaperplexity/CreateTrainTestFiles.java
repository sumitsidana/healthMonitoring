package tmldaperplexity;
import java.io.*;
import java.util.*;
public class CreateTrainTestFiles {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		FileInputStream fileInputStream = new FileInputStream
				(args[0]); //"/media/toshibasecond/tmldaperplexity/tweetregioninference"
		DataInputStream dataInputStream = new DataInputStream(fileInputStream);
		BufferedReader bufferedReader = new BufferedReader
				(new InputStreamReader(dataInputStream));
		String line = null;
		Map<String,LinkedHashMap<String,String>>regionTweetDistribution  = new 
				LinkedHashMap<String,LinkedHashMap<String,String>>();

		while((line = bufferedReader.readLine()) != null){
			String [] array = line.split("##,##");
			for(int i = 0 ; i < array.length ; i++){
				String region =  array[1];
				String tweetId = array[0];
				String distribution = array[2];
				LinkedHashMap<String,String>tweetDistribution = null;
				if(regionTweetDistribution.containsKey(region)){
					tweetDistribution = 
							regionTweetDistribution.get(region);
				}
				else{
					tweetDistribution = new LinkedHashMap<String,String>();
				}
				tweetDistribution.put(tweetId, distribution);
				regionTweetDistribution.put(region, tweetDistribution);
			}
		}
		PrintWriter outTrain  = new PrintWriter(new BufferedWriter
				(new FileWriter(args[1],true)));
		PrintWriter outTest  = new PrintWriter(new BufferedWriter
				(new FileWriter(args[2],true)));
		Iterator it = regionTweetDistribution.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry pairs = (Map.Entry)it.next();
			String region = (String)pairs.getKey();
			LinkedHashMap<String,String>tweetDistribution = (LinkedHashMap<String, String>) 
					pairs.getValue();
			Iterator tweetIterator = tweetDistribution.entrySet().iterator();
			int index = 0;
			while(tweetIterator.hasNext()){
				Map.Entry tweetDistPair = (Map.Entry)tweetIterator.next();
				String tweetId = (String)tweetDistPair.getKey();
				String distribution = (String)tweetDistPair.getValue();
				String [] entityArray = distribution.split(",");
				if(index == 0){
					int len = 0;
					for(String entity:entityArray){
						String [] singleEntity = entity.split(":");
						if(len < entityArray.length-1)
							outTrain.write(singleEntity[1]+",");
						else
							outTrain.write(singleEntity[1]);
						len++;
					}
					outTrain.println();
				}
				else if(index == tweetDistribution.size() - 1){
					int len = 0;
					for(String entity:entityArray){
						String [] singleEntity = entity.split(":");
						if(len < entityArray.length-1)
							outTest.write(singleEntity[1]+",");
						else
							outTest.write(singleEntity[1]);
						len++;
					}
					outTest.println();
				}
				else{
					int len = 0;
					for(String entity:entityArray){
						String [] singleEntity = entity.split(":");
						if(len < entityArray.length-1){
							outTrain.write(singleEntity[1]+",");
							outTest.write(singleEntity[1]+",");
						}
						else{
							outTrain.write(singleEntity[1]);
							outTest.write(singleEntity[1]);
						}
						len++;
					}
					outTrain.println();
					outTest.println();
				}
				index ++;
			}
		}
		outTrain.close();
		outTest.close();
	}

}
