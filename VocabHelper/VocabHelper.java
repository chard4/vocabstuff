import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

/**
 * Creates the GUI for the Vocab Practicer
 * --I hate making GUIs now... soooo tedious.
 * 
 * @author Richard Gu
 * @version 1 04
 */
public class VocabHelper
{
    private JFrame helper;
    
    private JPanel scoreTime;
    private JPanel defWordInput;
    
    private JTextField word;
    private JTextField def;
    private JTextField score;
    private JTextField missed;
    private JTextField remaining;
    private JTextField time;
    private JTextField input;
    
    private Timer myTimer;
    private int ticker;
    
    private VocabBackOffice bob;
    
    private String definition;
    private String theWord;
    
    private String fileName;
    
    private boolean pickedFile;
    private boolean doneWithWord;
    
    private boolean creativeMode;
    private boolean speedDemon;
    
    private int totalPossible;
    private int left;
    private int missedCount;
    private int scoreNum;
    
    /**
     * Initializes many instance variables and
     * creates a VocabHelper object.
     */
    public VocabHelper()
    {
        ticker = 20;
        fileName = "";
        bob = new VocabBackOffice(fileName);
        scoreNum = 0;
        left = 0;
        missedCount = 0;
        totalPossible = 0;
        pickedFile = false;
        doneWithWord = false;
        creativeMode = false;
        speedDemon = false;
    }
    
    /**
     * Our favorite little method that runs the thing.
     */
    public static void main (String[] args)
    {
        VocabHelper abby = new VocabHelper();
        abby.createAndShowGUI();
    }
    
