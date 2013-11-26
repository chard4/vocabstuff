import java.util.Random;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.*;

/**
 * This is supposed to combine 
 * WordDatabase2 and LatinVocabHelper.
 * 
 * @author Richard Gu
 * @version 1 04
 */
public class VocabBackOffice
{
    private String file;
    private ArrayList<String> missedWordsList;
    private ArrayList<String> missedCluesList;
    private ArrayList<String> wordsList;
    private ArrayList<String> cluesList;
    private String[] currentWord;

    /**
     * Creates a VocabBackOffice :)
     * 
     * @param newFileName the file name that you want
     *        to start out with
     */
    public VocabBackOffice(String newFileName)
    {
        file = newFileName;
        wordsList = new ArrayList<String>();
        cluesList = new ArrayList<String>();
        missedWordsList = new ArrayList<String>();
        missedCluesList = new ArrayList<String>();
    }
    
    /**
     * Gets the words list.
     * Note that if using the missed words lists for testing,
     * this will return those missed words, not the ones
     * that you missed while testing the missed words (awk wording...)
     * 
     * @return an ArrayList of all the words
     */
    public ArrayList<String> getWordsList()//useless
    {
        return wordsList;
    }
    
    /**
     * Gets the clues list.
     * Note that if using the missed words lists for testing,
     * this will return those missed clues, not the ones
     * that you missed while testing the missed words (awk wording...)
     * 
     * @return an ArrayList of all the clues
     */
    public ArrayList<String> getCluesList()//useless
    {
        return cluesList;
    }
    
    /**
     * Gets the missed words list.
     * Note that if the missed words/clues are the
     * active lists, then it won't return the current missed words;
     * instead, it will return the missed words from that testing.
     * 
     * @return an array of all the missed words
     */
    public ArrayList<String> getMissedWordsList()//useless
    {
        return missedWordsList;
    }
    
    /**
     * Gets the missed clues list.
     * Note that if the missed words/clues are the
     * active lists, then it won't return the current missed clues;
     * instead, it will return the missed clues from that testing.
     * 
     * @return an array of all the corresponding clues
     *         to the missed words
     */
    public ArrayList<String> getMissedCluesList()//useless
    {
        return missedCluesList;
    }
    
    /**
     * Gets the current file name.
     * 
     * @return the name of the current file
     */
    public String getFile()//useless
    {
        return file;
    }
    
    /**
     * Adds a missed word to the missedWordsList arraylist.
     * 
     * Precondition:    the word and clue correspond correctly
     * 
     * @param word the word you're adding
     * @param clue the clue you're adding
     */
    public void addWordToMissed(String word, String clue)
    {
        missedWordsList.add(word);
        missedCluesList.add(clue);
    }
    
    /**
     * Removes the word and clue from the arraylists.
     * 
     * Precondition:    the word and clue correspond correctly
     * 
     * @param word the word you're removing
     * @param clue the clue you're removing
     */
    public void removeWordAndClue(String word, String clue)
    {
        missedWordsList.remove(word);
        missedCluesList.remove(clue);
    }
    
    /**
     * A method to allow the user to test the missed words.
     */
    public void useMissedLists()
    {
        if (missedWordsList.size()==0&&missedCluesList.size()==0)
            return;
        wordsList = missedWordsList;
        cluesList = missedCluesList;
        missedWordsList.clear();
        missedCluesList.clear();
    }
    
    /**
     * Sets the file to a new one and updates the word list and clue list.
     * 
     * @param newFile the name of the new file
     */
    public void setStage(String newFile)
    {
        file = newFile;
        try
        {
            getWordsAndClues();
        }
        catch (IOException e)
        {
            if (e instanceof FileNotFoundException)
                System.out.println("file not found.");
        }
    }
    
    /**
     * Gets the file length of the current chapter file.
     * 
     * @throws IOException if the current file is invalid
     */
    public int getFileLength() throws IOException
    {
        FileReader fileToRead = new FileReader(file);
        BufferedReader bf = new BufferedReader(fileToRead);
        String line;
        int numOfLines = 0;
        while ((line = bf.readLine())!=null)
        {
            numOfLines++;
        }
        bf.close();
        fileToRead.close();
        return numOfLines;
    }
    
