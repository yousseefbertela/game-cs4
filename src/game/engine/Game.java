package game.engine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import game.engine.dataloader.DataLoader;
import game.engine.monsters.Monster;

public class Game {

    private final Board board;
    private final ArrayList<Monster> allMonsters;
    private final Monster player;
    private final Monster opponent;
    private Monster current;

    public Game(Role playerRole) throws IOException {
        this.board = new Board(DataLoader.readCards());
        this.allMonsters = DataLoader.readMonsters();
        this.player = selectRandomMonsterByRole(playerRole);

        Role opponentRole;
        if (playerRole == Role.SCARER) {
            opponentRole = Role.LAUGHER;
        } else {
            opponentRole = Role.SCARER;
        }

        this.opponent = selectRandomMonsterByRole(opponentRole);
        this.current = this.player;
    }

    private Monster selectRandomMonsterByRole(Role role) {
        ArrayList<Monster> matchingMonsters = new ArrayList<>();

        for (Monster monster : this.allMonsters) {
            if (monster.getRole() == role) {
            	if(!matchingMonsters.contains(monster))
            		matchingMonsters.add(monster);
            }
        }

        Random random = new Random();
        int index = random.nextInt(matchingMonsters.size());
        return matchingMonsters.get(index);
    }

    public Board getBoard() {
        return this.board;
    }

    public ArrayList<Monster> getAllMonsters() {
        return this.allMonsters;
    }

    public Monster getPlayer() {
        return this.player;
    }

    public Monster getOpponent() {
        return this.opponent;
    }

    public Monster getCurrent() {
        return this.current;
    }

    public void setCurrent(Monster current) {
        this.current = current;
    }
}