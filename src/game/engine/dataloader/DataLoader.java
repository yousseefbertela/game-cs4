package game.engine.dataloader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import game.engine.Role;
import game.engine.cards.Card;
import game.engine.cards.ConfusionCard;
import game.engine.cards.EnergyStealCard;
import game.engine.cards.ShieldCard;
import game.engine.cards.StartOverCard;
import game.engine.cards.SwapperCard;
import game.engine.cells.Cell;
import game.engine.cells.ContaminationSock;
import game.engine.cells.ConveyorBelt;
import game.engine.cells.DoorCell;
import game.engine.monsters.Dasher;
import game.engine.monsters.Dynamo;
import game.engine.monsters.Monster;
import game.engine.monsters.MultiTasker;
import game.engine.monsters.Schemer;

public class DataLoader {

	public static final String CARDS_FILE_NAME = "cards.csv";
    public static final String CELLS_FILE_NAME = "cells.csv";
    private static final String MONSTERS_FILE_NAME = "monsters.csv";

    public static ArrayList<Card> readCards() throws IOException {

        ArrayList<Card> cards = new ArrayList<>();

        BufferedReader br = new BufferedReader(new FileReader(CARDS_FILE_NAME));

        String line;

        while((line = br.readLine()) != null) {

        	String[] data = line.split(",", -1);

            String type = data[0].trim();
            String name = data[1].trim();
            String description = data[2].trim();
            int rarity = Integer.parseInt(data[3].trim());

            if(type.equals("SWAPPER")) {
                cards.add(new SwapperCard(name, description, rarity));
            }

            else if(type.equals("SHIELD")) {
                cards.add(new ShieldCard(name, description, rarity));
            }

            else if(type.equals("ENERGYSTEAL")) {
                int energy = Integer.parseInt(data[4].trim());
                cards.add(new EnergyStealCard(name, description, rarity, energy));
            }

            else if(type.equals("STARTOVER")) {
                boolean lucky = Boolean.parseBoolean(data[4].trim());
                cards.add(new StartOverCard(name, description, rarity, lucky));
            }

            else if(type.equals("CONFUSION")) {
                int duration = Integer.parseInt(data[4].trim());
                cards.add(new ConfusionCard(name, description, rarity, duration));
            }
        }

        br.close();

        return cards;
    }
    public static ArrayList<Cell> readCells() throws IOException {
        ArrayList<Cell> cells = new ArrayList<Cell>();
        BufferedReader br = new BufferedReader(new FileReader(CELLS_FILE_NAME));

        String line;
        while ((line = br.readLine()) != null) {
            String[] parts = line.split(",");

            if (parts.length == 3) {
                String name = parts[0].trim();
                Role role = Role.valueOf(parts[1].trim());
                int energy = Integer.parseInt(parts[2].trim());

                cells.add(new DoorCell(name, role, energy));
            } else if (parts.length == 2) {
                String name = parts[0].trim();
                int effect = Integer.parseInt(parts[1].trim());

                if (effect > 0) {
                    cells.add(new ConveyorBelt(name, effect));
                } else if (effect < 0) {
                    cells.add(new ContaminationSock(name, effect));
                }
            }
        }

        br.close();
        return cells;
    }

    public static ArrayList<Monster> readMonsters() throws IOException {
        ArrayList<Monster> monsters = new ArrayList<Monster>();
        BufferedReader br = new BufferedReader(new FileReader(MONSTERS_FILE_NAME));

        String line;
        while ((line = br.readLine()) != null) {
            String[] parts = line.split(",");

            String monsterType = parts[0].trim().toLowerCase();
            String name = parts[1].trim();
            String description = parts[2].trim();
            Role role = Role.valueOf(parts[3].trim());
            int energy = Integer.parseInt(parts[4].trim());

            switch (monsterType) {
                case "dasher":
                    monsters.add(new Dasher(name, description, role, energy));
                    break;

                case "dynamo":
                    monsters.add(new Dynamo(name, description, role, energy));
                    break;

                case "multitasker":
                    monsters.add(new MultiTasker(name, description, role, energy));
                    break;

                case "schemer":
                    monsters.add(new Schemer(name, description, role, energy));
                    break;
            }
        }

        br.close();
        return monsters;
    }
}