    /**
     * Sets up the word and clue arrays.
     * 
     * @throws IOException if the current file is invalid
     */
    public void getWordsAndClues() throws IOException
    {
        FileReader fileToRead = new FileReader(file);
        BufferedReader bf = new BufferedReader(fileToRead);
        Scanner s = new Scanner(bf);
        int i = 0;
        int max = getFileLength();
        wordsList.clear();
        cluesList.clear();
        s.useDelimiter(";\\s*");
        while (s.hasNext()&&i<max)
        {
            wordsList.add(s.next());
            if (s.hasNext())
            {
                cluesList.add(s.next());
            }
            else
            {
                System.out.println("Your file format was wrong.");
                return;
            }
            i++;
        }
        s.close();
    }
    
    /**
     * Creates a new file for the missed words.
     * 
     * @param fileName the name of the file you're creating
     */
    public void sendMissedToFile(String fileName)
    {
        File file = new File(fileName);
        try
        {
            file.createNewFile();
            BufferedWriter writer = new BufferedWriter(
            new FileWriter(fileName, true));
            for (int i = 0; i < missedWordsList.size(); i++)
            {
                writer.write(missedWordsList.get(i)+"; "+missedCluesList.get(i)+";");
                if (i<missedWordsList.size()-1)
                    writer.write("\n");
            }
            writer.close();
        }
        catch (IOException e)
        {
            System.out.println("The file already exists.");
        }
    }
    
    /**
     * If the user wants to create a new file within
     * the program, then use this method.
     * Another method will be used to add stuff to the selected file.
     * 
     * @param fileName the name of the file that you want to create
     */
    public void createNewFile(String fileName)
    {
        File newFile = new File(fileName);
        try
        {
            newFile.createNewFile();
            BufferedWriter writer = new BufferedWriter(
            new FileWriter(fileName, true));
        }
        catch (IOException e)
        {
            System.out.println("Pick another name. This one already exists.");
        }
    }
    
    /**
     * This adds a specified word and definition
     * to the specified file.
     * 
     * @param word the word you're adding
     * @param fileName the name of the file you're adding to
     * @param type whether the word you're adding 
     *        is a definition (false) or the required input (true)
     */
    public void addWordToFile(String word, String fileName, boolean type)
    {
        try
        {
            BufferedWriter writer = new BufferedWriter(
            new FileWriter(fileName, true));
            writer.write(word+"; ");
            if (type==false)//it's the definition
                writer.write("\n");
            //this little if-statement is just for reading convenience
            writer.close();
        }
        catch (IOException e)
        {
            System.out.println("File doesn't exist.");
        }
    }
    
    /**
     * Randomly selects a word and the corresponding clue
     * from the String[]s. 
     * 
     * @return a String[] of length 2 holding the word and clue
     *         that was picked
     */
    public String[] pickWordAndClue()
    {
        String[] temp = currentWord;
        currentWord = new String[2];
        if (wordsList.size()==0)
            return null;
        int index = (int)(Math.random()*wordsList.size());
        currentWord[0] = wordsList.get(index);
        currentWord[1] = cluesList.get(index);
        try
        {
            trimDownWordsAndClues();
        }
        catch (NullPointerException e)
        {
            //word array is empty
            setStage(file);//resets it
        }
        return currentWord;
    }
    
    /**
     * Gets rid of words that were already used.
     * This is to make sure that every word will be tested
     * and also so that words don't randomly repeat.
     * 
     * @throws NullPointerException if the words or clues are null
     */
    public void trimDownWordsAndClues() throws NullPointerException
    {
        removeWordAndClue(currentWord[0], currentWord[1]);
        for (int i = 0; i < wordsList.size(); i++)
        {
            if ((wordsList.get(i).equals(currentWord[0])))
            {
                wordsList.remove(i);
                cluesList.remove(i);
                return;
            }
        }
    }
    
    /**
     * Checks if the user guessed the word correctly.
     * 
     * @return true if the user did; otherwise false.
     */
    public boolean isCorrect(String guess)
    {
        return guess.equalsIgnoreCase(currentWord[0]);
    }
    
    /**
     * Clears the missed arraylists.
     * This is only useful if you want to clear it (no duh)
     * EX: I'm done with a file (this means you auto-save a missed words list for it).
     * So, I would want to clear the arraylists so I can have file-specific missed txts.
     */
    public void clearMissed()
    {   
        missedWordsList.clear();
        missedCluesList.clear();
    }
}