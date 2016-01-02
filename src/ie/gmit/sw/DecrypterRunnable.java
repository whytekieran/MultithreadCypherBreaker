package ie.gmit.sw;

//Imports used
import java.util.Map;
import java.util.concurrent.BlockingQueue;

/**
 * 
 * This class implements Runnable and hence is a Runnable, its purpose is to decrypt cypher text using a 
 * particular key and add the result to a queue. It decrypts text that has been initally encrypted using a 
 * railfence cypher
 * @author Ciaran Whyte
 * @version 1.0
 */
public class DecrypterRunnable implements Runnable //DecrypterRunnable is-a runnable, specification inheritance.
{
	//Private Instance Variables
	private BlockingQueue<Resultable> resultsQueue;
	private TextScorer scoreAwarder;    //DecrypterRunnable has-a text scorer, composition.
	private String cypherText;
	private int key;
	
	/**
	 * Constructor for DecrypterRunnable takes four arguments.
	 * @param resultsQueue The queue which will hold the result of the text after decryption
	 * @param key The key used for decrypting the text
	 * @param cypherText The text that needs to be decrypted
	 * @param qGramMap A HashMap that holds quad grams used for decrypting and will be passed as a parameter to a built in text scorers constructor.
	 */
	public DecrypterRunnable(BlockingQueue<Resultable> resultsQueue, int key, String cypherText,
			Map<String, Double> qGramMap)
	{
		//The constructor accepts a queue for the results, key for decrypting, the text to decrypt, and 
		//hash map containing the quad grams for decrypting. Constructor is called inside decryptCypherText()
		//which is called by the main() method
		this.resultsQueue = resultsQueue;
		this.cypherText = cypherText;
		this.key = key;
		this.scoreAwarder = new TextScorer(qGramMap);//Instantiate TextScorer with ConcurrentHashMap passed to constructor
	}
	
	/**
	 * 
	 * This method will decrypt the text provided using a railfence cypher then uses the classes built in text scorer to 
	 * give a score on how English like the text is. The text, key and score are added to a Result object which is then
	 * added to a queue of Result objects
	 */
	//Each worker thread used for decrypting will do the following
	public void run() 
	{
		double textScore;						//Variable to hold the score of the plain text calculated by TextScorer
		RailFence railCypher = new RailFence();						//RailFence cypher for decryption
		String plainText = railCypher.decrypt(cypherText, key);		//Decrypt using text and key passed to constructor
		textScore = scoreAwarder.getScore(plainText);				//Pass decrypted text to text scorer to get score.
		Resultable r = new Result(plainText, key, (int)textScore);	//Here we create a Result object and pass it the
																	//decrypted text, the key that was used and the score.
																	//The Result object references Resultable. Result is-a
																	//Resultable. This is an example of polymorphism.
		try 
		{
			resultsQueue.put(r);					//Add the Result object to a queue of Resultable objects
		} 
		catch(InterruptedException e) 
		{
			e.printStackTrace();
		}
	}
}
