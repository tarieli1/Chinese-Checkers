package View;

import Controller.gameOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class UserInterface {
    
    private final Scanner scanner = new Scanner(System.in);
    
    public int getTotalPlayers() {
        System.out.println("Enter the number of players 2-6");
        return scanner.nextInt();
    }

    public int getColorNumberForEach(int totalPlayers) {
        System.out.println("Enter number of colors each player want to use");
        return scanner.nextInt();
    }

    public int getHumanPlayers(int totalPlayers) {
        System.out.format(
                "Enter the number of human players 0-%d%n", totalPlayers);
        return scanner.nextInt();
    }

    public List getNames(int totalPlayers) {
        ArrayList<String> playerNames = new ArrayList<>(totalPlayers);
        for (int i = 1; i <= totalPlayers; i++) {
            System.out.format("Please enter the %d' player name: ", i);
            playerNames.add(scanner.next());
        }
        
        return playerNames;
    }

    public gameOption getGameOption() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
