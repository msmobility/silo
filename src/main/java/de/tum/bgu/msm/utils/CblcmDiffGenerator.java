/**
 * 
 */
package de.tum.bgu.msm.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.properties.modules.CblcmProperties;
import org.apache.log4j.Logger;

/**
 * @author Darshan Pandit
 *
 */
public class CblcmDiffGenerator {
	static Logger logger = Logger.getLogger(CblcmDiffGenerator.class);

	public static Pattern pattern;
	public static String header;
	public static boolean replaceNegativeValues = true;

	public static void main(String[] args) throws IOException {
		//Method used strictly for debugging.
		String base_path = "D:\\Work\\git\\";
		String[] inputFiles = { (base_path + "resultFileSpatial1.csv"), (base_path + "SpatialResult2010.csv") };
		String outputFile = (base_path + "resultFileSpatial2040Diff.csv");
		int baseYear = 2010;
		int finalYear = 2030;

		CblcmDiffGenerator.generateCblcmDiff(inputFiles, outputFile, baseYear, finalYear);

	}

	/**
	 * // * @param args
	 * 
	 * @throws IOException
	 */
	public static boolean generateCblcmDiff(String[] inputFiles, String outputFile, int baseYear, int finalYear)
			throws IOException {
		logger.info("Calculating Spatial Diff for Cblcm Compatible output");
		logger.info("baseYear : " + baseYear);
		logger.info("finalYear: " + finalYear);
		for (String inputFile : inputFiles)
			logger.info("InputFile: " + inputFile);
		logger.info("OutputFile: " + outputFile);
		logger.info("********************************************************");
		Path outputFilePath = Paths.get(outputFile);
		pattern = Pattern.compile("Year (?<year>\\d{4,})");

		Map<String, double[]> baseYearValue = null, finalYearValue = null, templateBaseYearValue = new HashMap<>();

		int templateBaseYear = 2010;
		Map<Integer,Double> multipliers = new HashMap<>();
		for (String inputFilePath : inputFiles) {
			Path path = Paths.get(inputFilePath);
			// map containing the year, filename and file offsets for the data
			Map<Integer, int[]> map = generateMetaInfo(path, finalYear);
			
			//Header has been setup from the method generateMetaInfo
			
			//To handle multipliers
			
			String[] columns = header.split(",");
			double temp;
			int columnNumber = 0;
			for(String column:columns){

					temp = Properties.get().cblcm.multiplierPrefix(column);

				if(temp!=0)
					multipliers.put(columnNumber, temp);
				columnNumber++;
				
			}
			

			if (inputFiles.length == 3 && map.containsKey(templateBaseYear)) {
				templateBaseYearValue = readYear(map.get(templateBaseYear)[0], map.get(templateBaseYear)[1], path);
			}

            if (map.containsKey(baseYear)) {
				baseYearValue = readYear(map.get(baseYear)[0], map.get(baseYear)[1], path);
			}
            if (map.containsKey(finalYear)) {
				finalYearValue = readYear(map.get(finalYear)[0], map.get(finalYear)[1], path);
			}
        }

		if (baseYearValue == null || finalYearValue == null) {
			logger.error("Unable to locate the configured Base or Final year for CBLCM diff generation process");
			throw new NullPointerException();
		}

		// We currently assume that the headers for both the files will match
		// and years will be found.
		// TODO Exception Handling for year search in files and header
		// mismatches between the files
		Map<String, double[]> t = null;
		try {
			t = computeDiff(baseYearValue, finalYearValue, templateBaseYearValue.keySet(), null, multipliers);
		} catch (Exception e) {
			logger.error("Error while calculating SpatialDiff", e);
			throw e;
		}
		Writer writer = new FileWriter(outputFilePath.toString());
		writer.write(header + "\n");

		for (String k : t.keySet()) {
			StringBuilder builder = new StringBuilder();
			builder.append(k);
			for (double d : t.get(k)) {
				builder.append(",").append(d);
			}
			builder.append("\n");
			writer.write(builder.toString());
		}
		writer.close();
		return true;
	}

