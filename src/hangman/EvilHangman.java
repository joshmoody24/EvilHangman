package hangman;

import java.io.File;
import java.util.Scanner;
import java.util.Set;

public class EvilHangman {

    public static void main(String[] args) {
        if(args.length != 3){
            System.out.println("Incorrect number of arguments");
            System.exit(1);
        }
        String dictionaryFileName = args[0];
        int wordLength = Integer.parseInt(args[1]);
        int numGuesses = Integer.parseInt(args[2]);
        File dictFile = new File(dictionaryFileName);

        IEvilHangmanGame game = new EvilHangmanGame();

        // create the initial hash
        StringBuilder initialHashBuilder = new StringBuilder();
        for(int i = 0; i < wordLength; i++){
            initialHashBuilder.append("_");
        }
        String hash = initialHashBuilder.toString();
        Scanner input = new Scanner(System.in);

        Set<String> newDict = null;

        try {
            game.startGame(dictFile, wordLength);
        }
        catch(Exception e){
            System.exit(1);
        }

        boolean won = false;
        while(numGuesses > 0 && won == false){
            System.out.println("You have " + numGuesses + " guesses left");
            System.out.println("Used letters: " + game.getGuessedLetters());
            System.out.println("Word: " + hash);

            String rawInput = "";
            boolean validGuess = false;
            while(validGuess == false){
                System.out.print("Enter guess: ");
                rawInput = input.next();
                if(rawInput.length() != 1 || !Character.isAlphabetic(rawInput.toCharArray()[0])){
                    System.out.print("Invalid input! ");
                    continue;
                }
                try{
                    newDict = game.makeGuess(rawInput.charAt(0));
                    validGuess = true;
                }
                catch(GuessAlreadyMadeException e) {
                    System.out.print("Guess already made! ");
                    continue;
                }
            }

            char guess = rawInput.toLowerCase().charAt(0);
            hash = getHash(newDict, game.getGuessedLetters());
            boolean correctGuess = hash.contains(Character.toString(guess));
            if(correctGuess){
                int numLetter = getLetterCount(hash, guess);
                String toBe = "is";
                if(numLetter > 1) toBe = "are";
                System.out.println("Yes, there " + toBe + " " + numLetter + " " + guess);
            }
            else{
                System.out.println("Sorry, there are no " + guess);
            }
            System.out.println("");
            if(!hash.contains(Character.toString('-'))){
                won = true;
            }
            if(!correctGuess) numGuesses -= 1;
        }
        if(won) System.out.println("You win! You guessed the word: " + hash);
        else System.out.println("Sorry, you lost! The correct word was: " + newDict.iterator().next());
        System.exit(0);
    }

    public static String getHash(Set<String> dict, Set<Character> guessedLetters){
        String word = dict.iterator().next();
        StringBuilder b = new StringBuilder();
        for(char c : word.toCharArray()){
            if(guessedLetters.contains(c)){
                b.append(c);
            }
            else{
                b.append("-");
            }
        }
        return b.toString();
    }
    public static int getLetterCount(String word, char letter){
        int count = 0;
        for(char c : word.toCharArray()){
            if(letter == c) count++;
        }
        return count;
    }
}
