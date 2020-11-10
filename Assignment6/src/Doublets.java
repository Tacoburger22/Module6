import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;


/**
 * Provides an implementation of the WordLadderGame interface. 
 *
 * @author Isaac Weiss (icw0001@auburn.edu)
 * @version 2020-11-09
 */
public class Doublets implements WordLadderGame {

    // The word list used to validate words.
    // Must be instantiated and populated in the constructor.
    /////////////////////////////////////////////////////////////////////////////
    // DECLARE A FIELD NAMED lexicon HERE. THIS FIELD IS USED TO STORE ALL THE //
    // WORDS IN THE WORD LIST. YOU CAN CREATE YOUR OWN COLLECTION FOR THIS     //
    // PURPOSE OF YOU CAN USE ONE OF THE JCF COLLECTIONS. SUGGESTED CHOICES    //
    // ARE TreeSet (a red-black tree) OR HashSet (a closed addressed hash      //
    // table with chaining).
    /////////////////////////////////////////////////////////////////////////////
    HashSet<String> lexicon;
    /**
     * Instantiates a new instance of Doublets with the lexicon populated with
     * the strings in the provided InputStream. The InputStream can be formatted
     * in different ways as long as the first string on each line is a word to be
     * stored in the lexicon.
     */
    public Doublets(InputStream in) {
        try {
            //////////////////////////////////////
            // INSTANTIATE lexicon OBJECT HERE  //
            //////////////////////////////////////
            lexicon = new HashSet<>();
            Scanner s =
                new Scanner(new BufferedReader(new InputStreamReader(in)));
            while (s.hasNext()) {
                String str = s.next();
                /////////////////////////////////////////////////////////////
                // INSERT CODE HERE TO APPROPRIATELY STORE str IN lexicon. //
                /////////////////////////////////////////////////////////////
                //lexicon.add(str.toUpperCase().split(" ")[0]);
                lexicon.add(str.toUpperCase());
                s.nextLine();
            }
            in.close();
        }
        catch (java.io.IOException e) {
            System.err.println("Error reading from InputStream.");
            System.exit(1);
        }
    }

    //////////////////////////////////////////////////////////////
    // ADD IMPLEMENTATIONS FOR ALL WordLadderGame METHODS HERE  //
    //////////////////////////////////////////////////////////////

    /**
     * Returns the Hamming distance between two strings, str1 and str2. The
     * Hamming distance between two strings of equal length is defined as the
     * number of positions at which the corresponding symbols are different. The
     * Hamming distance is undefined if the strings have different length, and
     * this method returns -1 in that case. See the following link for
     * reference: https://en.wikipedia.org/wiki/Hamming_distance
     *
     * @param  str1 the first string
     * @param  str2 the second string
     * @return      the Hamming distance between str1 and str2 if they are the
     *                  same length, -1 otherwise
     */
    public int getHammingDistance(String str1, String str2) {
        if (str1.length() != str2.length()) {
            return -1;
        }
        int hamming = 0;
        for (int i = 0; i < str1.length(); i++) {
            if (str1.charAt(i) != str2.charAt(i)) {
                hamming++;
            }
        }
        return hamming;
    }

    /**
     * Returns a minimum-length word ladder from start to end. If multiple
     * minimum-length word ladders exist, no guarantee is made regarding which
     * one is returned. If no word ladder exists, this method returns an empty
     * list.
     *
     * Breadth-first search must be used in all implementing classes.
     *
     * @param  start  the starting word
     * @param  end    the ending word
     * @return        a minimum length word ladder from start to end
     */
    public List<String> getMinLadder(String start, String end) {
        List<String> minString = new ArrayList<>();
        if (start.equals(end) && isWord(start)) {
            minString.add(start);
            return minString;
        }
        if (start.length() != end.length()) {
            return minString;
        }
        if (!isWord(start) || !isWord(end)) {
            return minString;
        }
        start = start.toUpperCase();
        end = end.toUpperCase();
        List<Node> minLadder = minLadderSupport(start, end); //returns solution list in reverse order
        for (int i = minLadder.size() - 1; i >= 0; i--) {
            minString.add(minLadder.remove(i).string);
        }
        return minString;
    }

    private List<Node> minLadderSupport(String start, String end) {
        Deque<Node> queue = new ArrayDeque<>();
        List<Node> solution = new ArrayList<>();
        List<String> usedWords = new ArrayList<>();
        queue.addLast(new Node(start, null));
        while (!queue.isEmpty()) {
            Node p = queue.removeFirst();
            for (String s : getNeighbors(p.string)) {
                if (s.equals(end)) {
                    Node n = new Node(s, p);
                    queue.addLast(n);
                    while (!n.string.equals(start)) {
                        solution.add(n);
                        n = new Node(p.string, p.predecessor);
                        if (!p.string.equals(start)) {
                            p = new Node(p.predecessor.string, p.predecessor.predecessor);
                        }
                    }
                    solution.add(new Node(start, null));
                    return solution;
                }
                if (!usedWords.contains(s)) {
                    queue.addLast(new Node(s, p));
                    usedWords.add(s);
                }
            }
        }
        return solution;
    }

    /**
     * Returns all the words that have a Hamming distance of one relative to the
     * given word.
     *
     * @param  word the given word
     * @return      the neighbors of the given word
     */
    public List<String> getNeighbors(String word) {
        ArrayList<String> neighbors = new ArrayList<>();
        StringBuilder neighborString;
        word = word.toUpperCase();
        for (int i = 0; i < word.length(); i++) {
            neighborString = new StringBuilder(word);
            for (int j = 0; j < 26; j++) {
                neighborString.setCharAt(i, (char)(j+65));
                if (isWord(neighborString.toString()) && !neighborString.toString().equals(word)) {
                    neighbors.add(neighborString.toString());
                }
            }
        }
        return neighbors;
    }

    /**
     * Returns the total number of words in the current lexicon.
     *
     * @return number of words in the lexicon
     */
    public int getWordCount() {
        return lexicon.size();
    }

    /**
     * Checks to see if the given string is a word.
     *
     * @param  str the string to check
     * @return     true if str is a word, false otherwise
     */
    public boolean isWord(String str) {
        return lexicon.contains(str.toUpperCase());
    }

    /**
     * Checks to see if the given sequence of strings is a valid word ladder.
     *
     * @param  sequence the given sequence of strings
     * @return          true if the given sequence is a valid word ladder,
     *                       false otherwise
     */
    public boolean isWordLadder(List<String> sequence) {
        if (sequence == null || sequence.size() == 0) {
            return false;
        }
        for (int i = 1; i < sequence.size(); i++) {
            if (getHammingDistance(sequence.get(i), sequence.get(i - 1)) != 1 || !isWord(sequence.get(i)) ||
                    !isWord(sequence.get(i - 1))) {
                return false;
            }
        }
        return true;
    }

    private static class Node {
        String string;
        Node predecessor;

        public Node(String s, Node pred) {
            string = s;
            predecessor = pred;
        }
    }
}

