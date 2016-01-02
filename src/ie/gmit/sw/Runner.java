package ie.gmit.sw;

//Imports used
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * This class contains the main method that runs my program and makes use of all the other classes and interfaces
 * shown in this API
 * @author Ciaran Whyte
 * @version 1.0
 */
public class Runner 
{
	//Scanner object used to except user input 
	static Scanner console = new Scanner(System.in);
	
	@SuppressWarnings("resource")
	/**
	 * The main method of the program
	 * @param args
	 */
	public static void main(String[] args) 
	{
		//Variables
		int choice = 0;					//Holds the users choice for text input input from keyboard or file
		String plainText;				//Holds text entered from user
		String filePath;				//Holds file path entered by user
		String fileText = "";			//Holds text read from a file
		boolean notExistant = true;		//Loop control variable used if user enters incorrect file path (loop & ask again)
		String cypherText;				//Holds text when its been encrypted
		
		do
		{
			//Ask the user do they want to enter the text or have it taken from a file
			System.out.println();
			System.out.println("Please enter a number for which input you are using:");
			System.out.println("1 = Plain Text");
			System.out.println("2 = Plain Text File");
			System.out.println("Enter a number ");
			choice = console.nextInt();
			
			if(choice != 1 && choice != 2)//Output appropriate message if there is invalid input
			{
				System.out.println("Incorrect number choosen please enter 1 or 2");//Message to user
			}
			
		}while(choice != 1 && choice != 2);//Keep asking the user to choose until valid number is choosen
		
		//Switch statement with two options depending on what user choose
		switch(choice)
		{
			//If the user chooses to enter the text manually
			case 1:
				console.nextLine();
				//Except the plain text typed from the user
				System.out.println("Minimum of 6 letters");
				System.out.println("Text entered must be all caps with no spaces");
				System.out.println("Enter text for encrypt/decrypt: ");
				plainText = console.nextLine();
				cypherText = encryptPlainText(plainText); //Pass into this method (defined below) which encrypts the text
				decryptCypherText(cypherText);//Once encrypted we pass that to method to decrypt with multiple threads
				break;
			//If the user chooses to use text read in from a .txt file
			case 2:
				do
				{
					console.nextLine();
					//Ask the user to enter the path for the file, specifies how it should be entered
					System.out.println("File path should be in format like the following: B:/Software/OOP/Example.txt");
					System.out.println("The text file's text should be all caps with no spaces to match the 4grams.txt quad gram");
					System.out.println("Please enter the path to the file you wish to encrypt/decrypt");
					filePath = console.nextLine();
					filePath = filePath.replace('\\', '/');  //Switch the slashes in case entered incorrectly
					File file = new File(filePath);		  	 //Create file object from the path
					
					if(!file.exists())//Check if the file exists and if it does not
					{
						//Output an appropriate message, then the do-while will iterate and ask user for the info again
						System.out.println("File does not exist please try again");
					}
					else//If it does exist
					{
						notExistant = false;//Set loop control variable to false
					}
				}while(notExistant);
				
				try 
				{
					//Scanner object which we pass a file reader object to the file specified
					Scanner plainTextFile = new Scanner(new FileReader(filePath));
					while(plainTextFile.hasNext())//Read the file and while the is data left to read
					{
						fileText += plainTextFile.nextLine();//Take the line read and append it to a string
					}
				}
				catch(FileNotFoundException e)
				{
					e.printStackTrace();
				}
				cypherText = encryptPlainText(fileText);//Pass plain text into method defined below to encrypt
				decryptCypherText(cypherText);//Once encrypted we pass that to method to decrypt with multiple threads
				break;
		}//End switch()
	}//End main()
	
