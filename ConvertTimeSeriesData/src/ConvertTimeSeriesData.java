
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.json.JSONObject;

import org.bson.BsonDateTime;
import org.bson.BsonTimestamp;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.ISODateTimeFormat;
import org.json.JSONArray;

/*
 * 
 * This class includes several static methods that may be used in combination to convert input timeseries data to the desired format. 
 * 
 */
public class ConvertTimeSeriesData {
	
	/**
	 * 
	 * This method takes a csv file delimited with commas as an input to generate a 2D List of the same size. 
	 * 
	 * */
	
	public static List<List<String>> loadCSV (String inputFile) {
		List<List<String>> inputData = new ArrayList<List<String>>(); 
		try {
			FileReader fr = new FileReader(inputFile); 
			BufferedReader br = new BufferedReader(fr); 
			String line;
			while((line = br.readLine())!=null)
			{ 
                    inputData.add(Arrays.asList(line.split(",")));   
			}
			br.close();		 
			fr.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return inputData;
		
	}
	
	/**
	 * 
	 * This method converts a 2D List into a 2D Array.
	 * 
	 * */
	
	public static String[][]  convert2DoubleArray(List<List<String>> data) {
		
		String[][] matrix = new String[data.get(0).size()][data.size()];
		for (int i = 0;i<data.size();i++) {
			for (int j = 0;j<data.get(i).size();j++) {
				matrix[i][j] = data.get(i).get(j);
			}
		}
		
		return matrix;
	}
	
	/**
	 * 
	 * This method transposes an input 2D List into a transposed 2D Array.
	 * 
	 * */
	
	public static String[][]  transpose2DoubleArray(List<List<String>> data) {
		
		String[][] transposed = new String[data.get(0).size()][data.size()];
		for (int i = 0;i<data.size();i++) {
			for (int j = 0;j<data.get(i).size();j++) {
				transposed[j][i] = data.get(i).get(j);
			}
		}
		
		return transposed;
	}
	
	/**
	 * 
	 * This method writes an input 2D array to a CSV file by optionally putting header information in the first row.
	 * The first column of the optional first row is the timestamp variable and the remaining variables are named with numbers based on their incremental order. 
	 * 
	 * **/
	
	public static void writeCSVWithInstant(String[][] doubleArray,String outputFile, boolean withoutHeaders) {
		
		LocalDateTime localDateTime = LocalDateTime.now();
		ZonedDateTime zdt;
		
		try {
			FileWriter fw = new FileWriter(outputFile); 	
			StringBuilder sb = new StringBuilder(0);
			
			if(!withoutHeaders) {
				List<String> temp = new ArrayList<String>();
				 temp.add("timestamp");
            	 for (int j = 0;j<doubleArray[0].length;j++)
            		 temp.add(String.valueOf(j));           	 
 				sb.append(String.join(",",temp.toArray(doubleArray[0])));
 				fw.append(sb.toString());
 				fw.append("\n");
 				sb.setLength(0);
			}			
			
			for (int i = 0;i<doubleArray.length;i++) {
				
				System.out.println(i);
				zdt = ZonedDateTime.of(localDateTime.plusNanos(10000000*i), ZoneId.systemDefault());
                List<String> temp = new ArrayList<String>();
                temp.add(zdt.toInstant().toString());
                temp.addAll(Arrays.asList(doubleArray[i]));
				sb.append(String.join(",",temp.toArray(doubleArray[i])));
				fw.append(sb.toString());
				fw.append("\n");
				sb.setLength(0);
			}			 
			fw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
			
	}
	
	/**
	 * 
	 * This method writes an input 2D array to a CSV file by optionally putting header information in the first row.
	 * The first column of the optional first row is the timestampInEpoc variable and the remaining variables are named with numbers based on their incremental order. 
	 * 
	 * **/
	
	public static void writeCSVWithEpocs(String[][] doubleArray,String outputFile, boolean withoutHeaders) {
		
		LocalDateTime localDateTime = LocalDateTime.now();
		ZonedDateTime zdt;
		
		try {
			FileWriter fw = new FileWriter(outputFile); 	
			StringBuilder sb = new StringBuilder(0);
			
			if(!withoutHeaders) {
				List<String> temp = new ArrayList<String>();
				 temp.add("timestampInEpoch");
            	 for (int j = 0;j<doubleArray[0].length;j++)
            		 temp.add(String.valueOf(j));           	 
 				sb.append(String.join(",",temp.toArray(doubleArray[0])));
 				fw.append(sb.toString());
 				fw.append("\n");
 				sb.setLength(0);
			}
			
			for (int i = 0;i<doubleArray.length;i++) {
				
				System.out.println(i);
				zdt = ZonedDateTime.of(localDateTime.plusNanos(10000000*i), ZoneId.systemDefault());
                List<String> temp = new ArrayList<String>();
                temp.add(String.valueOf(zdt.toInstant().toEpochMilli()));
                temp.addAll(Arrays.asList(doubleArray[i]));	
				sb.append(String.join(",",temp.toArray(doubleArray[i])));
				fw.append(sb.toString());
				fw.append("\n");
				sb.setLength(0);
			}			 
			fw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
			
	}
	
	/*
	 * 
	 * This method partitions an input csv file into the given number of horizontal partitions by considering the presence of a header in the csv file.
	 * The method is intended to be used for partial loading of data into a timeseries database.
	 * 
	 * **/
	
	public static void partitionCSV(String inputFile,  int noofPartitions, boolean withoutHeaders) {
		
		try {
			FileReader frRowCount = new FileReader(inputFile); 
			BufferedReader brRowCount = new BufferedReader(frRowCount);
			int noofRows = 0;
			while (brRowCount.readLine() != null) noofRows++;
			brRowCount.close();
			FileReader fr = new FileReader(inputFile); 
			BufferedReader br = new BufferedReader(fr);
				
			String line;
			List<FileWriter> fws = new ArrayList<FileWriter>();
			
			for(int i = 0;i<noofPartitions;i++) {
				fws.add(new FileWriter(inputFile.replace(".", String.valueOf(i) +"." )));
			}

			int partitionSize = 0;
			if(withoutHeaders ) {
				 partitionSize = (int) Math.ceil((double) noofRows / noofPartitions);
			} else 
				partitionSize = (int) Math.ceil((double) (noofRows-1) / noofPartitions);
			System.out.println("partitionSIze: "+partitionSize);
					
			int count = 0;
			String header = "";
			if(!withoutHeaders) {
				header = br.readLine();		
				for(int i = 0;i<noofPartitions;i++) {
					fws.get(i).append(header).append("\n");
				}	
			}
			
			while((line = br.readLine())!=null)
			{ 
				fws.get(count/partitionSize).append(line).append("\n");;
				count++;
			}	
			
			br.close();		 
			fr.close();
			
			for(int i = 0;i<noofPartitions;i++) {
				fws.get(i).close();
			}			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	}
	
	/**
	 * 
	 * This method converts an input 2D array that is ONLY composed of data points to a json file. with variables
	 * Each row of the input 2D array is treated as a separate time point and a timestamp is given as the first variable of each record.
	 * The remaining variables of each record are incrementing numbers that correspond to the columns of the input 2D array.
	 * 
	 * */
	
	public static void writeJSON(String[][] doubleArray,String outputFile) {
		
		JSONArray timeSeries = new JSONArray();		
		LocalDateTime localDateTime = LocalDateTime.now();
		ZonedDateTime zdt;
		
		try {
			FileWriter fw = new FileWriter(outputFile); 	
			BufferedWriter bw = new BufferedWriter(fw);
			
			for (int i = 0;i<doubleArray.length;i++) {
				JSONObject timeSlot = new JSONObject();
//				System.gc();
				timeSlot.clear();
				System.out.println(i);
				
				zdt = ZonedDateTime.of(localDateTime.plusNanos(10000000*i), ZoneId.systemDefault());
				timeSlot.put("timestamp", zdt.toInstant());
				
				for (int j = 0;j<doubleArray[i].length;j++) {
//					System.out.println(i + " - " + j);
					timeSlot.put(String.valueOf(j+1), doubleArray[i][j]);
				}
                timeSeries.put(timeSlot);
			}		
			
			bw.write(timeSeries.toString());
			bw.close();
			fw.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 
	 * This method is intended to be a json file as an import format for a mongodb timeseries collection 
	 * that includes a timeseries variable called timestamp and format of Bson UTC DateTime.
	 * 
	 * */
	
	public static void writeBSON(String[][] doubleArray,String outputFile) {
				
		JSONArray timeSeries = new JSONArray();	
		
		DateTime dt = DateTime.now();
		DateTimeZone zone = DateTimeZone.UTC;
	    DateTimeZone.setDefault(zone);
	    
		try {
			FileWriter fw = new FileWriter(outputFile); 	
			BufferedWriter bw = new BufferedWriter(fw);
			
			
			for (int i = 0;i<doubleArray.length;i++) {
				JSONObject timeSlot = new JSONObject();
				timeSlot.clear();
				System.out.println(i);

				timeSlot.put("timestamp", dt.plusMillis(i*10).withZone(zone));
				System.out.println(dt.plusMillis(i*10).withZone(zone));
				
				for (int j = 0;j<doubleArray[i].length;j++) {
					timeSlot.put(String.valueOf(j+1), doubleArray[i][j]);
				}
                timeSeries.put(timeSlot);
			}		
			
			bw.write(timeSeries.toString());
			bw.close();
			fw.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * This method generates a schema configuration file in json format to be used for Apache Pinot.
	 * The method assumes the time series will be in epochs . So its respective configration variable is named as timestampInEpoch.
	 * noof_ecgs assigns is the number of dimension variables denoted with a number.
	 * The schemaName will later be used in the table configuration for mapping.
	 * 
	 * */
	
	public static void createSchemaConfiguration(int noof_ecgs, String schemaName, String outputFile) {
		
		JSONObject wrapper = new JSONObject();
		
		wrapper.put("schemaName", schemaName);
			
		JSONArray dimensionFieldSpecs = new JSONArray();		
		
		for (int i = 0;i<noof_ecgs;i++) {
			
		    JSONObject  tempDimension = new JSONObject();
			tempDimension.clear();
			tempDimension.put("name", String.valueOf(i)).put("dataType", "DOUBLE");
			dimensionFieldSpecs.put(tempDimension);
			}
		
		JSONArray metricFieldSpecs = new JSONArray();
		JSONArray timeFieldSpecs = new JSONArray();
		
		 JSONObject  tempDateTime = new JSONObject();
		 
		 tempDateTime.put("name", "timestampInEpoch").put("dataType", "LONG").put("format", "1:MILLISECONDS:EPOCH").put("granularity", "1:MILLISECONDS");
		 timeFieldSpecs.put(tempDateTime);
		 
		 if (metricFieldSpecs.length()>0)
			 wrapper.put("metricFieldSpecs", metricFieldSpecs);
		 
		 wrapper.put("dateTimeFieldSpecs", timeFieldSpecs).put("dimensionFieldSpecs", dimensionFieldSpecs);
		 
			try {
				FileWriter fw = new FileWriter(outputFile); 	
				BufferedWriter bw = new BufferedWriter(fw);
				
				bw.write(wrapper.toString().replace("\n", ""));
				bw.close();
				fw.close();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	}
		
	public static void main(String[] args) {
			
		switch (args[0]) {
		  case "writeCSVWithInstant":
			  writeCSVWithInstant(transpose2DoubleArray(loadCSV(args[1])), args[2], Boolean.parseBoolean(args[3]));
		    break;
		  case "writeCSVWithEpocs":
			  writeCSVWithEpocs(transpose2DoubleArray(loadCSV(args[1])), args[2],Boolean.parseBoolean(args[3]));
		    break;
		  case "partitionCSV":
			  partitionCSV(args[1],Integer.parseInt(args[2]),Boolean.parseBoolean(args[3]));
		    break;
		  case "writeJSON":
			  writeJSON(transpose2DoubleArray(loadCSV(args[1])), args[2]);
		    break;
		  case "writeBSON":
			  writeBSON(transpose2DoubleArray(loadCSV(args[1])), args[2]);	
		    break;
		  case "schemaConfiguration":
			  createSchemaConfiguration(Integer.parseInt(args[1]),args[2],args[3]);
          default: System.out.println("Choose from writeCSVWithInstant, writeCSVWithEpocs, partitionCSV, writeJSON, writeBSON and schemaConfiguration options.");
          break;
		}		
	
	}

}