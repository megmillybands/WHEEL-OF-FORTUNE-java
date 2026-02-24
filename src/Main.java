import database.PlayerRepository;
import database.Queries;

import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class Main {

    public void main(String[] args) throws InterruptedException, SQLException {

        System.out.println("WHEEL...");
        Thread.sleep(1000);
        System.out.println("OF...");
        Thread.sleep(1000);
        System.out.println("FORTUNE!");
        Thread.sleep(1000);

        System.out.println("\nWelcome to Wheel of Fortune!\nThis game requires 3 players to play.");
        Thread.sleep(1000);

        System.out.println("\nPlayer 1, please enter your name: ");
        String player1 = PlayerRepository.createPlayer();
        System.out.println("\nPlayer 2, please enter your name: ");
        String player2 = PlayerRepository.createPlayer();
        System.out.println("\nPlayer 3, please enter your name: ");
        String player3 = PlayerRepository.createPlayer();

        System.out.println("\nAre you ready?");
        Thread.sleep(1000);
        System.out.println("Let's begin.");
        Thread.sleep(1000);

        List<Object> puzzle = Queries.getRandomPuzzle();

        int player = 1;
        String currentPlayer = "";
        String puzzleHidden;
        boolean puzzleIsSolved = false;

        end:
        while (!puzzleIsSolved) {

            if (player == 1) {
                currentPlayer = player1;
            } else if (player == 2) {
                currentPlayer = player2;
            } else if (player == 3) {
                currentPlayer = player3;
            } else {
                currentPlayer = player1;
                player = 1;
            }

            puzzleHidden = Queries.updatePuzzle(puzzle);

            System.out.println("\n" + currentPlayer + ", it's your turn!\n");
            Thread.sleep(1000);

            int currentPlayerMoney = Queries.currentPlayerMoney(currentPlayer);

            System.out.println("Category:");
            System.out.println(puzzle.get(1));
            Thread.sleep(1000);
            System.out.println("\nPuzzle:");
            System.out.println(puzzleHidden);
            Thread.sleep(1000);
            System.out.println("\nYou currently have $" + currentPlayerMoney + ".");
            System.out.println("Would you like to [spin] the wheel, [buy] a vowel, [solve] the puzzle, [check] letters, or [end] the game early?");
            Scanner input = new Scanner(System.in);
            String choice = input.nextLine().toLowerCase().trim();

            switch (choice) {
                case "spin" -> {
                    int spinResult = Wheel.spinTheWheel();
                    System.out.print("\nSpinning");
                    Thread.sleep(333);
                    System.out.print(".");
                    Thread.sleep(333);
                    System.out.print(".");
                    Thread.sleep(333);
                    System.out.print(".");
                    Thread.sleep(1000);
                    System.out.println("\n\n" + spinResult + "!\n");
                    Thread.sleep(1000);

                    System.out.println("Now, pick a letter: ");
                    input = new Scanner(System.in);
                    String letterString = input.nextLine().toUpperCase();
                    char letter = letterString.charAt(0);

                    if (letterString.length() > 1) {
                        System.out.println("\nWe just need a letter. Just one.");
                        Thread.sleep(1000);
                    } else if (Queries.isVowel(letter)) {
                        System.out.println("\nPlease select a consonant.");
                        Thread.sleep(1000);
                    } else if (Queries.selectedThisRound(letter)) {
                        System.out.println("\nThis letter has already been selected.");
                        Thread.sleep(1000);
                    } else {
                        if (Queries.flipLettersAndIsValid(letter, (Integer) puzzle.get(2), currentPlayer, spinResult)) {
                            puzzleHidden = Queries.updatePuzzle(puzzle);
                            if (!puzzleHidden.contains("-")) {
                                puzzleIsSolved = true;
                            }
                        } else {
                            player++;
                        }
                        Queries.assignPlayerToLetter(currentPlayer, letter);
                    }
                }
                case "buy" -> {
                    if (Queries.canBuyVowel(currentPlayer)) {
                        System.out.println("\nWhich vowel would you like to buy?");
                        input = new Scanner(System.in);
                        String letterString = input.nextLine().toUpperCase();
                        char letter = letterString.charAt(0);

                        if (letterString.length() > 1) {
                            System.out.println("\nWe just need a letter. Just one.");
                            Thread.sleep(1000);
                        } else if (!Queries.isVowel(letter)) {
                            System.out.println("\nWhy buy a consonant when you could spin for one instead? Please select a vowel.");
                            Thread.sleep(1000);
                        } else if (Queries.selectedThisRound(letter)) {
                            System.out.println("\nThis vowel has already been bought.");
                            Thread.sleep(1000);
                        } else {
                            if (Queries.buyVowelAndIsValid(letter, (Integer) puzzle.get(2), currentPlayer)) {
                                puzzleHidden = Queries.updatePuzzle(puzzle);
                                if (!puzzleHidden.contains("-")) {
                                    puzzleIsSolved = true;
                                }
                            } else {
                                player++;
                            }
                            Queries.assignPlayerToLetter(currentPlayer, letter);
                        }
                    } else {
                        System.out.println("\nYou don't have enough money to buy a vowel! Vowels cost $250.");
                        Thread.sleep(1000);
                    }
                }
                case "solve" -> {
                    System.out.println("\nOkay, give it a shot: ");
                    input = new Scanner(System.in);
                    String playerSolve = input.nextLine().toUpperCase();
                    if (playerSolve.equals(puzzle.get(3).toString())) {
                        puzzleIsSolved = true;
                    } else {
                        System.out.println("\nSorry! That is incorrect.");
                        Thread.sleep(1000);
                        player++;
                    }
                }
                case "check" -> {
                    System.out.println("\nHere are the letters that have already been chosen:");
                    Thread.sleep(1000);
                    HashMap<String, String> assignedLetters = Queries.showAssignedLetters();
                    if (!assignedLetters.isEmpty()) {
                        assignedLetters.forEach((key, value) -> System.out.println(key + ": " + value));
                        Thread.sleep(1000);
                    } else {
                        System.out.println("\nNo letters have been selected yet!");
                        Thread.sleep(1000);
                    }
                }
                case "end" -> {
                    break end;
                }
                default -> {
                    System.out.println("\nInvalid choice.");
                    Thread.sleep(1000);
                }
            }
        }

        if (puzzleIsSolved) {
            System.out.println("\nCongratulations, " + currentPlayer + "! You've solved the puzzle!");
            Thread.sleep(1000);
        }

        System.out.println("\nThank you for playing!");
        Queries.endGame(puzzle);
    }
}