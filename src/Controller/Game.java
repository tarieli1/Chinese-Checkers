package Controller;
import Model.Logic;
import View.UserInterface;
import java.util.*;

public class Game {
    
    private Logic gameLogic;
    private final UserInterface UI = new UserInterface();;
    
    public void Run()
    {
        Logic.Settings gameSettings = getGameSettings();
        gameLogic = new Logic(gameSettings);
    }

    private Logic.Settings getGameSettings() {
        int totalPlayers = UI.getTotalPlayers();
        Logic.Settings gameSettings = initSettings(totalPlayers);
        gameSettings.setTotalPlayers(totalPlayers);

        return gameSettings;
    }

    private Logic.Settings initSettings(int totalPlayers) {
        Logic.Settings gameSettings = new Logic.Settings();
        initColorNumber(totalPlayers, gameSettings);
        initHumanPlayers(totalPlayers, gameSettings);
        initPlayerNames(totalPlayers, gameSettings);
        
        return gameSettings;
    }

    private void initPlayerNames(int totalPlayers, Logic.Settings gameSettings) {
        List playerNames = UI.getNames(totalPlayers);
        gameSettings.setPlayerNames(playerNames);
    }

    private void initHumanPlayers(int totalPlayers, Logic.Settings gameSettings) {
        int humanPlayers = UI.getHumanPlayers(totalPlayers);
        gameSettings.setHumanPlayers(humanPlayers);
    }

    private void initColorNumber(int totalPlayers, Logic.Settings gameSettings) {
        int colorNumber = UI.getColorNumberForEach(totalPlayers);
        gameSettings.setColorNumber(colorNumber);
    }
    
}
