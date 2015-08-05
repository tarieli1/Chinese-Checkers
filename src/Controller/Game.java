package Controller;
import Model.Engine;
import View.UserInterface;
import java.util.*;

public class Game {
    
    private Engine gameLogic;
    private final UserInterface UI = new UserInterface();
    
    public void Run()
    {
        Engine.Settings gameSettings = getGameSettings();
        gameLogic = new Engine(gameSettings);
        playGame(gameSettings);
    }

    private void playGame(Engine.Settings gameSettings) {
        play();      
        newGame(gameSettings);
    }

    private void newGame(Engine.Settings gameSettings) {
        gameOption option = UI.getGameOption();
        switch(option)
        {
            case NewGame:
                gameSettings = getGameSettings();
            case Restart:
                gameLogic.restart(gameSettings);
                playGame(gameSettings);
                break;
            default:
                break;
        }
    }

    private void play() {
        boolean isRunning = true;
        
        while(isRunning)
        {
            isRunning = gameLogic.doIteration();
        }
    }

    private Engine.Settings getGameSettings() {
        int totalPlayers = UI.getTotalPlayers();
        Engine.Settings gameSettings = initSettings(totalPlayers);
        gameSettings.setTotalPlayers(totalPlayers);

        return gameSettings;
    }

    private Engine.Settings initSettings(int totalPlayers) {
        Engine.Settings gameSettings = new Engine.Settings();
        initColorNumber(totalPlayers, gameSettings);
        initHumanPlayers(totalPlayers, gameSettings);
        initPlayerNames(totalPlayers, gameSettings);
        
        return gameSettings;
    }

    private void initPlayerNames(int totalPlayers, Engine.Settings gameSettings) {
        List playerNames = UI.getNames(totalPlayers);
        gameSettings.setPlayerNames(playerNames);
    }

    private void initHumanPlayers(int totalPlayers, Engine.Settings gameSettings) {
        int humanPlayers = UI.getHumanPlayers(totalPlayers);
        gameSettings.setHumanPlayers(humanPlayers);
    }

    private void initColorNumber(int totalPlayers, Engine.Settings gameSettings) {
        int colorNumber = UI.getColorNumberForEach(totalPlayers);
        gameSettings.setColorNumber(colorNumber);
    }
    
}
