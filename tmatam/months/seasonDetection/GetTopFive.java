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
import java.io.*;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class GetTopFive {
	private static String getLastDayOfMonth(int month, int year) {
		LocalDate lastDayOfMonth = new LocalDate(year, month, 1).dayOfMonth().withMaximumValue();
		return lastDayOfMonth.toString("MM/dd/yyyy");
	}
	/*
	 * args[0] = /media/toshibafirst/regions/fullregion/sortedweeks
	 * args[0] = /media/toshibafirst/regions/fullregion/sortedpvaluesweeks
	 * args[0] = /media/toshibafirst/regions/fullregion/sortedcosinesimilarityweeks
	 */
	public static void main(String[]args) throws IOException{
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
					(new FileWriter("data" +
							"/predictedseasonsv3/"+
							chld[chldIndex],true)));
			brDifference.readLine();
			while((lineDifference = brDifference.readLine())!=null){
				String str_week = lineDifference.substring(lineDifference.indexOf(",")+1,
						lineDifference.lastIndexOf(","));
				int month = Integer.parseInt(str_week.replace("\"", ""));
				int year;
				if(month>=10)
					year = 2014;
				else
					year = 2015;
				//				DateTime monthEndDate = new DateTime().withWeekyear(year)
				//						.withMonthOfYear(month);
				DateTimeFormatter formatter = DateTimeFormat.forPattern("MM/dd/yyyy");

				String temp = getLastDayOfMonth(month, year);
				DateTime monthEndDate = formatter.parseDateTime(temp);
				System.out.println(monthEndDate);
				index++;
				System.out.println(chld[chldIndex]);
				LineNumberReader  lnr = new LineNumberReader(new FileReader
						(new File(args[0]+"/"+chld[chldIndex])));
				lnr.skip(Long.MAX_VALUE);
				System.out.println(lnr.getLineNumber() + 1); 
				long numberofLines = lnr.getLineNumber() + 1;

				//				if(index >= numberofLines - 7 && index < numberofLines - 2)
				if(index<=5)
					outDaysDiffSeason.println(monthEndDate.toLocalDate()+"," +
							(Math.sqrt
									(2.0-2*Double.parseDouble(lineDifference.substring
											(lineDifference.lastIndexOf(",")+1)))));



				//					outDaysDiffSeason.println(weekEndDate.toLocalDate()+"," +
				//							(lineDifference.substring
				//									(lineDifference.lastIndexOf(",")+1)));
			}
			outDaysDiffSeason.close();
		}
	}
}
