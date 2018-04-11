package tmldaperplexity;
import java.io.*;
import java.util.Arrays;
public class ComparisonPerplexities {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws NumberFormatException 
	 */
	public static void main(String[] args) throws NumberFormatException, IOException {
		// TODO Auto-generated method stub
		File dir = new File(args[0]); //path up to InvertedIndex
		String[] chld = dir.list();
		Arrays.sort(chld);

		double countofInvalidBoundaries = 0.0;
		double countofValidBoundaries = 0.0;
		double countoftmatambetter = 0.0;
		double countTotalBoundaries = 0.0;

		for(int chldIndex = 0 ; chldIndex < chld.length ; chldIndex++){
			if(new File(args[0]+"/"+chld[chldIndex]).isDirectory()){
				File dirRegion = new File(args[0]+"/"+chld[chldIndex]);
				String[] chldRegion = dirRegion.list();
				Arrays.sort(chldRegion);

				//				new File(args[1]+"/"+chld[chldIndex]).mkdirs();
				for(int chldBoundaryIndex = 0 ; chldBoundaryIndex
						< chldRegion.length ; chldBoundaryIndex++){
					countTotalBoundaries++;
					FileInputStream fstreamInvIndexBD = new FileInputStream
							(args[0]+"/"+chld[chldIndex]+"/"+chldRegion[chldBoundaryIndex]);
					DataInputStream inInvIndexBD = new DataInputStream(fstreamInvIndexBD);
					BufferedReader brInvIndexBD = new BufferedReader
							(new InputStreamReader(inInvIndexBD));
					String lineInvIndexBD = null;
					double perplexitytmatam = 0.0;
					double prplexitytmlda = 0.0;
					while((lineInvIndexBD = brInvIndexBD.readLine())!=null){
						perplexitytmatam = Double.parseDouble(lineInvIndexBD);
					}
					fstreamInvIndexBD = new FileInputStream
							(args[1]+"/"+chld[chldIndex]+"/"+chldRegion[chldBoundaryIndex]);
					inInvIndexBD = new DataInputStream(fstreamInvIndexBD);
					brInvIndexBD = new BufferedReader(new InputStreamReader(inInvIndexBD));
					while((lineInvIndexBD = brInvIndexBD.readLine())!=null){
						prplexitytmlda = Double.parseDouble(lineInvIndexBD); ;
					}
					if(Double.isInfinite(prplexitytmlda)||Double.isNaN(prplexitytmlda)||Double.isInfinite(perplexitytmatam)||Double.isNaN(perplexitytmatam))
					{
						countofInvalidBoundaries = countofInvalidBoundaries+1.0;
						continue;
					}
					else{
						countofValidBoundaries = countofValidBoundaries+1.0;
						if(perplexitytmatam<prplexitytmlda){
							countoftmatambetter=countoftmatambetter+1.0;
						}
						else
						{
							System.out.println(args[0]+"/"+chld[chldIndex]+"/"+chldRegion[chldBoundaryIndex]);
						}
					}
				}
			}
		}
		System.out.println("Total Boundaries:"+countTotalBoundaries);
		System.out.println("Total Invalid Boundaries:"+countofInvalidBoundaries);
		System.out.println("Total Valid Boundaries:"+countofValidBoundaries);
		System.out.println("Total Boundaries TMATAM did better:"+countoftmatambetter);

	}

}