	//Encrypts text using RailFence cypher
	/**
	 * Encrypts plain text using a Railfence cypher
	 * @param plainText The text to be encrypted
	 * @return The encrypted text
	 */
	public static String encryptPlainText(String plainText)
	{
		RailFence railCypher = new RailFence();			//Used to encrypt the text
		String cypherText;								//Holds text when its been encrypted
		cypherText = railCypher.encrypt(plainText, 5);	//Encrypt text with key of 5
		return cypherText;								//Return the text
	}//End encryptPlainText()
	
	 //Responsible from encrypting the text (passed as argument), creating/running worker threads which do the decrypting, 
	 //scoring and place the results into a queue. Then this method retrieves the results from the queue and compares them
	 //outputting the text with the best score 
	/**
	 * Decrypts the text using multiple threads and outputs the result to the screen
	 * @param cypherText The text to be decrypted
	 */
	public static void decryptCypherText(String cypherText)
	{
		//Variables
		int textLength = 0;			//Holds the length of the text
		int threadCount = 0;		//Increments when we take from the results queue
		int threadAmount = 0;		//Worker threads we have. When = to threadCount we have taken every result from queue
		boolean consume = true;							//Loop control variable, take from queue while this value is true
		BlockingQueue<Resultable> resultsQueue = new LinkedBlockingQueue<Resultable>(); //Holds results(passed to worker
																						//threads)
		Map<String, Double> qGramMap = new ConcurrentHashMap<String, Double>();//Holds Quad Grams (passed to worker threads
																				//because needed by text scorer each worker
																				//thread has-a text scorer
		QuadGramFileParser parser = new QuadGramFileParser(qGramMap);//Parse 4grams.txt into ConcurrentHashMap
		DecrypterRunnable job;										 //Runnable (The job each worker thread has to do)
		
		System.out.println("Encrypted Text: "+cypherText);	//Output the encrypted text
		textLength = cypherText.length();					//Get the length of the text
		qGramMap = parser.parseFile("4grams.txt");			//Parse 4grams.txt into concurrent has map
		
		 //Loop textlength/2 times and create a thread for each loop. Add 2 to this number because we will not use
		 //0 or 1 as a key.
		for(int i = 0; i <= (textLength / 2) + 2; ++i) 
		{
			//Make sure to only create threads that use a key of 2 or greater
			if(i > 1)
			{
				//Create a runnable that takes the result queue, key, encrypted text, and hash map of quad grams.
				job = new DecrypterRunnable(resultsQueue, i, cypherText, qGramMap);
				Thread worker = new Thread(job);	//Pass runnable to a thread
				worker.start();						//Start the thread
			}
		}

		threadAmount = (textLength / 2) + 1;							//Get how many worker threads we have
		Result tempResult2 = new Result("TestText", 0, -100000000);		/*Create a temp Result object with a score
																		that is certain to be lower than first score 
																		taken from the result queue*/
		//Amount of threads we created should equal the amount of results we have in the queue so we do the following..
		
		while(consume)	//while true
		{
			++threadCount; //increment thread counter because we are going to take result off queue
			
			try 
			{
				//Take result off the queue, if no thread has added to the queue yet because it may be still working 
				//the take() method will wait until an element has been added to the queue 
				Result r = (Result) resultsQueue.take(); 
				//Check if score taken is greater than the one previous (initially it always will be greater than temp)
				if(r.getScore() > tempResult2.getScore())
				{
					tempResult2 = r; //If greater then that result is the new temp
				}
			} 
			catch(InterruptedException e) 
			{
				e.printStackTrace();
			}
			//If the thread counter is equal to the amount of threads we have this means we have taken every Result
			//object from the queue. (Amount of threads is equal to amount of queue elements)
			if(threadCount == threadAmount)
			{
				consume = false;//So stop consuming and break the loop.
			}
		}
		
		//Temp Result object will now have the highest score so we output the result.
		System.out.println("Decrypted Text: "+tempResult2.getPlainText());
		System.out.println("Key used for encryption: "+tempResult2.getKey());
		System.out.println("Score generated: "+tempResult2.getScore());
	}//End decryptCypherText()
}//End class Runner