    /**
     * Creates the GUI.
     */
    private void createAndShowGUI()
    {
        helper = new JFrame("Vocab Helper");
        helper.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        helper.setPreferredSize(new Dimension(400,500));
        
        JPanel overall = new JPanel();
        overall.setLayout(new GridLayout(3, 1));
        createDefWordInput();
        overall.add(defWordInput);
        createScoreTime();
        overall.add(scoreTime);
        updateScore();
        JButton start = new JButton("Start (Gimme a word!)");
        start.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                if (!creativeMode)
                {
                    if (pickedFile&&time.getText().equals("20 s"))
                    {
                        setWordClue();
                        if (!doneWithWord)
                            myTimer.start();
                    }
                    else if (!pickedFile)
                    {
                        pickedFile = pickFile();
                    }
                    else
                    {
                        JOptionPane.showMessageDialog(helper, 
                        "Your time has not run out yet.",
                        "Warning", JOptionPane.PLAIN_MESSAGE);
                    }
                }
            }
        });
        overall.add(start);
        
        createMenuBar();
        
        helper.add(overall);
        
        helper.pack();
        helper.setVisible(true);
    }
    
    /**
     * Creates the definition, word, and input
     * text fields.
     */
    private void createDefWordInput()
    {
        defWordInput = new JPanel();
        defWordInput.setLayout(new GridLayout(3, 1));
        JPanel a = new JPanel();
        
        def = new JTextField(30);
        def.setEditable(false);
        a.add(new JLabel("Definition:"));
        a.add(def);
        defWordInput.add(a);
        
        word = new JTextField(30);
        word.setEditable(false);
        a = new JPanel();
        a.add(new JLabel("Word: "));
        a.add(word);
        defWordInput.add(a);
        
        input = new JTextField(30);
        input.setEditable(true);
        input.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                if (input.getText().equals(""))
                    return;
                if (creativeMode)
                {
                    String in = input.getText();
                    word.setText(in);
                    input.setText("");
                    //ask for confirmation (this is word or def):
                    Object[] options = {"It's the word",
                    "It's the definition",
                    "Neither"};
                    int n = JOptionPane.showOptionDialog(helper,
                    "Is your input one of the following?",
                    "Confirmation",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[2]);
                    //Neither (2), Definition (1), Word (0)
                    if (n==2)
                        return;
                    if (n==1)
                        bob.addWordToFile(in, fileName, false);
                    else if (n==0)
                        bob.addWordToFile(in, fileName, true);
                }
                else
                {
                    if (def.getText().equals(""))
                        return;
                    String hypothesis = input.getText();
                    if (pickedFile&&!doneWithWord)
                    {
                        if (bob.isCorrect(hypothesis))
                        {
                            JOptionPane.showMessageDialog(helper, 
                            "You got the word!",
                            "Euge!", JOptionPane.PLAIN_MESSAGE);
                            scoreNum++;
                        }
                        else
                        {
                            JOptionPane.showMessageDialog(helper, 
                            "The word has been given to you."+
                            "The word has been\n"+
                            "added to your missed list.",
                            "You didn't win...", JOptionPane.PLAIN_MESSAGE);
                            bob.addWordToMissed(theWord, definition);
                            missedCount++;
                        }
                        doneWithWord = true;
                        word.setText(theWord);
                        input.setText("");
                        myTimer.restart();
                        myTimer.stop();
                        ticker = 20;
                        time.setText(ticker+" s");
                        left--;
                        updateScore();
                    }
                    if (speedDemon)
                    {
                        if (pickedFile&&time.getText().equals("20 s"))
                        {
                            setWordClue();
                            if (!doneWithWord)
                                myTimer.start();
                        }
                        else if (!pickedFile)
                        {
                            pickedFile = pickFile();
                        }
                        else
                        {
                            JOptionPane.showMessageDialog(helper, 
                            "Your time has not run out yet.",
                            "Warning", JOptionPane.PLAIN_MESSAGE);
                        }
                    }
                }
            }
        });
        a = new JPanel();
        a.add(new JLabel("Type: "));
        a.add(input);
        defWordInput.add(a);
    }
    
    /**
     * Creates the menu bar and adds it to the JFrame.
     */
    private void createMenuBar()
    {
        JMenuBar menuBar = new JMenuBar();
        JMenu guide = new JMenu("Guide");
        guide.setToolTipText("Help and Other Things");
        guide.setMnemonic(KeyEvent.VK_G);
        guide.getAccessibleContext().setAccessibleDescription("Helping is Caring");
        JMenuItem help = new JMenuItem("Help", KeyEvent.VK_H);
        help.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, ActionEvent.ALT_MASK));
        help.getAccessibleContext().setAccessibleDescription("Help Period.");
        help.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                JOptionPane.showMessageDialog(helper, 
                "The Vocab Helper allows you to practice anything you choose.\n"+
                "You can create the files outside the program,\n "+
                "or inside (doesn't work right now), if you so choose.\n"+
                "If you make it outside, then you"+
                "will have to format it yourself:\n <word>; <definition>;\n",
                "Instructions", JOptionPane.PLAIN_MESSAGE);
            }
        });
        guide.add(help);
        JMenuItem other = new JMenuItem("Misc.", KeyEvent.VK_B);
        other.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, ActionEvent.ALT_MASK));
        other.getAccessibleContext().setAccessibleDescription("Info");
        other.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                JOptionPane.showMessageDialog(helper, 
                "Syntax is important. When inputting file names, you have to remember\n"+
                "that for Mac, it starts with a /. PCs start with a C:/ or whatever root folder-thing\n"+
                "that they use. Here's an example for Mac: /TXTFiles/randomname.txt\n"+
                "The TXTFiles is a folder in the Mac HD. The second / signals that your .txt file\n"+
                "is in TXTFiles. If you want multiple directories, that's fine too. For PC:\n"+
                "C:/TXTFiles/randomname.txt. Again, multiple directories is fine.",
                "", JOptionPane.PLAIN_MESSAGE);
            }
        });
        guide.add(other);
        menuBar.add(guide);
        
        JMenu options = new JMenu("Options");
        options.setToolTipText("Word List Changing");
        options.setMnemonic(KeyEvent.VK_O);
        options.getAccessibleContext().setAccessibleDescription("Preferences");
        JMenuItem stage = new JMenuItem("Stage", KeyEvent.VK_S);
        stage.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.ALT_MASK));
        stage.getAccessibleContext().setAccessibleDescription("Pick the Stage");
        stage.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                if (pickedFile)
                {
                    if (!pickFile())
                        return;
                }
                pickedFile = pickFile();
            }
        });
        options.add(stage);
        
        JMenuItem missed = new JMenuItem("Missed Words", KeyEvent.VK_M);
        missed.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, ActionEvent.ALT_MASK));
        missed.getAccessibleContext().setAccessibleDescription("Test your Missed Words.");
        missed.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                bob.useMissedLists();
                pickedFile = true;
            }
        });
        options.add(missed);
        
        JMenuItem saveMissedWords = new JMenuItem("Save Missed Words (may take a while)", KeyEvent.VK_S);
        saveMissedWords.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.ALT_MASK));
        saveMissedWords.getAccessibleContext().setAccessibleDescription("Save your Missed Words into a file..");
        saveMissedWords.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                String s = "";
                File f = new File(s);
                boolean a = true;
                while (f.exists()||a)
                {
                    s = (String)JOptionPane.showInputDialog(
                    helper, "Type in the name of the file:",
                    "Follow the syntax rules please.",
                    JOptionPane.PLAIN_MESSAGE);
                    if (s==null)
                        return;
                    f = new File(s);
                    a = false;
                }
                bob.sendMissedToFile(s);
            }
        });
        options.add(saveMissedWords);
        
        JMenuItem creative = new JMenuItem("Creative Mode", KeyEvent.VK_K);
        creative.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_K, ActionEvent.ALT_MASK));
        creative.getAccessibleContext().setAccessibleDescription("Add new words to a file.");
        creative.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                if (creativeMode)
                {
                    creativeMode = false;
                    return;
                }
                String s = (String)JOptionPane.showInputDialog(
                helper, "You will now create a file so you can add words to it",
                "Use the .txt extension and other important things.",
                JOptionPane.PLAIN_MESSAGE);
                if (s==null)
                    return;
                fileName = s;
                bob.createNewFile(fileName);
                creativeMode = true;
            }
        });
        options.add(creative);
        
        JMenuItem speedMode = new JMenuItem("Speed Mode", KeyEvent.VK_S);
        speedMode.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.ALT_MASK));
        speedMode.getAccessibleContext().setAccessibleDescription(
        "After typing in a word, you automatically get another word.");
        speedMode.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                if (speedDemon)
                {
                    speedDemon = false;
                    return;
                }
                speedDemon = true;
            }
        });
        options.add(speedMode);
        
        menuBar.add(options);
        
        helper.setJMenuBar(menuBar);
    }
    
    /**
     * Sets up the score and time text fields.
     */
    private void createScoreTime()
    {
        scoreTime = new JPanel();
        scoreTime.setLayout(new GridLayout(2, 2));
        
        JPanel d = new JPanel();
        score = new JTextField(7);
        score.setEditable(false);
        d.add(new JLabel("# Correct: "));
        d.add(score);
        scoreTime.add(d);
        
        d = new JPanel();
        d.add(new JLabel("Time Left: "));
        time = new JTextField(7);
        time.setEditable(false);
        addTimer();
        time.setText(ticker+" s");
        d.add(time);
        scoreTime.add(d);
        
        d = new JPanel();
        d.add(new JLabel("# Missed: "));
        missed = new JTextField(7);
        missed.setEditable(false);
        d.add(missed);
        scoreTime.add(d);
        
        d = new JPanel();
        d.add(new JLabel("# Left: "));
        remaining = new JTextField(7);
        remaining.setEditable(false);
        d.add(remaining);
        scoreTime.add(d);
    }
    
    /**
     * Creates a Timer so that the game can work.
     */
    private void addTimer()
    {
        ActionListener updater = new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                ticker--;
                if (ticker<1)
                {
                    myTimer.stop();
                    word.setText(theWord);
                    updateScore();
                    input.setText("");
                    ticker = 20;
                    doneWithWord = true;
                    JOptionPane.showMessageDialog(helper, 
                    "The word has been given to you."+
                    "The word has been\n"+
                    "added to your missed list.",
                    "You didn't win...", JOptionPane.PLAIN_MESSAGE);
                    bob.addWordToMissed(theWord, definition);
                    missedCount++;
                }   
                time.setText(ticker+" s");
            }
        };
        myTimer = new Timer(1000, updater);
    }
    
    /**
     * Picks a word and definition for that word. Then,
     * it gives the user the definition.
     */
    private void setWordClue()
    {
        String[] a = bob.pickWordAndClue();
        if (a==null||a[0]==null||a[1]==null)
        //this is if there are no more words to test
        {
            if (!bob.getMissedWordsList().isEmpty())
                bob.sendMissedToFile(fileName.substring(0, 
                fileName.length()-4)+"missed.txt");//the -4 is to get rid of .txt
            bob.clearMissed();
            if (!pickFile())
                return;
            doneWithWord = true;
        }
        else
        {
            theWord = a[0];
            definition = a[1];
            def.setText(definition);
            word.setText("");
            input.setText("");
            doneWithWord = false;
        }
    }
    
    /**
     * Updates the score, incorrect, and remaining
     * JTextFields.
     */
    private void updateScore()
    {
        if (!pickedFile)
        {
            scoreNum = 0;
            missedCount = 0;
            left = 0;
        }
        score.setText(scoreNum+"/"+totalPossible);
        missed.setText(missedCount+"/"+totalPossible);
        remaining.setText(left+"/"+totalPossible);
    }
    
    /**
     * Allows the user to pick a file to test.
     * 
     * @return false if the user did not pick a new file;
     *         if the user did, return true.
     */
    private boolean pickFile()
    {
        if (fileName!=null)
        {
            
        }        
        JOptionPane.showMessageDialog(
        helper, "Pick your file.", "Pick the File", JOptionPane.PLAIN_MESSAGE);
        final JFileChooser fc = new JFileChooser("/TXTfiles");
        int returnVal = fc.showOpenDialog(helper);
        if (returnVal>0)
            return false;
        File file = fc.getSelectedFile();
        String a = file.toString();
        fileName = a;
        bob.setStage(fileName);
        scoreNum = 0;
        missedCount = 0;
        left = 0;
        try
        {
            totalPossible = bob.getFileLength();
        }
        catch (IOException e)
        {
            totalPossible = 0;
        }
        left = totalPossible;
        pickedFile = true;
        updateScore();
        return true;
    }
    
    /**
     * Learning Mode
     */
    private void flashCards()
    {
        //not yet implemented
    }
}