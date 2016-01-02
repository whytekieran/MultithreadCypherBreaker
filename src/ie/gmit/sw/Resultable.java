package ie.gmit.sw;

/**
 * An interface for all Resultable's that are used in Multithread cyphers. Contains the basic methods you should be 
 * able to do with a Result object.
 * @author Ciaran Whyte
 * @version 1.0
 */
public interface Resultable 
{
	public abstract String getPlainText();

	public abstract void setPlainText(String plainText);

	public abstract int getKey();

	public abstract void setKey(int key);

	public abstract int getScore();

	public abstract void setScore(int score);
}