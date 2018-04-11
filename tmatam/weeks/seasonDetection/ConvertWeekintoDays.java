package seasonDetection;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.util.Arrays;

import org.joda.time.DateTime;

public class ConvertWeekintoDays {
	/*
	 * args[0] = /media/toshibasecond/healthmonitoringovertime/
	 * distributiondifferencedistances
	 */
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		File dir = new File(args[0]);
		String[] chld = dir.list();
		Arrays.sort(chld);
		for(int chldIndex = 0 ; chldIndex < chld.length ; chldIndex++){ 
			FileInputStream fstreamDifference = new FileInputStream(args[0]+"/"+chld[chldIndex]);
			DataInputStream inDifference = new DataInputStream(fstreamDifference);
			BufferedReader brDifference = new BufferedReader(new InputStreamReader(inDifference));
			String lineDifference = null;
			int index = 0;
			PrintWriter outDaysDiffSeason  = new PrintWriter(new BufferedWriter
					(new FileWriter("data/" +
							"predictedseasonsv1/"+
							chld[chldIndex].replace("bh", ""),true)));
			while((lineDifference = brDifference.readLine())!=null){
				int week = Integer.parseInt(lineDifference.substring(0,lineDifference.indexOf(",")));
				int year;
				if(week>=41)
					year = 2014;
				else
					year = 2015;
				DateTime weekEndDate = new DateTime().withWeekyear(year).withWeekOfWeekyear(week+1);
				System.out.println(weekEndDate);	
				index++;
				LineNumberReader  lnr = new LineNumberReader(new FileReader
						(new File(args[0]+"/"+chld[chldIndex])));
				lnr.skip(Long.MAX_VALUE);
				System.out.println(lnr.getLineNumber() + 1); 
				long numberofLines = lnr.getLineNumber() + 1;
				if(index >= numberofLines- 5)
					outDaysDiffSeason.println(weekEndDate.toLocalDate()+"," +
							lineDifference.substring(lineDifference.indexOf(",")+1));
				lnr.close();

			}
			outDaysDiffSeason.close();
		}
	}

}
