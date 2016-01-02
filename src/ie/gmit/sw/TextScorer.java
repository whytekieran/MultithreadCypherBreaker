package ie.gmit.sw;

import java.util.Map;
/**
 * This class will score a piece of text. The higher the score it gives, the more English like the text is.
 * @author John Healy
 * @version 1.0
 */
public class TextScorer 
{
	private Map<String, Double> map = null;
	
	/**
	 * Constructor for TextScorer takes one argument
	 * @param map Represents a HashMap (preferably concurrent) that contains quad grams used to determine English like words
	 */
	public TextScorer(Map<String, Double> map)
	{
		this.map = map;
	}
	
	/**
	 * Will score a piece of text by how close to English it is
	 * @param text The text that you wish to determine how English like it is
	 * @return A double value representing the score of the text entered, the higher the better
	 */
	public double getScore(String text)
	{
		double score = 0f;

		for (int i = 0; i < text.length(); i++)
		{
			if (i + QuadGram.GRAM_SIZE <= text.length() -1)
			{
				score += computeLogScore(text.substring(i, i + QuadGram.GRAM_SIZE));
			}
		}
		return score;
	}
	
	/**
	 * @param quadgram A piece of text (substring) that is quad gram in length that will be checked for validity
	 * @return The score (Double) generated from that piece of text representing its likness to English.
	 */
	public double computeLogScore(String quadgram)
	{
		if (map.containsKey(quadgram))
		{
			double frequency = map.get(quadgram);
			double total =  map.size();
			double probability = (frequency/total);
			
			return Math.log10(probability);
		}
		else
		{
			return 0f;
		}
	}
}
