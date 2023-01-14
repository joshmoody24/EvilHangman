package hangman;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Scanner;
import java.util.HashMap;

public class EvilHangmanGame implements IEvilHangmanGame {

    Set<String> dict;
    SortedSet<Character> guessedLetters;

    @Override
    public void startGame(File dictionary, int wordLength) throws IOException, EmptyDictionaryException {
        Scanner scanner = new Scanner(dictionary);
        dict = new TreeSet<String>();
        guessedLetters = new TreeSet<Character>();

        while(scanner.hasNext()) {
            String word = scanner.next().trim();
            if(word.length() == wordLength){
                dict.add(word);
            }
        }
        if(dict.size() == 0) throw new EmptyDictionaryException();
    }

    @Override
    public Set<String> makeGuess(char guess) throws GuessAlreadyMadeException {
        char lowerGuess = Character.toLowerCase(guess);
        if(guessedLetters.contains(lowerGuess)) throw new GuessAlreadyMadeException();
        guessedLetters.add(lowerGuess);
        // partition the sets
        HashMap<String, Set<String>> subsets = new HashMap<String, Set<String>>();
        for(String word : dict){
            String hash = getHashCode(word, lowerGuess);
            if(!subsets.containsKey(hash)) subsets.put(hash, new TreeSet<String>());
            subsets.get(hash).add(word);
        }

        // select the largest subset to be the new set
        // in case of tie:
        // 1. Choose the group in which the letter does not appear at all.
        // 2. If each group has the guessed letter, choose the one with the fewest letters.
        // 3. If this still has not resolved the issue, choose the one with the
        //    rightmost guessed letter (e.g. given the patterns E--E and -E-E, the second group would be chosen)
        // 4. If there is still more than one group, choose the one with the next
        //    rightmost letter. Repeat this step (step 4) until a group is chosen.

        int largestCount = 0;
        Set<String> largestSet = new TreeSet<String>();
        String chosenHash = "";
        for(String hash : subsets.keySet()){
            boolean newLargest = false;
            if(subsets.get(hash).size() > largestCount){
                newLargest = true;
            }
            // the priority stuff
            if(subsets.get(hash).size() == largestCount){
                if(!hash.contains(Character.toString(guess))){
                    newLargest = true;
                }
                else if(countLetters(hash) < countLetters(chosenHash)){
                    newLargest = true;
                }
                else if(countLetters(hash) == countLetters(chosenHash)){
                    // keep checking rightmost indexes until one beats the other
                    String hashCopy = String.copyValueOf(hash.toCharArray());
                    String chosenHashCopy = String.copyValueOf(chosenHash.toCharArray());
                    while(rightmostLetter(hashCopy) == rightmostLetter(chosenHashCopy)){
                        if(hashCopy.length() == 0) break;
                        hashCopy = hashCopy.substring(0, hashCopy.length() - 1);
                        chosenHashCopy = chosenHashCopy.substring(0, chosenHashCopy.length() - 1);
                    }
                    if(rightmostLetter(hashCopy) > rightmostLetter(chosenHashCopy)){
                        newLargest = true;
                    }
                }
            }

            if(newLargest){
                largestCount = subsets.get(hash).size();
                largestSet = subsets.get(hash);
                chosenHash = hash;
            }
        }
        dict = largestSet;
        return largestSet;
    }

    private String getHashCode(String word, char guess){
        StringBuilder builder = new StringBuilder();
        for(char c : word.toCharArray()){
            if(c == guess) builder.append(c);
            else builder.append("_");
        }
        return builder.toString();
    }

    private int countLetters(String word){
        int sum = 0;
        for (char c : word.toCharArray()){
            if(Character.isLetter(c)){
                sum++;
            }
        }
        return sum;
    }

    private int rightmostLetter(String word){
        int index = 0;
        for (int i = 0; i < word.length(); i++){
            if(Character.isLetter(word.charAt(i))){
                index = i;
            }
        }
        return index;
    }

    @Override
    public SortedSet<Character> getGuessedLetters() {
        return guessedLetters;
    }
}
