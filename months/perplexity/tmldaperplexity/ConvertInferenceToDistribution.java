package tmldaperplexity;
import java.io.*;
import java.util.*;
public class ConvertInferenceToDistribution {

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
		double numTopics = Integer.parseInt(args[1]);
		PrintWriter out  = new PrintWriter(new BufferedWriter
				(new FileWriter(args[2],true)));
		List<String> regions = new ArrayList<String>();
		Map<String,Map<String,Double>>tweetEntityDistribution = new 
				LinkedHashMap<String, Map<String,Double>>();

		while((line = bufferedReader.readLine())!=null){
			Map<String,Double>entityDistribution = new LinkedHashMap<String,Double>();
			//			System.out.println(line);
			for(int i = 1 ; i<=numTopics+1;i++){
				entityDistribution.put("Topic "+i, 0.0);
			}
			int indexofFirstDelimiter = line.indexOf("##,##");
			int indexofSecondDelimiter = line.indexOf("\t0 ");
			String tweetId = line.substring(0,indexofFirstDelimiter);
			String region = line.substring(indexofFirstDelimiter+5,indexofSecondDelimiter);
			String inference = line.substring(indexofSecondDelimiter+3);
			String [] tokens = inference.split(" ");
			for(String token : tokens){
				//				System.out.println(token);
				int indexofLastColon = token.lastIndexOf(":");
				String leaveOutToken = token.substring(0,indexofLastColon);
				int indexofSecondLastColon = leaveOutToken.lastIndexOf(":");
				String word = leaveOutToken.substring(0,indexofSecondLastColon);
				String topic = leaveOutToken.substring(indexofSecondLastColon+1);
				topic = (Integer.parseInt(topic)+1)+"";
				entityDistribution.put("Topic "+topic, entityDistribution.get
						("Topic "+topic)+1.0/tokens.length);
			}
			tweetEntityDistribution.put(tweetId, entityDistribution);
			regions.add(region);
		}
		Iterator it = tweetEntityDistribution.entrySet().iterator();
		int regionIndex = 0;
		while(it.hasNext()){
			Map.Entry pairs = (Map.Entry)it.next();
			String tweetId = (String) pairs.getKey();
			String region = regions.get(regionIndex);
			regionIndex++;
			LinkedHashMap<String,Double> entityDistribution = (LinkedHashMap<String, Double>)
					pairs.getValue();
			Iterator itEntity = entityDistribution.entrySet().iterator();
			out.write(tweetId+"##,##"+region+"##,##");
			while(itEntity.hasNext()){
				Map.Entry pairsEntity = (Map.Entry)itEntity.next();
				String entity = (String) pairsEntity.getKey();
				double probability = (double) pairsEntity.getValue();
				out.write(entity+":"+probability+",");	
			}
			out.println();
		}
		out.close();

	}

}
