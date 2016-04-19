/**
 * 
 */
package edu.umd.ncsg.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Range;

import edu.umd.ncsg.SiloCSDMS;

/**
 * @author Darshan Pandit
 *
 */
public class CblcmDiffGenerator {
	static Logger logger = Logger.getLogger(CblcmDiffGenerator.class);
    
  
  public static Pattern pattern;
  public static String header;
  public static boolean replaceNegativeValues = true;
  
  /*
  public static void main(String[] args) throws IOException{
	  String[] inputFiles = {"D:\\Work\\git\\resultFileSpatial1.csv","D:\\Work\\git\\SpatialData2010.csv"};
	  String outputFile = "D:\\Work\\git\\resultFileSpatialDiff.csv";
	  int baseYear = 2010;
	  int finalYear = 2040;
	  
	 
	  CblcmDiffGenerator.generateCblcmDiff(inputFiles, outputFile, baseYear, finalYear);
	  
  }
  */
   
  /**
   * @param args
   * @throws IOException 
   */
  public static boolean generateCblcmDiff(String[] inputFiles, String outputFile,int baseYear,int finalYear) throws IOException {
    //Path path = Paths.get("D:\\Downloads\\resultFileSpatial1.csv");
    //Path outputPath = Paths.get("D:\\Downloads\\resultFileSpatial1_diff.csv");
	logger.info("Calculating Spatial Diff for Cblcm Compatible output");
	logger.info("baseYear : " + baseYear);
	logger.info("finalYear: " + finalYear);
	logger.info("InputFile: " + inputFiles[0]);
	logger.info("InputFile: " + inputFiles[1]);
	logger.info("OutputFile: " + outputFile);
	logger.info("********************************************************");
	Path outputFilePath = Paths.get(outputFile);
	pattern = Pattern.compile("Year (?<year>\\d{4,})");
	
	Map<String,double[]> baseYearValue = null, finalYearValue = null;
	
	for (String inputFilePath: inputFiles){
		Path path = Paths.get(inputFilePath);
		//map containing the year, filename and file offsets for the data
	    Map <Integer, int[]> map = generateMetaInfo(path, finalYear);
	    if(map.containsKey(baseYear)){
	    	baseYearValue = readYear(map.get(baseYear)[0], map.get(baseYear)[1], path) ;
	    };
	    if(map.containsKey(finalYear)){
	    	finalYearValue = readYear(map.get(finalYear)[0], map.get(finalYear)[1], path) ;
	    };
	}
    
    /*
     // If no years are provided, we assume the last two
     
    List<Integer> keys = new ArrayList<>(map.keySet());
    Collections.sort(keys);
    baseYear = keys.get( (keys.size()-1) );
    finalYear = keys.get (keys.size()-2 );
    */
    
    
	//We currently assume that the headers for both the files will match and years will be found.
	//TODO Excepetion Handling for year search in files and header mismatches between the files
	Map<String,double[]> t = null;
	try{
	t = computeDiff(baseYearValue, finalYearValue, null);
	}catch(Exception e){
		logger.error("Error while calculating SpatialDiff", e);
		throw e;
	}
    Writer writer = new FileWriter(outputFilePath.toFile());
    writer.write(header+"\n");
    for(String k:t.keySet()){
      StringBuilder builder = new StringBuilder();
      builder.append(k);
      for(double d:t.get(k)){
        builder.append(","+d);
      }
      builder.append("\n");
      writer.write(builder.toString());
    }
    writer.close();
    return true;
  }
  
  private static Map<Integer, int[]> generateMetaInfo(Path path, int finalYear) throws IOException{
      BufferedReader reader = new BufferedReader( new FileReader(path.toFile()) );
      
      Map<Integer, int[]> map =  new HashMap<>();
      
      int lineNumber = 0;
      int startLine = 0;
      int year=-1;
      
      Matcher m;
      
      boolean headerFlg = false;
      String line = reader.readLine();
      while(line!=null){
        m = pattern.matcher(line);
        if (m.matches()){
          
          //if not first record
          if (startLine != 0){
            map.put(year, new int[]{startLine, (lineNumber-1)} );
          }
          startLine = lineNumber+1;
          year = Integer.parseInt(m.group("year"));
          if(header==null && year==finalYear )
              headerFlg = true;
        }
        
        line = reader.readLine();
        if(headerFlg){
        	header = line;
        	headerFlg = false;
        }
        
        lineNumber++;
        
      }
      //if lastRecord
      if(year>0)
        map.put(year, new int[]{startLine, lineNumber});
      reader.close();
      return map;
  }

private static Map<String,double[]> readYear(int startLine, int endLine, Path path) throws IOException {
    Map<String, double[]> map = new HashMap<>();
    BufferedReader reader = new BufferedReader( new FileReader(path.toFile()) );
    int lineNumber=0;
    
    String line = reader.readLine();
    while(line!=null){
      //Ignore the header line here
      if(lineNumber>startLine && lineNumber<=endLine){
        String[] temp = line.split(",");
        double[] a = new double[temp.length-1];
        for (int i = 1; i < temp.length; i++) {
          a[i-1] = Double.valueOf(temp[i]);
        }
        map.put(temp[0], a);
      }
      line = reader.readLine();
      lineNumber++;
    }
    reader.close();
    return map;
  }

private static  Map<String,double[]> computeDiff( Map<String,double[]> baseYear, Map<String,double[]> finalYear, Set<Integer> diffColumns) {
	Map<String,double[]> result = new HashMap<>();
	
	if(baseYear==null) baseYear = new HashMap<>();
	
	Set<String> join_keys = new HashSet<>();
    join_keys.addAll(finalYear.keySet());
    join_keys.addAll(baseYear.keySet());
	
    for(String k: join_keys ){
      
    	
      double[] r = new double[finalYear.get(k)!=null? finalYear.get(k).length : baseYear.get(k).length];
      if (diffColumns==null)
    	  diffColumns = ContiguousSet.create(Range.closed(0, r.length), DiscreteDomain.integers());
      for(int i = 0; i<r.length;i++){
    	  if(diffColumns.contains(i)){
    		if ( finalYear.containsKey(k) && baseYear.containsKey(k))
    			r[i] = finalYear.get(k)[i] - baseYear.get(k)[i];
    		else{
    			if( finalYear.containsKey(k) ) r[i] = finalYear.get(k)[i];
    				else if( baseYear.containsKey(k)  ) r[i] = 0 - baseYear.get(k)[i];
    					 	else r[i] = 0;
    	   }
            if ( r[i]<0 && replaceNegativeValues ) r[i] = 0;
    	  }
    	  
    	  else{
    		  if( finalYear.containsKey(k) ) r[i] = finalYear.get(k)[i];
  				else if( baseYear.containsKey(k)  ) r[i] = baseYear.get(k)[i];
  					else r[i] = 0;
    	  }
        
      }
      result.put(k, r);
    }
    return result;
  }


}
