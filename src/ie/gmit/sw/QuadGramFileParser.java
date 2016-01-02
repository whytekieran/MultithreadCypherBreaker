package ie.gmit.sw;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class is responsible for parsing a text file in the format "SAND 5996705" and placing the two values into a 
 * HashMap then returning the Map so it may be used by TextScorer.java in the decrypting process.
 * @author Ciaran Whyte
 * @version 1.0
 */
public class QuadGramFileParser 
{
	private Map<String, Double> qGramMap;

	/**
	 * Constructor for QuadGramFileParser takes one argument
	 * @param qGramMap A hash map that will hold all the quad gram values when we parse them from a file
	 */
	public QuadGramFileParser(Map<String, Double> qGramMap) 
	{
		this.qGramMap = qGramMap;
	}
	
	/**
	 * @param file Represents the path of the file we want to parse given in String format
	 * @return The HashMap generated from parsing the file given in ConcurrentHashMap format
	 */
	public ConcurrentHashMap<String, Double> parseFile(String file)
	{
		try(BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream(new File(file)))))
		{
			String fileLine;
			while((fileLine = input.readLine()) != null)
			{
				String[] keyValue = fileLine.split(" ");
				qGramMap.put(keyValue[0], Double.parseDouble(keyValue[1]));
			}
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		return (ConcurrentHashMap<String, Double>) qGramMap;
	}
}
