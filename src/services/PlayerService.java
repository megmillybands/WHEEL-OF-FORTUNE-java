package services;

import database.PlayerRepository;
import models.Player;

public class PlayerService {

    private PlayerRepository playerRepository = new PlayerRepository();

    public void createPlayer(String playerName) {
        Player player = new Player(playerName, 0);
    }

    public PlayerRepository getPlayerRepository() {
        return playerRepository;
    }

}