	private static Map<Integer, int[]> generateMetaInfo(Path path, int finalYear) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(path.toFile()));

		Map<Integer, int[]> map = new HashMap<>();

		int lineNumber = 0;
		int startLine = 0;
		int year = -1;

		Matcher m;

		boolean headerFlg = false;
		String line = reader.readLine();
		while (line != null) {
			m = pattern.matcher(line);
			if (m.matches()) {

				// if not first record
				if (startLine != 0) {
					map.put(year, new int[] { startLine, (lineNumber - 1) });
				}
				startLine = lineNumber + 1;
				year = Integer.parseInt(m.group("year"));
				if (header == null && year == finalYear)
					headerFlg = true;
			}

			line = reader.readLine();
			if (headerFlg) {
				header = line;
				headerFlg = false;
			}

			lineNumber++;

		}
		// if lastRecord
		if (year > 0)
			map.put(year, new int[] { startLine, lineNumber });
		reader.close();
		return map;
	}

	private static Map<String, double[]> readYear(int startLine, int endLine, Path path) throws IOException {
		Map<String, double[]> map = new HashMap<>(); 
		BufferedReader reader = new BufferedReader(new FileReader(path.toFile()));
		int lineNumber = 0;

		String line = reader.readLine();
		while (line != null) {
			// Ignore the header line here
			if (lineNumber > startLine && lineNumber <= endLine) {
				String[] temp = line.split(",");
				double[] a = new double[temp.length - 1];
				for (int i = 1; i < temp.length; i++) {
					a[i - 1] = Double.valueOf(temp[i]);
				}
				map.put(temp[0], a);
			}
			line = reader.readLine();
			lineNumber++;
		}
		reader.close();
		return map;
	}

	private static Map<String, double[]> computeDiff(Map<String, double[]> baseYear, Map<String, double[]> finalYear,
			Set<String> zoneKeys, Set<Integer> diffColumns, Map<Integer,Double> multipliers) {
		
		if (multipliers==null)
				multipliers = new HashMap<>();
		
		Map<String, double[]> result = new TreeMap<>();

		if (baseYear == null)
			baseYear = new HashMap<>();

		Set<String> join_keys = new HashSet<>();
		join_keys.addAll(finalYear.keySet());
		join_keys.addAll(baseYear.keySet());

		
		if (zoneKeys != null)
			join_keys.addAll(zoneKeys);

		for (String k : join_keys) {

			double[] r = new double[finalYear.get(k) != null ? finalYear.get(k).length : baseYear.get(k).length];
			if (diffColumns == null){
				diffColumns = new HashSet<Integer>();
				  for (int i=0; i<=r.length; i++) {
					  diffColumns.add(i);
				  }
			}
			for (int i = 0; i < r.length; i++) {
				if (diffColumns.contains(i)) {
					if (finalYear.containsKey(k) && baseYear.containsKey(k)){
						r[i] = finalYear.get(k)[i] - baseYear.get(k)[i];
					}
					
						
					else {
						if (finalYear.containsKey(k))
							r[i] = finalYear.get(k)[i];
						else if (baseYear.containsKey(k))
							r[i] = 0 - baseYear.get(k)[i];
						else
							r[i] = 0;
					}
					if (r[i] < 0 && replaceNegativeValues)
						r[i] = 0;
				}

				else {
					if (finalYear.containsKey(k))
						r[i] = finalYear.get(k)[i];
					else if (baseYear.containsKey(k))
						r[i] = baseYear.get(k)[i];
					else
						r[i] = 0;
				}
				
				if(multipliers.containsKey(i))
					r[i] = r[i] * multipliers.get(i);
			}
			result.put(k, r);
		}
		return result;

	}

	private static Map<String, double[]> computeDiff(Map<String, double[]> baseYear, Map<String, double[]> finalYear,
			Set<Integer> diffColumns, Map<Integer,Double> multipliers) {
		return computeDiff(baseYear, finalYear, null, diffColumns,multipliers);
	}
}
